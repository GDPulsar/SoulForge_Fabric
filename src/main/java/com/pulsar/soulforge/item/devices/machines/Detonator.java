package com.pulsar.soulforge.item.devices.machines;

import com.pulsar.soulforge.client.item.GeoMagicItemRenderer;
import com.pulsar.soulforge.client.item.RevivalIdolRenderer;
import com.pulsar.soulforge.entity.DetonatorMine;
import com.pulsar.soulforge.item.devices.DeviceBase;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.trait.Traits;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Detonator extends DeviceBase implements GeoItem {
    public Detonator() {
        super(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC), 1000, Traits.bravery);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        TypedActionResult<ItemStack> result = super.use(world, user, hand);
        if (result.getResult().isAccepted()) return result;
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            stack.getOrCreateNbt();
            if (!stack.getNbt().contains("mines")) {
                stack.getNbt().put("mines", new NbtList());
            }
            List<DetonatorMine> mines = new ArrayList<>();
            int[] array = stack.getNbt().getIntArray("mines");
            for (int hash : array) {
                mines.addAll(world.getEntitiesByClass(DetonatorMine.class, Box.of(user.getPos(), 200, 200, 200),
                        (entity) -> entity.getUuid().hashCode() == hash));
            }
            if (!user.isSneaking()) {
                if (mines.size() < 10) {
                    if (getCharge(stack) >= 25) {
                        BlockHitResult hit = world.raycast(new RaycastContext(user.getEyePos(), user.getRotationVector().multiply(32f).add(user.getEyePos()), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, user));
                        if (hit != null) {
                            if (hit.getType() == HitResult.Type.BLOCK) {
                                BlockPos pos = hit.getBlockPos();
                                Direction direction = hit.getSide();
                                DetonatorMine mine = new DetonatorMine(user, pos, direction);
                                world.spawnEntity(mine);
                                int mineHash = mine.getUuid().hashCode();
                                List<Integer> intList = new ArrayList<>();
                                intList.add(mineHash);
                                for (int hash : array) intList.add(hash);
                                stack.getNbt().putIntArray("mines", intList);
                                world.playSound(null, user.getBlockPos(), SoulForgeSounds.MINE_SUMMON_EVENT, SoundCategory.MASTER, 1f, 1f);
                                decreaseCharge(stack, 25);
                                return TypedActionResult.consume(stack);
                            }
                        }
                    }
                }
            } else {
                for (DetonatorMine mine : mines) {
                    mine.setDetonating(true);
                    mine.setDetonatingTimer((int)(Math.random()*20) + 15);
                }
                return TypedActionResult.consume(stack);
            }
        }
        return TypedActionResult.fail(stack);
    }

    public AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private final GeoMagicItemRenderer<Detonator> renderer = new GeoMagicItemRenderer<>("detonator", "detonator", "detonator");

            @Override
            public BuiltinModelItemRenderer getCustomRenderer() {
                return this.renderer;
            }
        });
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return renderProvider;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        tAnimationState.getController().setAnimation(RawAnimation.begin().then("spin", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}

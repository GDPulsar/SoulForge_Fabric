package com.pulsar.soulforge.item.devices.devices;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.item.GeoMagicItemRenderer;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.devices.PickaxeDeviceBase;
import com.pulsar.soulforge.trait.Traits;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Shatterdrill extends PickaxeDeviceBase implements GeoItem {
    public Shatterdrill() {
        super(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE), 300, Traits.bravery, 4f, -3.2f, ToolMaterials.IRON);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        TypedActionResult<ItemStack> result = super.use(world, user, hand);
        if (result.getResult().isAccepted()) return result;
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            if (getCharge(stack) >= 25) {
                BlockHitResult hit = world.raycast(new RaycastContext(user.getPos(), user.getPos().subtract(0, 100, 0), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.ANY, user));
                BlockState state = world.getBlockState(hit.getBlockPos());
                if (hit.getPos().distanceTo(user.getPos()) >= 1f && state.isSolidBlock(world, hit.getBlockPos())) {
                    SoulComponent playerSoul = SoulForge.getPlayerSoul(user);
                    playerSoul.addTag("shatterdrill");
                    user.setVelocity(0, -2f, 0);
                    user.velocityModified = true;
                    decreaseCharge(stack, 25);
                    user.getItemCooldownManager().set(this, 100);
                    return TypedActionResult.success(stack);
                }
            }
        }
        return TypedActionResult.fail(stack);
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        if (getCharge(stack) > 0) return super.getMiningSpeedMultiplier(stack, state);
        return 1f;
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (getCharge(stack) > 0) decreaseCharge(stack, 1);
        return super.postMine(stack, world, state, pos, miner);
    }

    public AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private final GeoMagicItemRenderer<Shatterdrill> renderer = new GeoMagicItemRenderer<>("shatterdrill", "shatterdrill", "shatterdrill");

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

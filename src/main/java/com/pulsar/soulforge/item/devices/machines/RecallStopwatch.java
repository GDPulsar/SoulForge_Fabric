package com.pulsar.soulforge.item.devices.machines;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.item.GeoMagicItemRenderer;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.item.devices.DeviceBase;
import com.pulsar.soulforge.trait.Traits;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RecallStopwatch extends DeviceBase implements GeoItem {
    private List<Vec3d> lastPositions = new ArrayList<>();
    private List<Vec2f> lastRotations = new ArrayList<>();

    public RecallStopwatch() {
        super(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC), 1000, Traits.patience);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        TypedActionResult<ItemStack> result = super.use(world, user, hand);
        if (result.getResult().isAccepted()) return result;
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            if (getCharge(stack) >= 250) {
                decreaseCharge(stack, 250);
                Vec3d tpPos = lastPositions.get(0);
                user.teleport(tpPos.x, tpPos.y, tpPos.z);
                user.setPitch(lastRotations.get(0).x);
                user.setYaw(lastRotations.get(0).y);
                lastPositions = new ArrayList<>();
                lastRotations = new ArrayList<>();
            }
        }
        return TypedActionResult.fail(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        lastPositions.add(entity.getPos());
        lastRotations.add(new Vec2f(entity.getPitch(), entity.getYaw()));
        if (lastPositions.size() > 300) {
            lastPositions.remove(0);
        }
        if (lastRotations.size() > 300) {
            lastRotations.remove(0);
        }
    }

    public AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private final GeoMagicItemRenderer<RecallStopwatch> renderer = new GeoMagicItemRenderer<>("recall_stopwatch", "recall_stopwatch");

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
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, (animationState) -> PlayState.STOP));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}

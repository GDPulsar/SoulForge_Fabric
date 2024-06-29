package com.pulsar.soulforge.item.devices.devices;

import com.pulsar.soulforge.client.item.GeoMagicItemRenderer;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.entity.BlastEntity;
import com.pulsar.soulforge.item.devices.DeviceBase;
import com.pulsar.soulforge.item.devices.machines.DeterminationInjector;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.trait.TraitBase;
import com.pulsar.soulforge.trait.Traits;
import com.pulsar.soulforge.util.Utils;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FreezeRay extends DeviceBase implements GeoItem {
    public FreezeRay() {
        super(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE), 300, Traits.patience);
    }

    public HitResult raycast(PlayerEntity player, Vec3d direction, float distance) {
        Vec3d vec3d = player.getCameraPosVec(1f);
        Vec3d vec3d3 = vec3d.add(direction.x * distance, direction.y * distance, direction.z * distance);
        return player.getWorld().raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        TypedActionResult<ItemStack> result = super.use(world, user, hand);
        if (result.getResult().isAccepted()) return result;
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            if (getCharge(stack) >= 15) {
                Vec3d end = user.getRotationVector().multiply(32f).add(user.getEyePos());
                HitResult hit = raycast(user, user.getRotationVector(), 32f);
                if (hit.getType() != HitResult.Type.MISS) {
                    if (hit.getPos().distanceTo(user.getEyePos()) < 32f) end = hit.getPos();
                }
                Vec3d start = Utils.getArmPosition(user);
                BlastEntity blast = new BlastEntity(world, start, user, 0.1f, Vec3d.ZERO, end.subtract(start), 6, Color.CYAN, false, 6, (entity) -> {
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 80 + 20, 2));
                    entity.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.VULNERABILITY, 80 + 20, 2));
                    entity.setFrozenTicks(entity.getFrozenTicks() + 10);
                });
                blast.setPosition(user.getEyePos());
                world.spawnEntity(blast);
                world.playSoundFromEntity(null, user, SoulForgeSounds.DR_ICESHOCK_EVENT, SoundCategory.PLAYERS, 1f, 1f);
                user.getItemCooldownManager().set(this, 20);
                decreaseCharge(stack, 15);
                return TypedActionResult.consume(user.getStackInHand(hand));
            }
        }
        return TypedActionResult.fail(stack);
    }

    public AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private final GeoMagicItemRenderer<FreezeRay> renderer = new GeoMagicItemRenderer<>("freeze_ray", "freeze_ray");

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

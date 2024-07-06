package com.pulsar.soulforge.item.weapons;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.item.GeoMagicItemRenderer;
import com.pulsar.soulforge.components.SoulComponent;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ShotgunFist extends MagicItem implements GeoItem {
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity player) {
            if (player.getItemCooldownManager().isCoolingDown(this)) return false;
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            float force = playerSoul.getEffectiveLV() * 0.33f;
            Vec3d vec3d = target.getVelocity();
            Vec3d vec3d2 = (new Vec3d(MathHelper.sin(player.getYaw() * MathHelper.RADIANS_PER_DEGREE), 0.0, -MathHelper.cos(player.getYaw() * MathHelper.RADIANS_PER_DEGREE))).normalize().multiply(force);
            target.setVelocity(vec3d.x / 2.0 - vec3d2.x, target.isOnGround() ? Math.min(0.4, vec3d.y / 2.0 + force) : vec3d.y, vec3d.z / 2.0 - vec3d2.z);
            target.velocityModified = true;
            player.getItemCooldownManager().set(this, 200);
            return true;
        }
        return false;
    }

    public AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private final GeoItemRenderer<ShotgunFist> renderer = new GeoMagicItemRenderer<>("shotgun_fist", "justice");

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

package com.pulsar.soulforge.item.weapons;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.item.GeoMagicItemRenderer;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.entity.JusticeHarpoonProjectile;
import com.pulsar.soulforge.entity.PVHarpoonProjectile;
import com.pulsar.soulforge.entity.YoyoProjectile;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
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

public class TrickAnchor extends MagicSwordItem implements GeoItem {
    public TrickAnchor() {
        super(5, 1.2f, 0.2f);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        SoulComponent playerSoul = SoulForge.getPlayerSoul(user);
        for (YoyoProjectile yoyo : world.getEntitiesByClass(YoyoProjectile.class, Box.of(user.getPos(), 200, 200, 200), (entity) -> entity.getOwner() == user)) {
            if (yoyo.projectiles.isEmpty()) {
                Vec3d a = yoyo.getPos();
                Vec3d b = user.getPos();
                Vec3d c = user.getPos().add(user.getRotationVector());
                Vec3d ab = b.subtract(a);
                Vec3d ac = c.subtract(a);
                Vec3d cross = ab.crossProduct(ac);
                playerSoul.setValue("axisX", (float) cross.x);
                playerSoul.setValue("axisY", (float) cross.y);
                playerSoul.setValue("axisZ", (float) cross.z);
                playerSoul.setValue("centerX", (float) yoyo.getX());
                playerSoul.setValue("centerY", (float) yoyo.getY());
                playerSoul.setValue("centerZ", (float) yoyo.getZ());
                playerSoul.setValue("startX", (float) user.getX());
                playerSoul.setValue("startY", (float) user.getY());
                playerSoul.setValue("startZ", (float) user.getZ());
                playerSoul.setValue("yoyoSpin", 20);
            } else {
                Vec3d highestVelocity = Vec3d.ZERO;
                for (ProjectileEntity projectile : yoyo.projectiles) {
                    if (projectile.getVelocity().length() > highestVelocity.length()) {
                        highestVelocity = projectile.getVelocity();
                    }
                    if (projectile instanceof TridentEntity trident) {
                        trident.setPosition(yoyo.getPos());
                        trident.setVelocity(Vec3d.ZERO);
                        yoyo.getWorld().spawnEntity(trident);
                    }
                }
                yoyo.projectiles.clear();
                Vec3d launch = yoyo.getPos().subtract(user.getPos()).normalize().multiply(highestVelocity.length());
                user.setVelocity(launch);
                user.velocityModified = true;
                yoyo.kill();
            }
            return TypedActionResult.consume(stack);
        }
        YoyoProjectile projectile = new YoyoProjectile(world, user);
        projectile.setOwner(user);
        projectile.setPosition(user.getEyePos());
        world.spawnEntity(projectile);
        world.playSoundFromEntity(null, projectile, SoundEvents.ENTITY_FISHING_BOBBER_THROW, SoundCategory.PLAYERS, 1.0f, 1.0f);
        return TypedActionResult.consume(stack);
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return ImmutableMultimap.of();
    }

    public AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private final GeoItemRenderer<TrickAnchor> renderer = new GeoMagicItemRenderer<>("trick_anchor", "integrity");

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

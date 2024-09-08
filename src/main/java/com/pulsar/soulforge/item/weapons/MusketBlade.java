package com.pulsar.soulforge.item.weapons;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.item.GeoMagicItemRenderer;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.entity.BlastEntity;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
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
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MusketBlade extends MagicSwordItem implements GeoItem {
    public MusketBlade() {
        super(3f, 1.6f, 0.2f);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul((PlayerEntity)attacker);
        if (stack.getOrCreateNbt().contains("loaded") && stack.getOrCreateNbt().getBoolean("loaded")) {
            HitResult hit = attacker.getWorld().raycast(new RaycastContext(attacker.getEyePos(), attacker.getRotationVector().multiply(8f).add(attacker.getPos()), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, attacker));
            Vec3d end = attacker.getRotationVector().multiply(8f);
            Vec3d start = Utils.getArmPosition((PlayerEntity)attacker);
            if (hit != null) end = hit.getPos().subtract(start);
            BlastEntity blast = new BlastEntity(attacker.getWorld(), start, attacker, 0.5f, Vec3d.ZERO, end, 5 + playerSoul.getLV()*0.75f, Color.YELLOW);
            blast.setPosition(attacker.getEyePos());
            attacker.getWorld().spawnEntity(blast);
            attacker.getWorld().playSoundFromEntity(null, attacker, SoulForgeSounds.UT_BLASTER_EVENT, SoundCategory.PLAYERS, 1f, 1f);
            stack.getOrCreateNbt().putBoolean("loaded", false);
        }
        for (Entity entity : attacker.getEntityWorld().getOtherEntities(attacker, Box.of(attacker.getPos(), 4, 4, 4))) {
            if (entity instanceof LivingEntity living && living != target) {
                living.damage(attacker.getDamageSources().playerAttack((PlayerEntity) attacker), this.attackDamage);
                Utils.addAntiheal(0.2f, playerSoul.getLV() * 5, target);
            }
        }
        Utils.addAntiheal(0.2f, playerSoul.getLV() * 5, target);
        return super.postHit(stack, target, attacker);
    }

    public HitResult raycast(PlayerEntity player, Vec3d direction, float distance) {
        Vec3d vec3d = player.getCameraPosVec(1f);
        Vec3d vec3d3 = vec3d.add(direction.x * distance, direction.y * distance, direction.z * distance);
        return player.getWorld().raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(user);
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            if (stack.getOrCreateNbt().contains("loaded") && stack.getOrCreateNbt().getBoolean("loaded")) {
                HitResult hit = world.raycast(new RaycastContext(user.getEyePos(), user.getRotationVector().multiply(50f).add(user.getPos()), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, user));
                Vec3d end = user.getRotationVector().multiply(50f);
                if (hit != null) end = hit.getPos();
                Vec3d start = user.getEyePos();
                BlastEntity blast = new BlastEntity(world, start, user, 0.2f, Vec3d.ZERO, end.subtract(start), 5 + playerSoul.getLV() / 2f, Color.YELLOW);
                blast.setPosition(user.getEyePos());
                world.spawnEntity(blast);
                world.playSoundFromEntity(null, user, SoulForgeSounds.UT_BLASTER_EVENT, SoundCategory.PLAYERS, 1f, 1f);
                user.getItemCooldownManager().set(this, 10);
                stack.getOrCreateNbt().putBoolean("loaded", false);
            }
        }
        user.setCurrentHand(hand);
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity player) {
            int i = this.getMaxUseTime(player) - remainingUseTicks;
            float f = getPullProgress(i, player);
            SoulForge.LOGGER.info("use time: {}", f);
            if (f >= 1.0F && !(stack.getOrCreateNbt().contains("loaded") && stack.getOrCreateNbt().getBoolean("loaded"))) {
                stack.getOrCreateNbt().putBoolean("true", false);
                SoundCategory soundCategory = user instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
                world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BLOCK_BEACON_POWER_SELECT, soundCategory, 1.0F, 1.0F);
            }
        }
    }

    public boolean isUsedOnRelease(ItemStack stack) {
        return stack.isOf(this);
    }

    private static float getPullProgress(int useTicks, PlayerEntity player) {
        float f = (float)useTicks / (float)getPullTime(player);
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    public int getMaxUseTime(PlayerEntity player) {
        return getPullTime(player) + 3;
    }

    public static int getPullTime(PlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        return playerSoul.hasCast("Furioso") ? 10 : 20;
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.CROSSBOW;
    }

    public AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private final GeoItemRenderer<MusketBlade> renderer = new GeoMagicItemRenderer<>("musket_blade", "musket_blade");

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

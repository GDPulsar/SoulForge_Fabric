package com.pulsar.soulforge.item.weapons.weapon_wheel;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.item.GeoMagicItemRenderer;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.entity.BlastEntity;
import com.pulsar.soulforge.entity.DeterminationStaffStarProjectile;
import com.pulsar.soulforge.item.weapons.MagicItem;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
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
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DeterminationStaff extends MagicItem implements GeoItem {
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(user);
            if (user.isSneaking() && user.getItemUseTimeLeft() <= 0 && playerSoul.getStyleRank() != 0) {
                user.setCurrentHand(hand);
                return TypedActionResult.consume(user.getStackInHand(hand));
            } else {
                Vec3d forward = user.getRotationVector();
                float pitchAngle = (user.getPitch() + 90) * MathHelper.RADIANS_PER_DEGREE;
                float yawAngle = -user.getYaw() * MathHelper.RADIANS_PER_DEGREE;
                Vec3d up = new Vec3d(
                        MathHelper.sin(yawAngle) * MathHelper.cos(pitchAngle),
                        -MathHelper.sin(pitchAngle),
                        MathHelper.cos(yawAngle) * MathHelper.cos(pitchAngle)
                );
                Vec3d right = forward.crossProduct(up);
                for (int i = -3; i <= 3; i++) {
                    Vec3d direction = forward.add(right.multiply(i * 0.1f));
                    DeterminationStaffStarProjectile star = new DeterminationStaffStarProjectile(world, user);
                    star.setPosition(user.getEyePos());
                    star.setVelocity(direction.normalize().multiply(2f));
                    world.spawnEntity(star);
                }
                user.getItemCooldownManager().set(this, 30);
                world.playSound(null, user.getBlockPos(), SoundEvents.BLOCK_BEACON_POWER_SELECT, SoundCategory.MASTER, 1f, 2f);
            }
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (remainingUseTicks % 10 == 0) {
            world.playSound(null, user.getBlockPos(), SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.MASTER, 1f, 1f);
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof PlayerEntity player) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            if (user.getItemUseTimeLeft() <= 0 && playerSoul.getStyleRank() != 0) {
                float damage = playerSoul.getEffectiveLV() * 1.5f * (playerSoul.getStyleRank() * 0.2f);
                int duration = 20 * (int)playerSoul.getMagic();
                playerSoul.setMagic(0f);
                playerSoul.setStyleRank(0);
                playerSoul.setStyle(0);
                Vec3d end = player.getEyePos().add(player.getRotationVector().multiply(50f));
                HitResult hit = player.getWorld().raycast(new RaycastContext(player.getEyePos(), player.getEyePos().add(player.getRotationVector().multiply(50f)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player));
                if (hit != null) end = hit.getPos().subtract(Utils.getArmPosition(player));
                BlastEntity blast = new BlastEntity(world, Utils.getArmPosition(player), player, 5f, Vec3d.ZERO, end, damage, Color.RED, true, 100, 5);
                blast.owner = player;
                world.spawnEntity(blast);
                user.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.MANA_OVERLOAD, duration, 0));
                ((PlayerEntity) user).getItemCooldownManager().set(this, 100);
            }
        }
        return stack;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.CROSSBOW;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 100;
    }

    public AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private final GeoItemRenderer<DeterminationStaff> renderer = new GeoMagicItemRenderer<>("determination_staff", "determination");

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

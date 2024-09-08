package com.pulsar.soulforge.mixin;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(ProjectileEntity.class)
public abstract class ProjectileEntityMixin {
    @Shadow @Nullable public abstract Entity getOwner();

    @Shadow protected abstract boolean canHit(Entity entity);

    @Inject(method = "canHit", at=@At("HEAD"), cancellable = true)
    public void canHit(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!entity.getWorld().isClient) {
            ProjectileEntity projectile = (ProjectileEntity) (Object) this;
            if (entity instanceof LivingEntity living) {
                if (Utils.isParrying(living)) {
                    float speed = (float) projectile.getVelocity().distanceTo(Vec3d.ZERO);
                    projectile.velocityDirty = true;
                    projectile.setVelocity(living.getRotationVector().multiply(speed));
                    if (projectile instanceof ExplosiveProjectileEntity eProjectile) {
                        eProjectile.powerX = projectile.getVelocity().x * 0.1;
                        eProjectile.powerY = projectile.getVelocity().y * 0.1;
                        eProjectile.powerZ = projectile.getVelocity().z * 0.1;
                    }
                    projectile.setOwner(living);
                    living.playSound(SoulForgeSounds.PARRY_EVENT, 1f, 1f);
                    cir.setReturnValue(false);
                }
            }
        }
    }

    @Inject(method="onEntityHit", at=@At("HEAD"), cancellable = true)
    protected void onEntityHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        ProjectileEntity projectile = (ProjectileEntity)(Object)this;
        if (entityHitResult.getEntity() instanceof LivingEntity target) {
            if (target.isUsingItem() && (target.getActiveItem().isOf(SoulForgeItems.PERSEVERANCE_EDGE) || target.getActiveItem().isOf(SoulForgeItems.DETERMINATION_EDGE))) {
                projectile.kill();
                ci.cancel();
                return;
            }
            if (getOwner() != null) {
                if (projectile instanceof PersistentProjectileEntity persistentProjectile) {
                    if (getOwner() instanceof PlayerEntity player) {
                        float f = (float) persistentProjectile.getVelocity().length();
                        int i = MathHelper.ceil(MathHelper.clamp((double) f * persistentProjectile.getDamage(), 0.0, 2.147483647E9));
                        if (persistentProjectile.isCritical()) {
                            long l = new Random().nextInt(i / 2 + 2);
                            i = (int) Math.min(l + (long) i, Integer.MAX_VALUE);
                        }

                        float targetDefence;
                        if (target.getAttributes().hasAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE))
                            targetDefence = (float) target.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                        else targetDefence = 0f;

                        float targetDamage;
                        if (target.getAttributes().hasAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE))
                            targetDamage = (float) target.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                        else targetDamage = 0f;

                        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                        playerSoul.setEXP(playerSoul.getEXP() + (int) (i * (1 + (targetDefence / 10) + (targetDamage / 10))));

                        if (!persistentProjectile.getCommandTags().isEmpty()) {
                            if (persistentProjectile.getCommandTags().contains("Determination Siphon")) {
                                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 60, 2));
                            }
                            if (persistentProjectile.getCommandTags().contains("Patience Siphon")) {
                                target.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.MANA_SICKNESS, 60, 0));
                            }
                            if (persistentProjectile.getCommandTags().contains("Kindness Siphon")) {
                                player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 40, 1));
                            }
                        }
                    }
                }
            }
        }
    }
}

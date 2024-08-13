package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.components.EntityInitializer;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.components.TemporaryModifierComponent;
import com.pulsar.soulforge.util.TeamUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

public class SlowballProjectile extends ProjectileEntity {
    public SlowballProjectile(World world, LivingEntity thrower) {
        super(SoulForgeEntities.SLOWBALL_ENTITY_TYPE, world);
        this.setOwner(thrower);
        thrower.setPosition(thrower.getPos());
        thrower.setVelocity(thrower.getRotationVector().multiply(1.5f));
    }

    public SlowballProjectile(EntityType<SlowballProjectile> entityType, World world) {
        super(entityType, world);
    }

    public boolean canUsePortals() {
        return false;
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    protected boolean canHit(Entity entity) {
        if (entity instanceof LivingEntity target && this.getOwner() instanceof PlayerEntity player) {
            if (!TeamUtils.canDamageEntity(this.getServer(), player, target)) return false;
        }
        return super.canHit(entity) && !entity.noClip;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient) {
            HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
            if (hitResult.getType() != HitResult.Type.MISS) {
                this.onCollision(hitResult);
            }
        }

        this.updateRotation();
        this.checkBlockCollision();
        this.setVelocity(this.getVelocity().subtract(0, 0.04, 0));
        Vec3d vec3d = this.getVelocity();
        this.setPosition(this.getX() + vec3d.x, this.getY() + vec3d.y, this.getZ() + vec3d.z);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        DamageSource damageSource = this.getDamageSources().thrown(this, this.getOwner());
        entity.damage(damageSource, 1f);
        if (getOwner() instanceof PlayerEntity player && entity instanceof LivingEntity living) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            TemporaryModifierComponent modifiers = EntityInitializer.TEMPORARY_MODIFIERS.get(living);
            modifiers.addTemporaryModifier(SoulForgeAttributes.AIR_SPEED_BECAUSE_MOJANG_SUCKS, new EntityAttributeModifier(
                    UUID.fromString("41f1ed9f-7c16-41b3-b07b-95c944067a46"), "slowball",
                    -0.02f * playerSoul.getEffectiveLV(), EntityAttributeModifier.Operation.MULTIPLY_TOTAL
            ), playerSoul.getEffectiveLV() * 20);
            modifiers.addTemporaryModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, new EntityAttributeModifier(
                    UUID.fromString("41f1ed9f-7c16-41b3-b07b-95c944067a46"), "slowball",
                    -0.02f * playerSoul.getEffectiveLV(), EntityAttributeModifier.Operation.MULTIPLY_TOTAL
            ), playerSoul.getEffectiveLV() * 20);
            modifiers.addTemporaryModifier(SoulForgeAttributes.KNOCKBACK_MULTIPLIER, new EntityAttributeModifier(
                    UUID.fromString("41f1ed9f-7c16-41b3-b07b-95c944067a46"), "slowball",
                    0.04f * playerSoul.getEffectiveLV(), EntityAttributeModifier.Operation.MULTIPLY_TOTAL
            ), playerSoul.getEffectiveLV() * 20);
            modifiers.addTemporaryModifier(SoulForgeAttributes.SLIP_MODIFIER, new EntityAttributeModifier(
                    UUID.fromString("41f1ed9f-7c16-41b3-b07b-95c944067a46"), "slowball",
                    0.2f * playerSoul.getEffectiveLV(), EntityAttributeModifier.Operation.MULTIPLY_TOTAL
            ), playerSoul.getEffectiveLV() * 20);
            if (playerSoul.getLV() >= 10) {
                modifiers.addTemporaryModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(
                        UUID.fromString("41f1ed9f-7c16-41b3-b07b-95c944067a46"), "slowball",
                        0.04f * playerSoul.getEffectiveLV(), EntityAttributeModifier.Operation.MULTIPLY_TOTAL
                ), playerSoul.getEffectiveLV() * 20);
            }
        }
    }
}

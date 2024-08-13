package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.util.TeamUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class IceSpikeProjectile extends ProjectileEntity {
    private static final TrackedData<Float> DIRECTION = DataTracker.registerData(IceSpikeProjectile.class, TrackedDataHandlerRegistry.FLOAT);

    public IceSpikeProjectile(World world, LivingEntity owner) {
        this(SoulForgeEntities.ICE_SPIKE_ENTITY_TYPE, world);
        this.setOwner(owner);
        this.setYaw(owner.getYaw());
        this.dataTracker.set(DIRECTION, owner.getYaw());
    }

    public IceSpikeProjectile(EntityType<IceSpikeProjectile> entityType, World world) {
        super(entityType, world);
    }

    public boolean canUsePortals() {
        return false;
    }

    protected void initDataTracker() {
        this.dataTracker.startTracking(DIRECTION, 0f);
    }

    public void tick() {
        super.tick();

        this.setYaw(this.dataTracker.get(DIRECTION));
        if (this.age == 5) {
            for (LivingEntity living : this.getWorld().getEntitiesByClass(LivingEntity.class, getBoundingBox(), this::canHit)) {
                if (this.getOwner() instanceof PlayerEntity player) {
                    SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                    float damage = playerSoul.getEffectiveLV() * 0.5f;
                    DamageSource source = SoulForgeDamageTypes.of(getOwner(), getWorld(), SoulForgeDamageTypes.ABILITY_PROJECTILE_DAMAGE_TYPE);
                    if (living instanceof PlayerEntity target && target.isBlocking()) {
                        target.getItemCooldownManager().set(target.getActiveItem().getItem(), playerSoul.getEffectiveLV() * 30);
                        target.clearActiveItem();
                        target.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + this.getWorld().random.nextFloat() * 0.4F);
                    }
                    living.damage(source, damage);
                }
            }
        }
        if (this.age >= 60) {
            this.kill();
        }
    }

    @Override
    protected Box calculateBoundingBox() {
        float size = 1f;
        if (this.getOwner() instanceof PlayerEntity player) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            size = playerSoul.getEffectiveLV()/5f;
        }
        return super.calculateBoundingBox().offset(this.getRotationVector(0f, this.dataTracker.get(DIRECTION)).multiply(size/2f));
    }

    @Override
    protected boolean canHit(Entity entity) {
        if (entity instanceof LivingEntity target && this.getOwner() instanceof PlayerEntity player) {
            if (!TeamUtils.canDamageEntity(this.getServer(), player, target)) return false;
        }
        return super.canHit(entity) && !entity.noClip;
    }

    public boolean isOnFire() {
        return false;
    }

    public boolean shouldRender(double distance) {
        return distance < 16384.0D;
    }
}

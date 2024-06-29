package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AutoTurretEntity extends MobEntity implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public Vec3d direction = new Vec3d(1, 0, 0);

    public AutoTurretEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    public AutoTurretEntity(World world) {
        super(SoulForgeEntities.TURRET_ENTITY_TYPE, world);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "main", 0, (event) -> PlayState.STOP));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    public boolean canPickupItem(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false;
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putDouble("dirX", direction.x);
        nbt.putDouble("dirZ", direction.z);
        super.writeCustomDataToNbt(nbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        direction = new Vec3d(nbt.getDouble("dirX"), 0f, nbt.getDouble("dirZ"));
    }


    private int attackCooldown = 0;
    @Override
    public void baseTick() {
        if (!this.getWorld().isClient) {
            this.setPitch(0f);
            if (this.getTarget() != null) {
                if (this.getTarget().isDead() || !this.getAttackBox().contains(this.getTarget().getPos()) || !this.canTarget(this.getTarget())) {
                    this.setTarget(null);
                } else {
                    if (attackCooldown <= 0) {
                        LivingEntity target = this.getTarget();
                        Vec3d look = target.getPos().subtract(this.getPos());
                        this.setYaw(MathHelper.wrapDegrees((float) (MathHelper.atan2(look.x, look.z) * 57.2957763671875)));
                        if (!this.getAttackBox().contains(target.getPos().add(0f, target.getHeight() / 2f, 0f))) {
                            this.setTarget(null);
                        }
                        Vec3d direction = target.getPos().add(0f, target.getHeight() / 2f, 0f).subtract(this.getPos().add(0, 1f, 0)).normalize();
                        JusticePelletProjectile pellet = new JusticePelletProjectile(target.getWorld(), this);
                        pellet.setVelocity(direction.multiply(4f));
                        pellet.setPos(this.getPos().add(0, 1f, 0));
                        this.playSound(SoulForgeSounds.PELLET_SUMMON_EVENT, 1f, 1f);
                        this.getWorld().spawnEntity(pellet);
                        attackCooldown = 5;
                    }
                }
            } else {
                this.setYaw(MathHelper.wrapDegrees((float) (MathHelper.atan2(direction.x, direction.z) * 57.2957763671875)));
                resetTarget();
            }
            if (attackCooldown > 0) attackCooldown--;
        }
        this.setVelocity(0, this.getVelocity().y, 0);
        super.baseTick();
    }

    private void resetTarget() {
        for (Entity entity : this.getEntityWorld().getOtherEntities(this, this.getAttackBox())) {
            if (entity instanceof LivingEntity target) {
                if (this.canTarget(target)) {
                    this.setTarget(target);
                    break;
                }
            }
        }
    }

    @Override
    public boolean canTarget(LivingEntity entity) {
        float angle = MathHelper.wrapDegrees((float) (MathHelper.atan2(entity.getX() - this.getX(), entity.getZ() - this.getZ()) * 57.2957763671875));
        return entity.canTakeDamage() && this.canSee(entity) && Math.abs(angle - this.getYaw()) <= 32.5f;
    }

    protected Box getAttackBox() {
        return new Box(-30f, -2f, -30f, 30f, 2f, 30f).offset(this.getPos());
    }

    @Override
    public void takeKnockback(double strength, double x, double z) {}
}

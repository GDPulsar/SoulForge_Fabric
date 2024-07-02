package com.pulsar.soulforge.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SphereDomeEntity extends Entity implements Attackable {
    public float maxHealth;
    public float health;
    public float size;

    public SphereDomeEntity(World world, Vec3d position, float size, float health) {
        super(SoulForgeEntities.DOME_ENTITY_TYPE, world);
        this.setPosition(position);
        maxHealth = health;
        this.health = health;
        this.size = size;
        this.ignoreCameraFrustum = true;
        Vec3d negCorner = this.getPos().add(-size, -size, -size);
        Vec3d posCorner = this.getPos().add(size, size, size);
        setBoundingBox(new Box(negCorner.x, negCorner.y, negCorner.z, posCorner.x, posCorner.y, posCorner.z));
    }

    public boolean canUsePortals() {
        return false;
    }

    public SphereDomeEntity(EntityType<? extends Entity> type, World world) {
        super(type, world);
        maxHealth = 100f;
        health = 100f;
        size = 10f;
        this.ignoreCameraFrustum = true;
        Vec3d negCorner = this.getPos().add(-size, -size, -size);
        Vec3d posCorner = this.getPos().add(size, size, size);
        setBoundingBox(new Box(negCorner.x, negCorner.y, negCorner.z, posCorner.x, posCorner.y, posCorner.z));
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        size = nbt.getFloat("size");
        maxHealth = nbt.getFloat("maxHealth");
        health = nbt.getFloat("health");
        this.ignoreCameraFrustum = true;
        Vec3d negCorner = this.getPos().add(-size, -size, -size);
        Vec3d posCorner = this.getPos().add(size, size, size);
        setBoundingBox(new Box(negCorner.x, negCorner.y, negCorner.z, posCorner.x, posCorner.y, posCorner.z));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putFloat("size", size);
        nbt.putFloat("maxHealth", maxHealth);
        nbt.putFloat("health", health);
    }

    @Nullable
    @Override
    public LivingEntity getLastAttacker() {
        return null;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        health -= amount;
        if (health <= 0) {
            kill();
        }
        return true;
    }

    @Override
    protected Box calculateBoundingBox() {
        return new Box(getX() - size/2f, getY() - size/2f, getZ() - size/2f, getX() + size/2f, getY() + size/2f, getZ() + size/2f);
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        Vec3d direction = (player.getPos().add(0f, player.getHeight()/2f, 0f).subtract(getPos())).normalize();
        Vec3d surfacePos = direction.multiply(size/2f).add(getPos());
        if (player.getBoundingBox().contains(surfacePos)) {
            player.velocityDirty = true;
            player.setVelocity(0, 0, 0);
            player.setPos(player.prevX, player.prevY, player.prevZ);
        }
    }

    @Override
    public boolean shouldRender(double distance) {
        return distance < 256D * 256D;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return EntityDimensions.fixed(size, size/2f);
    }
}

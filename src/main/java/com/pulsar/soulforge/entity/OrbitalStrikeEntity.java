package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.util.TeamUtils;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class OrbitalStrikeEntity extends Entity {
    public PlayerEntity owner;

    private static final TrackedData<Vector3f> POSITION = DataTracker.registerData(OrbitalStrikeEntity.class, TrackedDataHandlerRegistry.VECTOR3F);

    public OrbitalStrikeEntity(World world, Vec3d position, PlayerEntity owner) {
        super(SoulForgeEntities.ORBITAL_STRIKE_ENTITY_TYPE, world);
        this.owner = owner;
        this.setPosition(position);
        setPosition(position);
    }

    public OrbitalStrikeEntity(EntityType<? extends Entity> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(POSITION, new Vector3f(0, 0, 0));
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        timer = nbt.getInt("timer");
    }

    public Vec3d getPosition() { return new Vec3d(this.dataTracker.get(POSITION).x, this.dataTracker.get(POSITION).y, this.dataTracker.get(POSITION).z); }
    public void setPos(Vec3d position) { this.dataTracker.set(POSITION, position.toVector3f()); }

    private int timer = 0;
    @Override
    public void tick() {
        this.setPosition(getPosition());
        if (this.timer % 5 == 0) {
            for (Entity entity : getWorld().getOtherEntities(this, getBoundingBox())) {
                if (entity instanceof LivingEntity && entity != this.owner) {
                    if (entity instanceof PlayerEntity targetPlayer && this.owner != null) {
                        if (!TeamUtils.canDamagePlayer(this.getServer(), this.owner, targetPlayer)) return;
                    }
                    float horizDist = (float) new Vec3d(getX(), 0f, getZ()).distanceTo(new Vec3d(entity.getX(), 0f, entity.getZ()));
                    if (horizDist <= 2f) {
                        if (this.owner != null) {
                            entity.damage(SoulForgeDamageTypes.of(owner.getWorld(), SoulForgeDamageTypes.ABILITY_DAMAGE_TYPE), 30);
                        } else {
                            entity.damage(SoulForgeDamageTypes.of(getWorld(), SoulForgeDamageTypes.ABILITY_DAMAGE_TYPE), 30);
                        }
                    }
                }
            }
        }
        if (this.timer >= 300) this.kill();
        timer++;
        super.tick();
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("timer", timer);
    }

    @Override
    public boolean shouldRender(double distance) {
        return true;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return EntityDimensions.fixed(4f, 20f);
    }
}

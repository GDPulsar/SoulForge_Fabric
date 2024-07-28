package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class BlastEntity extends Entity {
    public LivingEntity owner;
    private static final TrackedData<Vector3f> POSITION = DataTracker.registerData(BlastEntity.class, TrackedDataHandlerRegistry.VECTOR3F);
    private static final TrackedData<Vector3f> START = DataTracker.registerData(BlastEntity.class, TrackedDataHandlerRegistry.VECTOR3F);
    private static final TrackedData<Vector3f> END = DataTracker.registerData(BlastEntity.class, TrackedDataHandlerRegistry.VECTOR3F);
    private static final TrackedData<Integer> COLOR = DataTracker.registerData(BlastEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> RADIUS = DataTracker.registerData(BlastEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> DAMAGE = DataTracker.registerData(BlastEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Boolean> IGNORES_IFRAMES = DataTracker.registerData(BlastEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> DURATION = DataTracker.registerData(BlastEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private Consumer<LivingEntity> onDamageEvent = null;

    public BlastEntity(EntityType<? extends Entity> type, World world) {
        super(type, world);
        this.ignoreCameraFrustum = true;
    }

    public boolean canUsePortals() {
        return false;
    }

    public BlastEntity(World world, Vec3d pos, LivingEntity owner, float radius, Vec3d start, Vec3d end, float damage, Color color) {
        super(SoulForgeEntities.HORIZONTAL_BLAST_ENTITY_TYPE, world);
        this.setPosition(pos);
        this.owner = owner;
        this.dataTracker.set(POSITION, pos.toVector3f());
        this.dataTracker.set(START, start.toVector3f());
        this.dataTracker.set(END, end.toVector3f());
        this.dataTracker.set(COLOR, color.getRGB());
        this.dataTracker.set(RADIUS, radius);
        this.dataTracker.set(DAMAGE, damage);
        this.dataTracker.set(IGNORES_IFRAMES, false);
        this.dataTracker.set(DURATION, 20);
        this.ignoreCameraFrustum = true;
    }

    public BlastEntity(World world, Vec3d pos, LivingEntity owner, float radius, Vec3d start, Vec3d end, float damage, Color color, boolean ignoresIframes) {
        super(SoulForgeEntities.HORIZONTAL_BLAST_ENTITY_TYPE, world);
        this.setPosition(pos);
        this.owner = owner;
        this.dataTracker.set(POSITION, pos.toVector3f());
        this.dataTracker.set(START, start.toVector3f());
        this.dataTracker.set(END, end.toVector3f());
        this.dataTracker.set(COLOR, color.getRGB());
        this.dataTracker.set(RADIUS, radius);
        this.dataTracker.set(DAMAGE, damage);
        this.dataTracker.set(IGNORES_IFRAMES, ignoresIframes);
        this.ignoreCameraFrustum = true;
    }

    public BlastEntity(World world, Vec3d pos, LivingEntity owner, float radius, Vec3d start, Vec3d end, float damage, Color color, boolean ignoresIframes, int duration) {
        super(SoulForgeEntities.HORIZONTAL_BLAST_ENTITY_TYPE, world);
        this.setPosition(pos);
        this.owner = owner;
        this.dataTracker.set(POSITION, pos.toVector3f());
        this.dataTracker.set(START, start.toVector3f());
        this.dataTracker.set(END, end.toVector3f());
        this.dataTracker.set(COLOR, color.getRGB());
        this.dataTracker.set(RADIUS, radius);
        this.dataTracker.set(DAMAGE, damage);
        this.dataTracker.set(IGNORES_IFRAMES, ignoresIframes);
        this.dataTracker.set(DURATION, duration);
        this.ignoreCameraFrustum = true;
    }

    public BlastEntity(World world, Vec3d pos, LivingEntity owner, float radius, Vec3d start, Vec3d end, float damage, Color color, boolean ignoresIframes, int duration, Consumer<LivingEntity> onDamageEvent) {
        super(SoulForgeEntities.HORIZONTAL_BLAST_ENTITY_TYPE, world);
        this.setPosition(pos);
        this.owner = owner;
        this.dataTracker.set(POSITION, pos.toVector3f());
        this.dataTracker.set(START, start.toVector3f());
        this.dataTracker.set(END, end.toVector3f());
        this.dataTracker.set(COLOR, color.getRGB());
        this.dataTracker.set(RADIUS, radius);
        this.dataTracker.set(DAMAGE, damage);
        this.dataTracker.set(IGNORES_IFRAMES, ignoresIframes);
        this.dataTracker.set(DURATION, duration);
        this.ignoreCameraFrustum = true;
        this.onDamageEvent = onDamageEvent;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(POSITION, new Vector3f(0, 0, 0));
        this.dataTracker.startTracking(START, new Vector3f(0, 0, 0));
        this.dataTracker.startTracking(END, new Vector3f(0, 5, 0));
        this.dataTracker.startTracking(COLOR, Color.BLACK.getRGB());
        this.dataTracker.startTracking(RADIUS, 1f);
        this.dataTracker.startTracking(DAMAGE, 5f);
        this.dataTracker.startTracking(IGNORES_IFRAMES, false);
        this.dataTracker.startTracking(DURATION, 20);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {}

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {}

    public void setPos(Vec3d position) { this.dataTracker.set(POSITION, position.toVector3f()); }
    public Vec3d getStart() { return new Vec3d(this.dataTracker.get(START).x, this.dataTracker.get(START).y, this.dataTracker.get(START).z); }
    public Vec3d getEnd() { return new Vec3d(this.dataTracker.get(END).x, this.dataTracker.get(END).y, this.dataTracker.get(END).z); }
    public void setEnd(Vec3d value) { this.dataTracker.set(END, value.toVector3f()); }
    public Color getColor() { return new Color(this.dataTracker.get(COLOR)); }
    public float getRadius() { return this.dataTracker.get(RADIUS); }
    public float getDamage() { return this.dataTracker.get(DAMAGE); }
    public boolean getIgnoresIframes() { return this.dataTracker.get(IGNORES_IFRAMES); }
    public int getDuration() { return this.dataTracker.get(DURATION); }

    public int timer = 0;
    @Override
    public void tick() {
        this.setPosition(Utils.vector3fToVec3d(this.dataTracker.get(POSITION)));
        if (this.timer % 5 == 0) {
            List<UUID> affected = new ArrayList<>();
            for (int i = 0; i < 40; i++) {
                Vec3d pos = getStart().lerp(getEnd(), i/40f).add(getPos());
                Box box = new Box(pos.subtract(getRadius(), getRadius(), getRadius()), pos.add(getRadius(), getRadius(), getRadius()));
                for (Entity entity : getWorld().getOtherEntities(this, box)) {
                    if (entity instanceof LivingEntity living && !affected.contains(living.getUuid()) && !(living == owner)) {
                        if (living.isUsingItem() && (living.getActiveItem().isOf(SoulForgeItems.PERSEVERANCE_EDGE) || living.getActiveItem().isOf(SoulForgeItems.DETERMINATION_EDGE))) {
                            continue;
                        }
                        if (entity instanceof PlayerEntity targetPlayer && this.owner instanceof PlayerEntity player) {
                            if (!TeamUtils.canDamagePlayer(this.getServer(), player, targetPlayer)) continue;
                        }
                        DamageSource source;
                        assert owner instanceof PlayerEntity;
                        source = SoulForgeDamageTypes.of((PlayerEntity)owner, this.getWorld(), SoulForgeDamageTypes.ABILITY_PROJECTILE_DAMAGE_TYPE);
                        if (getIgnoresIframes()) living.timeUntilRegen = 0;
                        if (living.damage(source, getDamage()) && owner != null) {
                            SoulComponent playerSoul = SoulForge.getPlayerSoul((PlayerEntity)owner);
                            playerSoul.setStyle(playerSoul.getStyle() + (int)getDamage());
                        }
                        if (this.onDamageEvent != null) {
                            this.onDamageEvent.accept(living);
                        }
                        affected.add(living.getUuid());
                    }
                }
            }
        }
        if (this.timer >= this.dataTracker.get(DURATION)) this.kill();
        timer++;
        super.tick();
    }

    @Override
    public boolean shouldRender(double distance) {
        return true;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return EntityDimensions.fixed(1f, 1f);
    }
}

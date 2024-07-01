package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.util.TeamUtils;
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
import java.util.Objects;

public class GunlanceBlastEntity extends Entity {
    public LivingEntity owner;
    private static final TrackedData<Vector3f> START = DataTracker.registerData(GunlanceBlastEntity.class, TrackedDataHandlerRegistry.VECTOR3F);
    private static final TrackedData<Vector3f> END = DataTracker.registerData(GunlanceBlastEntity.class, TrackedDataHandlerRegistry.VECTOR3F);
    private static final TrackedData<Integer> COLOR = DataTracker.registerData(GunlanceBlastEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> RADIUS = DataTracker.registerData(GunlanceBlastEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> DAMAGE = DataTracker.registerData(GunlanceBlastEntity.class, TrackedDataHandlerRegistry.FLOAT);

    public GunlanceBlastEntity(EntityType<? extends Entity> type, World world) {
        super(type, world);
        this.ignoreCameraFrustum = true;
    }

    public GunlanceBlastEntity(World world, Vec3d pos, LivingEntity owner, Vec3d start, Vec3d end, float damage) {
        super(SoulForgeEntities.GUNLANCE_BLAST_ENTITY_TYPE, world);
        this.setPosition(pos);
        this.owner = owner;
        this.dataTracker.set(START, start.toVector3f());
        this.dataTracker.set(END, end.toVector3f());
        this.dataTracker.set(COLOR, Color.MAGENTA.darker().getRGB());
        this.dataTracker.set(RADIUS, 0.25f);
        this.dataTracker.set(DAMAGE, damage);
        this.ignoreCameraFrustum = true;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(START, new Vector3f(0, 0, 0));
        builder.add(END, new Vector3f(0, 5, 0));
        builder.add(COLOR, Color.BLACK.getRGB());
        builder.add(RADIUS, 0.25f);
        builder.add(DAMAGE, 7.5f);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {}

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {}

    public Vec3d getStart() { return new Vec3d(this.dataTracker.get(START).x, this.dataTracker.get(START).y, this.dataTracker.get(START).z); }
    public Vec3d getEnd() { return new Vec3d(this.dataTracker.get(END).x, this.dataTracker.get(END).y, this.dataTracker.get(END).z); }
    public void setEnd(Vec3d end) { this.dataTracker.set(END, end.toVector3f()); }
    public Color getColor() { return new Color(this.dataTracker.get(COLOR)); }
    public float getRadius() { return this.dataTracker.get(RADIUS); }
    public float getDamage() { return this.dataTracker.get(DAMAGE); }

    public int timer = 0;
    @Override
    public void tick() {
        if (timer >= 20) {
            if (this.timer % 5 == 0) {
                List<LivingEntity> affected = new ArrayList<>();
                for (int i = 0; i < 40; i++) {
                    Vec3d pos = getStart().lerp(getEnd(), i / 40f).add(getPos());
                    Box box = new Box(pos.subtract(getRadius(), getRadius(), getRadius()), pos.add(getRadius(), getRadius(), getRadius()));
                    for (Entity entity : getWorld().getOtherEntities(this, box)) {
                        if (entity instanceof LivingEntity living && !(affected.contains(living)) && !(living == owner)) {
                            if (entity instanceof PlayerEntity targetPlayer && this.owner instanceof PlayerEntity player) {
                                if (!TeamUtils.canDamagePlayer(this.getServer(), player, targetPlayer)) return;
                            }
                            DamageSource source;
                            source = SoulForgeDamageTypes.of(Objects.requireNonNullElse(owner, entity).getWorld(), SoulForgeDamageTypes.ABILITY_DAMAGE_TYPE);
                            living.damage(source, getDamage());
                            living.timeUntilRegen = 0;
                            affected.add(living);
                        }
                    }
                }
            }
        }
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

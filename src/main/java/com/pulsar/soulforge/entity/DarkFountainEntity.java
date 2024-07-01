package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.util.TeamUtils;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DarkFountainEntity extends Entity {
    public PlayerEntity owner;

    public DarkFountainEntity(World world, Vec3d position, PlayerEntity owner) {
        super(SoulForgeEntities.DARK_FOUNTAIN_ENTITY_TYPE, world);
        this.setPosition(position);
        this.owner = owner;
        this.ignoreCameraFrustum = true;
    }

    public DarkFountainEntity(EntityType<? extends Entity> type, World world) {
        super(type, world);
        this.ignoreCameraFrustum = true;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    private int timer = 0;
    @Override
    public void tick() {
        if (this.timer % 3 == 0) {
            for (Entity entity : getWorld().getOtherEntities(this, getBoundingBox())) {
                if (entity instanceof LivingEntity && entity != owner) {
                    if (entity instanceof PlayerEntity targetPlayer && this.owner != null) {
                        if (!TeamUtils.canDamagePlayer(this.getServer(), this.owner, targetPlayer)) return;
                    }
                    float horizDist = (float) new Vec3d(getX(), 0f, getZ()).distanceTo(new Vec3d(entity.getX(), 0f, entity.getZ()));
                    if (horizDist <= 4f) {
                        DamageSource source;
                        if (owner != null) source = SoulForgeDamageTypes.of(owner.getWorld(), SoulForgeDamageTypes.ABILITY_DAMAGE_TYPE);
                        else source = SoulForgeDamageTypes.of(entity.getWorld(), SoulForgeDamageTypes.ABILITY_DAMAGE_TYPE);
                        entity.damage(source, 3);
                        entity.timeUntilRegen = 10;
                    }
                }
            }
        }
        if (this.timer >= 40) this.kill();
        timer++;
        super.tick();
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    @Override
    public boolean shouldRender(double distance) {
        return true;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return EntityDimensions.fixed(8f, 50f);
    }
    /*

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        SoulForge.LOGGER.info("Collided with player.");
    }*/
}

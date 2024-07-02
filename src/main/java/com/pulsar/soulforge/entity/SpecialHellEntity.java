package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.util.TeamUtils;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SpecialHellEntity extends Entity {
    public PlayerEntity owner;

    public SpecialHellEntity(World world, Vec3d position, PlayerEntity owner) {
        super(SoulForgeEntities.SPECIAL_HELL_ENTITY_TYPE, world);
        this.setPosition(position);
        this.owner = owner;
        this.ignoreCameraFrustum = true;
    }

    public SpecialHellEntity(EntityType<? extends Entity> type, World world) {
        super(type, world);
        this.ignoreCameraFrustum = true;
    }

    public boolean canUsePortals() {
        return false;
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    public int timer = 0;
    @Override
    public void tick() {
        if (this.timer % 3 == 0 && timer >= 60) {
            for (Entity entity : getWorld().getOtherEntities(this, getBoundingBox())) {
                if (entity instanceof LivingEntity && entity != owner) {
                    if (entity instanceof PlayerEntity targetPlayer && this.owner != null) {
                        if (!TeamUtils.canDamagePlayer(this.getServer(), this.owner, targetPlayer)) return;
                    }
                    float horizDist = (float)this.getPos().withAxis(Direction.Axis.Y, 0).distanceTo(entity.getPos().withAxis(Direction.Axis.Y, 0));
                    if (horizDist <= 10f) {
                        DamageSource source;
                        float damage = 20f;
                        if (owner != null) {
                            SoulComponent playerSoul = SoulForge.getPlayerSoul(owner);
                            damage = playerSoul.getEffectiveLV();
                            source = SoulForgeDamageTypes.of(owner.getWorld(), SoulForgeDamageTypes.ABILITY_DAMAGE_TYPE);
                        } else
                            source = SoulForgeDamageTypes.of(entity.getWorld(), SoulForgeDamageTypes.ABILITY_DAMAGE_TYPE);
                        entity.damage(source, damage);
                        entity.timeUntilRegen = 10;
                    }
                }
            }
        }
        if (this.timer >= 140) this.kill();
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

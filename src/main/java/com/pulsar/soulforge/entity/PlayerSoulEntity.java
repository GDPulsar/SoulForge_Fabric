package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;

import java.util.Objects;

public class PlayerSoulEntity extends Entity {
    private static final TrackedData<String> OWNER = DataTracker.registerData(PlayerSoulEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<String> TRAIT1 = DataTracker.registerData(PlayerSoulEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<String> TRAIT2 = DataTracker.registerData(PlayerSoulEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Boolean> STRONG = DataTracker.registerData(PlayerSoulEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> PURE = DataTracker.registerData(PlayerSoulEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> LV = DataTracker.registerData(PlayerSoulEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> EXP = DataTracker.registerData(PlayerSoulEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public PlayerSoulEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public String getOwner() { return this.dataTracker.get(OWNER); }
    public void setOwner(String value) { this.dataTracker.set(OWNER, value); }
    public String getTrait1() { return this.dataTracker.get(TRAIT1); }
    public void setTrait1(String value) { this.dataTracker.set(TRAIT1, value); }
    public String getTrait2() { return this.dataTracker.get(TRAIT2); }
    public void setTrait2(String value) { this.dataTracker.set(TRAIT2, value); }
    public boolean getStrong() { return this.dataTracker.get(STRONG); }
    public void setStrong(boolean value) { this.dataTracker.set(STRONG, value); }
    public boolean getPure() { return this.dataTracker.get(PURE); }
    public void setPure(boolean value) { this.dataTracker.set(PURE, value); }
    public int getLV() { return this.dataTracker.get(LV); }
    public void setLV(int value) { this.dataTracker.set(LV, value); }
    public int getEXP() { return this.dataTracker.get(EXP); }
    public void setEXP(int value) { this.dataTracker.set(EXP, value); }

    public PlayerSoulEntity(PlayerEntity player) {
        super(SoulForgeEntities.PLAYER_SOUL_ENTITY_TYPE, player.getWorld());
        this.setOwner(player.getName().getString());
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        this.setTrait1(playerSoul.getTrait(0).getName());
        if (playerSoul.getTraitCount() == 2) this.setTrait2(playerSoul.getTrait(1).getName());
        else this.setTrait2("");
        this.setStrong(playerSoul.isStrong());
        this.setPure(playerSoul.isPure());
        this.setLV(playerSoul.getLV());
        this.setEXP(playerSoul.getEXP());
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(OWNER, "BettyDrakos");
        this.dataTracker.startTracking(TRAIT1, "Bravery");
        this.dataTracker.startTracking(TRAIT2, "Justice");
        this.dataTracker.startTracking(STRONG, true);
        this.dataTracker.startTracking(PURE, false);
        this.dataTracker.startTracking(LV, 20);
        this.dataTracker.startTracking(EXP, 69420);
    }

    @Override
    public void tick() {
        this.setYaw(this.getYaw() + 0.2f);
        super.tick();
        int lifetime = 200;
        if (Objects.equals(getTrait1(), "Determination")) lifetime += 300;
        if (!Objects.equals(getTrait2(), "")) lifetime += 100;
        if (getStrong()) lifetime += 100;
        if (getPure()) lifetime += 200;
        if (this.age >= lifetime) {
            this.kill();
        }
    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("owner")) setOwner(nbt.getString("owner"));
        if (nbt.contains("trait1")) setTrait1(nbt.getString("trait1"));
        if (nbt.contains("trait2")) setTrait2(nbt.getString("trait2"));
        if (nbt.contains("strong")) setStrong(nbt.getBoolean("strong"));
        if (nbt.contains("pure")) setPure(nbt.getBoolean("pure"));
        if (nbt.contains("lv")) setLV(nbt.getInt("lv"));
        if (nbt.contains("exp")) setEXP(nbt.getInt("exp"));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putString("owner", getOwner());
        nbt.putString("trait1", getTrait1());
        nbt.putString("trait2", getTrait2());
        nbt.putBoolean("strong", getStrong());
        nbt.putBoolean("pure", getPure());
        nbt.putInt("lv", getLV());
        nbt.putInt("exp", getEXP());
    }

    @Override
    public void kill() {
        if (this.getWorld().isClient) {
            this.getWorld().playSound(this.getBlockX(), this.getBlockY(), this.getBlockZ(), SoulForgeSounds.UT_SOUL_CRACK_EVENT, SoundCategory.MASTER, 1f, 1f, false);
        }
        for (int i = 0; i < 10; i++) {
            this.getWorld().addParticle(ParticleTypes.CRIT, this.getX() + Math.random() * 0.2f - 0.1f,
                    this.getY() + Math.random() * 0.2f - 0.1f, this.getZ() + Math.random() * 0.2f - 0.1f, 0.0, 0.0, 0.0);
        }
        super.kill();
    }

    public boolean damage(DamageSource source, float amount) {
        this.kill();
        return true;
    }
}

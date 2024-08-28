package com.pulsar.soulforge.components;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;

public class HateComponent implements AutoSyncedComponent, CommonTickingComponent {
    final LivingEntity entity;

    float hatePercent = 0f;
    boolean hasHate = false;

    public HateComponent(LivingEntity living) {
        this.entity = living;
    }

    public float getHatePercent() { return hatePercent; }
    public void setHatePercent(float percent) {
        this.hatePercent = MathHelper.clamp(percent, 0f, 100f);
        sync();
    }
    public void addHatePercent(float percent) {
        setHatePercent(getHatePercent() + percent);
    }

    public boolean hasHate() { return hasHate; }
    public void setHasHate(boolean hasHate) {
        this.hasHate = hasHate;
        sync();
    }

    private void sync() {
        EntityInitializer.HATE.sync(entity);
    }

    @Override
    public void tick() {

    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        hatePercent = tag.getFloat("hatePercent");
        hasHate = tag.getBoolean("hasHate");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putFloat("hatePercent", hatePercent);
        tag.putBoolean("hasHate", hasHate);
    }
}

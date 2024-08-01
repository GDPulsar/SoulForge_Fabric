package com.pulsar.soulforge.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.TimeHelper;

public class TickManager {
    protected float tickRate = 20.0F;
    protected long nanosPerTick;
    protected boolean shouldTick;
    protected boolean frozen;

    public TickManager() {
        this.nanosPerTick = TimeHelper.SECOND_IN_NANOS / 20L;
        this.shouldTick = true;
        this.frozen = false;
    }

    public void setTickRate(float tickRate) {
        this.tickRate = Math.max(tickRate, 1.0F);
        this.nanosPerTick = (long)((double)TimeHelper.SECOND_IN_NANOS / (double)this.tickRate);
    }

    public float getTickRate() {
        return this.tickRate;
    }

    public float getMillisPerTick() {
        return (float)this.nanosPerTick / (float)TimeHelper.MILLI_IN_NANOS;
    }

    public long getNanosPerTick() {
        return this.nanosPerTick;
    }

    public boolean shouldTick() {
        return this.shouldTick;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    public boolean isFrozen() {
        return this.frozen;
    }

    public boolean shouldSkipTick(Entity entity) {
        return !this.shouldTick() && !(entity instanceof PlayerEntity);
    }
}

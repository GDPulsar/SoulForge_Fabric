package com.pulsar.soulforge.components;

import net.minecraft.nbt.NbtCompound;

public class WorldComponent implements WorldConfigComponent {
    public float expMultiplier = 1f;

    @Override
    public void readFromNbt(NbtCompound tag) {
        expMultiplier = tag.getFloat("expMultiplier");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putFloat("expMultiplier", expMultiplier);
    }

    @Override
    public float getExpMultiplier() {
        return expMultiplier;
    }

    @Override
    public void setExpMultiplier(float multiplier) {
        expMultiplier = multiplier;
    }
}

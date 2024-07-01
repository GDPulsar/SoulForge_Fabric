package com.pulsar.soulforge.components;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class WorldComponent implements WorldBaseComponent {
    public float expMultiplier = 1f;
    public List<BlockPos> activeCreativeZones = new ArrayList<>();

    @Override
    public void readFromNbt(NbtCompound tag) {
        expMultiplier = tag.getFloat("expMultiplier");
        activeCreativeZones = new ArrayList<>();
        NbtList list = tag.getList("creativeZones", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < list.size(); i++) {
            NbtCompound compound = list.getCompound(i);
            BlockPos pos = new BlockPos(compound.getInt("x"), compound.getInt("y"), compound.getInt("z"));
            activeCreativeZones.add(pos);
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putFloat("expMultiplier", expMultiplier);
        NbtList list = new NbtList();
        for (BlockPos pos : activeCreativeZones) {
            NbtCompound compound = new NbtCompound();
            compound.putInt("x", pos.getX());
            compound.putInt("y", pos.getY());
            compound.putInt("z", pos.getZ());
            list.add(compound);
        }
        tag.put("creativeZones", list);
    }

    @Override
    public float getExpMultiplier() {
        return expMultiplier;
    }

    @Override
    public void setExpMultiplier(float multiplier) {
        expMultiplier = multiplier;
    }

    @Override
    public List<BlockPos> getActiveCreativeZones() {
        return activeCreativeZones;
    }

    @Override
    public boolean isInRangeOfActiveCreativeZone(BlockPos pos) {
        return activeCreativeZones.stream().anyMatch(blockPos -> blockPos.isWithinDistance(pos, 80));
    }

    @Override
    public void addActiveCreativeZone(BlockPos pos) {
        if (!activeCreativeZones.contains(pos)) activeCreativeZones.add(pos);
    }

    @Override
    public void removeActiveCreativeZone(BlockPos pos) {
        if (activeCreativeZones.contains(pos)) activeCreativeZones.remove(pos);
    }
}

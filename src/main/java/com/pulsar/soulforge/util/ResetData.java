package com.pulsar.soulforge.util;

import com.pulsar.soulforge.trait.TraitBase;
import com.pulsar.soulforge.trait.Traits;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class ResetData {
    public int totalResets;
    public int resetsSincePure;
    public int resetsSinceStrong;
    public int resetsSinceDual;
    public int resetsSinceDT;
    public boolean bravery;
    public boolean justice;
    public boolean kindness;
    public boolean patience;
    public boolean integrity;
    public boolean perseverance;
    public boolean determination;
    public boolean strongDual;
    public List<Pair<TraitBase, TraitBase>> unlockedDuals;
    public List<TraitBase> unlockedPures;

    public ResetData(int totalResets, int resetsSincePure, int resetsSinceStrong, int resetsSinceDual, int resetsSinceDT, List<Pair<TraitBase, TraitBase>> unlockedDuals, List<TraitBase> unlockedPures) {
        this.totalResets = totalResets;
        this.resetsSincePure = resetsSincePure;
        this.resetsSinceStrong = resetsSinceStrong;
        this.resetsSinceDual = resetsSinceDual;
        this.resetsSinceDT = resetsSinceDT;
        this.unlockedDuals = unlockedDuals;
        this.unlockedPures = unlockedPures;
    }

    public ResetData() {
        this.totalResets = 0;
        this.resetsSincePure = 0;
        this.resetsSinceStrong = 0;
        this.resetsSinceDual = 0;
        this.resetsSinceDT = 0;
        this.unlockedDuals = new ArrayList<>();
        this.unlockedPures = new ArrayList<>();
    }

    public ResetData(NbtCompound nbt) {
        this.totalResets = nbt.getInt("total");
        this.resetsSincePure = nbt.getInt("pure");
        this.resetsSinceStrong = nbt.getInt("strong");
        this.resetsSinceDual = nbt.getInt("dual");
        this.resetsSinceDT = nbt.getInt("determination");
        NbtCompound achieved = nbt.getCompound("achieved");
        bravery = achieved.getBoolean("bravery");
        justice = achieved.getBoolean("justice");
        kindness = achieved.getBoolean("kindness");
        patience = achieved.getBoolean("patience");
        integrity = achieved.getBoolean("integrity");
        perseverance = achieved.getBoolean("perseverance");
        determination = achieved.getBoolean("determination");
        strongDual = achieved.getBoolean("strongDual");
        this.unlockedDuals = new ArrayList<>();
        NbtList duals = nbt.getList("duals", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < duals.size(); i++) {
            NbtCompound dual = duals.getCompound(i);
            TraitBase trait1 = Traits.get(dual.getString("trait1"));
            TraitBase trait2 = Traits.get(dual.getString("trait2"));
            if (trait1 != null && trait2 != null) {
                this.unlockedDuals.add(new Pair<>(trait1, trait2));
            }
        }
        this.unlockedPures = new ArrayList<>();
        NbtList pures = nbt.getList("pures", NbtElement.STRING_TYPE);
        for (int i = 0; i < pures.size(); i++) {
            String pure = pures.getString(i);
            TraitBase trait = Traits.get(pure);
            if (trait != null) {
                this.unlockedPures.add(trait);
            }
        }
    }

    public NbtCompound toNBT() {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("total", totalResets);
        nbt.putInt("pure", resetsSincePure);
        nbt.putInt("strong", resetsSinceStrong);
        nbt.putInt("dual", resetsSinceDual);
        nbt.putInt("determination", resetsSinceDT);
        NbtCompound achieved = new NbtCompound();
        achieved.putBoolean("bravery", bravery);
        achieved.putBoolean("justice", justice);
        achieved.putBoolean("kindness", kindness);
        achieved.putBoolean("patience", patience);
        achieved.putBoolean("integrity", integrity);
        achieved.putBoolean("perseverance", perseverance);
        achieved.putBoolean("determination", determination);
        achieved.putBoolean("strongDual", strongDual);
        nbt.put("achieved", achieved);
        NbtList duals = new NbtList();
        for (Pair<TraitBase, TraitBase> dual : unlockedDuals) {
            NbtCompound compound = new NbtCompound();
            compound.putString("trait1", dual.getLeft().getName());
            compound.putString("trait2", dual.getRight().getName());
            duals.add(compound);
        }
        nbt.put("duals", duals);
        NbtList pures = new NbtList();
        for (TraitBase pure : unlockedPures) {
            pures.add(NbtString.of(pure.getName()));
        }
        nbt.put("pures", pures);
        return nbt;
    }

    public void writeBuf(PacketByteBuf buf) {
        buf.writeVarInt(totalResets);
        buf.writeVarInt(resetsSincePure);
        buf.writeVarInt(resetsSinceStrong);
        buf.writeVarInt(resetsSinceDual);
        buf.writeVarInt(resetsSinceDT);
        buf.writeBoolean(bravery);
        buf.writeBoolean(justice);
        buf.writeBoolean(kindness);
        buf.writeBoolean(patience);
        buf.writeBoolean(integrity);
        buf.writeBoolean(perseverance);
        buf.writeBoolean(determination);
        buf.writeBoolean(strongDual);
        buf.writeVarInt(unlockedDuals.size());
        for (Pair<TraitBase, TraitBase> dual : unlockedDuals) {
            buf.writeString(dual.getLeft().getName());
            buf.writeString(dual.getRight().getName());
        }
        buf.writeVarInt(unlockedPures.size());
        for (TraitBase pure : unlockedPures) {
            buf.writeString(pure.getName());
        }
    }

    public static ResetData fromBuf(PacketByteBuf buf) {
        ResetData resetData = new ResetData();
        resetData.totalResets = buf.readVarInt();
        resetData.resetsSincePure = buf.readVarInt();
        resetData.resetsSinceStrong = buf.readVarInt();
        resetData.resetsSinceDual = buf.readVarInt();
        resetData.resetsSinceDT = buf.readVarInt();
        resetData.bravery = buf.readBoolean();
        resetData.justice = buf.readBoolean();
        resetData.kindness = buf.readBoolean();
        resetData.patience = buf.readBoolean();
        resetData.integrity = buf.readBoolean();
        resetData.perseverance = buf.readBoolean();
        resetData.determination = buf.readBoolean();
        resetData.strongDual = buf.readBoolean();
        resetData.unlockedDuals = new ArrayList<>();
        int unlockedDualCount = buf.readVarInt();
        for (int i = 0; i < unlockedDualCount; i++) {
            TraitBase trait1 = Traits.get(buf.readString());
            TraitBase trait2 = Traits.get(buf.readString());
            resetData.unlockedDuals.add(new Pair<>(trait1, trait2));
        }
        resetData.unlockedPures = new ArrayList<>();
        int unlockedPureCount = buf.readVarInt();
        for (int i = 0; i < unlockedPureCount; i++) {
            TraitBase trait = Traits.get(buf.readString());
            resetData.unlockedPures.add(trait);
        }
        return resetData;
    }

    public void addDual(TraitBase trait1, TraitBase trait2) {
        if (hasDual(trait1, trait2)) return;
        unlockedDuals.add(new Pair<>(trait1, trait2));
    }

    public boolean hasDual(TraitBase trait1, TraitBase trait2) {
        for (Pair<TraitBase, TraitBase> dual : unlockedDuals) {
            if ((dual.getLeft() == trait1 || dual.getRight() == trait1) && (dual.getLeft() == trait2 || dual.getRight() == trait2)) {
                return true;
            }
        }
        return false;
    }

    public void addPure(TraitBase trait) {
        if (hasPure(trait)) return;
        unlockedPures.add(trait);
    }

    public boolean hasPure(TraitBase trait) {
        return unlockedPures.contains(trait);
    }
}

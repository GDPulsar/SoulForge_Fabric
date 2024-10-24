package com.pulsar.soulforge.data;

import com.pulsar.soulforge.ability.AbilityBase;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AbilityLayout {
    public List<AbilityRow> rows;

    public AbilityLayout(List<AbilityRow> rows) {
        this.rows = rows;
    }

    public AbilityLayout() {
        this.rows = new ArrayList<>(Arrays.asList(new AbilityRow(), new AbilityRow(), new AbilityRow(), new AbilityRow()));
    }

    public static AbilityLayout fromBuf(List<AbilityBase> abilities, PacketByteBuf buf) {
        AbilityLayout layout = new AbilityLayout();
        try {
            int abilityCount = buf.readVarInt();
            for (int i = 0; i < abilityCount; i++) {
                String abilityName = buf.readString();
                int row = buf.readVarInt();
                int column = buf.readVarInt();
                AbilityBase slotAbility = null;
                for (AbilityBase ability : abilities) {
                    if (Objects.equals(ability.getID().toString(), abilityName)) {
                        slotAbility = ability;
                    }
                }
                layout.setSlot(slotAbility, row, column);
            }
        } catch (Exception ignored) {}
        return layout;
    }

    public void toBuf(PacketByteBuf buf) {
        int abilityCount = 0;
        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 9; column++) {
                AbilityBase ability = this.getSlot(row, column);
                if (ability != null) abilityCount++;
            }
        }
        buf.writeVarInt(abilityCount);
        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 9; column++) {
                AbilityBase ability = this.getSlot(row, column);
                if (ability != null) {
                    buf.writeString(ability.getID().toString());
                    buf.writeVarInt(row);
                    buf.writeVarInt(column);
                }
            }
        }
    }

    public static AbilityLayout fromNbt(List<AbilityBase> abilities, NbtList list) {
        AbilityLayout layout = new AbilityLayout();
        for (int i = 0; i < list.size(); i++) {
            NbtCompound compound = list.getCompound(i);
            for (AbilityBase ability : abilities) {
                if (Objects.equals(ability.getID().toString(), compound.getString("ability"))) {
                    layout.setSlot(ability, compound.getInt("row"), compound.getInt("column"));
                }
            }
        }
        return layout;
    }

    public NbtList toNbt() {
        NbtList list = new NbtList();
        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 9; column++) {
                AbilityBase ability = getSlot(row, column);
                if (ability != null) {
                    NbtCompound compound = new NbtCompound();
                    compound.putString("ability", ability.getID().toString());
                    compound.putInt("row", row);
                    compound.putInt("column", column);
                    list.add(compound);
                }
            }
        }
        return list;
    }

    public void setSlot(AbilityBase ability, int row, int column) {
        if (row < 0 || row >= 4 || column < 0 || column >= 9) return;
        this.rows.get(row).abilities.set(column, ability);
    }

    public AbilityBase getSlot(int row, int column) {
        if (row < 0 || row >= 4 || column < 0 || column >= 9) return null;
        return this.rows.get(row).abilities.get(column);
    }

    public static class AbilityRow {
        public List<AbilityBase> abilities;

        public AbilityRow(List<AbilityBase> abilities) {
            this.abilities = abilities;
        }

        public AbilityRow() {
            this.abilities = new ArrayList<>(Arrays.asList(null, null, null, null, null, null, null, null, null));
        }
    }
}

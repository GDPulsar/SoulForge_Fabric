package com.pulsar.soulforge.components;

import com.pulsar.soulforge.ability.Abilities;
import com.pulsar.soulforge.ability.AbilityBase;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AbilityList {
    public static final PacketCodec<RegistryByteBuf, AbilityList> CODEC = new PacketCodec<>() {
        @Override
        public AbilityList decode(RegistryByteBuf buf) {
            AbilityList abilityList = new AbilityList();
            int abilityCount = buf.readVarInt();
            for (int i = 0; i < abilityCount; i++) {
                AbilityBase ability = Abilities.get(buf.readIdentifier());
                ability.readNbt(buf.readNbt());
                abilityList.add(ability);
            }
            return abilityList;
        }

        @Override
        public void encode(RegistryByteBuf buf, AbilityList value) {
            buf.writeVarInt(value.abilities.size());
            for (Map.Entry<String, AbilityBase> ability : value.abilities.entrySet()) {
                buf.writeIdentifier(ability.getValue().getID());
                buf.writeNbt(ability.getValue().saveNbt(new NbtCompound()));
            }
        }
    };

    HashMap<String, AbilityBase> abilities = new HashMap<>();

    public void add(AbilityBase ability) {
        abilities.put(ability.getName(), ability);
    }

    public void remove(AbilityBase ability) {
        abilities.remove(ability.getName());
    }

    public boolean contains(AbilityBase ability) {
        return abilities.containsKey(ability.getName());
    }

    public boolean contains(String abilityName) {
        return abilities.containsKey(abilityName);
    }

    public <T extends AbilityBase> T get(T ability) {
        return (T)abilities.get(ability.getName());
    }

    public AbilityBase get(String abilityName) {
        return abilities.get(abilityName);
    }

    public Collection<AbilityBase> getAll() {
        return abilities.values();
    }

    public Collection<AbilityBase> getAllActive() {
        Collection<AbilityBase> all = getAll();
        all.removeIf(abilityBase -> !abilityBase.isActive());
        return all;
    }
}

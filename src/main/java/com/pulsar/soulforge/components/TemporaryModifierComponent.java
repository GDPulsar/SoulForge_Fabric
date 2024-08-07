package com.pulsar.soulforge.components;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class TemporaryModifierComponent implements AutoSyncedComponent, CommonTickingComponent {
    private final LivingEntity entity;
    private HashMap<EntityAttributeModifier, Pair<EntityAttribute, Integer>> modifierDurations;

    public TemporaryModifierComponent(LivingEntity living) {
        this.entity = living;
        this.modifierDurations = new HashMap<>();
    }

    public void addTemporaryModifier(EntityAttribute attribute, EntityAttributeModifier modifier, int duration) {
        EntityAttributeInstance instance = this.entity.getAttributeInstance(attribute);
        if (instance != null) {
            if (instance.getModifier(modifier.getId()) != null) instance.removeModifier(modifier.getId());
            instance.addPersistentModifier(modifier);
            this.modifierDurations.put(modifier, new Pair<>(attribute, duration));
        }
    }

    public void removeTemporaryModifier(EntityAttribute attribute, EntityAttributeModifier modifier) {
        EntityAttributeInstance instance = this.entity.getAttributeInstance(attribute);
        if (instance != null) {
            instance.tryRemoveModifier(modifier.getId());
            this.modifierDurations.remove(modifier);
        }
    }

    @Override
    public void tick() {
        for (Map.Entry<EntityAttributeModifier, Pair<EntityAttribute, Integer>> modifier : Set.copyOf(modifierDurations.entrySet())) {
            int duration = modifier.getValue().getRight();
            modifier.getValue().setRight(duration - 1);
            if (duration - 1 <= 0) {
                removeTemporaryModifier(modifier.getValue().getLeft(), modifier.getKey());
            }
        }
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        if (tag.contains("modifiers")) {
            modifierDurations = new HashMap<>();
            HashMap<EntityAttributeModifier, Pair<EntityAttribute, Integer>> newDurations = new HashMap<>();
            NbtList list = tag.getList("modifiers", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < list.size(); i++) {
                NbtCompound modifierNbt = list.getCompound(i);
                EntityAttributeModifier modifier = EntityAttributeModifier.fromNbt(modifierNbt.getCompound("modifier"));
                EntityAttribute attribute = null;
                for (EntityAttributeInstance instance : entity.getAttributes().getTracked()) {
                    if (Objects.equals(instance.getAttribute().getTranslationKey(), modifierNbt.getString("attribute"))) {
                        attribute = instance.getAttribute();
                        break;
                    }
                }
                int duration = modifierNbt.getInt("duration");
                if (attribute != null) newDurations.put(modifier, new Pair<>(attribute, duration));
            }
            modifierDurations = new HashMap<>(Map.copyOf(newDurations));
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        NbtList list = new NbtList();
        for (Map.Entry<EntityAttributeModifier, Pair<EntityAttribute, Integer>> modifier : Set.copyOf(modifierDurations.entrySet())) {
            NbtCompound modifierNbt = new NbtCompound();
            modifierNbt.put("modifier", modifier.getKey().toNbt());
            modifierNbt.putString("attribute", modifier.getValue().getLeft().getTranslationKey());
            modifierNbt.putInt("duration", modifier.getValue().getRight());
            list.add(modifierNbt);
        }
        tag.put("modifiers", list);
    }
}

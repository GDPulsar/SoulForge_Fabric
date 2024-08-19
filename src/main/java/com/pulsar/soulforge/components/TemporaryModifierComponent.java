package com.pulsar.soulforge.components;

import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.util.Triplet;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.*;

public class TemporaryModifierComponent implements AutoSyncedComponent, CommonTickingComponent {
    private final LivingEntity entity;
    private List<Triplet<EntityAttributeModifier, EntityAttribute, Float>> modifierDurations;

    public TemporaryModifierComponent(LivingEntity living) {
        this.entity = living;
        this.modifierDurations = new ArrayList<>();
    }

    public List<Triplet<EntityAttributeModifier, EntityAttribute, Float>> getModifiers() {
        return this.modifierDurations;
    }

    public int getModifierCount() {
        return this.modifierDurations.size();
    }

    public void addStackingTemporaryModifier(EntityAttribute attribute, EntityAttributeModifier modifier, float duration) {
        EntityAttributeInstance instance = this.entity.getAttributeInstance(attribute);
        if (instance != null) {
            if (instance.hasModifier(modifier)) {
                modifier = new EntityAttributeModifier(UUID.randomUUID(), modifier.getName(), modifier.getValue(), modifier.getOperation());
            }
            instance.addPersistentModifier(modifier);
            this.modifierDurations.add(new Triplet<>(modifier, attribute, duration));
        }
    }

    public void addTemporaryModifier(EntityAttribute attribute, EntityAttributeModifier modifier, float duration) {
        EntityAttributeInstance instance = this.entity.getAttributeInstance(attribute);
        if (instance != null) {
            this.removeTemporaryModifier(attribute, modifier);
            instance.addPersistentModifier(modifier);
            this.modifierDurations.add(new Triplet<>(modifier, attribute, duration));
        }
    }

    public void removeTemporaryModifier(EntityAttribute attribute, EntityAttributeModifier modifier) {
        EntityAttributeInstance instance = this.entity.getAttributeInstance(attribute);
        if (instance != null) {
            instance.tryRemoveModifier(modifier.getId());
            Triplet<EntityAttributeModifier, EntityAttribute, Float> match = null;
            for (Triplet<EntityAttributeModifier, EntityAttribute, Float> testing : Set.copyOf(modifierDurations)) {
                if (testing.getFirst().getId().compareTo(modifier.getId()) == 0 && testing.getSecond() == attribute) {
                    match = testing;
                    break;
                }
            }
            if (match != null) this.modifierDurations.remove(match);
        }
    }

    @Override
    public void tick() {
        for (Triplet<EntityAttributeModifier, EntityAttribute, Float> modifier : Set.copyOf(modifierDurations)) {
            float duration = modifier.getThird();
            modifier.setThird(duration - (float)entity.getAttributeValue(SoulForgeAttributes.EFFECT_DURATION_MULTIPLIER));
            if (duration - 1 <= 0) {
                removeTemporaryModifier(modifier.getSecond(), modifier.getFirst());
            }
        }
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        if (tag.contains("modifiers")) {
            modifierDurations = new ArrayList<>();
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
                float duration = modifierNbt.getFloat("duration");
                if (attribute != null) modifierDurations.add(new Triplet<>(modifier, attribute, duration));
            }
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        NbtList list = new NbtList();
        for (Triplet<EntityAttributeModifier, EntityAttribute, Float> modifier : Set.copyOf(modifierDurations)) {
            NbtCompound modifierNbt = new NbtCompound();
            modifierNbt.put("modifier", modifier.getFirst().toNbt());
            modifierNbt.putString("attribute", modifier.getSecond().getTranslationKey());
            modifierNbt.putFloat("duration", modifier.getThird());
            list.add(modifierNbt);
        }
        tag.put("modifiers", list);
    }
}

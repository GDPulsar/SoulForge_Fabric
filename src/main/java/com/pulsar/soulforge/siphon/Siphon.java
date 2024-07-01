package com.pulsar.soulforge.siphon;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Nullable;

public record Siphon(Siphon.Type type) {
    public enum Type implements StringIdentifiable {
        BRAVERY("bravery", 0.1f, new Effects(
                ComponentMap.builder().add(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder().add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, "Bravery Siphon"), 1f, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.ANY).build()).build(),
                ComponentMap.builder().add(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder().add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, "Bravery Siphon"), 0.25f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), AttributeModifierSlot.ANY).build()).build(),
                ComponentMap.builder().build(),
                ComponentMap.builder().build(),
                ComponentMap.builder().add(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder().add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, "Bravery Siphon"), 2f, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.ANY).build()).build(),
                ComponentMap.builder().build(),
                ComponentMap.builder().build()
        )),
        JUSTICE("justice", 0.2f, new Effects(
                ComponentMap.builder().add(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder().add(SoulForgeAttributes.MAGIC_COOLDOWN,
                        new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, "Justice Siphon"), -0.05f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), AttributeModifierSlot.ANY).build()).build(),
                ComponentMap.builder().add(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder().add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, "Justice Siphon"), 0.25f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), AttributeModifierSlot.ANY).build()).build(),
                ComponentMap.builder().build(),
                ComponentMap.builder().build(),
                ComponentMap.builder().add(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder().add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, "Justice Siphon"), 2f, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.ANY).build()).build(),
                ComponentMap.builder().build(),
                ComponentMap.builder().build()
        )),
        KINDNESS("kindness", 0.3f, new Effects(
                ComponentMap.builder().add(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder().add(EntityAttributes.GENERIC_MAX_HEALTH,
                        new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, "Kindness Siphon"), 1f, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.ANY).build()).build(),
                ComponentMap.builder().add(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder().add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, "Kindness Siphon"), 0.25f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), AttributeModifierSlot.ANY).build()).build(),
                ComponentMap.builder().build(),
                ComponentMap.builder().build(),
                ComponentMap.builder().add(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder().add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, "Kindness Siphon"), 2f, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.ANY).build()).build(),
                ComponentMap.builder().build(),
                ComponentMap.builder().build()
        )),
        PATIENCE("patience", 0.4f, new Effects(
                ComponentMap.builder().add(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder().add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, "Bravery Siphon"), 1f, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.ANY).build()).build(),
                ComponentMap.builder().add(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder().add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, "Bravery Siphon"), 0.25f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), AttributeModifierSlot.ANY).build()).build(),
                ComponentMap.builder().build(),
                ComponentMap.builder().build(),
                ComponentMap.builder().add(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder().add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, "Bravery Siphon"), 2f, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.ANY).build()).build(),
                ComponentMap.builder().build(),
                ComponentMap.builder().build()
        )),
        INTEGRITY("integrity", 0.5f, new Effects(
                ComponentMap.builder().add(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder().add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, "Bravery Siphon"), 1f, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.ANY).build()).build(),
                ComponentMap.builder().add(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder().add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, "Bravery Siphon"), 0.25f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), AttributeModifierSlot.ANY).build()).build(),
                ComponentMap.builder().build(),
                ComponentMap.builder().build(),
                ComponentMap.builder().add(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder().add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, "Bravery Siphon"), 2f, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.ANY).build()).build(),
                ComponentMap.builder().build(),
                ComponentMap.builder().build()
        )),
        PERSEVERANCE("perseverance", 0.6f, new Effects(
                ComponentMap.builder().add(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder().add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, "Bravery Siphon"), 1f, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.ANY).build()).build(),
                ComponentMap.builder().add(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder().add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, "Bravery Siphon"), 0.25f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), AttributeModifierSlot.ANY).build()).build(),
                ComponentMap.builder().build(),
                ComponentMap.builder().build(),
                ComponentMap.builder().add(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder().add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, "Bravery Siphon"), 2f, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.ANY).build()).build(),
                ComponentMap.builder().build(),
                ComponentMap.builder().build()
        )),
        DETERMINATION("determination", 0.7f, new Effects(
                ComponentMap.builder().add(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder().add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, "Bravery Siphon"), 1f, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.ANY).build()).build(),
                ComponentMap.builder().add(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder().add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, "Bravery Siphon"), 0.25f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), AttributeModifierSlot.ANY).build()).build(),
                ComponentMap.builder().build(),
                ComponentMap.builder().build(),
                ComponentMap.builder().add(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder().add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, "Bravery Siphon"), 2f, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.ANY).build()).build(),
                ComponentMap.builder().build(),
                ComponentMap.builder().build()
        )),
        SPITE("spite", 0.8f, new Effects(
                ComponentMap.builder().add(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder().add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, "Bravery Siphon"), 1f, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.ANY).build()).build(),
                ComponentMap.builder().add(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder().add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, "Bravery Siphon"), 0.25f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), AttributeModifierSlot.ANY).build()).build(),
                ComponentMap.builder().build(),
                ComponentMap.builder().build(),
                ComponentMap.builder().add(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.builder().add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, "Bravery Siphon"), 2f, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.ANY).build()).build(),
                ComponentMap.builder().build(),
                ComponentMap.builder().build()
        ));

        private final String name;
        private final float index;
        private final Effects effects;

        Type(String name, float index, Effects effects) {
            this.name = name;
            this.index = index;
            this.effects = effects;
        }

        @Override
        public String asString() {
            return this.name;
        }

        public float getIndex() {
            return this.index;
        }

        @Nullable
        public static Type getSiphon(String name) {
            return switch (name) {
                case "bravery" -> Type.BRAVERY;
                case "justice" -> Type.JUSTICE;
                case "kindness" -> Type.KINDNESS;
                case "patience" -> Type.PATIENCE;
                case "integrity" -> Type.INTEGRITY;
                case "perseverance" -> Type.PERSEVERANCE;
                case "determination" -> Type.DETERMINATION;
                case "spite" -> Type.SPITE;
                default -> null;
            };
        }
    }

    public static class Effects {
        private final ComponentMap armorEffects;
        private final ComponentMap weaponEffects;
        private final ComponentMap rangedEffects;
        private final ComponentMap toolEffects;
        private final ComponentMap tridentEffects;
        private final ComponentMap elytraEffects;
        private final ComponentMap maceEffects;

        public Effects(ComponentMap armorEffects, ComponentMap weaponEffects, ComponentMap rangedEffects,
                       ComponentMap toolEffects, ComponentMap tridentEffects, ComponentMap elytraEffects, ComponentMap maceEffects) {
            this.armorEffects = armorEffects;
            this.weaponEffects = weaponEffects;
            this.rangedEffects = rangedEffects;
            this.toolEffects = toolEffects;
            this.tridentEffects = tridentEffects;
            this.elytraEffects = elytraEffects;
            this.maceEffects = maceEffects;
        }

        public ComponentMap getArmorEffects() {
            return this.armorEffects;
        }

        public ComponentMap getWeaponEffects() {
            return weaponEffects;
        }

        public ComponentMap getRangedEffects() {
            return rangedEffects;
        }

        public ComponentMap getToolEffects() {
            return toolEffects;
        }

        public ComponentMap getTridentEffects() {
            return tridentEffects;
        }

        public ComponentMap getElytraEffects() {
            return elytraEffects;
        }

        public ComponentMap getMaceEffects() {
            return maceEffects;
        }
    }
}

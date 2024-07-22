package com.pulsar.soulforge.mixin;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.siphon.Siphon;
import com.pulsar.soulforge.siphon.Siphon.Type;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow public abstract boolean hasNbt();

    @Shadow private @Nullable NbtCompound nbt;

    @Inject(method="getAttributeModifiers", at=@At("RETURN"), cancellable = true)
    public void getAttributeModifiers(EquipmentSlot slot, CallbackInfoReturnable<Multimap<EntityAttribute, EntityAttributeModifier>> cir) {
        Multimap<EntityAttribute, EntityAttributeModifier> multimap = cir.getReturnValue();
        if (this.hasNbt() && this.nbt.contains("Siphon")) {
            multimap.putAll(getSiphonModifiers((ItemStack)(Object)this, slot));
            cir.setReturnValue(multimap);
        }
    }

    @Unique
    private static Map<EquipmentSlot, Map<Siphon.Type, Map.Entry<EntityAttribute, EntityAttributeModifier>>> armorSiphonModifiers;

    @Inject(method = "<clinit>", at=@At("HEAD"))
    private static void init(CallbackInfo ci) {
        initSiphonModifiers();
    }

    @Unique
    private static void initSiphonModifiers() {
        armorSiphonModifiers = new HashMap<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            Map<Siphon.Type, Map.Entry<EntityAttribute, EntityAttributeModifier>> slotSiphonModifiers = new HashMap<>();
            slotSiphonModifiers.put(Siphon.Type.BRAVERY, new AbstractMap.SimpleEntry<>(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(UUID.randomUUID(), "Bravery Siphon", 1, EntityAttributeModifier.Operation.ADDITION)));
            slotSiphonModifiers.put(Siphon.Type.JUSTICE, new AbstractMap.SimpleEntry<>(SoulForgeAttributes.MAGIC_COOLDOWN, new EntityAttributeModifier(UUID.randomUUID(), "Justice Siphon", -0.05, EntityAttributeModifier.Operation.ADDITION)));
            slotSiphonModifiers.put(Siphon.Type.KINDNESS, new AbstractMap.SimpleEntry<>(EntityAttributes.GENERIC_MAX_HEALTH, new EntityAttributeModifier(UUID.randomUUID(), "Kindness Siphon", 2, EntityAttributeModifier.Operation.ADDITION)));
            slotSiphonModifiers.put(Siphon.Type.PATIENCE, new AbstractMap.SimpleEntry<>(SoulForgeAttributes.MAGIC_POWER, new EntityAttributeModifier(UUID.randomUUID(), "Patience Siphon", 0.05, EntityAttributeModifier.Operation.ADDITION)));
            slotSiphonModifiers.put(Siphon.Type.INTEGRITY, new AbstractMap.SimpleEntry<>(EntityAttributes.GENERIC_MOVEMENT_SPEED, new EntityAttributeModifier(UUID.randomUUID(), "Integrity Siphon", 0.05, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)));
            slotSiphonModifiers.put(Siphon.Type.PERSEVERANCE, new AbstractMap.SimpleEntry<>(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, new EntityAttributeModifier(UUID.randomUUID(), "Perseverance Siphon", 1f, EntityAttributeModifier.Operation.ADDITION)));
            slotSiphonModifiers.put(Siphon.Type.DETERMINATION, new AbstractMap.SimpleEntry<>(SoulForgeAttributes.MAGIC_COST, new EntityAttributeModifier(UUID.randomUUID(), "Determination Siphon", -0.05, EntityAttributeModifier.Operation.ADDITION)));
            armorSiphonModifiers.put(slot, slotSiphonModifiers);
        }
        braveryWeaponModifier = new AbstractMap.SimpleEntry<>(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(UUID.randomUUID(), "Bravery Siphon", 0.25, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
        braveryTridentModifier = new AbstractMap.SimpleEntry<>(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(UUID.randomUUID(), "Bravery Siphon", 2, Operation.ADDITION));
        justiceTridentModifier = new AbstractMap.SimpleEntry<>(ReachEntityAttributes.ATTACK_RANGE, new EntityAttributeModifier(UUID.randomUUID(), "Justice Siphon", 1.5, Operation.ADDITION));
        integrityWeaponDamageModifier = new AbstractMap.SimpleEntry<>(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(UUID.randomUUID(), "Integrity Siphon", -0.25, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
        integrityWeaponSpeedModifier = new AbstractMap.SimpleEntry<>(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(UUID.randomUUID(), "Integrity Siphon", 0.5, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
    }

    @Unique
    private static Map.Entry<EntityAttribute, EntityAttributeModifier> braveryWeaponModifier;
    @Unique
    private static Map.Entry<EntityAttribute, EntityAttributeModifier> braveryTridentModifier;
    @Unique
    private static Map.Entry<EntityAttribute, EntityAttributeModifier> justiceTridentModifier;
    @Unique
    private static Map.Entry<EntityAttribute, EntityAttributeModifier> integrityWeaponDamageModifier;
    @Unique
    private static Map.Entry<EntityAttribute, EntityAttributeModifier> integrityWeaponSpeedModifier;

    @Unique
    private Multimap<EntityAttribute, EntityAttributeModifier> getSiphonModifiers(ItemStack stack, EquipmentSlot slot) {
        if (stack.getNbt() != null) {
            if (stack.getNbt().contains("Siphon")) {
                ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> modifiers = ImmutableMultimap.builder();
                String siphonStr = stack.getNbt().getString("Siphon");
                Siphon.Type type = Siphon.Type.getSiphon(siphonStr);
                if (type != null) {
                    if (type != Siphon.Type.SPITE) {
                        if (stack.getItem() instanceof ArmorItem armor && armor.getSlotType() == slot) {
                            modifiers.put(armorSiphonModifiers.get(slot).get(type));
                        } else if ((stack.getItem() instanceof SwordItem || stack.getItem() instanceof ToolItem) && slot == EquipmentSlot.MAINHAND) {
                            if (type == Siphon.Type.BRAVERY) modifiers.put(braveryWeaponModifier);
                            if (type == Siphon.Type.INTEGRITY) {
                                modifiers.put(integrityWeaponDamageModifier);
                                modifiers.put(integrityWeaponSpeedModifier);
                            }
                        } else if (stack.getItem() instanceof TridentItem && slot == EquipmentSlot.MAINHAND) {
                            if (type == Type.BRAVERY) {
                                modifiers.put(braveryTridentModifier);
                            }
                            if (type == Type.JUSTICE) {
                                modifiers.put(justiceTridentModifier);
                            }
                        }
                    } else {
                        for (Siphon.Type siphonType : Siphon.Type.values()) {
                            if (siphonType != Siphon.Type.SPITE) {
                                if (stack.getItem() instanceof ArmorItem armor && armor.getSlotType() == slot) {
                                    modifiers.put(armorSiphonModifiers.get(slot).get(siphonType));
                                } else if ((stack.getItem() instanceof SwordItem || stack.getItem() instanceof ToolItem) && slot == EquipmentSlot.MAINHAND) {
                                    if (siphonType == Siphon.Type.BRAVERY) modifiers.put(braveryWeaponModifier);
                                    if (siphonType == Siphon.Type.INTEGRITY) {
                                        modifiers.put(integrityWeaponDamageModifier);
                                        modifiers.put(integrityWeaponSpeedModifier);
                                    }
                                } else if (stack.getItem() instanceof TridentItem && slot == EquipmentSlot.MAINHAND) {
                                    if (siphonType == Type.BRAVERY) {
                                        modifiers.put(braveryTridentModifier);
                                    }
                                    if (siphonType == Type.JUSTICE) {
                                        modifiers.put(justiceTridentModifier);
                                    }
                                }
                            }
                        }
                    }
                }
                return modifiers.build();
            }
        }
        return ImmutableMultimap.of();
    }
}

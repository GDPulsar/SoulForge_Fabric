package com.pulsar.soulforge.item.weapons;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.tag.SoulForgeTags;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;

public class MagicSwordItem extends MagicToolItem {
    public float baseAttackDamage;
    public float attackDamage;
    public float attackSpeed;
    public float lvIncrease;
    private Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> attributeModifiers = ImmutableMultimap.of();

    public MagicSwordItem(float attackDamage, float attackSpeed, float lvIncrease) {
        super(new Item.Settings().component(DataComponentTypes.TOOL, ToolMaterials.IRON.createComponent(BlockTags.SWORD_EFFICIENT)));
        this.baseAttackDamage = attackDamage;
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
        this.lvIncrease = lvIncrease;
    }

    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return !miner.isCreative();
    }

    public float getBonusAttackDamage(Entity target, float baseAttackDamage, DamageSource damageSource) {
        if (damageSource.getAttacker() instanceof PlayerEntity attacker) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(attacker);
            if (this.getDefaultStack().isIn(SoulForgeTags.EFFECTIVE_LV_WEAPON)) return this.baseAttackDamage + this.lvIncrease * playerSoul.getEffectiveLV();
            return this.baseAttackDamage + this.lvIncrease * playerSoul.getLV();
        }
        return 0.0f;
    }

    public ComponentMap getComponents() {
        ComponentMap map = super.getComponents();
        return ComponentMap.builder().addAll(map).add(DataComponentTypes.ATTRIBUTE_MODIFIERS, getAttributeModifiers()).build();
    }

    public AttributeModifiersComponent getAttributeModifiers() {
        AttributeModifiersComponent.Builder builder = AttributeModifiersComponent.builder();
        for (Map.Entry<RegistryEntry<EntityAttribute>, EntityAttributeModifier> modifier : this.attributeModifiers.entries()) {
            builder.add(modifier.getKey(), modifier.getValue(), AttributeModifierSlot.HAND);
        }
        return builder.build();
    }

    public void addAttribute(RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier) {
        ImmutableMultimap.Builder<RegistryEntry<EntityAttribute>, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(this.attributeModifiers);
        builder.put(attribute, modifier);
        this.attributeModifiers = builder.build();
    }
}

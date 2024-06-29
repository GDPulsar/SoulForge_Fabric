package com.pulsar.soulforge.item.weapons;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MagicSwordItem extends MagicToolItem {
    public float baseAttackDamage;
    public float attackDamage;
    public float attackSpeed;
    public float lvIncrease;
    private Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers = ImmutableMultimap.of();

    public MagicSwordItem(float attackDamage, float attackSpeed, float lvIncrease) {
        super();
        this.baseAttackDamage = attackDamage;
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
        this.lvIncrease = lvIncrease;
    }

    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return !miner.isCreative();
    }

    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        if (state.isOf(Blocks.COBWEB)) {
            return 15.0F;
        } else {
            return state.isIn(BlockTags.SWORD_EFFICIENT) ? 1.5F : 1.0F;
        }
    }

    public boolean isSuitableFor(BlockState state) {
        return state.isOf(Blocks.COBWEB);
    }

    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        if (slot != EquipmentSlot.MAINHAND) return ImmutableMultimap.of();
        return ImmutableMultimap.<EntityAttribute, EntityAttributeModifier>builder()
                .putAll(this.attributeModifiers)
                .put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", this.attackDamage, EntityAttributeModifier.Operation.ADDITION))
                .put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", this.attackSpeed - 4f, EntityAttributeModifier.Operation.ADDITION))
                .build();
    }

    public void addAttribute(EntityAttribute attribute, EntityAttributeModifier modifier) {
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(this.attributeModifiers);
        builder.put(attribute, modifier);
        this.attributeModifiers = builder.build();
    }
}

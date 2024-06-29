package com.pulsar.soulforge.ability.perseverance;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class PerseveranceAura extends ToggleableAbilityBase {
    public final String name = "Perseverance Aura";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "perseverance_aura");
    public final int requiredLv = 15;
    public final int cost = 100;
    public final int cooldown = 0;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        toggleActive();
        if (getActive()) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            playerSoul.setMagic(0f);
        }
        return true;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MAX_HEALTH, "perseverance_aura_health");
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_ARMOR, "perseverance_aura_armor");
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "perseverance_aura_strength");
        EntityAttributeModifier healthModifier = new EntityAttributeModifier("perseverance_aura_health", playerSoul.getEffectiveLV() / 2f, EntityAttributeModifier.Operation.ADDITION);
        EntityAttributeModifier armorModifier = new EntityAttributeModifier("perseverance_aura_armor", playerSoul.getEffectiveLV() / 1.66f, EntityAttributeModifier.Operation.ADDITION);
        EntityAttributeModifier strengthModifier = new EntityAttributeModifier("perseverance_aura_strength", playerSoul.getEffectiveLV() * 0.0175f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
        player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).addPersistentModifier(healthModifier);
        player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).addPersistentModifier(armorModifier);
        player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).addPersistentModifier(strengthModifier);
        return !getActive();
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MAX_HEALTH, "perseverance_aura_health");
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_ARMOR, "perseverance_aura_armor");
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "perseverance_aura_strength");
        return true;
    }
    
    public String getName() { return name; }

    public Text getLocalizedText() { return Text.translatable("ability."+id.getPath()+".name"); }

    public Identifier getID() { return id; }

    public String getTooltip() { return Text.translatable("ability."+id.getPath()+".tooltip").getString(); }

    public int getLV() { return requiredLv; }

    public int getCost() { return cost; }

    public int getCooldown() { return cooldown; }

    @Override
    public AbilityBase getInstance() {
        return new PerseveranceAura();
    }
}

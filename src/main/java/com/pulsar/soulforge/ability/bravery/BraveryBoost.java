package com.pulsar.soulforge.ability.bravery;

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

public class BraveryBoost extends ToggleableAbilityBase {
    public final String name = "Bravery Boost";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "bravery_boost");
    public final int requiredLv = 15;
    public final int cost = 100;
    public final int cooldown = 0;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        toggleActive();
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (getActive()) {
            if (playerSoul.getMagic() < 100f) {
                setActive(false);
                return false;
            }
            playerSoul.setMagic(0f);
        }
        return true;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MAX_HEALTH, "bravery_boost_health");
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "bravery_boost_strength");
        EntityAttributeModifier healthModifier = new EntityAttributeModifier("bravery_boost_health", playerSoul.getEffectiveLV() / 40f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
        EntityAttributeModifier strengthModifier = new EntityAttributeModifier("bravery_boost_strength", playerSoul.getEffectiveLV() / 40f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
        player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).addPersistentModifier(healthModifier);
        player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).addPersistentModifier(strengthModifier);
        return !getActive();
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MAX_HEALTH, "bravery_boost_health");
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "bravery_boost_strength");
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
        return new BraveryBoost();
    }
}

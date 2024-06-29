package com.pulsar.soulforge.ability.determination;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.trait.Traits;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.Objects;

public class DeterminationAura extends ToggleableAbilityBase {
    public final String name = "Determination Aura";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "determination_aura");
    public final int requiredLv = 19;
    public final int cost = 100;
    public final int cooldown = 0;

    int timer = 0;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        toggleActive();
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (getActive()) {
            playerSoul.setMagic(0f);
            timer = 0;
        }
        return true;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        timer++;
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (timer == 20) {
            player.heal(1f);
            timer = 0;
        }
        float effLv = playerSoul.getLV();
        float multiplier = 1f;
        if (player.getAttributeInstance(SoulForgeAttributes.MAGIC_POWER) != null) multiplier = (float)player.getAttributeInstance(SoulForgeAttributes.MAGIC_POWER).getValue();
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MAX_HEALTH, "determination_aura_health");
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "determination_aura_strength");
        EntityAttributeModifier healthModifier = new EntityAttributeModifier("determination_aura_health", MathHelper.floor(effLv*multiplier)/2f, EntityAttributeModifier.Operation.ADDITION);
        EntityAttributeModifier strengthModifier = new EntityAttributeModifier("determination_aura_strength", playerSoul.getEffectiveLV()*0.0175f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
        player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).addPersistentModifier(healthModifier);
        player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).addPersistentModifier(strengthModifier);
        return !getActive();
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MAX_HEALTH, "determination_aura_health");
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "determination_aura_strength");
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
        return new DeterminationAura();
    }
}

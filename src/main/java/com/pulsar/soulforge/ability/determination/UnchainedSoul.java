package com.pulsar.soulforge.ability.determination;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class UnchainedSoul extends ToggleableAbilityBase {
    public final String name = "Unchained Soul";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "unchained_soul");
    public final int requiredLv = 17;
    public final int cost = 0;
    public final int cooldown = 0;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        toggleActive();
        return getActive();
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        int effectCount = 0;
        for (StatusEffectInstance effect : player.getStatusEffects()) {
            effectCount += effect.getAmplifier();
        }
        Utils.clearModifiersByName(player, SoulForgeAttributes.MAGIC_POWER, "unchained_soul_power");
        Objects.requireNonNull(player.getAttributeInstance(SoulForgeAttributes.MAGIC_POWER)).addPersistentModifier(
                new EntityAttributeModifier("unchained_soul_power", 0.05f * effectCount, EntityAttributeModifier.Operation.ADDITION));
        Utils.clearModifiersByName(player, SoulForgeAttributes.MAGIC_COST, "unchained_soul_cost");
        Objects.requireNonNull(player.getAttributeInstance(SoulForgeAttributes.MAGIC_COST)).addPersistentModifier(
                new EntityAttributeModifier("unchained_soul_cost", 0.25f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
        return !getActive();
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        Utils.clearModifiersByName(player, SoulForgeAttributes.MAGIC_POWER, "unchained_soul_power");
        Utils.clearModifiersByName(player, SoulForgeAttributes.MAGIC_COST, "unchained_soul_cost");
        setActive(false);
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
    public AbilityBase getInstance() { return new UnchainedSoul(); }
}

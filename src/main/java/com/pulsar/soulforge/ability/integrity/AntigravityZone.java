package com.pulsar.soulforge.ability.integrity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.components.SoulComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class AntigravityZone extends ToggleableAbilityBase {
    public final String name = "Antigravity Zone";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "antigravity_zone");
    public final int requiredLv = 17;
    public final int cost = 40;
    public final int cooldown = 400;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        toggleActive();
        return getActive();
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        return !getActive();
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        return true;
    }
    
    public String getName() { return name; }

    public Text getLocalizedText() { return Text.translatable("ability."+id.getPath()+".name"); }

    public Identifier getID() { return id; }

    public String getTooltip() { return Text.translatable("ability."+id.getPath()+".tooltip").getString(); }

    public int getLV() { return requiredLv; }

    public int getCost() { return cost; }

    public int getCooldown() { return cooldown; }

    public AbilityType getType() { return type; }

    @Override
    public AbilityBase getInstance() {
        return new AntigravityZone();
    }
}

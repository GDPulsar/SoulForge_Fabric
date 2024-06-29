package com.pulsar.soulforge.ability.duals;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class Nanomachines extends AbilityBase {
    public final String name = "Nanomachines";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "nanomachines");
    public final int requiredLv = 10;
    public final int cost = 50;
    public final int cooldown = 2000;
    public final AbilityType type = AbilityType.CAST;

    public int timer = 0;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        timer = 1000;
        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoulForgeSounds.HEAL_EVENT, SoundCategory.PLAYERS, 1f, 1f);
        return true;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        if (timer > 0) timer--;
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        int rate = Math.round(120f/(Math.min(36, playerSoul.getEffectiveLV() + 6)));
        if (timer % rate * 2 == 0) player.heal(1f);
        return timer <= 0;
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
        return new Nanomachines();
    }
}

package com.pulsar.soulforge.ability.kindness;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.CooldownDisplayEntry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.Objects;
import java.util.Optional;

public class SelfHeal extends AbilityBase {
    public int timer = 0;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        timer = 200;
        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoulForgeSounds.HEAL_EVENT, SoundCategory.PLAYERS, 1f, 1f);
        return true;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        if (player.age - player.getLastAttackedTime() >= 0 && player.age - player.getLastAttackedTime() <= 1) return true;
        if (timer > 0) timer--;
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        int rate = Math.round(120f/(Math.min(36, playerSoul.getEffectiveLV() + 6)));
        if (timer % rate == 0) {
            if (player.getHealth() < player.getMaxHealth()) playerSoul.setStyle(playerSoul.getStyle() + 1);
            player.heal(1f);
        }
        return timer <= 0;
    }

    public int getLV() { return 10; }

    public int getCost() { return 50; }

    public int getCooldown() { return 500; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new SelfHeal();
    }

    @Override
    public NbtCompound saveNbt(NbtCompound nbt) {
        nbt.putInt("timer", timer);
        return super.saveNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (!Objects.equals(nbt.getString("id"), getID().getPath())) return;
        timer = nbt.getInt("timer");
        super.readNbt(nbt);
    }

    @Override
    public Optional<CooldownDisplayEntry> getCooldownEntry() {
        return Optional.of(new CooldownDisplayEntry(
                new Identifier(SoulForge.MOD_ID, "self_heal"), "Self Heal",
                0, timer / 20f, 10f, new Color(0f, 1f, 0f)
        ));
    }
}

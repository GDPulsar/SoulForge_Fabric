package com.pulsar.soulforge.ability.determination;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.client.networking.PerformAnimationPacket;
import com.pulsar.soulforge.client.networking.SetThirdPersonPacket;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.entity.DarkFountainEntity;
import com.pulsar.soulforge.networking.SoulForgeNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Objects;

public class DarkFountain extends AbilityBase {
    private int timer = 0;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        playerSoul.addTag("immobile");
        playerSoul.addTag("forcedThirdPerson");
        if (player.getServer() != null) SoulForgeNetworking.broadcast(null, player.getServer(), new PerformAnimationPacket(player.getUuid(), "dark_fountain", false));
        timer = 20;
        return super.cast(player);
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        timer--;
        if (timer < 10) {
            ServerWorld serverWorld = (player).getServerWorld();
            serverWorld.spawnParticles(ParticleTypes.SQUID_INK, player.getX(), player.getY(), player.getZ(), 5, 0.5, 0.2, 0.5, 1);
        }
        if (timer == 5) {
            DarkFountainEntity fountain = new DarkFountainEntity(player.getWorld(), player.getPos(), player);
            fountain.setPosition(player.getPos());
            fountain.owner = player;
            player.getWorld().spawnEntity(fountain);
        }
        return timer <= 0;
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        playerSoul.removeTag("immobile");
        playerSoul.removeTag("forcedThirdPerson");
        playerSoul.sync();
        ServerPlayNetworking.send(player, new SetThirdPersonPacket(false));
        return super.end(player);
    }

    public int getLV() { return 17; }

    public int getCost() { return 40; }

    public int getCooldown() { return 1200; }

    @Override
    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new DarkFountain();
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
}

package com.pulsar.soulforge.ability.determination;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.entity.DarkFountainEntity;
import com.pulsar.soulforge.networking.SoulForgeNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class DarkFountain extends AbilityBase {
    public final String name = "Dark Fountain";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "dark_fountain");
    public final int requiredLv = 17;
    public final int cost = 40;
    public final int cooldown = 1200;
    public final AbilityType type = AbilityType.CAST;

    private int timer = 0;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        playerSoul.addTag("immobile");
        playerSoul.addTag("forcedThirdPerson");
        PacketByteBuf buf = PacketByteBufs.create().writeUuid(player.getUuid()).writeString("dark_fountain");
        buf.writeBoolean(false);
        if (player.getServer() != null) SoulForgeNetworking.broadcast(null, player.getServer(), SoulForgeNetworking.PERFORM_ANIMATION, buf);
        timer = 20;
        return true;
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
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(false);
        ServerPlayNetworking.send(player, SoulForgeNetworking.SET_THIRD_PERSON, buf);
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
    }
}

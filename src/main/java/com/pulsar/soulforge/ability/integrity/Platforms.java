package com.pulsar.soulforge.ability.integrity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.ability.bravery.BraveryBoost;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.entity.DeterminationPlatformEntity;
import com.pulsar.soulforge.entity.IntegrityPlatformEntity;
import com.pulsar.soulforge.networking.SoulForgeNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class Platforms extends ToggleableAbilityBase {
    public final String name = "Platforms";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "platforms");
    public final int requiredLv = 1;
    public final int cost = 0;
    public final int cooldown = 0;

    public void spawn(PlayerEntity player) {
        IntegrityPlatformEntity platform = new IntegrityPlatformEntity(player.getWorld(), player.getPos().subtract(0, 0.25f, 0));
        player.getWorld().spawnEntity(platform);
        player.teleport(platform.getX(), platform.getY()+0.25f, platform.getZ());
        player.fallDistance = 0;
    }

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
        return new Platforms();
    }
}

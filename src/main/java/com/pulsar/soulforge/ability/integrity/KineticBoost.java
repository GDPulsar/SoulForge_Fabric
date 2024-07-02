package com.pulsar.soulforge.ability.integrity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.networking.SoulForgeNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class KineticBoost extends AbilityBase {
    public final String name = "Kinetic Boost";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "kinetic_boost");
    public final int requiredLv = 12;
    public final int cost = 25;
    public final int cooldown = 80;
    public final AbilityType type = AbilityType.CAST;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        float horiz = playerSoul.getEffectiveLV()*0.15f;
        float vert = playerSoul.getEffectiveLV()*0.02f;
        Vec3d direction = new Vec3d(player.getRotationVector().x, 0f, player.getRotationVector().z).normalize().multiply(horiz);
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(false).writeBoolean(false).writeBoolean(true);
        buf.writeVector3f(new Vec3d(direction.x, vert, direction.z).toVector3f());
        ServerPlayNetworking.send(player, SoulForgeNetworking.POSITION_VELOCITY, buf);
        return true;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        return true;
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
        return new KineticBoost();
    }
}

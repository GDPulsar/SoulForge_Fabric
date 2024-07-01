package com.pulsar.soulforge.ability.integrity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.client.networking.PositionVelocityPacket;
import com.pulsar.soulforge.components.SoulComponent;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public class KineticBoost extends AbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        float horiz = playerSoul.getEffectiveLV()*0.15f;
        float vert = playerSoul.getEffectiveLV()*0.02f;
        Vec3d direction = new Vec3d(player.getRotationVector().x, 0f, player.getRotationVector().z).normalize().multiply(horiz);
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(false).writeBoolean(false).writeBoolean(true);
        buf.writeVector3f(new Vec3d(direction.x, vert, direction.z).toVector3f());
        ServerPlayNetworking.send(player, new PositionVelocityPacket(new Vector3f(), new Vector3f(), new Vector3f((float) direction.x, vert, (float) direction.z)));
        return super.cast(player);
    }

    public int getLV() { return 12; }
    public int getCost() { return 25; }
    public int getCooldown() { return 80; }
    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new KineticBoost();
    }
}

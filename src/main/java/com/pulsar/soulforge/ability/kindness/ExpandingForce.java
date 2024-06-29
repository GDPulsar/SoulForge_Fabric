package com.pulsar.soulforge.ability.kindness;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.bravery.BraveryBoost;
import com.pulsar.soulforge.networking.SoulForgeNetworking;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.TeamUtils;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class ExpandingForce extends AbilityBase {
    public final String name = "Expanding Force";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "expanding_force");
    public final int requiredLv = 12;
    public final int cost = 25;
    public final int cooldown = 200;
    public final AbilityType type = AbilityType.CAST;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        Box box = new Box(player.getPos().subtract(10, 10, 10), player.getPos().add(10, 10, 10));
        for (Entity target : world.getOtherEntities(player, box)) {
            if (target.distanceTo(player) < 4f) {
                if (target instanceof PlayerEntity targetPlayer) {
                    if (!TeamUtils.canDamagePlayer(player.getServer(), player, targetPlayer)) return false;
                }
                Vec3d diff = target.getPos().subtract(player.getPos());
                float dist = (float) diff.length();
                Vec3d push = diff.normalize().multiply(2.5f * Math.log(-dist + 5));
                target.addVelocity(push);
                target.velocityModified = true;
            }
        }
        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoulForgeSounds.UT_REFLECT_EVENT, SoundCategory.PLAYERS, 1f, 1f);
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
        return new ExpandingForce();
    }
}

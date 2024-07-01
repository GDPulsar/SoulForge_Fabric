package com.pulsar.soulforge.ability.pures;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class MartyrsTouch extends AbilityBase {
    public List<PlayerEntity> players = null;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        if (players == null) {
            players = new ArrayList<>();
        }
        EntityHitResult hit = Utils.getFocussedEntity(player, 32f);
        if (hit != null) {
            Entity entity = hit.getEntity();
            if (entity instanceof PlayerEntity target) {
                if (!TeamUtils.canHealPlayer(player.getServer(), player, target)) return false;
                boolean removed = false;
                for (PlayerEntity selected : players) {
                    if (selected.getName() == target.getName()) {
                        players.remove(selected);
                        removed = true;
                        break;
                    }
                }
                if (removed) {
                    player.sendMessageToClient(Text.literal("You have deselected ").append(target.getName()).formatted(Formatting.GREEN), true);
                } else {
                    if (players.size() >= 5) {
                        player.sendMessageToClient(Text.literal("You can only select five players!").formatted(Formatting.RED), true);
                    } else {
                        players.add(target);
                        player.sendMessageToClient(Text.literal("You have selected ").append(target.getName()).formatted(Formatting.GREEN), true);
                        return super.cast(player);
                    }
                }
            } else {
                player.sendMessageToClient(Text.literal("You may only select players!").formatted(Formatting.RED), true);
            }
        }
        return false;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        if (players != null) {
            for (PlayerEntity target : List.copyOf(players)) {
                if (target != null) {
                    if (target.distanceTo(player) >= 300f) {
                        players.remove(target);
                    }
                }
            }
            return players.isEmpty();
        }
        return false;
    }

    @Override
    public void displayTick(PlayerEntity player) {
        if (players != null) {
            ServerWorld serverWorld = ((ServerPlayerEntity) player).getServerWorld();
            for (PlayerEntity target : players) {
                serverWorld.spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0x00FF00).toVector3f(), 1f), target.getX(), target.getY() + 1f, target.getZ(), 5, 0.4f, 1f, 0.4f, 0);
            }
        }
    }

    public int getLV() { return 15; }
    public int getCost() { return 40; }
    public int getCooldown() { return 0; }
    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new MartyrsTouch();
    }
}

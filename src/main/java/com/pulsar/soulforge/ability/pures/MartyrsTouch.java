package com.pulsar.soulforge.ability.pures;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class MartyrsTouch extends AbilityBase {
    public final String name = "Martyr's Touch";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "martyrs_touch");
    public final int requiredLv = 15;
    public final int cost = 40;
    public final int cooldown = 0;
    public final AbilityType type = AbilityType.CAST;

    public List<PlayerEntity> players = null;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        if (players == null) {
            players = new ArrayList<>();
        }
        EntityHitResult hit = Utils.getFocussedEntity(player, 32f);
        if (hit != null) {
            if (hit.getEntity() instanceof PlayerEntity target) {
                if (!TeamUtils.canHealPlayer(player.getServer(), player, target)) return false;
                if (player.isSneaking()) {
                    for (PlayerEntity selected : players) {
                        if (selected.getName() == target.getName()) {
                            players.remove(selected);
                            player.sendMessageToClient(Text.literal("You have deselected ").append(target.getName()).formatted(Formatting.GREEN), true);
                            return false;
                        }
                    }
                } else {
                    if (players.size() >= 5) {
                        player.sendMessageToClient(Text.literal("You can only select five players!").formatted(Formatting.RED), true);
                    } else if (players.contains(target)) {
                        players.add(target);
                        player.sendMessageToClient(Text.literal("You have selected ").append(target.getName()).formatted(Formatting.GREEN), true);
                        return true;
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
    public boolean end(ServerPlayerEntity player) {
        return true;
    }

    @Override
    public void displayTick(ClientPlayerEntity player) {
        if (players != null) {
            for (PlayerEntity target : players) {
                player.getWorld().addParticle(new DustParticleEffect(Vec3d.unpackRgb(0x00FF00).toVector3f(), 1f),
                        target.getX() + Math.random()*0.8f - 0.4f, target.getY() + Math.random()*2, target.getZ() + Math.random()*0.8f - 0.4f, 0f, 0f, 0f);
            }
        }
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
        return new MartyrsTouch();
    }
}

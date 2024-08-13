package com.pulsar.soulforge.ability.kindness;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.EntityHitResult;

import java.util.ArrayList;
import java.util.List;

public class Overclock extends AbilityBase {
    public List<PlayerEntity> players = null;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (playerSoul.getStyleRank() < 3) {
            player.sendMessageToClient(Text.translatable(Math.random() < 0.01f ? "soulforge.style.get_real" : "soulforge.style.not_enough"), true);
            return false;
        }
        if (players == null) {
            players = new ArrayList<>();
        }
        EntityHitResult hit = Utils.getFocussedEntity(player, 32f);
        if (hit != null) {
            Entity entity = hit.getEntity();
            if (entity instanceof PlayerEntity target) {
                if (!TeamUtils.canHealEntity(player.getServer(), player, target)) return false;
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
                    if (players.size() == 4) {
                        player.sendMessageToClient(Text.literal("You can only select four players!").formatted(Formatting.RED), true);
                    } else {
                        players.add(target);
                        player.sendMessageToClient(Text.literal("You have selected ").append(target.getName()).formatted(Formatting.GREEN), true);
                    }
                }
            } else {
                player.sendMessageToClient(Text.literal("You may only select players!").formatted(Formatting.RED), true);
            }
        } else {
            if (player.isSneaking()) {
                if (playerSoul.getMagic() >= 100f) {
                    for (PlayerEntity target : players) {
                        target.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 600, 4-players.size()));
                        target.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 600, 4-players.size()));
                    }
                    player.sendMessageToClient(Text.literal("OVERCLOCK ACTIVE").formatted(Formatting.GREEN, Formatting.BOLD), true);
                    playerSoul.setMagic(0f);
                    player.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.MANA_OVERLOAD, 1200, 1));
                    return super.cast(player);
                }
            } else {
                player.sendMessageToClient(Text.literal("You must be sneaking to activate overclock!").formatted(Formatting.RED), true);
            }
        }
        return false;
    }

    public int getLV() { return 20; }

    public int getCost() { return 0; }

    public int getCooldown() { return 8400; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new Overclock();
    }
}

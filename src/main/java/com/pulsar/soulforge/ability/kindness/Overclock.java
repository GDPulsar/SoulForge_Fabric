package com.pulsar.soulforge.ability.kindness;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.ArrayList;
import java.util.List;

public class Overclock extends AbilityBase {
    public final String name = "Overclock";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "overclock");
    public final int requiredLv = 20;
    public final int cost = 0;
    public final int cooldown = 8400;
    public final AbilityType type = AbilityType.CAST;

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
                SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                if (playerSoul.getMagic() >= 100f) {
                    for (PlayerEntity target : players) {
                        target.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 600, 4-players.size()));
                        target.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 600, 4-players.size()));
                    }
                    player.sendMessageToClient(Text.literal("OVERCLOCK ACTIVE").formatted(Formatting.GREEN, Formatting.BOLD), true);
                    playerSoul.setMagic(0f);
                    player.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.MANA_OVERLOAD, 2400, 1));
                    return true;
                }
            } else {
                player.sendMessageToClient(Text.literal("You must be sneaking to activate overclock!").formatted(Formatting.RED), true);
            }
        }
        return false;
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
        return new Overclock();
    }
}

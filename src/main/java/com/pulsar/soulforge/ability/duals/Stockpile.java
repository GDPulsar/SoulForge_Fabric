package com.pulsar.soulforge.ability.duals;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;

public class Stockpile extends AbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (playerSoul.getStyleRank() < 2) {
            player.sendMessageToClient(Text.translatable(Math.random() < 0.01f ? "soulforge.style.get_real" : "soulforge.style.not_enough"), true);
            return false;
        }
        EntityHitResult hit = Utils.getFocussedEntity(player, 3f);
        if (hit != null) {
            if (hit.getEntity() instanceof LivingEntity target) {
                if (target instanceof PlayerEntity targetPlayer) {
                    if (!TeamUtils.canDamagePlayer(player.getServer(), player, targetPlayer)) return false;
                }
                if (!playerSoul.hasValue("stockpiles")) playerSoul.setValue("stockpiles", 0);
                playerSoul.setValue("stockpiles", playerSoul.getValue("stockpiles")+1);
                playerSoul.setValue("stockpileTimer", 2400);
                playerSoul.setStyleRank(playerSoul.getStyleRank() - 2);
                target.damage(SoulForgeDamageTypes.of(player, SoulForgeDamageTypes.STOCKPILE_DAMAGE_TYPE), 3f);
                return super.cast(player);
            }
        }
        return false;
    }

    public int getLV() { return 20; }

    public int getCost() { return 0; }

    public int getCooldown() { return 0; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new Stockpile();
    }
}

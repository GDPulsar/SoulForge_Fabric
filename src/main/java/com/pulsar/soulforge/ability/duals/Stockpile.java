package com.pulsar.soulforge.ability.duals;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.components.ValueComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
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
                if (!TeamUtils.canDamageEntity(player.getServer(), player, target)) return false;
                ValueComponent values = SoulForge.getValues(player);
                if (values != null) {
                    if (!values.hasInt("stockpiles")) {
                        values.setInt("stockpiles", 1);
                        values.setTimer("stockpileTimer", 2400);
                    } else {
                        if (values.getTimer("stockpileTimer") != 0) {
                            values.setInt("stockpiles", values.getInt("stockpiles") + 1);
                            values.setTimer("stockpileTimer", 2400);
                        } else {
                            values.setInt("stockpiles", 1);
                            values.setTimer("stockpileTimer", 2400);
                        }
                    }
                }
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

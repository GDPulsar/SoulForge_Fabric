package com.pulsar.soulforge.ability.duals;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;

public class Stockpile extends AbilityBase {
    public final String name = "Stockpile";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "stockpile");
    public final int requiredLv = 20;
    public final int cost = 0;
    public final int cooldown = 860;
    public final AbilityType type = AbilityType.CAST;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        EntityHitResult hit = Utils.getFocussedEntity(player, 3f);
        if (hit != null) {
            if (hit.getEntity() instanceof LivingEntity target) {
                if (target instanceof PlayerEntity targetPlayer) {
                    if (!TeamUtils.canDamagePlayer(player.getServer(), player, targetPlayer)) return false;
                }
                SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                if (!playerSoul.hasValue("stockpiles")) playerSoul.setValue("stockpiles", 0);
                playerSoul.setValue("stockpiles", playerSoul.getValue("stockpiles")+1);
                playerSoul.setValue("stockpileTimer", 2400);
                target.damage(player.getDamageSources().playerAttack(player), 3f);
                return true;
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
        return new Stockpile();
    }
}

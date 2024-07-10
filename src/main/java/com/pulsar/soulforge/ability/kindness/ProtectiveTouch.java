package com.pulsar.soulforge.ability.kindness;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.EntityHitResult;

public class ProtectiveTouch extends AbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        EntityHitResult hit = Utils.getFocussedEntity(player, 10);
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (hit != null && hit.getEntity() instanceof LivingEntity target) {
            if (target.isPlayer()) {
                if (target instanceof PlayerEntity targetPlayer) {
                    if (!TeamUtils.canHealPlayer(player.getServer(), player, targetPlayer)) return false;
                }
                float newAbsorptionAmount = Math.max(target.getAbsorptionAmount(), (float)(playerSoul.getEffectiveLV()));
                float absorptionIncrease = newAbsorptionAmount - target.getAbsorptionAmount();
                target.setAbsorptionAmount(newAbsorptionAmount);
                playerSoul.setStyle(playerSoul.getStyle() + (int)absorptionIncrease);
                player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoulForgeSounds.UT_HEAL_EVENT, SoundCategory.PLAYERS, 1f, 1f);
                return super.cast(player);
            }
        }
        return false;
    }

    public int getLV() { return 17; }

    public int getCost() { return 75; }

    public int getCooldown() { return 600; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new ProtectiveTouch();
    }
}

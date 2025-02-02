package com.pulsar.soulforge.ability.kindness;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.tag.SoulForgeTags;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.EntityHitResult;

public class Immobilization extends ToggleableAbilityBase {
    private LivingEntity target = null;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        if (!getActive()) {
            if (target != null) {
                target.removeStatusEffect(SoulForgeEffects.IMMOBILIZED);
                target = null;
                setActive(false);
                return true;
            }
            EntityHitResult result = Utils.getFocussedEntity(player, 16);
            if (result != null) {
                SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                if (result.getEntity() instanceof LivingEntity living) {
                    if (living.getType().isIn(SoulForgeTags.BOSS_ENTITY)) return false;
                    target = living;
                    target.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.IMMOBILIZED, -1, playerSoul.getEffectiveLV() * 5 - 1));
                    player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoulForgeSounds.UT_REFLECT_EVENT, SoundCategory.PLAYERS, 1f, 1f);
                    return super.cast(player);
                }
            }
        } else {
            setActive(false);
        }
        return false;
    }

    public int getLV() { return 7; }

    public int getCost() { return 30; }

    public int getCooldown() { return 1200; }

    @Override
    public AbilityBase getInstance() {
        return new Immobilization();
    }
}

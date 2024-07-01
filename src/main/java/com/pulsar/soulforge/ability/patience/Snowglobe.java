package com.pulsar.soulforge.ability.patience;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.trait.Traits;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

public class Snowglobe extends ToggleableAbilityBase {
    public int timer = 0;

    @Override
    public boolean tick(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        boolean frostburn = playerSoul.getTraits().contains(Traits.bravery) && playerSoul.getTraits().contains(Traits.patience);
        timer++;
        if (timer >= 20) {
            if (playerSoul.getMagic() >= 5f) {
                playerSoul.setMagic(playerSoul.getMagic() - 5f);
                playerSoul.resetLastCastTime();
            } else {
                setActive(false);
                return super.tick(player);
            }
            timer = 0;
            if (isActive()) {
                Utils.removeModifier(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "snowglobe_speed");
                Utils.removeModifier(player, SoulForgeAttributes.AIR_SPEED, "snowglobe_air_speed");
                Utils.addModifier(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "snowglobe_speed", -0.85f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                Utils.addModifier(player, SoulForgeAttributes.AIR_SPEED, "snowglobe_air_speed", -0.85f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                for (Entity entity : player.getEntityWorld().getOtherEntities(player, Box.of(player.getPos(), 8, 8, 8))) {
                    if (entity instanceof LivingEntity living) {
                        if (living.distanceTo(player) > 4f) continue;
                        if (living instanceof PlayerEntity targetPlayer) {
                            if (!TeamUtils.canDamagePlayer(player.getServer(), player, targetPlayer)) continue;
                        }
                        living.addStatusEffect(new StatusEffectInstance(frostburn ? SoulForgeEffects.FROSTBURN : SoulForgeEffects.FROSTBITE, 20 * playerSoul.getEffectiveLV(), 0));
                        living.damage(player.getDamageSources().freeze(), playerSoul.getEffectiveLV() / 6f);
                        if (playerSoul.getLV() >= 10) {
                            living.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.VULNERABILITY, 140, MathHelper.ceil(playerSoul.getEffectiveLV() / 5f) - 1));
                            living.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 140, MathHelper.ceil(playerSoul.getEffectiveLV() / 5f) - 1));
                        }
                    }
                }
            }
        }
        return super.tick(player);
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        Utils.removeModifier(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "snowglobe");
        Utils.removeModifier(player, SoulForgeAttributes.AIR_SPEED, "snowglobe");
        return super.end(player);
    }

    public int getLV() { return 3; }
    public int getCost() { return 30; }
    public int getCooldown() { return 300; }
    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new Snowglobe();
    }
}

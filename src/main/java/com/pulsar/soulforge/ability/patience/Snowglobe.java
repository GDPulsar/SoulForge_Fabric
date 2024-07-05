package com.pulsar.soulforge.ability.patience;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
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
                return true;
            }
            timer = 0;
            if (getActive()) {
                Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "snowglobe");
                Utils.clearModifiersByName(player, SoulForgeAttributes.AIR_SPEED_BECAUSE_MOJANG_SUCKS, "snowglobe");
                player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).addPersistentModifier(new EntityAttributeModifier("snowglobe", -0.85f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
                player.getAttributeInstance(SoulForgeAttributes.AIR_SPEED_BECAUSE_MOJANG_SUCKS).addPersistentModifier(new EntityAttributeModifier("snowglobe", -0.85f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
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
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "snowglobe");
        Utils.clearModifiersByName(player, SoulForgeAttributes.AIR_SPEED_BECAUSE_MOJANG_SUCKS, "snowglobe");
        return super.cast(player);
    }

    public int getLV() { return 3; }

    public int getCost() { return 30; }

    public int getCooldown() { return 300; }

    @Override
    public AbilityBase getInstance() {
        return new Snowglobe();
    }
}

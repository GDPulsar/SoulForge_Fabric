package com.pulsar.soulforge.ai;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.components.ValueComponent;
import com.pulsar.soulforge.tag.SoulForgeTags;
import com.pulsar.soulforge.trait.Traits;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class HATEBaseAI {
    /**
     * HATE Base AI
     * Inputs:
     * - Health Percentage
     * - Armor Points
     * - Armor Toughness Points
     * - Mana
     * - Inverted (1 or 0)
     * - Magic Gauge Percent
     * - Hate Percent
     * - Nearby Enemy Count
     * - Nearby Neutral Count
     * - Nearby Ally Count
     * - Nearest Enemy Distance
     * - Nearest Enemy Health Percent
     * - Nearest Enemy Survivability
     * - Nearest Neutral Distance
     * - Nearest Neutral Health Percent
     * - Nearest Neutral Survivability
     * - Nearest Ally Distance
     * - Nearest Ally Health Percent
     * - Nearest Ally Survivability
     * Outputs:
     * - Attempt to attack an enemy
     * - Attempt to defend an ally
     * - Attempt to escape
     */
    public static NeuralNetwork network = new NeuralNetwork(new int[] {
            19,
            24, 24, 24,
            3
    });

    public enum AiResult {
        ATTACK,
        DEFEND,
        ESCAPE
    }

    public static float calculateSurvivability(LivingEntity living) {
        float value = living.getMaxHealth() + living.getHealth() + living.getArmor();
        value += MathHelper.floor(living.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS));
        if (living instanceof PlayerEntity player) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            if (playerSoul.hasTrait(Traits.bravery)) value *= 1.2f;
            if (playerSoul.hasTrait(Traits.justice)) value *= 1.2f;
            if (playerSoul.hasTrait(Traits.kindness)) value *= 1.5f;
            if (playerSoul.hasTrait(Traits.patience)) value *= 1.1f;
            if (playerSoul.hasTrait(Traits.integrity)) value *= 1.35f;
            if (playerSoul.hasTrait(Traits.perseverance)) value *= 1.5f;
            if (playerSoul.hasTrait(Traits.determination)) value *= 2f;
            if (Utils.isInverted(playerSoul)) value *= 1.5f;
        }
        return value;
    }

    public static double[] getInputsFromEntity(LivingEntity entity) {
        int nearbyEnemies = 0;
        int nearbyNeutrals = 0;
        int nearbyAllies = 0;
        LivingEntity nearestEnemy = null;
        LivingEntity nearestNeutral = null;
        LivingEntity nearestAlly = null;
        List<LivingEntity> nearby = entity.getWorld().getEntitiesByClass(LivingEntity.class, Box.of(entity.getPos(), 30, 30, 30), (target) -> true);
        for (LivingEntity living : nearby) {
            ValueComponent values = SoulForge.getValues(living);
            SoulComponent playerSoul = living instanceof PlayerEntity targetPlayer ? SoulForge.getPlayerSoul(targetPlayer) : null;
            if (living.getType().isIn(SoulForgeTags.HATE_ALLY) || values.getBool("HatePossessed")) {
                nearbyAllies++;
                if (nearestAlly == null) nearestAlly = living;
                else if (living.distanceTo(entity) < nearestAlly.distanceTo(entity)) nearestAlly = living;
            } else if (playerSoul != null && Utils.isInverted(playerSoul)) {
                nearbyNeutrals++;
                if (nearestNeutral == null) nearestNeutral = living;
                else if (living.distanceTo(entity) < nearestNeutral.distanceTo(entity)) nearestNeutral = living;
            } else {
                nearbyEnemies++;
                if (nearestEnemy == null) nearestEnemy = living;
                else if (living.distanceTo(entity) < nearestEnemy.distanceTo(entity)) nearestEnemy = living;
            }
        }
        double magic = 0;
        int inverted = 0;
        double magicGauge = 0;
        if (entity instanceof PlayerEntity player) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            magic = playerSoul.getMagic();
            inverted = Utils.isInverted(playerSoul) ? 1 : 0;
            magicGauge = playerSoul.getMagicGauge();
        }
        double[] inputs = new double[] {
                entity.getHealth() / entity.getMaxHealth(),
                entity.getArmor(),
                MathHelper.floor(entity.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)),
                magic,
                inverted,
                magicGauge,
                Utils.getHate(entity),
                nearbyEnemies,
                nearbyNeutrals,
                nearbyAllies,
                nearestEnemy != null ? nearestEnemy.distanceTo(entity) : 0,
                nearestEnemy != null ? nearestEnemy.getHealth() / nearestEnemy.getMaxHealth() : 0,
                nearestEnemy != null ? calculateSurvivability(nearestEnemy) : 0,
                nearestNeutral != null ? nearestNeutral.distanceTo(entity) : 0,
                nearestNeutral != null ? nearestNeutral.getHealth() / nearestNeutral.getMaxHealth() : 0,
                nearestNeutral != null ? calculateSurvivability(nearestNeutral) : 0,
                nearestAlly != null ? nearestAlly.distanceTo(entity) : 0,
                nearestAlly != null ? nearestAlly.getHealth() / nearestAlly.getMaxHealth() : 0,
                nearestAlly != null ? calculateSurvivability(nearestAlly) : 0,
        };
        return inputs;
    }

    public static double[] getOutputs(LivingEntity entity) {
        double[] inputs = getInputsFromEntity(entity);
        return network.feedforward(inputs);
    }

    public static AiResult getResult(double[] outputs) {
        int highestIndex = 0;
        for (int i = 0; i < outputs.length; i++) {
            if (outputs[i] > outputs[highestIndex]) highestIndex = i;
        }
        if (highestIndex == 0) return AiResult.ATTACK;
        if (highestIndex == 1) return AiResult.DEFEND;
        if (highestIndex == 2) return AiResult.ESCAPE;
        return AiResult.ATTACK;
    }

    public static AiResult getResult(PlayerEntity player) {
        double[] outputs = getOutputs(player);
        int highestIndex = 0;
        for (int i = 0; i < outputs.length; i++) {
            if (outputs[i] > outputs[highestIndex]) highestIndex = i;
        }
        if (highestIndex == 0) return AiResult.ATTACK;
        if (highestIndex == 1) return AiResult.DEFEND;
        if (highestIndex == 2) return AiResult.ESCAPE;
        return AiResult.ATTACK;
    }

    public static void train(LivingEntity entity, double[] expected) {
        double[] inputs = getInputsFromEntity(entity);
        if (expected.length != 3) return;
        network.backpropagate(inputs, expected, 0.1);
    }
}

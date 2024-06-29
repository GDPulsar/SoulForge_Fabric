package com.pulsar.soulforge.advancement;

import com.pulsar.soulforge.SoulForge;
import net.minecraft.advancement.criterion.Criteria;

public class SoulForgeCriterions {
    public static PlayerLVCriterion PLAYER_LV;
    public static PlayerTraitCriterion PLAYER_TRAIT;
    public static CastAbilityCriterion CAST_ABILITY;
    public static MonsterSoulCriterion MONSTER_SOUL;

    public static void registerCriterions() {
        PLAYER_LV = Criteria.register(new PlayerLVCriterion());
        PLAYER_TRAIT = Criteria.register(new PlayerTraitCriterion());
        CAST_ABILITY = Criteria.register(new CastAbilityCriterion());
        MONSTER_SOUL = Criteria.register(new MonsterSoulCriterion());
    }
}

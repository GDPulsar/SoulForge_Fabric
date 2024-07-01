package com.pulsar.soulforge.advancement;

import net.minecraft.advancement.criterion.Criteria;

public class SoulForgeCriterions {
    public static PlayerLVCriterion PLAYER_LV;
    public static PlayerTraitCriterion PLAYER_TRAIT;
    public static CastAbilityCriterion CAST_ABILITY;
    public static MonsterSoulCriterion MONSTER_SOUL;

    public static void registerCriterions() {
        PLAYER_LV = Criteria.register("soulforge:player_lv", new PlayerLVCriterion());
        PLAYER_TRAIT = Criteria.register("soulforge:player_trait", new PlayerTraitCriterion());
        CAST_ABILITY = Criteria.register("soulforge:cast_ability", new CastAbilityCriterion());
        MONSTER_SOUL = Criteria.register("soulforge:monster_soul", new MonsterSoulCriterion());
    }
}

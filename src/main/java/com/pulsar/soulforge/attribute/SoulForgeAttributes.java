package com.pulsar.soulforge.attribute;

import com.pulsar.soulforge.SoulForge;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SoulForgeAttributes {
    public static final EntityAttribute MAGIC_COOLDOWN = make("magic_cooldown", 1.0, 0, 1);
    public static final EntityAttribute MAGIC_POWER = make("magic_power", 1.0, 0, 1024);
    public static final EntityAttribute MAGIC_COST = make("magic_cost", 1.0, 0, 1024);
    public static final EntityAttribute AIR_SPEED_BECAUSE_MOJANG_SUCKS = make("air_speed", 1.0, 0, 1024);
    public static final EntityAttribute DAMAGE_REDUCTION = make("damage_reduction", 1.0, 0, 1024);

    private static EntityAttribute make(final String name, final double base, final double min, final double max) {
        return new ClampedEntityAttribute("attribute.name.generic." + SoulForge.MOD_ID + '.' + name, base, min, max).setTracked(true);
    }

    public static void register() {
        Registry.register(Registries.ATTRIBUTE, new Identifier(SoulForge.MOD_ID, "magic_cooldown"), MAGIC_COOLDOWN);
        Registry.register(Registries.ATTRIBUTE, new Identifier(SoulForge.MOD_ID, "magic_power"), MAGIC_POWER);
        Registry.register(Registries.ATTRIBUTE, new Identifier(SoulForge.MOD_ID, "magic_cost"), MAGIC_COST);
        Registry.register(Registries.ATTRIBUTE, new Identifier(SoulForge.MOD_ID, "air_speed"), AIR_SPEED_BECAUSE_MOJANG_SUCKS);
        Registry.register(Registries.ATTRIBUTE, new Identifier(SoulForge.MOD_ID, "damage_reduction"), DAMAGE_REDUCTION);
    }
}

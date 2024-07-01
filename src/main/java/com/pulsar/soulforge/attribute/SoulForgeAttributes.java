package com.pulsar.soulforge.attribute;

import com.pulsar.soulforge.SoulForge;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class SoulForgeAttributes {

    public static RegistryEntry<EntityAttribute> MAGIC_COOLDOWN;
    public static RegistryEntry<EntityAttribute> MAGIC_POWER;
    public static RegistryEntry<EntityAttribute> MAGIC_COST;
    public static RegistryEntry<EntityAttribute> AIR_SPEED;

    private static EntityAttribute make(final String name, final double base, final double min, final double max) {
        return new ClampedEntityAttribute("attribute.name.generic." + SoulForge.MOD_ID + '.' + name, base, min, max).setTracked(true);
    }

    public static void register() {
        MAGIC_COOLDOWN = Registry.registerReference(Registries.ATTRIBUTE, Identifier.of(SoulForge.MOD_ID, "magic_cooldown"), make("magic_cooldown", 1.0, 0, 1));
        MAGIC_POWER = Registry.registerReference(Registries.ATTRIBUTE, Identifier.of(SoulForge.MOD_ID, "magic_power"), make("magic_power", 1.0, 0, 1024));
        MAGIC_COST = Registry.registerReference(Registries.ATTRIBUTE, Identifier.of(SoulForge.MOD_ID, "magic_cost"), make("magic_cost", 1.0, 0, 1024));
        AIR_SPEED = Registry.registerReference(Registries.ATTRIBUTE, Identifier.of(SoulForge.MOD_ID, "air_speed"), make("air_speed", 1.0, 0, 1024));
    }
}

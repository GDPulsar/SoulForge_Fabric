package com.pulsar.soulforge.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.loader.api.FabricLoader;

public class ConfigHelper {
    private static boolean initalized = false;
    public static void register() {
        if (FabricLoader.getInstance().isModLoaded("cloth-config")) {
            AutoConfig.register(SoulForgeConfig.class, GsonConfigSerializer::new);
            initalized = true;
        }
    }

    public static SoulForgeConfig getConfig() {
        return AutoConfig.getConfigHolder(SoulForgeConfig.class).getConfig();
    }

    public static MagicBarLocation getMagicBarLocation() {
        return initalized ? getConfig().magicBarLocation : MagicBarLocation.BOTTOM_LEFT;
    }

    public static boolean getShowValues() {
        return !initalized || getConfig().showValues;
    }

    public static boolean getDebugResetPercentages() {
        return initalized && getConfig().debugResetPercentages;
    }

    public static boolean getMagicBarHotkeying() {
        return !initalized || getConfig().magicBarHotkeying;
    }

    public static boolean getSplitHotbars() {
        return !initalized || getConfig().splitHotbars;
    }

    public enum MagicBarLocation {
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        TOP_LEFT,
        TOP_RIGHT
    }
}

package com.pulsar.soulforge.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "soulforge")
public class SoulForgeConfig implements ConfigData {
    ConfigHelper.MagicBarLocation magicBarLocation = ConfigHelper.MagicBarLocation.BOTTOM_LEFT;
    boolean showValues = true;
    boolean debugResetPercentages = false;
    boolean magicBarHotkeying = true;
    boolean splitHotbars = false;
}

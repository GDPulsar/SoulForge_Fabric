package com.pulsar.soulforge.config;

import com.mojang.serialization.Codec;
import com.terraformersmc.modmenu.config.option.ConfigOptionStorage;
import com.terraformersmc.modmenu.config.option.OptionConvertable;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;

public class SoulForgeConfig {
    public static final SimpleOption<MagicBarLocation> MAGIC_BAR_LOCATION = new SimpleOption<>("option.modmenu.magic_bar_location",
            SimpleOption.emptyTooltip(), (text, value) -> Text.translatable("option.modmenu.magic_bar_location." + value),
            new SimpleOption.PotentialValuesBasedCallbacks<>(Arrays.asList(MagicBarLocation.class.getEnumConstants()),
                    Codec.STRING.xmap(
                            string -> Arrays.stream(MagicBarLocation.class.getEnumConstants()).filter(e -> e.name().toLowerCase().equals(string)).findAny().orElse(null),
                            newValue -> newValue.name().toLowerCase()
                    )),
            MagicBarLocation.BOTTOM_LEFT, value -> {
                if (FabricLoader.getInstance().isModLoaded("modmenu")) {
                    ConfigOptionStorage.setEnum("magic_bar_location", value);
                }
            }
    );
    //public static final BooleanConfigOption DEBUG_RESET_PERCENTAGES = new BooleanConfigOption("debug_reset_percentages", false);

    public static SimpleOption<?>[] asOptions() {
        ArrayList<SimpleOption<?>> options = new ArrayList<>();
        for (Field field : SoulForgeConfig.class.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()) &&
                    OptionConvertable.class.isAssignableFrom(field.getType())) {
                try {
                    options.add(((OptionConvertable)field.get(null)).asOption());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return options.stream().toArray(SimpleOption[]::new);
    }

    public enum MagicBarLocation {
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        TOP_LEFT,
        TOP_RIGHT
    }
}

package com.pulsar.soulforge.compat.modmenu;

import com.pulsar.soulforge.config.SoulForgeConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.loader.api.FabricLoader;

public class SoulForgeModMenuImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (!FabricLoader.getInstance().isModLoaded("cloth-config")) {
            return null;
        }
        return parent -> AutoConfig.getConfigScreen(SoulForgeConfig.class, parent).get();
    }
}

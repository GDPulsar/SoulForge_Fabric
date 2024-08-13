package com.pulsar.soulforge.compat.rei;

import com.pulsar.soulforge.block.SoulForgeBlocks;
import com.pulsar.soulforge.client.ui.SoulForgeScreen;
import com.pulsar.soulforge.recipe.SiphonRecipe;
import com.pulsar.soulforge.recipe.SoulForgeRecipe;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.block.Blocks;

public class SoulForgeREIClientPlugin implements REIClientPlugin {
    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new SoulForgeCategory());
        registry.add(new SiphonCategory());

        registry.addWorkstations(SoulForgeCategory.SOUL_FORGE, EntryStacks.of(SoulForgeBlocks.SOUL_FORGE_BLOCK));
        registry.addWorkstations(SiphonCategory.SIPHONING, EntryStacks.of(Blocks.SMITHING_TABLE));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(SoulForgeRecipe.class, SoulForgeRecipe.Type.INSTANCE, SoulForgeDisplay::new);
        registry.registerRecipeFiller(SiphonRecipe.class, SiphonRecipe.Type.INSTANCE, SiphonDisplay::new);
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerClickArea(screen -> new Rectangle(93, 36, 13, 13), SoulForgeScreen.class, SoulForgeCategory.SOUL_FORGE);
    }
}

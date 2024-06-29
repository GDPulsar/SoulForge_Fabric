package com.pulsar.soulforge.util;

import com.pulsar.soulforge.item.SoulForgeItems;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;

public class SoulForgeCustomTrades {
    public static void register() {
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.TOOLSMITH, 5, factories -> {
            factories.add(((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, 45),
                    new ItemStack(SoulForgeItems.SIPHON_TEMPLATE, 1),
                    1, 5, 0.1f
            )));
        });
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.WEAPONSMITH, 5, factories -> {
            factories.add(((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, 45),
                    new ItemStack(SoulForgeItems.SIPHON_TEMPLATE, 1),
                    1, 5, 0.1f
            )));
        });
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.ARMORER, 5, factories -> {
            factories.add(((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, 45),
                    new ItemStack(SoulForgeItems.SIPHON_TEMPLATE, 1),
                    1, 5, 0.1f
            )));
        });
    }
}

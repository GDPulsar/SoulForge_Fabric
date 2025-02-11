package com.pulsar.soulforge.item.devices.trinkets;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.item.SoulJarItem;
import com.pulsar.soulforge.trait.TraitBase;
import com.pulsar.soulforge.trait.Traits;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TraitTunerItem extends Item  {
    public TraitTunerItem() {
        super(new FabricItemSettings().maxCount(1));
    }

    public static Map<TraitBase, String> traitKeys = Map.of(
            Traits.bravery, "Bravery",
            Traits.justice, "Justice",
            Traits.kindness, "Kindness",
            Traits.patience, "Patience",
            Traits.integrity, "Integrity",
            Traits.perseverance, "Perseverance"
    );

    public static Map<TraitBase, Item> traitItems = Map.of(
            Traits.bravery, SoulForgeItems.BRAVERY_ESSENCE,
            Traits.justice, SoulForgeItems.JUSTICE_ESSENCE,
            Traits.kindness, SoulForgeItems.KINDNESS_ESSENCE,
            Traits.patience, SoulForgeItems.PATIENCE_ESSENCE,
            Traits.integrity, SoulForgeItems.INTEGRITY_ESSENCE,
            Traits.perseverance, SoulForgeItems.PERSEVERANCE_ESSENCE
    );

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("soulforge.trait_tuner.description").setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
        int braveryChance = (int)(100f * getAmount(stack, "Bravery") / 15f);
        int justiceChance = (int)(100f * getAmount(stack, "Justice") / 15f);
        int kindnessChance = (int)(100f * getAmount(stack, "Kindness") / 15f);
        int patienceChance = (int)(100f * getAmount(stack, "Patience") / 15f);
        int integrityChance = (int)(100f * getAmount(stack, "Integrity") / 15f);
        int perseveranceChance = (int)(100f * getAmount(stack, "Perseverance") / 15f);
        int determinationChance = (int)(100f * getDTChance(stack));
        MutableText chances1 = Text.empty();
        MutableText chances2 = Text.empty();
        chances1.append(Text.literal(String.format("%d%%   ", braveryChance)).setStyle(Style.EMPTY.withColor(Traits.bravery.getColor())));
        chances1.append(Text.literal(String.format("%d%%   ", justiceChance)).setStyle(Style.EMPTY.withColor(Traits.justice.getColor())));
        chances1.append(Text.literal(String.format("%d%%", kindnessChance)).setStyle(Style.EMPTY.withColor(Traits.kindness.getColor())));
        chances2.append(Text.literal(String.format("%d%%   ", patienceChance)).setStyle(Style.EMPTY.withColor(Traits.patience.getColor())));
        chances2.append(Text.literal(String.format("%d%%   ", integrityChance)).setStyle(Style.EMPTY.withColor(Traits.integrity.getColor())));
        chances2.append(Text.literal(String.format("%d%%", perseveranceChance)).setStyle(Style.EMPTY.withColor(Traits.perseverance.getColor())));
        tooltip.add(chances1);
        tooltip.add(chances2);
        if (determinationChance != 0) tooltip.add(Text.literal(String.format("%d%%", determinationChance)).setStyle(Style.EMPTY.withColor(Traits.determination.getColor())));
        super.appendTooltip(stack, world, tooltip, context);
    }

    public float getDTChance(ItemStack stack) {
        int minimum = 100;
        int maximum = 0;
        int total = 0;
        for (String key : traitKeys.values()) {
            int value = getAmount(stack, key);
            minimum = Math.min(minimum, value);
            maximum = Math.max(maximum, value);
            total += value;
        }
        if (Math.abs(maximum - minimum) <= 4) {
            return total / 60f;
        }
        return 0f;
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        DefaultedList<ItemStack> items = DefaultedList.of();
        for (TraitBase trait : traitKeys.keySet()) {
            String key = traitKeys.get(trait);
            Item item = traitItems.get(trait);
            if (getAmount(stack, key) > 0) items.add(new ItemStack(item, getAmount(stack, key)));
        }
        return Optional.of(new BundleTooltipData(items, items.size()));
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (clickType == ClickType.RIGHT) {
            String traitKey = null;
            for (TraitBase trait : traitKeys.keySet()) {
                String key = traitKeys.get(trait);
                Item item = traitItems.get(trait);
                if (otherStack.isOf(item)) traitKey = key;
            }
            if (traitKey != null) {
                otherStack.decrement(1);
                incrementAmount(stack, traitKey, 1);
                return true;
            }
        }
        return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            ItemStack stack = user.getStackInHand(hand);
            if (hand == Hand.MAIN_HAND && user.getStackInHand(Hand.OFF_HAND).isOf(SoulForgeItems.SOUL_JAR)) {
                ItemStack jar = user.getStackInHand(Hand.OFF_HAND);
                if (!SoulJarItem.getHasSoul(jar)) {
                    SoulJarItem.setFromPlayer(jar, user);
                }
            }
            SoulComponent playerSoul = SoulForge.getPlayerSoul(user);
            playerSoul.softReset();
            playerSoul.setStrong(false);
            playerSoul.setPure(false);
            Random random = new Random();
            float determinationChance = getDTChance(stack);
            if (random.nextFloat() <= determinationChance) { // do determination check first
                playerSoul.setTraits(List.of(Traits.determination));
                user.setStackInHand(hand, ItemStack.EMPTY);
                SoulForge.LOGGER.info("guaranteed dt");
                return super.use(world, user, hand);
            }
            List<TraitBase> pures = new ArrayList<>();
            for (Map.Entry<TraitBase, String> traitEntry : traitKeys.entrySet()) {
                if (getAmount(stack, traitEntry.getValue()) >= 50) pures.add(traitEntry.getKey());
            }
            if (!pures.isEmpty()) { // guaranteed pure
                TraitBase trait = pures.get(random.nextInt(pures.size()));
                playerSoul.setTraits(List.of(trait));
                playerSoul.setPure(true);
                user.setStackInHand(hand, ItemStack.EMPTY);
                SoulForge.LOGGER.info("guaranteed pure");
                return super.use(world, user, hand);
            }
            List<TraitBase> guaranteed = new ArrayList<>(); // get guaranteed traits
            for (Map.Entry<TraitBase, String> traitEntry : traitKeys.entrySet()) {
                if (getAmount(stack, traitEntry.getValue()) >= 15) guaranteed.add(traitEntry.getKey());
            }
            if (!guaranteed.isEmpty()) {
                TraitBase trait = guaranteed.get(random.nextInt(guaranteed.size()));
                int affectedTotal = 0;
                for (Map.Entry<TraitBase, String> traitEntry : traitKeys.entrySet()) {
                    if (trait == traitEntry.getKey()) affectedTotal += getAmount(stack, traitEntry.getValue());
                }
                if (random.nextFloat() <= affectedTotal / 50f) { // pure check for first guaranteed trait
                    playerSoul.setTraits(List.of(trait));
                    playerSoul.setPure(true);
                    user.setStackInHand(hand, ItemStack.EMPTY);
                    SoulForge.LOGGER.info("guaranteed random pure");
                    return super.use(world, user, hand);
                }
                if (guaranteed.size() >= 2) { // make it a dual otherwise
                    TraitBase trait2 = guaranteed.get(random.nextInt(guaranteed.size()));
                    while (trait == trait2) trait2 = guaranteed.get(random.nextInt(guaranteed.size()));
                    affectedTotal += getAmount(stack, traitKeys.get(trait2));
                    playerSoul.setTraits(List.of(trait, trait2));
                    if (random.nextFloat() <= affectedTotal / 40f) { // strong dual check
                        playerSoul.setStrong(true);
                        SoulForge.LOGGER.info("guaranteed strong dual");
                    }
                    SoulForge.LOGGER.info("guaranteed dual");
                } else {
                    if (random.nextFloat() <= affectedTotal / 40f) { // normal strong check
                        playerSoul.setTraits(List.of(trait));
                        playerSoul.setStrong(true);
                        SoulForge.LOGGER.info("guaranteed strong");
                    }
                    SoulForge.LOGGER.info("guaranteed single");
                }
            } else { // no traits are guaranteed
                float total = 0f;
                List<Map.Entry<TraitBase, String>> entries = traitKeys.entrySet().stream().toList();
                for (Map.Entry<TraitBase, String> key : entries) {
                    total += getAmount(stack, key.getValue()) / 18f + 1/6f;
                }
                int i = 0;
                for (float val = random.nextFloat() * total; i < traitKeys.size() - 1; i++) {
                    val -= getAmount(stack, entries.get(i).getValue()) / 18f + 1/6f;
                    if (val <= 0f) break;
                }
                TraitBase trait = entries.get(i).getKey(); // wtf weighted trait selection
                int affectedTotal = 0;
                for (Map.Entry<TraitBase, String> traitEntry : traitKeys.entrySet()) {
                    if (trait == traitEntry.getKey()) affectedTotal += getAmount(stack, traitEntry.getValue());
                }
                if (random.nextFloat() <= affectedTotal / 50f) { // pure check for first guaranteed trait
                    playerSoul.setTraits(List.of(trait));
                    playerSoul.setPure(true);
                    SoulForge.LOGGER.info("random pure");
                    user.setStackInHand(hand, ItemStack.EMPTY);
                    return super.use(world, user, hand);
                }
                for (Map.Entry<TraitBase, String> entry : traitKeys.entrySet()) {
                    if (entry.getKey() == trait) continue; // NO MORE PATIENCE-PATIENCE
                    if (random.nextFloat() <= getAmount(stack, entry.getValue()) / 15f) {
                        playerSoul.setTraits(List.of(trait, entry.getKey()));
                        affectedTotal += getAmount(stack, entry.getValue());
                        if (random.nextFloat() <= affectedTotal / 40f) { // strong dual check part two electric boogaloo
                            playerSoul.setStrong(true);
                            SoulForge.LOGGER.info("random strong dual");
                        }
                        SoulForge.LOGGER.info("random dual");
                        user.setStackInHand(hand, ItemStack.EMPTY);
                        return super.use(world, user, hand);
                    }
                }
                playerSoul.setTraits(List.of(trait));
                if (random.nextFloat() <= affectedTotal / 40f) { // normal strong check
                    playerSoul.setStrong(true);
                    SoulForge.LOGGER.info("random strong");
                }
                SoulForge.LOGGER.info("random single");
            }
            user.setStackInHand(hand, ItemStack.EMPTY);
        }
        return super.use(world, user, hand);
    }

    public int getAmount(ItemStack stack, String trait) {
        if (stack.getOrCreateNbt().contains(trait)) {
            return stack.getOrCreateNbt().getInt(trait);
        }
        return 0;
    }

    public void setAmount(ItemStack stack, String trait, int amount) {
        stack.getOrCreateNbt().putInt(trait, amount);
    }

    public void incrementAmount(ItemStack stack, String trait, int amount) {
        setAmount(stack, trait, getAmount(stack, trait) + amount);
    }
}

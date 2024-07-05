package com.pulsar.soulforge.item;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.block.SoulForgeBlocks;
import com.pulsar.soulforge.data.AbilityLayout;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.entity.PlayerSoulEntity;
import com.pulsar.soulforge.trait.TraitBase;
import com.pulsar.soulforge.trait.Traits;
import com.pulsar.soulforge.util.Utils;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SoulJarItem extends BlockItem {
    public SoulJarItem() {
        super(SoulForgeBlocks.SOUL_JAR, new FabricItemSettings().maxCount(1));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (Objects.requireNonNull(context.getPlayer()).isSneaking()) {
            return super.useOnBlock(context);
        }
        ActionResult useResult = this.use(context.getWorld(), context.getPlayer(), context.getHand()).getResult();
        return useResult == ActionResult.CONSUME ? ActionResult.CONSUME_PARTIAL : useResult;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            EntityHitResult hit = Utils.getFocussedEntity(user, (float)ReachEntityAttributes.getReachDistance(user, 3f), entity -> entity instanceof PlayerSoulEntity);
            if (hit != null) {
                if (hit.getEntity() instanceof PlayerSoulEntity soulEntity) {
                    setHasSoul(stack, true);
                    setOwner(stack, soulEntity.getOwner());
                    setTrait1(stack, soulEntity.getTrait1());
                    setTrait2(stack, soulEntity.getTrait2());
                    setStrong(stack, soulEntity.getStrong());
                    setPure(stack, soulEntity.getPure());
                    setLv(stack, soulEntity.getLV());
                    setExp(stack, soulEntity.getEXP());
                    setLayout(stack, new AbilityLayout());
                    soulEntity.kill();
                }
            }
        }
        return TypedActionResult.consume(stack);
    }

    public static void setFromPlayer(ItemStack stack, PlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        setHasSoul(stack, true);
        setOwner(stack, player.getName().getString());
        setTrait1(stack, playerSoul.getTrait(0).getName());
        if (playerSoul.getTraitCount() == 2) setTrait2(stack, playerSoul.getTrait(1).getName());
        else setTrait2(stack, "");
        setStrong(stack, playerSoul.isStrong());
        setPure(stack, playerSoul.isPure());
        setLv(stack, playerSoul.getLV());
        setExp(stack, playerSoul.getEXP());
        setLayout(stack, playerSoul.getAbilityLayout());
    }

    public static NbtCompound getJarNbt(ItemStack stack) {
        NbtCompound nbt = BlockItem.getBlockEntityNbt(stack);
        if (nbt == null) return new NbtCompound();
        return nbt;
    }

    public static void setHasSoul(ItemStack stack, boolean value) {
        NbtCompound nbt = getJarNbt(stack);
        nbt.putBoolean("hasSoul", value);
        BlockItem.setBlockEntityNbt(stack, SoulForgeBlocks.SOUL_JAR_BLOCK_ENTITY, nbt);
    }
    public static boolean getHasSoul(ItemStack stack) {
        return getJarNbt(stack).contains("soul");
    }

    public static void setOwner(ItemStack stack, String value) {
        if (!getJarNbt(stack).contains("soul")) {
            getJarNbt(stack).put("soul", new NbtCompound());
        }
        NbtCompound nbt = getJarNbt(stack);
        nbt.getCompound("soul").putString("owner", value);
        BlockItem.setBlockEntityNbt(stack, SoulForgeBlocks.SOUL_JAR_BLOCK_ENTITY, nbt);
    }
    public static String getOwner(ItemStack stack) {
        if (!getJarNbt(stack).contains("soul")) return "";
        return getJarNbt(stack).getCompound("soul").getString("owner");
    }

    public static void setTrait1(ItemStack stack, String value) {
        if (!getJarNbt(stack).contains("soul")) {
            getJarNbt(stack).put("soul", new NbtCompound());
        }
        NbtCompound nbt = getJarNbt(stack);
        nbt.getCompound("soul").putString("trait1", value);
        BlockItem.setBlockEntityNbt(stack, SoulForgeBlocks.SOUL_JAR_BLOCK_ENTITY, nbt);
    }
    public static String getTrait1(ItemStack stack) {
        if (!getJarNbt(stack).contains("soul")) return "";
        return getJarNbt(stack).getCompound("soul").getString("trait1");
    }

    public static void setTrait2(ItemStack stack, String value) {
        if (!getJarNbt(stack).contains("soul")) {
            getJarNbt(stack).put("soul", new NbtCompound());
        }
        NbtCompound nbt = getJarNbt(stack);
        nbt.getCompound("soul").putString("trait2", value);
        BlockItem.setBlockEntityNbt(stack, SoulForgeBlocks.SOUL_JAR_BLOCK_ENTITY, nbt);
    }
    public static String getTrait2(ItemStack stack) {
        if (!getJarNbt(stack).contains("soul")) return "";
        return getJarNbt(stack).getCompound("soul").getString("trait2");
    }

    public static void setStrong(ItemStack stack, boolean value) {
        if (!getJarNbt(stack).contains("soul")) {
            getJarNbt(stack).put("soul", new NbtCompound());
        }
        NbtCompound nbt = getJarNbt(stack);
        nbt.getCompound("soul").putBoolean("strong", value);
        BlockItem.setBlockEntityNbt(stack, SoulForgeBlocks.SOUL_JAR_BLOCK_ENTITY, nbt);
    }
    public static boolean getStrong(ItemStack stack) {
        if (!getJarNbt(stack).contains("soul")) return false;
        return getJarNbt(stack).getCompound("soul").getBoolean("strong");
    }

    public static void setPure(ItemStack stack, boolean value) {
        if (!getJarNbt(stack).contains("soul")) {
            getJarNbt(stack).put("soul", new NbtCompound());
        }
        NbtCompound nbt = getJarNbt(stack);
        nbt.getCompound("soul").putBoolean("pure", value);
        BlockItem.setBlockEntityNbt(stack, SoulForgeBlocks.SOUL_JAR_BLOCK_ENTITY, nbt);
    }
    public static boolean getPure(ItemStack stack) {
        if (!getJarNbt(stack).contains("soul")) return false;
        return getJarNbt(stack).getCompound("soul").getBoolean("pure");
    }

    public static void setLv(ItemStack stack, int value) {
        if (!getJarNbt(stack).contains("soul")) {
            getJarNbt(stack).put("soul", new NbtCompound());
        }
        NbtCompound nbt = getJarNbt(stack);
        nbt.getCompound("soul").putInt("lv", value);
        BlockItem.setBlockEntityNbt(stack, SoulForgeBlocks.SOUL_JAR_BLOCK_ENTITY, nbt);
    }
    public static int getLv(ItemStack stack) {
        if (!getJarNbt(stack).contains("soul")) return 0;
        return getJarNbt(stack).getCompound("soul").getInt("lv");
    }

    public static void setExp(ItemStack stack, int value) {
        if (!getJarNbt(stack).contains("soul")) {
            getJarNbt(stack).put("soul", new NbtCompound());
        }
        NbtCompound nbt = getJarNbt(stack);
        nbt.getCompound("soul").putInt("exp", value);
        BlockItem.setBlockEntityNbt(stack, SoulForgeBlocks.SOUL_JAR_BLOCK_ENTITY, nbt);
    }
    public static int getExp(ItemStack stack) {
        if (!getJarNbt(stack).contains("soul")) return 0;
        return getJarNbt(stack).getCompound("soul").getInt("exp");
    }

    public static void setLayout(ItemStack stack, AbilityLayout layout) {
        if (!getJarNbt(stack).contains("soul")) {
            getJarNbt(stack).put("soul", new NbtCompound());
        }
        NbtCompound nbt = getJarNbt(stack);
        nbt.getCompound("soul").put("layout", layout.toNbt());
        BlockItem.setBlockEntityNbt(stack, SoulForgeBlocks.SOUL_JAR_BLOCK_ENTITY, nbt);
    }
    public static AbilityLayout getLayout(ItemStack stack) {
        if (!getJarNbt(stack).contains("soul")) return null;
        String trait1 = getJarNbt(stack).getCompound("soul").getString("trait1");
        String trait2 = getJarNbt(stack).getCompound("soul").getString("trait2");
        int lv = getJarNbt(stack).getCompound("soul").getInt("lv");
        boolean pure = getJarNbt(stack).getCompound("soul").getBoolean("pure");
        List<TraitBase> traits = new ArrayList<>();
        if (!Objects.equals(trait1, "")) traits.add(Traits.get(trait1));
        if (!Objects.equals(trait2, "")) traits.add(Traits.get(trait2));
        List<AbilityBase> abilities = Traits.getAbilities(traits, lv, pure);
        return AbilityLayout.fromNbt(abilities, getJarNbt(stack).getCompound("soul").getList("layout", NbtElement.COMPOUND_TYPE));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (getHasSoul(stack)) {
            tooltip.add(Text.literal(getOwner(stack)));
            String ttString = "";
            if (getPure(stack)) ttString += "Pure ";
            else if (getStrong(stack)) ttString += "Strong ";
            ttString += getTrait1(stack);
            if (!Objects.equals(getTrait2(stack), "")) ttString += "-" + getTrait2(stack);
            ttString += ": LV " + getLv(stack);
            tooltip.add(Text.literal(ttString));
        } else {
            tooltip.add(Text.literal("Empty"));
        }
    }
}

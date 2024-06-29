package com.pulsar.soulforge.mixin;

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.sugar.Local;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.item.devices.machines.SiphonImbuer;
import com.pulsar.soulforge.item.special.CrushingRound;
import com.pulsar.soulforge.item.special.FrostbiteRound;
import com.pulsar.soulforge.item.special.PuncturingRound;
import com.pulsar.soulforge.item.special.SuppressingRound;
import com.pulsar.soulforge.siphon.Siphon;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin {

    @Shadow @Final private static String CHARGED_PROJECTILES_KEY;

    @ModifyArgs(method = "shootAll", at=@At(value = "INVOKE", target = "Lnet/minecraft/item/CrossbowItem;shoot(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;FZFFF)V"))
    private static void modifyProjectileSpeed(Args args) {
        ItemStack stack = args.get(3);
        if (stack.hasNbt() && stack.getNbt().contains("Siphon")) {
            Siphon.Type type = Siphon.Type.getSiphon(stack.getNbt().getString("Siphon"));
            if (type == Siphon.Type.BRAVERY || type == Siphon.Type.SPITE) {
                args.set(7, (float)args.get(7) * 1.2f);
            }
        }
        if (stack.getNbt() != null) {
            if (stack.getNbt().getBoolean("imbued")) {
                args.set(7, (float)args.get(7) * 2f);
            }
        }
    }

    @Inject(method = "getPullTime", at=@At("RETURN"), cancellable = true)
    private static void modifyPullTime(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (stack.getNbt() != null) {
            if (stack.getNbt().getBoolean("imbued")) {
                cir.setReturnValue(cir.getReturnValue()/5);
            }
        }
    }

    @Redirect(method = "createArrow", at=@At(value = "INVOKE", target = "Lnet/minecraft/item/ArrowItem;createArrow(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/entity/projectile/PersistentProjectileEntity;"))
    private static PersistentProjectileEntity addProjectileEffects(ArrowItem instance, World world, ItemStack arrowStack, LivingEntity shooter) {
        ItemStack stack = shooter.getMainHandStack();
        NbtCompound arrowNbt = arrowStack.getNbt();
        if (arrowNbt == null) arrowNbt = new NbtCompound();
        if (stack.getNbt() != null) {
            if (stack.getNbt().contains("Siphon")) {
                arrowNbt.putString("Siphon", stack.getNbt().getString("Siphon"));
            }
        }
        arrowStack.setNbt(arrowNbt);
        PersistentProjectileEntity persistentProjectileEntity = null;
        if (stack.getOrCreateNbt().contains("reloaded")) {
            switch (stack.getOrCreateNbt().getString("reloaded")) {
                case "frostbite":
                    persistentProjectileEntity = FrostbiteRound.createProjectile(world, stack, shooter);
                    break;
                case "crushing":
                    persistentProjectileEntity = CrushingRound.createProjectile(world, stack, shooter);
                    break;
                case "puncturing":
                    persistentProjectileEntity = PuncturingRound.createProjectile(world, stack, shooter);
                    break;
                case "suppressing":
                    persistentProjectileEntity = SuppressingRound.createProjectile(world, stack, shooter);
                    break;
            }
            stack.getOrCreateNbt().remove("reloaded");
        } else {
            persistentProjectileEntity = instance.createArrow(world, arrowStack, shooter);
        }
        assert persistentProjectileEntity != null;
        if (stack.getNbt() != null) {
            if (stack.getNbt().contains("Siphon")) {
                Siphon.Type type = Siphon.Type.getSiphon(stack.getNbt().getString("Siphon"));
                if (type == Siphon.Type.DETERMINATION || type == Siphon.Type.SPITE) {
                    persistentProjectileEntity.addCommandTag("Determination Siphon");
                }
                if (type == Siphon.Type.PATIENCE || type == Siphon.Type.SPITE) {
                    persistentProjectileEntity.addCommandTag("Patience Siphon");
                }
                if (type == Siphon.Type.KINDNESS || type == Siphon.Type.SPITE) {
                    persistentProjectileEntity.addCommandTag("Kindness Siphon");
                }
                if (type == Siphon.Type.JUSTICE || type == Siphon.Type.SPITE) {
                    persistentProjectileEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                }
            }
        }
        if (stack.getNbt() != null) {
            if (stack.getNbt().getBoolean("imbued")) {
                persistentProjectileEntity.setNoGravity(true);
            }
        }
        return persistentProjectileEntity;
    }

    @Inject(method = "loadProjectile", at=@At(value="INVOKE", target = "Lnet/minecraft/item/ItemStack;split(I)Lnet/minecraft/item/ItemStack;", shift = At.Shift.BEFORE), cancellable = true)
    private static void onLoadProjectile(LivingEntity shooter, ItemStack crossbow, ItemStack projectile, boolean simulated, boolean creative, CallbackInfoReturnable<Boolean> cir) {
        if (crossbow.hasNbt() && crossbow.getNbt().contains("Siphon")) {
            Siphon.Type type = Siphon.Type.getSiphon(crossbow.getNbt().getString("Siphon"));
            if (type == Siphon.Type.JUSTICE || type == Siphon.Type.SPITE) {
                CrossbowItemMixin.putProjectile(crossbow, projectile);
                cir.setReturnValue(true);
            }
        }
        if (shooter instanceof PlayerEntity player) {
            if (crossbow.getNbt() != null) {
                if (crossbow.getNbt().getBoolean("imbued")) {
                    ItemStack stack = Utils.getImbuer(crossbow, player);
                    if (stack != null) {
                        if (((SiphonImbuer)stack.getItem()).getCharge(stack) <= 0) cir.setReturnValue(false);
                        ((SiphonImbuer) stack.getItem()).decreaseCharge(stack, 3);
                    }
                }
            }
        }
    }

    @Unique
    private static void putProjectile(ItemStack crossbow, ItemStack projectile) {
        NbtCompound nbtCompound = crossbow.getOrCreateNbt();
        NbtList nbtList = nbtCompound.contains(CHARGED_PROJECTILES_KEY, NbtElement.LIST_TYPE) ? nbtCompound.getList(CHARGED_PROJECTILES_KEY, NbtElement.COMPOUND_TYPE) : new NbtList();
        NbtCompound nbtCompound2 = new NbtCompound();
        projectile.writeNbt(nbtCompound2);
        nbtList.add(nbtCompound2);
        nbtCompound.put(CHARGED_PROJECTILES_KEY, nbtList);
    }

    @Inject(method = "appendTooltip", at=@At("HEAD"))
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
        if (stack.getNbt() != null) {
            if (stack.getNbt().contains("Siphon")) {
                Siphon.Type siphonType = Siphon.Type.getSiphon(stack.getNbt().getString("Siphon"));
                if (siphonType != null) {
                    switch (siphonType) {
                        case BRAVERY -> tooltip.add(Text.translatable("siphon.soulforge.bravery").formatted(Formatting.GOLD));
                        case JUSTICE -> tooltip.add(Text.translatable("siphon.soulforge.justice").formatted(Formatting.YELLOW));
                        case KINDNESS -> tooltip.add(Text.translatable("siphon.soulforge.kindness").formatted(Formatting.GREEN));
                        case PATIENCE -> tooltip.add(Text.translatable("siphon.soulforge.patience").formatted(Formatting.BLUE));
                        case INTEGRITY -> tooltip.add(Text.translatable("siphon.soulforge.integrity").formatted(Formatting.DARK_BLUE));
                        case PERSEVERANCE -> tooltip.add(Text.translatable("siphon.soulforge.perseverance").formatted(Formatting.LIGHT_PURPLE));
                        case DETERMINATION -> tooltip.add(Text.translatable("siphon.soulforge.determination").formatted(Formatting.RED));
                        case SPITE -> tooltip.add(Text.literal("Siphon: Phoenix").formatted(Formatting.DARK_RED));
                    }
                }
            }
            if (stack.getNbt().contains("imbued")) {
                tooltip.add(Text.translatable("item.soulforge.siphon_imbuer.imbued"));
            }
        }
    }
}

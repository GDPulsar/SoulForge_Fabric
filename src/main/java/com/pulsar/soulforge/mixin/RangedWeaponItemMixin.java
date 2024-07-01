package com.pulsar.soulforge.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.item.special.CrushingRound;
import com.pulsar.soulforge.item.special.FrostbiteRound;
import com.pulsar.soulforge.item.special.PuncturingRound;
import com.pulsar.soulforge.item.special.SuppressingRound;
import com.pulsar.soulforge.siphon.Siphon;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(RangedWeaponItem.class)
public class RangedWeaponItemMixin {
    @Redirect(method = "createArrowEntity", at=@At(value = "INVOKE", target = "Lnet/minecraft/item/ArrowItem;createArrow(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/projectile/PersistentProjectileEntity;"))
    private PersistentProjectileEntity addProjectileEffects(ArrowItem instance, World world, ItemStack stack, LivingEntity shooter, ItemStack shotFrom) {
        ItemStack arrow = stack.copy();
        if (shotFrom.get(SoulForgeItems.SIPHON_COMPONENT) != null) {
            arrow.set(SoulForgeItems.SIPHON_COMPONENT, shotFrom.get(SoulForgeItems.SIPHON_COMPONENT));
        }
        PersistentProjectileEntity persistentProjectileEntity = null;
        if (stack.get(SoulForgeItems.RELOAD_COMPONENT) != null) {
            persistentProjectileEntity = switch (stack.get(SoulForgeItems.RELOAD_COMPONENT)) {
                case "frostbite" -> FrostbiteRound.createProjectile(world, stack, shooter);
                case "crushing" -> CrushingRound.createProjectile(world, stack, shooter);
                case "puncturing" -> PuncturingRound.createProjectile(world, stack, shooter);
                case "suppressing" -> SuppressingRound.createProjectile(world, stack, shooter);
                default -> persistentProjectileEntity;
            };
            stack.remove(SoulForgeItems.RELOAD_COMPONENT);
        } else {
            persistentProjectileEntity = instance.createArrow(world, stack, shooter, shotFrom);
        }
        assert persistentProjectileEntity != null;
        if (shotFrom.get(SoulForgeItems.SIPHON_COMPONENT) != null) {
            Siphon.Type type = Siphon.Type.getSiphon(shotFrom.get(SoulForgeItems.SIPHON_COMPONENT));
            if (type == Siphon.Type.PERSEVERANCE || type == Siphon.Type.SPITE) {
                persistentProjectileEntity.getPierceLevel();
            }
            if (type == Siphon.Type.DETERMINATION || type == Siphon.Type.SPITE) {
                persistentProjectileEntity.addCommandTag("Determination Siphon");
            }
            if (type == Siphon.Type.PATIENCE || type == Siphon.Type.SPITE) {
                persistentProjectileEntity.addCommandTag("Patience Siphon");
            }
            if (type == Siphon.Type.KINDNESS || type == Siphon.Type.SPITE) {
                persistentProjectileEntity.addCommandTag("Kindness Siphon");
            }
        }
        if (stack.getItem() instanceof CrossbowItem && stack.get(SoulForgeItems.IMBUED_COMPONENT)) {
            persistentProjectileEntity.setNoGravity(true);
        }
        return persistentProjectileEntity;
    }

    @ModifyVariable(method = "shootAll", at = @At(value = "HEAD"), ordinal = 5)
    private float modifyShoot(float speed, @Local ItemStack stack) {
        float newSpeed = speed;
        if (stack.get(SoulForgeItems.SIPHON_COMPONENT) != null) {
            Siphon.Type type = Siphon.Type.getSiphon(stack.get(SoulForgeItems.SIPHON_COMPONENT));
            if (type == Siphon.Type.BRAVERY) {
                newSpeed *= 1.25f;
            }
        }
        if (stack.getItem() instanceof CrossbowItem && stack.get(SoulForgeItems.IMBUED_COMPONENT)) {
            newSpeed *= 1.25f;
        }
        return newSpeed;
    }


}

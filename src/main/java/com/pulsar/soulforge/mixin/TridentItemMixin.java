package com.pulsar.soulforge.mixin;

import com.pulsar.soulforge.item.devices.machines.SiphonImbuer;
import com.pulsar.soulforge.siphon.Siphon;
import com.pulsar.soulforge.tag.SoulForgeTags;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TridentItem.class)
public class TridentItemMixin {
    @Redirect(method = "onStoppedUsing", at=@At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/TridentEntity;setVelocity(Lnet/minecraft/entity/Entity;FFFFF)V"))
    private void modifyProjectileSpeed(TridentEntity instance, Entity entity, float pitch, float yaw, float roll, float speed, float divergence) {
        instance.setVelocity(entity, pitch, yaw, roll, speed, divergence);
        if (entity instanceof LivingEntity owner) {
            ItemStack held = owner.getMainHandStack();
            if (held.getNbt() != null) {
                if (held.getNbt().contains("Siphon")) {
                    Siphon.Type type = Siphon.Type.getSiphon(held.getNbt().getString("Siphon"));
                    if (type == Siphon.Type.BRAVERY || type == Siphon.Type.SPITE) {
                        instance.setVelocity(entity, pitch, yaw, roll, speed*1.2f, divergence);
                    }
                }
            }
        }
    }

    @Redirect(method = "onStoppedUsing", at=@At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addVelocity(DDD)V"))
    private void modifyRiptideSpeed(PlayerEntity instance, double x, double y, double z) {
        ItemStack held = instance.getMainHandStack();
        if (held.getNbt() != null) {
            if (held.getNbt().contains("Siphon")) {
                Siphon.Type type = Siphon.Type.getSiphon(held.getNbt().getString("Siphon"));
                if (type == Siphon.Type.BRAVERY || type == Siphon.Type.SPITE) {
                    instance.addVelocity(x*1.2f, y*1.2f, z*1.2f);
                    return;
                }
            }
        }
        instance.addVelocity(x, y, z);
    }

    @Redirect(method = "onStoppedUsing", at=@At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isTouchingWaterOrRain()Z"))
    private boolean modifyStoppedUsing(PlayerEntity player) {
        ItemStack stack;
        if (player.getMainHandStack().isIn(SoulForgeTags.IMBUER_TRIDENTS)) stack = player.getMainHandStack();
        else stack = player.getOffHandStack();
        if (stack.getNbt() != null) {
            if (stack.getNbt().getBoolean("imbued")) {
                ItemStack imbuerStack = Utils.getImbuer(stack, player);
                if (imbuerStack != null) {
                    if (((SiphonImbuer)imbuerStack.getItem()).getCharge(imbuerStack) >= 10) {
                        ((SiphonImbuer)imbuerStack.getItem()).decreaseCharge(imbuerStack, 10);
                        return true;
                    }
                }
            }
        }
        return player.isTouchingWaterOrRain();
    }

    @Redirect(method = "use", at=@At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isTouchingWaterOrRain()Z"))
    private boolean modifyUse(PlayerEntity player) {
        ItemStack stack;
        if (player.getMainHandStack().isIn(SoulForgeTags.IMBUER_TRIDENTS)) stack = player.getMainHandStack();
        else stack = player.getOffHandStack();
        if (stack.getNbt() != null) {
            if (stack.getNbt().getBoolean("imbued")) {
                ItemStack imbuerStack = Utils.getImbuer(stack, player);
                if (imbuerStack != null) {
                    if (((SiphonImbuer)imbuerStack.getItem()).getCharge(imbuerStack) >= 10) {
                        return true;
                    }
                }
            }
        }
        return player.isTouchingWaterOrRain();
    }
}

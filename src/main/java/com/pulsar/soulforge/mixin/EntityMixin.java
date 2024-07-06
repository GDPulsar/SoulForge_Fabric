package com.pulsar.soulforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.item.SoulForgeItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Entity.class)
public class EntityMixin {
    @ModifyReturnValue(method = "isFireImmune", at=@At("RETURN"))
    private boolean modifyFireImmunity(boolean original) {
        Entity entity = (Entity)(Object)this;
        if (entity instanceof PlayerEntity player) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            return original || playerSoul.hasCast("Bravery Boost") || playerSoul.hasCast("Fearless Instincts") || playerSoul.hasCast("Perfected Aura Technique");
        }
        return original;
    }

    @ModifyReturnValue(method = "shouldRenderName", at=@At("RETURN"))
    private boolean modifyNameTagVisibility(boolean original) {
        Entity entity = (Entity)(Object)this;
        if (entity instanceof LivingEntity living) {
            if (living.hasStatusEffect(SoulForgeEffects.SNOWED_VISION)) return false;
        }
        return original;
    }

    @ModifyVariable(method = "changeLookDirection", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private double modifyHorizontalLookSpeed(double value) {
        Entity entity = (Entity)(Object)this;
        if (entity instanceof PlayerEntity player) {
            if (player.isUsingItem()) {
                if (player.getActiveItem().isOf(SoulForgeItems.GUNLANCE)) {
                    return MathHelper.clamp(value, -0.5, 0.5);
                }
            }
        }
        return value;
    }

    @ModifyVariable(method = "changeLookDirection", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private double modifyVerticalLookSpeed(double value) {
        Entity entity = (Entity)(Object)this;
        if (entity instanceof PlayerEntity player) {
            if (player.isUsingItem()) {
                if (player.getActiveItem().isOf(SoulForgeItems.GUNLANCE)) {
                    return MathHelper.clamp(value, -0.5, 0.5);
                }
            }
        }
        return value;
    }
}

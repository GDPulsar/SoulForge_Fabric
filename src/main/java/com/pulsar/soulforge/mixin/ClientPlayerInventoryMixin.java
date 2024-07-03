package com.pulsar.soulforge.mixin;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public abstract class ClientPlayerInventoryMixin {
    @Shadow @Final public PlayerEntity player;

    @Shadow public int selectedSlot;

    @Shadow public abstract ItemStack getStack(int slot);

    @Inject(method="scrollInHotbar", at=@At("HEAD"), cancellable = true)
    protected void handleHotbarScroll(double scrollAmount, CallbackInfo ci) {
        PlayerEntity player = this.player;
        if (player.getWorld().isClient) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            int direction = (int) Math.signum(scrollAmount);
            if (playerSoul.magicModeActive()) {
                if (playerSoul.getAbilitySlot() - direction < 0)
                    playerSoul.setAbilitySlot(playerSoul.getAbilitySlot() - direction + 9);
                else if (playerSoul.getAbilitySlot() - direction > 8)
                    playerSoul.setAbilitySlot(playerSoul.getAbilitySlot() - direction - 9);
                else playerSoul.setAbilitySlot(playerSoul.getAbilitySlot() - direction);
                ci.cancel();
            }
        }
    }
}

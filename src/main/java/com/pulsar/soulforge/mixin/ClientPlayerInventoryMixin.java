package com.pulsar.soulforge.mixin;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.networking.AbilityHotbarScrollPacket;
import com.pulsar.soulforge.networking.SoulForgeNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
            PlayerInventory inventory = (PlayerInventory) (Object) this;
            int direction = (int) Math.signum(scrollAmount);
            if (playerSoul != null) {
                if (playerSoul.magicModeActive()) {
                    if (playerSoul.getAbilitySlot() - direction < 0)
                        playerSoul.setAbilitySlot(playerSoul.getAbilitySlot() - direction + 9);
                    else if (playerSoul.getAbilitySlot() - direction > 8)
                        playerSoul.setAbilitySlot(playerSoul.getAbilitySlot() - direction - 9);
                    else playerSoul.setAbilitySlot(playerSoul.getAbilitySlot() - direction);
                    ClientPlayNetworking.send(new AbilityHotbarScrollPacket(playerSoul.getAbilitySlot()));
                    ci.cancel();
                }
            }
        }
    }
}

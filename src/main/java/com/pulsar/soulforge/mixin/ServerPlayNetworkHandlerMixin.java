package com.pulsar.soulforge.mixin;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
abstract class ServerPlayNetworkHandlerMixin implements ServerPlayPacketListener {
    @Shadow public ServerPlayerEntity player;

    @Inject(method = "onUpdateSelectedSlot", at=@At("HEAD"), cancellable = true)
    public void onUpdateSelectedSlot(UpdateSelectedSlotC2SPacket packet, CallbackInfo ci) {
        ServerPlayNetworkHandler handler = (ServerPlayNetworkHandler)(Object)this;
        NetworkThreadUtils.forceMainThread(packet, handler, handler.player.getServerWorld());
        if (packet.getSelectedSlot() >= 0 && packet.getSelectedSlot() < PlayerInventory.getHotbarSize()) {
            if (handler.player.getInventory().selectedSlot != packet.getSelectedSlot() && handler.player.getActiveHand() == Hand.MAIN_HAND) {
                handler.player.clearActiveItem();
            }

            handler.player.getInventory().selectedSlot = packet.getSelectedSlot();
            handler.player.updateLastActionTime();
        } else if (packet.getSelectedSlot() == 9) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(handler.player);
            if (playerSoul.hasWeapon()) {
                handler.player.getInventory().selectedSlot = 9;
                handler.player.updateLastActionTime();
            }
        }
        ci.cancel();
    }

    @Inject(method = "onPlayerAction", at=@At("HEAD"), cancellable = true)
    protected void modifyPlayerAction(PlayerActionC2SPacket packet, CallbackInfo ci) {
        NetworkThreadUtils.forceMainThread(packet, (ServerPlayNetworkHandler)(Object)this, this.player.getServerWorld());
        this.player.updateLastActionTime();
        PlayerActionC2SPacket.Action action = packet.getAction();
        switch (action) {
            case SWAP_ITEM_WITH_OFFHAND: {
                if (!(this.player.isSpectator() || this.player.getInventory().selectedSlot == 9)) {
                    ItemStack itemStack = this.player.getStackInHand(Hand.OFF_HAND);
                    this.player.setStackInHand(Hand.OFF_HAND, this.player.getStackInHand(Hand.MAIN_HAND));
                    this.player.setStackInHand(Hand.MAIN_HAND, itemStack);
                    this.player.clearActiveItem();
                }
                ci.cancel();
                break;
            }
            case DROP_ITEM: {
                if (!(this.player.isSpectator() || this.player.getInventory().selectedSlot == 9)) {
                    this.player.dropSelectedItem(false);
                }
                if (this.player.getInventory().selectedSlot == 9) {
                    SoulComponent playerSoul = SoulForge.getPlayerSoul(this.player);
                    playerSoul.removeWeapon();
                    //this.player.getInventory().selectedSlot = 0;
                }
                ci.cancel();
                break;
            }
        }
    }

    @Redirect(method = "onPlayerMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;isHost()Z"))
    private boolean modifyPreventAntiCheat(ServerPlayNetworkHandler instance) {
        return true;
    }
}

package com.pulsar.soulforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
abstract class ServerPlayNetworkHandlerMixin implements ServerPlayPacketListener {
    @Shadow public ServerPlayerEntity player;

    @Inject(method = "onPlayerAction", at=@At("HEAD"), cancellable = true)
    protected void modifyPlayerAction(PlayerActionC2SPacket packet, CallbackInfo ci) {
        PlayerActionC2SPacket.Action action = packet.getAction();
        if (action == PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND) {
            if (this.player.getInventory().selectedSlot == 9) {
                ci.cancel();
            }
        }
        if (action == PlayerActionC2SPacket.Action.DROP_ITEM ||
                action == PlayerActionC2SPacket.Action.DROP_ALL_ITEMS) {
            if (this.player.getInventory().selectedSlot == 9) {
                SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                playerSoul.removeWeapon(true);
                ci.cancel();
            }
        }
    }

    @ModifyExpressionValue(method = "onPlayerMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;isHost()Z"))
    private boolean modifyPreventAntiCheat(boolean original) {
        return true;
    }
}

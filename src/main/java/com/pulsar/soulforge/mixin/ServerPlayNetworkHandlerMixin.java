package com.pulsar.soulforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.components.ValueComponent;
import com.pulsar.soulforge.item.SoulForgeItems;
import net.minecraft.item.ItemStack;
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
                ItemStack held = player.getMainHandStack();
                if (held.isOf(SoulForgeItems.PERSEVERANCE_BLADES) || held.isOf(SoulForgeItems.PERSEVERANCE_EDGE) ||
                    held.isOf(SoulForgeItems.PERSEVERANCE_CLAW) || held.isOf(SoulForgeItems.PERSEVERANCE_HARPOON)) {
                    ValueComponent values = SoulForge.getValues(player);
                    SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                    if (values != null && (values.getTimer("FreeWeaponMorph") > 0 || playerSoul.getMagic() > 20f)) {
                        if (!player.isSneaking()) {
                            if (held.isOf(SoulForgeItems.PERSEVERANCE_BLADES) && playerSoul.getLV() >= 5) {
                                playerSoul.setWeapon(SoulForgeItems.PERSEVERANCE_EDGE.getDefaultStack());
                            }
                            if (held.isOf(SoulForgeItems.PERSEVERANCE_EDGE) && playerSoul.getLV() >= 10) {
                                playerSoul.setWeapon(SoulForgeItems.PERSEVERANCE_CLAW.getDefaultStack());
                            }
                            if (held.isOf(SoulForgeItems.PERSEVERANCE_CLAW) && playerSoul.getLV() >= 17) {
                                playerSoul.setWeapon(SoulForgeItems.PERSEVERANCE_HARPOON.getDefaultStack());
                            }
                            if (held.isOf(SoulForgeItems.PERSEVERANCE_HARPOON)) {
                                playerSoul.setWeapon(SoulForgeItems.PERSEVERANCE_BLADES.getDefaultStack());
                            }
                        } else {
                            if (held.isOf(SoulForgeItems.PERSEVERANCE_BLADES)) {
                                if (playerSoul.getLV() >= 17) playerSoul.setWeapon(SoulForgeItems.PERSEVERANCE_HARPOON.getDefaultStack());
                                else if (playerSoul.getLV() >= 10) playerSoul.setWeapon(SoulForgeItems.PERSEVERANCE_CLAW.getDefaultStack());
                                else if (playerSoul.getLV() >= 5) playerSoul.setWeapon(SoulForgeItems.PERSEVERANCE_EDGE.getDefaultStack());
                            }
                            if (held.isOf(SoulForgeItems.PERSEVERANCE_EDGE)) {
                                playerSoul.setWeapon(SoulForgeItems.PERSEVERANCE_BLADES.getDefaultStack());
                            }
                            if (held.isOf(SoulForgeItems.PERSEVERANCE_CLAW)) {
                                playerSoul.setWeapon(SoulForgeItems.PERSEVERANCE_EDGE.getDefaultStack());
                            }
                            if (held.isOf(SoulForgeItems.PERSEVERANCE_HARPOON)) {
                                playerSoul.setWeapon(SoulForgeItems.PERSEVERANCE_CLAW.getDefaultStack());
                            }
                        }
                        if (values.getTimer("FreeWeaponMorph") <= 0) {
                            playerSoul.tryConsumeMagic(20f);
                        }
                        playerSoul.resetLastCastTime();
                        values.setTimer("FreeWeaponMorph", 20);
                    }
                }
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

package com.pulsar.soulforge.mixin;

import com.pulsar.soulforge.client.ui.EncyclopediaScreen;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.networking.SoulForgeNetworking;
import com.pulsar.soulforge.siphon.Siphon;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "breakBlock", at=@At("HEAD"))
    public void modifyBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        ClientWorld world = this.client.world;
        BlockState blockState = world.getBlockState(pos);
        ItemStack held = this.client.player.getMainHandStack();
        if (blockState.isAir()) { return; }
        if (held.getNbt() != null) {
            if (held.getNbt().contains("Siphon")) {
                Siphon.Type type = Siphon.Type.getSiphon(held.getNbt().getString("Siphon"));
                if ((type == Siphon.Type.PERSEVERANCE || type == Siphon.Type.SPITE) && this.client.player.isSneaking()) {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeBlockPos(pos);
                    ClientPlayNetworking.send(SoulForgeNetworking.VEINMINE, buf);
                }
            }
        }
    }

    @Inject(method = "interactItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/SequencedPacketCreator;)V", shift = At.Shift.BEFORE))
    private void beforeUseItem(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.isOf(SoulForgeItems.ENCYCLOPEDIA)) {
            MinecraftClient.getInstance().setScreen(new EncyclopediaScreen());
        }
    }
}

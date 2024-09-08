package com.pulsar.soulforge.mixin;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.item.weapons.MagicItem;
import me.x150.renderer.util.AlphaOverride;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    @ModifyArgs(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V",
    at=@At(value="INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
    public void renderItem(Args args) {
        PlayerEntity player = args.get(0);
        Hand hand = args.get(3);
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (playerSoul.hasCast("Colossal Claymore")) {
            if (hand == Hand.MAIN_HAND) {
                args.set(5, new ItemStack(SoulForgeItems.COLOSSAL_CLAYMORE_DISPLAY));
                return;
            }
        }
        /*if (playerSoul.hasValue("rampageTimer") && playerSoul.hasValue("rampageStart")) {
            if (playerSoul.getValue("rampageTimer") < 20 && playerSoul.getValue("rampageStart") == 5) {
                if (hand == Hand.MAIN_HAND) {
                    args.set(5, new ItemStack(SoulForgeItems.DETERMINATION_CLAYMORE_DISPLAY));
                    return;
                }
            }
        }*/
        /*if (hand == Hand.MAIN_HAND) {
            if (player.getMainHandStack().getItem() == SoulForgeItems.KINDNESS_SHIELD) {
                args.set(5, ItemStack.EMPTY);
            }
        }*/
        if (hand == Hand.OFF_HAND) {
            if (player.getMainHandStack().getItem() == SoulForgeItems.BRAVERY_GAUNTLETS || player.getMainHandStack().getItem() == SoulForgeItems.PERSEVERANCE_BLADES ||
                player.getMainHandStack().getItem() == SoulForgeItems.DETERMINATION_GAUNTLETS || player.getMainHandStack().getItem() == SoulForgeItems.DETERMINATION_BLADES ||
                    player.getMainHandStack().getItem() == SoulForgeItems.GUNBLADES) {
                args.set(5, player.getMainHandStack());
            }
            /*if (playerSoul.hasWeapon() && playerSoul.getWeapon().isOf(SoulForgeItems.KINDNESS_SHIELD)) {
                args.set(5, playerSoul.getWeapon());
            }*/
        }
    }

    @Inject(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V",
            at=@At(value="INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", ordinal = 0, shift = At.Shift.BEFORE))
    public void modifyItemOpacity(float tickDelta, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, ClientPlayerEntity player, int light, CallbackInfo ci) {
        if (player.getMainHandStack().getItem() instanceof MagicItem) {
            AlphaOverride.pushAlphaMul(0.6f);
        }
    }

    @Inject(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V",
            at=@At(value="INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", ordinal = 0, shift = At.Shift.AFTER))
    public void resetItemOpacity(float tickDelta, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, ClientPlayerEntity player, int light, CallbackInfo ci) {
        if (player.getMainHandStack().getItem() instanceof MagicItem) {
            AlphaOverride.popAlphaMul();
        }
    }

    @Inject(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V",
            at=@At(value="INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", ordinal = 1, shift = At.Shift.BEFORE))
    public void modifyItemOpacity2(float tickDelta, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, ClientPlayerEntity player, int light, CallbackInfo ci) {
        if (player.getMainHandStack().getItem() instanceof MagicItem) {
            AlphaOverride.pushAlphaMul(0.6f);
        }
    }

    @Inject(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V",
            at=@At(value="INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", ordinal = 1, shift = At.Shift.AFTER))
    public void resetItemOpacity2(float tickDelta, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, ClientPlayerEntity player, int light, CallbackInfo ci) {
        if (player.getMainHandStack().getItem() instanceof MagicItem) {
            AlphaOverride.popAlphaMul();
        }
    }
}

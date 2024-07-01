package com.pulsar.soulforge.mixin;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.SoulForgeItems;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(HeldItemFeatureRenderer.class)
public class HeldItemFeatureRendererMixin<T extends LivingEntity, M extends EntityModel<T> & ModelWithArms> {
    @ModifyArgs(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at=@At(value="INVOKE", target = "Lnet/minecraft/client/render/entity/feature/HeldItemFeatureRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;Lnet/minecraft/util/Arm;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
    public void renderItem(Args args) {
        LivingEntity entity = args.get(0);
        Arm arm = args.get(3);
        if (entity instanceof PlayerEntity player) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            if (playerSoul.hasCast("Colossal Claymore")) {
                if (arm == player.getMainArm()) {
                    args.set(1, new ItemStack(SoulForgeItems.COLOSSAL_CLAYMORE_DISPLAY));
                    return;
                }
            }
            /*if (arm == player.getMainArm()) {
                if (player.getMainHandStack().getItem() == SoulForgeItems.KINDNESS_SHIELD) {
                    args.set(5, ItemStack.EMPTY);
                }
            } else {
                if (playerSoul.hasWeapon() && playerSoul.getWeapon().isOf(SoulForgeItems.KINDNESS_SHIELD)) {
                    args.set(5, playerSoul.getWeapon());
                }
            }*/
        }
        if (arm != entity.getMainArm()) {
            ItemStack mainStack = entity.getMainHandStack();
            if (mainStack.getItem() == SoulForgeItems.BRAVERY_GAUNTLETS || mainStack.getItem() == SoulForgeItems.PERSEVERANCE_BLADES ||
                    mainStack.getItem() == SoulForgeItems.DETERMINATION_GAUNTLETS || mainStack.getItem() == SoulForgeItems.DETERMINATION_BLADES ||
                mainStack.getItem() == SoulForgeItems.GUNBLADES) {
                args.set(1, mainStack);
            }
        }
    }
}

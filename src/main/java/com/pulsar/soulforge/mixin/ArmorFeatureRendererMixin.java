package com.pulsar.soulforge.mixin;

import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.siphon.Siphon;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> {

    @Shadow protected abstract boolean usesInnerModel(EquipmentSlot slot);

    @Shadow @Final private static Map<String, Identifier> ARMOR_TEXTURE_CACHE;

    @Inject(method = "renderArmor", at=@At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;get(Lnet/minecraft/component/ComponentType;)Ljava/lang/Object;"))
    private void addSiphonRender(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model, CallbackInfo ci) {
        ItemStack stack = entity.getEquippedStack(armorSlot);
        if (stack.isIn(ItemTags.TRIMMABLE_ARMOR) && stack.get(SoulForgeItems.SIPHON_COMPONENT) != null) {
            String siphonStr = stack.get(SoulForgeItems.SIPHON_COMPONENT);

            Siphon.Type siphonType = Siphon.Type.getSiphon(siphonStr);
            if (siphonType != null) {
                boolean bl = this.usesInnerModel(armorSlot);
                VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getArmorCutoutNoCull(this.getSiphonTexture(siphonType, bl)));
                model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 0xFFFFFF);
            }
        }
    }

    @Unique
    private Identifier getSiphonTexture(Siphon.Type type, boolean secondLayer) {
        String string = "textures/models/armor/" + type.asString() + "_layer_" + (secondLayer ? 2 : 1) + ".png";
        return ARMOR_TEXTURE_CACHE.computeIfAbsent(string, Identifier::of);
    }
}

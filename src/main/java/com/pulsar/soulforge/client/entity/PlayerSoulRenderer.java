package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.entity.PlayerSoulEntity;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.trait.Traits;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import java.util.Objects;

public class PlayerSoulRenderer extends EntityRenderer<PlayerSoulEntity> {
    private final ItemRenderer itemRenderer;

    public PlayerSoulRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    public void render(PlayerSoulEntity entity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        float n = entity.getYaw();
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotation(n + g * 0.2f));

        try {
            ItemStack soulItem = SoulForgeItems.getSoulItem(Objects.requireNonNull(Traits.get(entity.getTrait1())),
                    Objects.requireNonNull(Objects.equals(entity.getTrait2(), "") ? Traits.get(entity.getTrait1()) : Traits.get(entity.getTrait2())));
            this.itemRenderer.renderItem(soulItem, ModelTransformationMode.GROUND, i, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider, entity.getWorld(), 0);
        } catch (NullPointerException ignored) {}
        matrixStack.pop();
    }

    @Override
    public Identifier getTexture(PlayerSoulEntity entity) {
        return null;
    }
}

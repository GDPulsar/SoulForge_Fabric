package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.entity.SlowballProjectile;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class SlowballRenderer extends EntityRenderer<SlowballProjectile> {
    private final ItemRenderer itemRenderer;

    public SlowballRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.itemRenderer = ctx.getItemRenderer();
    }

    public void render(SlowballProjectile slowball, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (slowball.age >= 2 || !(this.dispatcher.camera.getFocusedEntity().squaredDistanceTo(slowball) < 12.25)) {
            matrices.push();
            matrices.multiply(this.dispatcher.getRotation());
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
            this.itemRenderer.renderItem(new ItemStack(Items.SNOWBALL), ModelTransformationMode.GROUND, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, slowball.getWorld(), slowball.getId());
            matrices.pop();
            super.render(slowball, yaw, tickDelta, matrices, vertexConsumers, light);
        }
    }

    @Override
    public Identifier getTexture(SlowballProjectile entity) {
        return null;
    }
}

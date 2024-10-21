package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.SoulForgeClient;
import com.pulsar.soulforge.entity.DeterminationStaffStarProjectile;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class StaffStarRenderer extends EntityRenderer<DeterminationStaffStarProjectile> {
    private static final Identifier TEXTURE = new Identifier(SoulForge.MOD_ID, "textures/entity/staff_star.png");
    private final StaffStarModel MODEL;

    public StaffStarRenderer(EntityRendererFactory.Context context) {
        super(context);
        MODEL = new StaffStarModel(context.getPart(SoulForgeClient.MODEL_STAFF_STAR_LAYER));
    }

    public void render(DeterminationStaffStarProjectile star, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.translate(0f, -0.5f, 0f);
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(MODEL.getLayer(TEXTURE));
        MODEL.render(matrixStack, vertexConsumer, i, 0, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.pop();
        super.render(star, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(DeterminationStaffStarProjectile entity) {
        return TEXTURE;
    }
}

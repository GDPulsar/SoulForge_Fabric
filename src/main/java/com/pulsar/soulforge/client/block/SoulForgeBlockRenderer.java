package com.pulsar.soulforge.client.block;

import com.pulsar.soulforge.block.SoulForgeBlockEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class SoulForgeBlockRenderer extends GeoBlockRenderer<SoulForgeBlockEntity> {
    public SoulForgeBlockRenderer(BlockEntityRendererFactory.Context context) {
        super(new SoulForgeBlockModel());
    }

    @Override
    public void preRender(MatrixStack poseStack, SoulForgeBlockEntity animatable, BakedGeoModel model,
                               VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender,
                               float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        GeoBone lava = model.getBone("lava").orElse(null);
        if (lava != null) {
            lava.setScaleY(((float)animatable.getLava() / (float)animatable.getLavaMax()));
        }
    }
}

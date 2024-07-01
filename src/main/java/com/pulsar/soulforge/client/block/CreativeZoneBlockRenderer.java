package com.pulsar.soulforge.client.block;

import com.pulsar.soulforge.block.CreativeZoneBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class CreativeZoneBlockRenderer extends GeoBlockRenderer<CreativeZoneBlockEntity> {
    public CreativeZoneBlockRenderer(BlockEntityRendererFactory.Context context) {
        super(new CreativeZoneBlockModel());
    }

    @Override
    public void actuallyRender(MatrixStack poseStack, CreativeZoneBlockEntity animatable, BakedGeoModel model, RenderLayer renderType,
                               VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
                               int packedOverlay, float red, float green, float blue, float alpha) {
        long l = animatable.getWorld().getTime();
        if (animatable.isActive(animatable.getCachedState())) {
            BeaconBlockEntityRenderer.renderBeam(poseStack, bufferSource, BeaconBlockEntityRenderer.BEAM_TEXTURE,
                    partialTick, 1f, l, 0, 1024, 0xFFFFFF, 0.2f, 0.25f);
        }
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}

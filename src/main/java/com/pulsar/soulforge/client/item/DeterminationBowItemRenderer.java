package com.pulsar.soulforge.client.item;

import com.pulsar.soulforge.item.weapons.weapon_wheel.DeterminationBow;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class DeterminationBowItemRenderer extends GeoItemRenderer<DeterminationBow> {
    public DeterminationBowItemRenderer() {
        super(new DeterminationBowModel());
    }

    @Override
    public RenderLayer getRenderType(DeterminationBow animatable, Identifier texture,
                                     VertexConsumerProvider bufferSource,
                                     float partialTick) {
        return RenderLayer.getEntityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void preRender(MatrixStack poseStack, DeterminationBow animatable, BakedGeoModel model, VertexConsumerProvider bufferSource,
                          VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                          float red, float green, float blue, float alpha) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        GeoBone string0 = model.getBone("Stage0").orElse(null);
        GeoBone string1 = model.getBone("Stage1").orElse(null);
        GeoBone string2 = model.getBone("Stage2").orElse(null);
        GeoBone string3 = model.getBone("Stage3").orElse(null);
        if (string0 != null && string1 != null && string2 != null && string3 != null) {
            string0.setHidden(true);
            string1.setHidden(true);
            string2.setHidden(true);
            string3.setHidden(true);
            if (MinecraftClient.getInstance().player != null) {
                ItemStack using = MinecraftClient.getInstance().player.getActiveItem();
                if (using.getItem() instanceof DeterminationBow) {
                    float pullProgress = DeterminationBow.getPullProgress(MinecraftClient.getInstance().player.getItemUseTime());
                    if (pullProgress < 0.3f) {
                        string0.setHidden(false);
                    } else if (pullProgress < 0.6f) {
                        string1.setHidden(false);
                    } else if (pullProgress < 0.9f) {
                        string2.setHidden(false);
                    } else {
                        string3.setHidden(false);
                    }
                } else {
                    string0.setHidden(false);
                }
            } else {
                string0.setHidden(false);
            }
        }
    }
}

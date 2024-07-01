package com.pulsar.soulforge.client.item;

import com.pulsar.soulforge.entity.AntihealDartProjectile;
import com.pulsar.soulforge.item.devices.machines.Detonator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.Optional;

public class DetonatorRenderer extends GeoItemRenderer<Detonator> {
    static float SINE_45_DEGREES = (float)Math.sin(0.7853981633974483);

    public DetonatorRenderer() {
        super(new DetonatorModel());
    }

    /*@Override
    public void preRender(MatrixStack poseStack, Detonator animatable, BakedGeoModel model, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

        Optional<GeoBone> glass1 = model.getBone("ecglass1");
        Optional<GeoBone> glass2 = model.getBone("ecglass2");
        Optional<GeoBone> core = model.getBone("eccore");
        if (glass1.isPresent() && glass2.isPresent() && core.isPresent()) {
            Matrix4f glass1mat = glass1.get().getLocalSpaceMatrix();
            Matrix4f glass2mat = glass2.get().getLocalSpaceMatrix();
            Matrix4f coremat = core.get().getLocalSpaceMatrix();

            glass1mat.
        }
    }*/

    /*@Override
    public void postRender(MatrixStack matrixStack, Detonator animatable, BakedGeoModel model, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        matrixStack.push();
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderLayer.getEntityCutoutNoCull(Identifier.of("textures/entity/end_crystal/end_crystal.png")));
        matrixStack.push();
        matrixStack.scale(2.0F, 2.0F, 2.0F);
        matrixStack.translate(0.0F, -0.5F, 0.0F);
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("glass", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), ModelTransform.NONE);
        modelPartData.addChild("cube", ModelPartBuilder.create().uv(32, 0).cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), ModelTransform.NONE);
        TexturedModelData texturedModelData = TexturedModelData.of(modelData, 64, 32);
        ModelPart modelPart = texturedModelData.createModel();
        ModelPart frame = modelPart.getChild("glass");
        ModelPart core = modelPart.getChild("cube");
        int k = OverlayTexture.DEFAULT_UV;
        //matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
        matrixStack.multiply((new Quaternionf()).setAngleAxis(1.0471976F, SINE_45_DEGREES, 0.0F, SINE_45_DEGREES));
        frame.render(matrixStack, vertexConsumer, packedLight, k);
        float l = 0.875F;
        matrixStack.scale(0.875F, 0.875F, 0.875F);
        matrixStack.multiply((new Quaternionf()).setAngleAxis(1.0471976F, SINE_45_DEGREES, 0.0F, SINE_45_DEGREES));
        //matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
        frame.render(matrixStack, vertexConsumer, packedLight, k);
        matrixStack.scale(0.875F, 0.875F, 0.875F);
        matrixStack.multiply((new Quaternionf()).setAngleAxis(1.0471976F, SINE_45_DEGREES, 0.0F, SINE_45_DEGREES));
        //matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
        core.render(matrixStack, vertexConsumer, packedLight, k);
        matrixStack.pop();
        matrixStack.pop();

        super.postRender(matrixStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }*/
}

package com.pulsar.soulforge.client.block;

import com.pulsar.soulforge.block.SoulJarBlockEntity;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.trait.Traits;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class SoulJarEntityRenderer implements BlockEntityRenderer<SoulJarBlockEntity> {
    public SoulJarEntityRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(SoulJarBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.hasSoul) {
            matrices.push();
            double offset = Math.sin((entity.getWorld().getTime() + tickDelta) / 24) / 12;
            matrices.translate(0.5f, 0.25f + offset, 0.5f);
            matrices.scale(0.75f, 0.75f, 0.75f);

            Vec3d blockPos = entity.getPos().toCenterPos();
            Vec3d faceDir = blockPos.subtract(MinecraftClient.getInstance().player.getPos()).withAxis(Direction.Axis.Y, 0).normalize();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)Math.atan2(faceDir.x, faceDir.z)));

            int lightAbove = WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().up());
            ItemStack soulItem = SoulForgeItems.getSoulItem(Objects.requireNonNull(Traits.get(entity.trait1)),
                    Objects.requireNonNull(Objects.equals(entity.trait2, "") ? Traits.get(entity.trait1) : Traits.get(entity.trait2)));
            MinecraftClient.getInstance().getItemRenderer().renderItem(soulItem, ModelTransformationMode.GROUND, lightAbove, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), 0);
            matrices.pop();
        }
    }
}

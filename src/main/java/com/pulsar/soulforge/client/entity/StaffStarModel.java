package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.entity.DeterminationStaffStarProjectile;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class StaffStarModel extends EntityModel<DeterminationStaffStarProjectile> {
	private final ModelPart main;
	public StaffStarModel(ModelPart root) {
		this.main = root.getChild("main");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData main = modelPartData.addChild("main", ModelPartBuilder.create().uv(5, 6).cuboid(0.0F, -4.0F, -1.0F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F))
		.uv(3, 4).cuboid(0.0F, -3.0F, -2.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F))
		.uv(4, 6).cuboid(-1.0F, -3.0F, -1.0F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F))
		.uv(4, 6).cuboid(-1.0F, -6.0F, -1.0F, 3.0F, 2.0F, 1.0F, new Dilation(0.0F))
		.uv(3, 4).cuboid(0.0F, -6.0F, -2.0F, 1.0F, 2.0F, 3.0F, new Dilation(0.0F))
		.uv(2, 4).cuboid(-1.0F, -9.0F, -2.0F, 3.0F, 3.0F, 3.0F, new Dilation(0.0F))
		.uv(3, 4).cuboid(0.0F, -11.0F, -2.0F, 1.0F, 2.0F, 3.0F, new Dilation(0.0F))
		.uv(4, 6).cuboid(-1.0F, -11.0F, -1.0F, 3.0F, 2.0F, 1.0F, new Dilation(0.0F))
		.uv(5, 6).cuboid(0.0F, -15.0F, -1.0F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F))
		.uv(4, 6).cuboid(-1.0F, -13.0F, -1.0F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F))
		.uv(3, 4).cuboid(0.0F, -13.0F, -2.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F))
		.uv(3, 5).cuboid(-1.0F, -8.0F, -4.0F, 3.0F, 1.0F, 2.0F, new Dilation(0.0F))
		.uv(4, 5).cuboid(0.0F, -9.0F, -4.0F, 1.0F, 3.0F, 2.0F, new Dilation(0.0F))
		.uv(2, 3).cuboid(0.0F, -8.0F, -8.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F))
		.uv(5, 6).cuboid(0.0F, -9.0F, -6.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
		.uv(4, 6).cuboid(-1.0F, -8.0F, -6.0F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F))
		.uv(4, 5).cuboid(0.0F, -9.0F, 1.0F, 1.0F, 3.0F, 2.0F, new Dilation(0.0F))
		.uv(3, 5).cuboid(-1.0F, -8.0F, 1.0F, 3.0F, 1.0F, 2.0F, new Dilation(0.0F))
		.uv(2, 3).cuboid(0.0F, -8.0F, 3.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F))
		.uv(5, 6).cuboid(0.0F, -9.0F, 4.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
		.uv(4, 6).cuboid(-1.0F, -8.0F, 4.0F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F))
		.uv(2, 4).cuboid(2.0F, -8.0F, -2.0F, 2.0F, 1.0F, 3.0F, new Dilation(0.0F))
		.uv(4, 6).cuboid(2.0F, -9.0F, -1.0F, 2.0F, 3.0F, 1.0F, new Dilation(0.0F))
		.uv(4, 6).cuboid(4.0F, -8.0F, -1.0F, 4.0F, 1.0F, 1.0F, new Dilation(0.0F))
		.uv(4, 6).cuboid(5.0F, -9.0F, -1.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
		.uv(2, 4).cuboid(5.0F, -8.0F, -2.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F))
		.uv(4, 6).cuboid(-3.0F, -9.0F, -1.0F, 2.0F, 3.0F, 1.0F, new Dilation(0.0F))
		.uv(2, 4).cuboid(-3.0F, -8.0F, -2.0F, 2.0F, 1.0F, 3.0F, new Dilation(0.0F))
		.uv(4, 6).cuboid(-7.0F, -8.0F, -1.0F, 4.0F, 1.0F, 1.0F, new Dilation(0.0F))
		.uv(2, 4).cuboid(-5.0F, -8.0F, -2.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F))
		.uv(4, 6).cuboid(-5.0F, -9.0F, -1.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		return TexturedModelData.of(modelData, 16, 16);
	}
	@Override
	public void setAngles(DeterminationStaffStarProjectile entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		main.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}
package com.pulsar.soulforge.mixin;

import com.pulsar.soulforge.entity.DomeEntity;
import com.pulsar.soulforge.entity.DomePart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Inject(method = "renderHitbox", at=@At("HEAD"))
    private static void renderHitbox(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, CallbackInfo ci) {
        if (entity instanceof DomeEntity) {
            for (DomePart domePart : ((DomeEntity) entity).getParts()) {
                matrices.push();
                matrices.translate(entity.getX() + domePart.getX(), entity.getY() + domePart.getY(), entity.getZ() + domePart.getZ());
                WorldRenderer.drawBox(matrices, vertices, domePart.getBoundingBox().offset(-domePart.getX(), -domePart.getY(), -domePart.getZ()), 0.25f, 1.0f, 0.0f, 1.0f);
                matrices.pop();
            }
        }
    }
}

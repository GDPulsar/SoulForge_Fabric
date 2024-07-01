package com.pulsar.soulforge.mixin;

import com.pulsar.soulforge.effects.SoulForgeEffects;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> {
    protected LivingEntityRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Inject(method = "hasLabel(Lnet/minecraft/entity/LivingEntity;)Z", at=@At("HEAD"), cancellable = true)
    protected void hasLabel(T livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if (livingEntity.hasStatusEffect(SoulForgeEffects.SNOWED_VISION)) cir.setReturnValue(false);
    }

    @Override
    public Identifier getTexture(T entity) {
        return null;
    }
}

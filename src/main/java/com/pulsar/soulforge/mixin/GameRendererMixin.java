package com.pulsar.soulforge.mixin;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.ValueComponent;
import com.pulsar.soulforge.entity.ShieldShardEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.SynchronousResourceReloader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.function.Predicate;

@Mixin(GameRenderer.class)
abstract class GameRendererMixin implements SynchronousResourceReloader {
    @Shadow @Final private MinecraftClient client;

    @Unique
    private boolean wasForcedThirdPerson = false;

    @Inject(method = "renderWorld", at=@At("HEAD"))
    private void beforeCameraSetup(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci) {
        PlayerEntity player = this.client.player;
        if (player != null) {
            ValueComponent values = SoulForge.getValues(player);
            if (values != null) {
                if (values.getBool("forcedThirdPerson")) {
                    this.client.options.setPerspective(Perspective.THIRD_PERSON_BACK);
                    wasForcedThirdPerson = true;
                } else if (wasForcedThirdPerson) {
                    this.client.options.setPerspective(Perspective.FIRST_PERSON);
                    wasForcedThirdPerson = false;
                }
            }
        }
    }

    @ModifyArgs(method = "updateTargetedEntity", at=@At(value="INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileUtil;raycast(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;D)Lnet/minecraft/util/hit/EntityHitResult;"))
    private void modifyTargetedEntity(Args args) {
        Predicate<Entity> predicate = args.get(4);
        args.set(4, predicate.and((entity) -> {
            if (entity instanceof ShieldShardEntity shieldShard && shieldShard.owner != null) {
                if (shieldShard.owner.isSneaking()) return true;
                return shieldShard.owner.getUuid() != ((Entity)args.get(0)).getUuid();
            }
            return true;
        }));
    }
}

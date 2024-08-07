package com.pulsar.soulforge.mixin;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.accessors.SiphonableEntity;
import com.pulsar.soulforge.siphon.Siphon;
import net.minecraft.client.render.entity.TridentEntityRenderer;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(TridentEntityRenderer.class)
public class TridentEntityRendererMixin {
    @Inject(method = "getTexture(Lnet/minecraft/entity/projectile/TridentEntity;)Lnet/minecraft/util/Identifier;", at = @At("HEAD"), cancellable = true)
    private void modifyTridentTexture(TridentEntity tridentEntity, CallbackInfoReturnable<Identifier> cir) {
        String siphon = ((SiphonableEntity)tridentEntity).getSiphonString();
        if (!Objects.equals(siphon, "")) {
            Siphon.Type type = Siphon.Type.getSiphon(siphon);
            if (type != null) {
                cir.setReturnValue(new Identifier(SoulForge.MOD_ID, "textures/entity/" + siphon + "_trident.png"));
            }
        }
    }
}

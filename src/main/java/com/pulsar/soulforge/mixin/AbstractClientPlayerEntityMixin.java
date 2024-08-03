package com.pulsar.soulforge.mixin;

import com.mojang.authlib.GameProfile;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.animation.ISoulForgeAnimatedPlayer;
import com.pulsar.soulforge.components.SoulComponent;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin implements ISoulForgeAnimatedPlayer {
    @Unique
    private final ModifierLayer<IAnimation> modAnimationContainer = new ModifierLayer<>();

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void init(ClientWorld world, GameProfile profile, CallbackInfo ci) {
        PlayerAnimationAccess.getPlayerAnimLayer((AbstractClientPlayerEntity) (Object)this).addAnimLayer(1000, modAnimationContainer);
    }

    @Override
    public ModifierLayer<IAnimation> soulforge_getModAnimation() {
        return modAnimationContainer;
    }

    @Inject(method = "getSkinTexture", at = @At("HEAD"), cancellable = true)
    public void getSkinTexture(CallbackInfoReturnable<Identifier> cir) {
        AbstractClientPlayerEntity player = (AbstractClientPlayerEntity)(Object)this;
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (playerSoul.getDisguise() != null) {
            cir.setReturnValue(DefaultSkinHelper.getTexture(playerSoul.getDisguise().getUuid()));
        }
    }
}

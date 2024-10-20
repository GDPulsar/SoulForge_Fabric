package com.pulsar.soulforge.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.patience.BlindingSnowstorm;
import com.pulsar.soulforge.client.ui.ANOTHERHIM;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.components.ValueComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.trait.Traits;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.resource.ResourceReload;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.client.MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow @Nullable public ClientPlayerEntity player;

    @Shadow public abstract void setScreen(@Nullable Screen screen);

    @Inject(method = "hasOutline", at=@At("HEAD"), cancellable = true)
    public void hasOutline(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (this.player != null) {
            if (this.player.hasStatusEffect(SoulForgeEffects.SNOWED_VISION)) {
                SoulComponent playerSoul = SoulForge.getPlayerSoul(this.player);
                if (playerSoul.hasTrait(Traits.patience) && playerSoul.hasTrait(Traits.perseverance)) {
                    if (entity instanceof LivingEntity) {
                        for (AbilityBase ability : playerSoul.getActiveAbilities()) {
                            if (ability instanceof BlindingSnowstorm snowstorm) {
                                if (snowstorm.location.distanceTo(entity.getPos()) < 140f) {
                                    cir.setReturnValue(true);
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void soulforge$cancelSetScreen(Screen screen, CallbackInfo ci) {
        if (this.player != null) {
            ValueComponent values = SoulForge.getValues(this.player);
            if (values.getBool("resettingSoul")) {
                ci.cancel();
            }
        }
    }

    @WrapOperation(method = "handleInputEvents", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerInventory;selectedSlot:I", opcode = Opcodes.PUTFIELD))
    private void soulforge$onHotbarKeyPress(PlayerInventory instance, int value, Operation<Void> original) {
        if (this.player != null) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(this.player);
            if (playerSoul.magicModeActive()) {
                playerSoul.setAbilitySlot(value);
                return;
            }
        }
        original.call(instance, value);
    }

    @Inject(method = "onInitFinished", at = @At("HEAD"), cancellable = true)
    private void THEYRE_COMING(RealmsClient realms, ResourceReload reload, RunArgs.QuickPlay quickPlay, CallbackInfo ci) {
        if (Math.random() < 0.001) {
            this.setScreen(new ANOTHERHIM());
            ci.cancel();
        }
    }
}

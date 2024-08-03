package com.pulsar.soulforge.mixin;

import com.mojang.authlib.GameProfile;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.entity.PlayerSoulEntity;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "dropSelectedItem", at=@At("HEAD"))
    public void dropSelectedItem(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (player.getInventory().selectedSlot == 9) {
            playerSoul.removeWeapon();
        }
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeath(DamageSource damageSource, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;
        PlayerSoulEntity soulEntity = new PlayerSoulEntity(player);
        soulEntity.setPosition(player.getPos().add(0f, 0.75f, 0f));
        player.getServerWorld().spawnEntity(soulEntity);
    }

    @Inject(method = "wakeUp", at = @At("HEAD"), cancellable = true)
    private void soulforge$canWakeUp(CallbackInfo ci) {
        if (this.hasStatusEffect(SoulForgeEffects.EEPY)) {
            if (this.isSleepingInBed()) {
                ci.cancel();
            }
        }
    }
}

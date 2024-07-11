package com.pulsar.soulforge.mixin;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.siphon.Siphon;
import com.pulsar.soulforge.siphon.Siphon.Type;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentProjectileEntity.class)
public class PersistentProjectileEntityMixin {
    @Inject(method = "onBlockHit", at = @At("HEAD"))
    private void injectOnBlockHit(BlockHitResult blockHitResult, CallbackInfo ci) {
        PersistentProjectileEntity projectile = (PersistentProjectileEntity)(Object)this;
        if (projectile instanceof TridentEntity trident) {
            NbtCompound nbt = trident.tridentStack.getOrCreateNbt();
            SoulForge.LOGGER.info("trident nbt: {}, owner: {}", nbt, trident.getOwner());
            if (nbt.contains("Siphon")) {
                Siphon.Type siphonType = Siphon.Type.getSiphon(nbt.getString("Siphon"));
                if (siphonType == Type.BRAVERY) {
                    if (trident.getOwner() instanceof PlayerEntity player) {
                        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                        if (playerSoul.getMagic() >= 20f) {
                            if (trident.getWorld() instanceof ServerWorld && trident.getWorld().isThundering() && trident.hasChanneling()) {
                                BlockPos blockPos = trident.getBlockPos();
                                if (trident.getWorld().isSkyVisible(blockPos)) {
                                    LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(trident.getWorld());
                                    if (lightningEntity != null) {
                                        lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(blockPos));
                                        lightningEntity.setChanneler(player instanceof ServerPlayerEntity ? (ServerPlayerEntity)player : null);
                                        trident.getWorld().spawnEntity(lightningEntity);
                                        trident.playSound(SoundEvents.ITEM_TRIDENT_THUNDER, 5f, 1f);
                                        playerSoul.setMagic(playerSoul.getMagic() - 20f);
                                        playerSoul.resetLastCastTime();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

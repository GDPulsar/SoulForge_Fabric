package com.pulsar.soulforge.mixin;

import com.pulsar.soulforge.item.devices.machines.SiphonImbuer;
import com.pulsar.soulforge.siphon.Siphon;
import com.pulsar.soulforge.tag.SoulForgeTags;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TridentItem.class)
public abstract class TridentItemMixin {
    @Shadow public abstract int getMaxUseTime(ItemStack stack);

    @Inject(method = "onStoppedUsing", at=@At("HEAD"), cancellable = true)
    private void useJusticeTrident(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
        if (stack.getOrCreateNbt().contains("Siphon")) {
            Siphon.Type siphonType = Siphon.Type.getSiphon(stack.getOrCreateNbt().getString("Siphon"));
            if (user instanceof PlayerEntity playerEntity) {
                int useTicks = this.getMaxUseTime(stack) - remainingUseTicks;
                if (useTicks >= 10) {
                    int riptide = EnchantmentHelper.getRiptide(stack);
                    if (riptide <= 0) {
                        if (siphonType == Siphon.Type.JUSTICE) {
                            TridentEntity tridentEntity = new TridentEntity(world, playerEntity, stack);
                            Vec3d start = playerEntity.getEyePos();
                            Vec3d end = playerEntity.getEyePos().add(playerEntity.getRotationVector().multiply(200f));
                            HitResult hitResult = ProjectileUtil.getCollision(start, playerEntity, tridentEntity::canHit, playerEntity.getRotationVector().multiply(200f), world);
                            if (hitResult.getType() != HitResult.Type.MISS) {
                                end = hitResult.getPos();
                            }
                            tridentEntity.setPosition(end.subtract(playerEntity.getRotationVector()));
                            tridentEntity.setVelocity(playerEntity.getRotationVector().multiply(2f));
                            if (playerEntity.getAbilities().creativeMode) {
                                tridentEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                            }

                            world.spawnEntity(tridentEntity);
                            world.playSoundFromEntity(null, playerEntity, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 0.8F);
                            if (!playerEntity.getAbilities().creativeMode) {
                                playerEntity.getInventory().removeOne(stack);
                            }
                            if (!world.isClient) {
                                double distance = start.distanceTo(end);
                                int particleCount = (int) (distance * 10);
                                ServerWorld serverWorld = (ServerWorld) world;
                                for (int i = 0; i < particleCount; i++) {
                                    Vec3d pos = start.lerp(end, i / (float) particleCount);
                                    serverWorld.spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0xFFFF00).toVector3f(), 0.5f), pos.x, pos.y, pos.z, 1, 0f, 0f, 0f, 0f);
                                }
                            }
                            ci.cancel();
                        }
                        if (siphonType == Siphon.Type.PATIENCE) {
                            stack.getOrCreateNbt().putInt("useLevel", MathHelper.clamp(useTicks / 10, 1, 4));
                        }
                    } else if (playerEntity.isTouchingWaterOrRain()) {
                        if (siphonType == Siphon.Type.JUSTICE) {
                            float expectedVelocity = 0.75f * (1 + riptide);
                            Vec3d start = playerEntity.getPos();
                            Vec3d end = playerEntity.getPos().add(playerEntity.getRotationVector().multiply(20f * expectedVelocity));
                            HitResult hitResult = world.raycast(new RaycastContext(start, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, playerEntity));
                            if (hitResult.getType() != HitResult.Type.MISS) {
                                end = hitResult.getPos();
                            }
                            playerEntity.teleport(end.x, end.y, end.z);
                            playerEntity.setVelocity(playerEntity.getRotationVector().multiply(expectedVelocity));
                            playerEntity.velocityModified = true;
                            ci.cancel();
                        }
                        if (siphonType == Siphon.Type.KINDNESS) {
                            for (PlayerEntity nearby : world.getEntitiesByClass(PlayerEntity.class, Box.of(user.getPos(), 30, 30, 30), entity -> true)) {
                                nearby.addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 400));
                                nearby.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 400));
                            }
                        }
                    }
                }
            }
        }
    }

    @Redirect(method = "onStoppedUsing", at=@At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isTouchingWaterOrRain()Z"))
    private boolean modifyStoppedUsing(PlayerEntity player) {
        ItemStack stack;
        if (player.getMainHandStack().isIn(SoulForgeTags.IMBUER_TRIDENTS)) stack = player.getMainHandStack();
        else stack = player.getOffHandStack();
        if (!player.isTouchingWaterOrRain()) {
            if (stack.getNbt() != null) {
                if (stack.getNbt().getBoolean("imbued")) {
                    ItemStack imbuerStack = Utils.getImbuer(stack, player);
                    if (imbuerStack != null) {
                        if (((SiphonImbuer) imbuerStack.getItem()).getCharge(imbuerStack) >= 10) {
                            ((SiphonImbuer) imbuerStack.getItem()).decreaseCharge(imbuerStack, 10);
                            return true;
                        }
                    }
                }
            }
        }
        return player.isTouchingWaterOrRain();
    }

    @Redirect(method = "use", at=@At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isTouchingWaterOrRain()Z"))
    private boolean modifyUse(PlayerEntity player) {
        ItemStack stack;
        if (player.getMainHandStack().isIn(SoulForgeTags.IMBUER_TRIDENTS)) stack = player.getMainHandStack();
        else stack = player.getOffHandStack();
        if (!player.isTouchingWaterOrRain()) {
            if (stack.getNbt() != null) {
                if (stack.getNbt().getBoolean("imbued")) {
                    ItemStack imbuerStack = Utils.getImbuer(stack, player);
                    if (imbuerStack != null) {
                        if (((SiphonImbuer) imbuerStack.getItem()).getCharge(imbuerStack) >= 10) {
                            return true;
                        }
                    }
                }
            }
        }
        return player.isTouchingWaterOrRain();
    }
}

package com.pulsar.soulforge.mixin;

import com.google.common.collect.ImmutableMultimap;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import com.llamalad7.mixinextras.sugar.Local;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.devices.machines.SiphonImbuer;
import com.pulsar.soulforge.siphon.Siphon;
import com.pulsar.soulforge.tag.SoulForgeTags;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.TridentItem;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.UUID;

@Mixin(TridentItem.class)
public abstract class TridentItemMixin {
    @Shadow public abstract int getMaxUseTime(ItemStack stack);

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMultimap$Builder;build()Lcom/google/common/collect/ImmutableMultimap;"))
    private ImmutableMultimap<EntityAttribute, EntityAttributeModifier> onInit(ImmutableMultimap.Builder instance) {
        instance.put(ReachEntityAttributes.ATTACK_RANGE, new EntityAttributeModifier(UUID.randomUUID(), "Range modifier", 2.0, EntityAttributeModifier.Operation.ADDITION));
        return instance.build();
    }

    @Inject(method = "onStoppedUsing", at=@At("HEAD"), cancellable = true)
    private void throwTrident(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
        if (stack.getOrCreateNbt().contains("Siphon")) {
            Siphon.Type siphonType = Siphon.Type.getSiphon(stack.getOrCreateNbt().getString("Siphon"));
            if (user instanceof PlayerEntity playerEntity) {
                int useTicks = this.getMaxUseTime(stack) - remainingUseTicks;
                if (useTicks >= (siphonType == Siphon.Type.PERSEVERANCE ? 20 : 10)) {
                    int riptide = EnchantmentHelper.getRiptide(stack);
                    if (riptide <= 0) {
                        if (siphonType == Siphon.Type.JUSTICE || siphonType == Siphon.Type.SPITE) {
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
                        if (siphonType == Siphon.Type.PATIENCE || siphonType == Siphon.Type.SPITE) {
                            stack.getOrCreateNbt().putInt("useLevel", MathHelper.clamp(useTicks / 10, 1, 4));
                        }
                        if (siphonType == Siphon.Type.DETERMINATION || siphonType == Siphon.Type.SPITE) {
                            SoulComponent playerSoul = SoulForge.getPlayerSoul(playerEntity);
                            if (user.isSneaking() && playerSoul.getMagic() >= 10f) {
                                ItemStack stackCopy = stack.copy();
                                Map<Enchantment, Integer> enchants = EnchantmentHelper.fromNbt(stackCopy.getEnchantments());
                                enchants.remove(Enchantments.LOYALTY);
                                enchants.forEach(stackCopy::addEnchantment);
                                TridentEntity tridentEntity = new TridentEntity(world, playerEntity, stackCopy);
                                tridentEntity.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0F, 2.5F, 1.0F);
                                tridentEntity.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;

                                world.spawnEntity(tridentEntity);
                                world.playSoundFromEntity(null, playerEntity, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 0.8F);
                                playerSoul.setMagic(playerSoul.getMagic() - 10f);
                                playerSoul.resetLastCastTime();
                                ci.cancel();
                            }
                        }
                    } else if (playerEntity.isTouchingWaterOrRain()) {
                        if (siphonType == Siphon.Type.JUSTICE || siphonType == Siphon.Type.SPITE) {
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
                        if (siphonType == Siphon.Type.KINDNESS || siphonType == Siphon.Type.SPITE) {
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

    @ModifyConstant(method = "onStoppedUsing", constant = @Constant(intValue = 10))
    private int modifyRequiredUseTime(int original, @Local ItemStack stack) {
        if (stack.getOrCreateNbt().contains("Siphon")) {
            Siphon.Type siphonType = Siphon.Type.getSiphon(stack.getOrCreateNbt().getString("Siphon"));
            if (siphonType == Siphon.Type.PERSEVERANCE || siphonType == Siphon.Type.SPITE) {
                return 20;
            }
        }
        return original;
    }

    @ModifyArg(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/TridentEntity;setVelocity(Lnet/minecraft/entity/Entity;FFFFF)V"), index = 4)
    private float modifyThrownSpeed(float original, @Local ItemStack stack) {
        if (stack.getOrCreateNbt().contains("Siphon")) {
            Siphon.Type siphonType = Siphon.Type.getSiphon(stack.getOrCreateNbt().getString("Siphon"));
            if (siphonType == Siphon.Type.PERSEVERANCE || siphonType == Siphon.Type.SPITE) {
                return original * 1.25f;
            }
        }
        return original;
    }

    @Redirect(method = "onStoppedUsing", at=@At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isTouchingWaterOrRain()Z"))
    private boolean modifyTouchingWaterOrRain(PlayerEntity player) {
        ItemStack stack;
        if (player.getMainHandStack().isIn(SoulForgeTags.IMBUER_TRIDENTS)) stack = player.getMainHandStack();
        else stack = player.getOffHandStack();
        if (!player.isTouchingWaterOrRain()) {
            if (stack.getOrCreateNbt().contains("imbued") && stack.getOrCreateNbt().getBoolean("imbued")) {
                ItemStack imbuerStack = Utils.getImbuer(stack, player);
                if (imbuerStack != null) {
                    if (((SiphonImbuer) imbuerStack.getItem()).getCharge(imbuerStack) >= 10) {
                        ((SiphonImbuer) imbuerStack.getItem()).decreaseCharge(imbuerStack, 10);
                        return true;
                    }
                }
            }
            if (stack.getOrCreateNbt().contains("Siphon")) {
                Siphon.Type siphonType = Siphon.Type.getSiphon(stack.getOrCreateNbt().getString("Siphon"));
                if (siphonType == Siphon.Type.DETERMINATION || siphonType == Siphon.Type.SPITE) {
                    SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                    if (playerSoul.getMagic() >= 25f) {
                        playerSoul.setMagic(playerSoul.getMagic() - 25f);
                        playerSoul.resetLastCastTime();
                        return true;
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

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void onUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (itemStack.getOrCreateNbt().contains("Siphon")) {
            Siphon.Type type = Siphon.Type.getSiphon(itemStack.getOrCreateNbt().getString("Siphon"));
            if (type == Siphon.Type.INTEGRITY || type == Siphon.Type.SPITE) {
                if (itemStack.getDamage() < itemStack.getMaxDamage() - 1) {
                    int j = EnchantmentHelper.getRiptide(itemStack);
                    if (j == 0) {
                        TridentEntity tridentEntity = new TridentEntity(world, user, itemStack);
                        tridentEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 2.5F + (float) j * 0.5F, 1.0F);
                        if (user.getAbilities().creativeMode) {
                            tridentEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                        }

                        world.spawnEntity(tridentEntity);
                        world.playSoundFromEntity(null, tridentEntity, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
                        if (!user.getAbilities().creativeMode) {
                            user.getInventory().removeOne(itemStack);
                        }
                        user.getItemCooldownManager().set(Items.TRIDENT, 20);
                    } else if (j > 0 && user.isTouchingWaterOrRain()) {
                        float f = user.getYaw();
                        float g = user.getPitch();
                        float h = -MathHelper.sin(f * 0.017453292F) * MathHelper.cos(g * 0.017453292F);
                        float k = -MathHelper.sin(g * 0.017453292F);
                        float l = MathHelper.cos(f * 0.017453292F) * MathHelper.cos(g * 0.017453292F);
                        float m = MathHelper.sqrt(h * h + k * k + l * l);
                        float n = 3.0F * ((1.0F + (float) j) / 4.0F);
                        h *= n / m;
                        k *= n / m;
                        l *= n / m;
                        user.addVelocity(h, k, l);
                        user.useRiptide(20);
                        if (user.isOnGround()) {
                            user.move(MovementType.SELF, new Vec3d(0.0, 1.1999999284744263, 0.0));
                        }

                        SoundEvent soundEvent;
                        if (j >= 3) {
                            soundEvent = SoundEvents.ITEM_TRIDENT_RIPTIDE_3;
                        } else if (j == 2) {
                            soundEvent = SoundEvents.ITEM_TRIDENT_RIPTIDE_2;
                        } else {
                            soundEvent = SoundEvents.ITEM_TRIDENT_RIPTIDE_1;
                        }

                        world.playSoundFromEntity(null, user, soundEvent, SoundCategory.PLAYERS, 1.0F, 1.0F);
                        user.getItemCooldownManager().set(Items.TRIDENT, 20);
                    }
                    cir.setReturnValue(TypedActionResult.consume(itemStack));
                }
            }
        }
    }
}

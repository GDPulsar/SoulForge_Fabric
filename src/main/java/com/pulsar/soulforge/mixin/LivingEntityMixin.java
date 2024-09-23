package com.pulsar.soulforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.accessors.HasTickManager;
import com.pulsar.soulforge.accessors.OwnableMinion;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.components.ValueComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.entity.DeterminationPlatformEntity;
import com.pulsar.soulforge.entity.IntegrityPlatformEntity;
import com.pulsar.soulforge.event.LivingDamageEvent;
import com.pulsar.soulforge.event.LivingDeathEvent;
import com.pulsar.soulforge.event.LivingEntityTick;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.item.devices.devices.RevivalIdol;
import com.pulsar.soulforge.shield.ShieldBlockCallback;
import com.pulsar.soulforge.siphon.Siphon;
import com.pulsar.soulforge.siphon.Siphon.Type;
import com.pulsar.soulforge.tag.SoulForgeTags;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow public abstract ItemStack getMainHandStack();

    @Shadow public abstract ItemStack getStackInHand(Hand hand);

    @Shadow public abstract boolean addStatusEffect(StatusEffectInstance effect);

    @Shadow public abstract void setHealth(float health);

    @Shadow public abstract boolean clearStatusEffects();

    @Shadow public abstract boolean hasStatusEffect(StatusEffect effect);

    @Shadow protected abstract boolean isSleepingInBed();

    @Shadow @Nullable public abstract StatusEffectInstance getStatusEffect(StatusEffect effect);

    @Shadow @Nullable public abstract EntityAttributeInstance getAttributeInstance(EntityAttribute attribute);

    @Shadow public abstract float getBodyYaw();

    @Shadow protected boolean jumping;

    @Shadow public float sidewaysSpeed;

    @Shadow public float forwardSpeed;

    @Shadow public abstract double getAttributeValue(EntityAttribute attribute);

    @Shadow public abstract AttributeContainer getAttributes();

    @Shadow public abstract double getAttributeValue(RegistryEntry<EntityAttribute> attribute);

    @ModifyReturnValue(method = "isBlocking", at=@At("RETURN"))
    public boolean parryBlocking(boolean original) {
        LivingEntity living = (LivingEntity)(Object)this;
        if (living.isUsingItem() && living.getActiveItem().isIn(SoulForgeTags.PARRY_ITEMS)) {
            return true;
        }
        return original;
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void whenDamaged(DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        if (!LivingDamageEvent.onTakeDamage((LivingEntity)(Object)this, source, damage)) {
            cir.setReturnValue(false);
        }
    }

    /*@ModifyExpressionValue(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageSource;isIn(Lnet/minecraft/registry/tag/TagKey;)Z", ordinal = 3))
    private boolean soulforge$modifyBypassesCooldown(boolean original, @Local DamageSource source) {
        if (source.getAttacker() instanceof PlayerEntity player) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            if (playerSoul.hasValue("rampageTimer") && playerSoul.hasValue("rampageActive")) {
                if (playerSoul.getValue("rampageActive") == 4) {
                    return true;
                }
            }
        }
        return original;
    }*/

    @Inject(method = "onKilledBy", at = @At("HEAD"))
    private void whenKilled(LivingEntity adversary, CallbackInfo ci) {
        LivingDeathEvent.onDeath((LivingEntity)(Object)this);
        LivingDeathEvent.onKilledBy((LivingEntity)(Object)this, adversary);
    }

    @ModifyReturnValue(method="getJumpVelocity", at=@At("RETURN"))
    private float addJumpVelocityIncrease(float original) {
        float multiplier = 1f;
        multiplier *= (float)this.getAttributeValue(SoulForgeAttributes.JUMP_MULTIPLIER);
        if (this.isOnGround()) {
            Box box = this.getBoundingBox();
            for (Entity other : this.getEntityWorld().getOtherEntities((LivingEntity)(Object)this, box.expand(0.0001))) {
                if (this.collidesWith(other)) {
                    if (other instanceof DeterminationPlatformEntity platform && platform.getStack() == 1) multiplier *= 2.5f;
                    if (other instanceof DeterminationPlatformEntity platform && platform.getStack() == 2) multiplier *= 4f;
                    if (other instanceof IntegrityPlatformEntity platform && platform.getStack() == 1) multiplier *= 2.5f;
                    if (other instanceof IntegrityPlatformEntity platform && platform.getStack() == 2) multiplier *= 4f;
                    break;
                }
            }
        }
        return original * multiplier;
    }

    @ModifyVariable(method = "travel", at=@At(value="STORE"), ordinal = 0)
    private float modifySlipperiness(float slipperiness) {
        float slipMultiplier = (float)this.getAttributeValue(SoulForgeAttributes.SLIP_MODIFIER);
        // actually what the fuck is this
        float newSlipperiness = slipperiness;
        if (slipMultiplier > 0f) newSlipperiness = 1f/((-1f-slipMultiplier)/(1-slipperiness)) + 1f;
        else if (slipMultiplier < 0f) newSlipperiness = 1f/(slipMultiplier/slipperiness);
        return newSlipperiness;
    }

    @ModifyReturnValue(method = "canWalkOnFluid", at = @At("RETURN"))
    private boolean soulforge$canWalkOnWater(boolean original, @Local FluidState fluid) {
        if ((LivingEntity)(Object)this instanceof PlayerEntity && !fluid.isOf(Fluids.EMPTY)) {
            if (this.getAttributes().hasAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED)) {
                try {
                    if (this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) > 0.3) {
                        if (fluid.isIn(FluidTags.LAVA)) {
                            return this.isFireImmune();
                        } else {
                            return true;
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return original;
    }

    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(DDD)V", shift = At.Shift.AFTER))
    protected void soulforge$modifyImmobility(CallbackInfo ci) {
        if (!this.canMoveVoluntarily()) {
            ValueComponent values = SoulForge.getValues((LivingEntity) (Object) this);
            if (values.getBool("Immobilized")) {
                this.jumping = false;
                this.sidewaysSpeed = 0.0F;
                this.forwardSpeed = 0.0F;
            }
        }
    }

    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;tickNewAi()V", shift = At.Shift.AFTER))
    protected void soulforge$resetImmobilityMovement(CallbackInfo ci) {
        ValueComponent values = SoulForge.getValues((LivingEntity)(Object)this);
        if (values.getBool("Immobilized")) {
            this.jumping = false;
            this.sidewaysSpeed = 0.0F;
            this.forwardSpeed = 0.0F;
        }
    }

    @ModifyConstant(method = "travel", constant = @Constant(doubleValue = 0.08))
    private double modifyEntityGravity(double baseGravity) {
        LivingEntity living = (LivingEntity)(Object)this;
        ValueComponent values = SoulForge.getValues(living);
        if (values != null) {
            if (values.getBool("Immobilized")) return 0f;
        }
        return baseGravity * this.getAttributeValue(SoulForgeAttributes.GRAVITY_MODIFIER);
    }

    @Inject(method = "disablesShield", at=@At("RETURN"), cancellable=true)
    protected void addShieldBreakers(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValue() || this.getMainHandStack().isIn(SoulForgeTags.BREAKS_SHIELD));
    }

    @Inject(method = "damage", at=@At(value = "HEAD"))
    private void invokeEvent(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity)(Object)this;
        ItemStack activeItem = entity.getActiveItem();
        ShieldBlockCallback.EVENT.invoker().block(entity, source, amount, entity.getActiveHand(), activeItem);
    }

    @Inject(method = "damage", at = @At(value = "TAIL"))
    private void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callbackInfo) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!entity.isInvulnerableTo(source) || !entity.getWorld().isClient || !entity.isDead() || !(source.isIn(DamageTypeTags.IS_FIRE) && entity.hasStatusEffect(StatusEffects.FIRE_RESISTANCE))) {
            if (amount > 0.0F && ((LivingEntityAccessor) entity).invokeBlockedByShield(source)) {
                ((LivingEntityAccessor) entity).invokeDamageShield(amount);
                if (!source.isIn(DamageTypeTags.IS_PROJECTILE)) {
                    Entity sourceEntity = source.getSource();

                    if (sourceEntity instanceof LivingEntity) {
                        ((LivingEntityAccessor) entity).invokeTakeShieldHit((LivingEntity) sourceEntity);
                    }
                }
            }
        }
    }

    @Redirect(method = "jump", at=@At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V"))
    protected void jump(LivingEntity living, Vec3d vel) {
        if (living instanceof PlayerEntity player) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            if (playerSoul.hasCast("Fearless Instincts")) {
                Vec3d vec3d = this.getVelocity();
                float angle = (float) (Math.atan2(-vec3d.z, -vec3d.x) + Math.PI / 2);
                this.setVelocity(this.getVelocity().add((-MathHelper.sin(angle) * 0.2F), 0.0, (MathHelper.cos(angle) * 0.2F)));
                return;
            }
        }
        this.setVelocity(vel);
    }

    @ModifyReturnValue(method = "tryUseTotem", at=@At("RETURN"))
    private boolean tryUseTotem(boolean original, @Local DamageSource source) {
        if (original) return true;
        if (!source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            ItemStack mainHand = this.getStackInHand(Hand.MAIN_HAND);
            ItemStack offHand = this.getStackInHand(Hand.OFF_HAND);
            ItemStack stack = null;
            if (mainHand.isOf(SoulForgeItems.REVIVAL_IDOL)) {
                RevivalIdol idol = (RevivalIdol)mainHand.getItem();
                if (idol.getCharge(mainHand) >= 300) {
                    idol.decreaseCharge(mainHand, 300);
                    stack = mainHand;
                }
            } else if (offHand.isOf(SoulForgeItems.REVIVAL_IDOL)) {
                RevivalIdol idol = (RevivalIdol)offHand.getItem();
                if (idol.getCharge(offHand) >= 300) {
                    idol.decreaseCharge(offHand, 300);
                    stack = offHand;
                }
            }

            LivingEntity living = (LivingEntity) (Object) this;
            if (stack != null) {
                if (living instanceof ServerPlayerEntity player) {
                    player.incrementStat(Stats.USED.getOrCreateStat(Items.TOTEM_OF_UNDYING));
                    Criteria.USED_TOTEM.trigger(player, stack);
                    this.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH);
                }

                this.setHealth(1f);
                this.clearStatusEffects();
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0));
                this.getWorld().sendEntityStatus(this, (byte) 35);
                return true;
            }
        }
        return false;
    }

    @ModifyVariable(method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;)Z", at=@At("HEAD"), argsOnly = true)
    private StatusEffectInstance modifyStatusEffect(StatusEffectInstance effect) {
        if (this.hasStatusEffect(SoulForgeEffects.SNOWED_VISION) && !effect.getEffectType().isBeneficial()) {
            return new StatusEffectInstance(effect.getEffectType(), effect.getDuration(), effect.getAmplifier()+1);
        }
        return effect;
    }

    @Inject(method = "createLivingAttributes", require = 1, allow = 1, at = @At("RETURN"))
    private static void addAttributes(final CallbackInfoReturnable<DefaultAttributeContainer.Builder> info) {
        info.getReturnValue()
                .add(SoulForgeAttributes.DAMAGE_REDUCTION)
                .add(SoulForgeAttributes.KNOCKBACK_MULTIPLIER)
                .add(SoulForgeAttributes.SLIP_MODIFIER)
                .add(SoulForgeAttributes.EFFECT_DURATION_MULTIPLIER)
                .add(SoulForgeAttributes.ANTIHEAL)
                .add(SoulForgeAttributes.AIR_SPEED_BECAUSE_MOJANG_SUCKS)
                .add(SoulForgeAttributes.JUMP_MULTIPLIER)
                .add(SoulForgeAttributes.FALL_DAMAGE_MULTIPLIER)
                .add(SoulForgeAttributes.GRAVITY_MODIFIER)
                .add(SoulForgeAttributes.STEP_HEIGHT)
                .add(SoulForgeAttributes.SHIELD_BREAK);
    }

    @ModifyReturnValue(method = "getOffGroundSpeed", at=@At("RETURN"))
    protected float modifyOffGroundSpeed(float original) {
        LivingEntity living = (LivingEntity)(Object)this;
        if (living.getAttributes().hasAttribute(SoulForgeAttributes.AIR_SPEED_BECAUSE_MOJANG_SUCKS)) {
            return (float)(original * living.getAttributeValue(SoulForgeAttributes.AIR_SPEED_BECAUSE_MOJANG_SUCKS));
        }
        return original;
    }

    @Redirect(method = "tickRiptide", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;union(Lnet/minecraft/util/math/Box;)Lnet/minecraft/util/math/Box;"))
    private Box modifyRiptideCollisionBox(Box a, Box b) {
        Box box = a.union(b);
        if (this.getMainHandStack().isOf(Items.TRIDENT)) {
            NbtCompound nbt = this.getMainHandStack().getOrCreateNbt();
            if (nbt.contains("Siphon")) {
                Siphon.Type siphonType = Siphon.Type.getSiphon(nbt.getString("Siphon"));
                if (siphonType == Type.BRAVERY) {
                    return box.expand(2);
                }
            }
        }
        return box;
    }

    @ModifyArg(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damageShield(F)V"))
    private float modifyShieldDamage(float original, @Local DamageSource source) {
        if (source.getAttacker() instanceof LivingEntity living) {
            return original * (float)living.getAttributeValue(SoulForgeAttributes.SHIELD_BREAK);
        }
        return original;
    }

    @ModifyReturnValue(method = "canTarget(Lnet/minecraft/entity/LivingEntity;)Z", at = @At("RETURN"))
    private boolean modifyCanTarget(boolean original, @Local LivingEntity target) {
        if ((LivingEntity)(Object)this instanceof MobEntity mobEntity) {
            if (((OwnableMinion)mobEntity).getOwnerUUID() != null) {
                if (((OwnableMinion)mobEntity).getOwnerUUID().compareTo(target.getUuid()) == 0) return false;
            }
        }
        return original;
    }



    @Inject(method = "tick", at = @At("HEAD"))
    private void soulforge$onEntityTick(CallbackInfo ci) {
        LivingEntityTick.tick((LivingEntity)(Object)this);
    }

    @Inject(method = "tickCramming", at = @At("HEAD"), cancellable = true)
    private void canTickCramming(CallbackInfo ci) {
        if (!((HasTickManager)this.getWorld()).getTickManager().shouldTick()) ci.cancel();
    }

    @Inject(method = "wakeUp", at = @At("HEAD"), cancellable = true)
    private void soulforge$canWakeUp(CallbackInfo ci) {
        if (this.hasStatusEffect(SoulForgeEffects.EEPY)) {
            if (this.isSleepingInBed()) {
                ci.cancel();
            }
        }
    }

    @ModifyConstant(method = "modifyAppliedDamage", constant = @Constant(intValue = 5))
    private int soulforge$modifyResistanceMultiplier(int constant) {
        return 10;
    }

    @ModifyConstant(method = "modifyAppliedDamage", constant = @Constant(intValue = 25))
    private int soulforge$modifyResistanceSubtraction(int constant) {
        return 50;
    }

    @ModifyConstant(method = "modifyAppliedDamage", constant = @Constant(floatValue = 25f))
    private float soulforge$modifyResistanceDivision(float constant) {
        return 50f;
    }

    @ModifyVariable(method = "modifyAppliedDamage", at = @At("STORE"), ordinal = 1)
    private int soulforge$modifyResistanceDamage(int value) {
        if (this.hasStatusEffect(SoulForgeEffects.VULNERABILITY)) {
            return value + 5 * (this.getStatusEffect(SoulForgeEffects.VULNERABILITY).getAmplifier() + 1);
        }
        return value;
    }

    @ModifyArg(method = "modifyAppliedDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/DamageUtil;getInflictedDamage(FF)F"), index = 1)
    private float soulforge$modifyProtectionLevel(float value) {
        if (this.hasStatusEffect(SoulForgeEffects.VULNERABILITY)) {
            for (int i = 0; i < this.getStatusEffect(SoulForgeEffects.VULNERABILITY).getAmplifier() + 1; i++) {
                value *= 0.9f;
            }
        }
        return value;
    }

    @ModifyReturnValue(method = "modifyAppliedDamage", at = @At("RETURN"))
    private float soulforge$modifyAppliedDamage(float value, @Local DamageSource source) {
        EntityAttributeInstance attribute = this.getAttributeInstance(SoulForgeAttributes.DAMAGE_REDUCTION);
        if (attribute != null) {
            value *= (float)attribute.getValue();
        }
        if (!source.isIn(DamageTypeTags.BYPASSES_EFFECTS) && !this.hasStatusEffect(StatusEffects.RESISTANCE)) {
            if (this.hasStatusEffect(SoulForgeEffects.VULNERABILITY)) {
                return value * ((this.getStatusEffect(SoulForgeEffects.VULNERABILITY).getAmplifier() + 1) * 0.1f + 1);
            }
        }
        return value;
    }

    @Inject(method = "applyDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setHealth(F)V"))
    private void soulforge$onAppliedDamage(DamageSource source, float amount, CallbackInfo ci) {
        LivingDamageEvent.onApplyDamage((LivingEntity)(Object)this, source, amount);
    }

    @ModifyVariable(method = "takeKnockback", at = @At("HEAD"), ordinal = 0)
    private double soulforge$modifyKnockbackStrength(double original) {
        return original * this.getAttributeValue(SoulForgeAttributes.KNOCKBACK_MULTIPLIER);
    }

    @ModifyReturnValue(method = "getStepHeight", at = @At("RETURN"))
    private float soulforge$modifyStepHeight(float original) {
        return original + (float)this.getAttributeValue(SoulForgeAttributes.STEP_HEIGHT);
    }

    @ModifyReturnValue(method = "handleFallDamage", at = @At("RETURN"))
    private boolean soulforge$modifyFallDamageMultiplier(boolean original) {
        return original && this.getAttributeValue(SoulForgeAttributes.FALL_DAMAGE_MULTIPLIER) > 0;
    }

    @ModifyArg(method = "computeFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;ceil(F)I"))
    private float soulforge$modifyFallDamageMultiplier(float original) {
        return original * (float)this.getAttributeValue(SoulForgeAttributes.FALL_DAMAGE_MULTIPLIER);
    }
}

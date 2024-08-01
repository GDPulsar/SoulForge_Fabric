package com.pulsar.soulforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.accessors.HasTickManager;
import com.pulsar.soulforge.accessors.OwnableMinion;
import com.pulsar.soulforge.accessors.ValueHolder;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.components.WorldBaseComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.effects.VulnerabilityEffect;
import com.pulsar.soulforge.entity.DeterminationPlatformEntity;
import com.pulsar.soulforge.entity.IntegrityPlatformEntity;
import com.pulsar.soulforge.event.LivingEntityTick;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.item.devices.devices.RevivalIdol;
import com.pulsar.soulforge.shield.ShieldBlockCallback;
import com.pulsar.soulforge.siphon.Siphon;
import com.pulsar.soulforge.siphon.Siphon.Type;
import com.pulsar.soulforge.tag.SoulForgeTags;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends Entity implements ValueHolder {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow public abstract Collection<StatusEffectInstance> getStatusEffects();
    @Shadow public abstract ItemStack getMainHandStack();

    @Shadow public abstract ItemStack getStackInHand(Hand hand);

    @Shadow public abstract boolean addStatusEffect(StatusEffectInstance effect);

    @Shadow public abstract void setHealth(float health);

    @Shadow public abstract boolean clearStatusEffects();

    @Shadow public abstract boolean hasStatusEffect(StatusEffect effect);

    @Shadow public abstract boolean isUndead();

    @Shadow public abstract AttributeContainer getAttributes();

    @Shadow public abstract double getAttributeValue(EntityAttribute attribute);

    @Shadow public abstract boolean isMobOrPlayer();

    @Inject(method = "isBlocking", at=@At("HEAD"), cancellable = true)
    public void parryBlocking(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity living = (LivingEntity)(Object)this;
        if (living instanceof PlayerEntity player) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            if (playerSoul != null) {
                if (playerSoul.hasValue("parry") && playerSoul.getValue("parry") > 0) {
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true)
    private float onDamage(float amount) {
        float multiplier = 1f;
        for (StatusEffectInstance effect : this.getStatusEffects()) {
            if (effect.getEffectType() instanceof VulnerabilityEffect) {
                multiplier = 1f + 0.2f * effect.getAmplifier();
            }
        }
        return amount * multiplier;
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void whenDamaged(DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        if (hasInt("HangToAThreadTimer") && getInt("HangToAThreadTimer") > 0
                && (!hasBool("HangToAThreadDamaging") || !getBool("HangToAThreadDamaging"))) {
            float totalDamage = 0f;
            if (hasFloat("HangToAThreadDamage")) totalDamage = getFloat("HangToAThreadDamage");
            totalDamage += damage;
            setFloat("HangToAThreadDamage", totalDamage);
            cir.setReturnValue(false);
        }
        if (source.getAttacker() instanceof ServerPlayerEntity player) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            float targetDefence;
            if (this.getAttributes().hasAttribute(EntityAttributes.GENERIC_ARMOR)) targetDefence = (float) this.getAttributeValue(EntityAttributes.GENERIC_ARMOR);
            else targetDefence = 0f;

            float targetDamage;
            if (this.getAttributes().hasAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE)) targetDamage = (float) this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            else targetDamage = 0f;

            int expIncrease = (int)(damage * (1f + (targetDefence / 10f) + (targetDamage / 10f)));

            WorldBaseComponent worldComponent = SoulForge.getWorldComponent(player.getWorld());
            expIncrease = (int)(worldComponent.getExpMultiplier() * expIncrease);
            if (this.isMobOrPlayer()) {
                if (this.isPlayer()) {
                    if (playerSoul.getPlayerSouls().containsKey(this.getUuidAsString())) {
                        expIncrease = (int)(MathHelper.clamp(1f-playerSoul.getPlayerSouls().get(this.getUuidAsString())/3f, 0f, 1f) * expIncrease);
                    }
                } else {
                    if (playerSoul.getMonsterSouls().containsKey(this.getType().getUntranslatedName())) {
                        expIncrease = (int)(MathHelper.clamp(1f-playerSoul.getMonsterSouls().get(this.getType().getUntranslatedName())/50f, 0.2f, 1f) * expIncrease);
                    }
                }
            }
            playerSoul.setEXP(playerSoul.getEXP() + expIncrease);

            if (source.isOf(DamageTypes.ARROW)) {
                if (source.getSource() instanceof PersistentProjectileEntity projectile) {
                    if (projectile.inBlockState == null) {
                        float distance = this.distanceTo(source.getAttacker());
                        boolean lineOfSight = player.canSee(projectile);
                        int addedStyle = (int)(damage * (distance / 20f) * (lineOfSight ? 1f : 2f));
                        playerSoul.setStyle(playerSoul.getStyle() + addedStyle);
                    }
                }
            }
            if (source.isOf(DamageTypes.TRIDENT)) {
                if (source.getSource() instanceof TridentEntity projectile) {
                    if (projectile.inBlockState == null) {
                        float distance = this.distanceTo(source.getAttacker());
                        boolean lineOfSight = player.canSee(projectile);
                        int addedStyle = (int)(damage * (distance / 20f) * (lineOfSight ? 1f : 2f));
                        playerSoul.setStyle(playerSoul.getStyle() + addedStyle);
                    }
                }
            }
            if (source.isOf(DamageTypes.EXPLOSION)) {
                playerSoul.setStyle(playerSoul.getStyle() + (int)damage);
            }
            if (source.isOf(DamageTypes.FALLING_ANVIL)) {
                playerSoul.setStyle(playerSoul.getStyle() + (int)(damage * 10));
            }
            if (source.isOf(DamageTypes.FALLING_STALACTITE)) {
                playerSoul.setStyle(playerSoul.getStyle() + (int)(damage * 5));
            }
            if (source.isOf(DamageTypes.FIREWORKS)) {
                playerSoul.setStyle(playerSoul.getStyle() + (int)(damage));
            }
            if (source.isOf(DamageTypes.LIGHTNING_BOLT)) {
                playerSoul.setStyle(playerSoul.getStyle() + (int)(damage));
            }
            if (source.isOf(DamageTypes.PLAYER_ATTACK)) {
                playerSoul.setStyle(playerSoul.getStyle() + (int)(damage / 2f));
            }
            if (source.isOf(DamageTypes.PLAYER_EXPLOSION)) {
                playerSoul.setStyle(playerSoul.getStyle() + (int)(damage));
            }
            if (source.isOf(DamageTypes.THROWN)) {
                playerSoul.setStyle(playerSoul.getStyle() + (int)(damage));
            }
            if (source.isOf(SoulForgeDamageTypes.PARRY_DAMAGE_TYPE)) {
                playerSoul.setStyle(playerSoul.getStyle() + (int)(damage * 3f));
            }
            if ((LivingEntity)(Object)this instanceof PlayerEntity targetPlayer) {
                if (source.isOf(SoulForgeDamageTypes.PAIN_SPLIT_DAMAGE_TYPE)) {
                    SoulComponent targetSoul = SoulForge.getPlayerSoul(targetPlayer);
                    targetSoul.setStyle(targetSoul.getStyle() + (int)damage);
                }
            }
        }
    }

    @Inject(method = "onKilledBy", at = @At("HEAD"))
    private void whenKilled(LivingEntity adversary, CallbackInfo ci) {
        if (adversary instanceof ServerPlayerEntity player) {
            SoulComponent soulData = SoulForge.getPlayerSoul(player);
            float targetHealth;
            if (this.getAttributes().hasAttribute(EntityAttributes.GENERIC_MAX_HEALTH)) targetHealth = (float)this.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
            else targetHealth = 0f;

            float targetDefence;
            if (this.getAttributes().hasAttribute(EntityAttributes.GENERIC_ARMOR)) targetDefence = (float)this.getAttributeValue(EntityAttributes.GENERIC_ARMOR);
            else targetDefence = 0f;

            float targetDamage;
            if (this.getAttributes().hasAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE)) targetDamage = (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            else targetDamage = 0f;

            int expIncrease;
            if (this.isPlayer()) {
                PlayerEntity targetPlayer = (PlayerEntity)(Object)this;
                SoulComponent targetSoul = SoulForge.getPlayerSoul(targetPlayer);
                expIncrease = (int)(250*(1+(targetDefence/10)*(targetSoul.getLV()/4)));
            } else if (this.getType() == EntityType.ENDER_DRAGON) {
                expIncrease = 3000;
            } else if (this.getType() == EntityType.WITHER) {
                expIncrease = 1500;
            } else if (this.getType() == EntityType.ELDER_GUARDIAN) {
                expIncrease = 500;
            } else if (this.getType() == EntityType.EVOKER) {
                expIncrease = 250;
            } else if (this.getType() == EntityType.WARDEN) {
                expIncrease = 1000;
            } else if (this.getType() == EntityType.PIGLIN_BRUTE) {
                expIncrease = 250;
            } else {
                expIncrease = (int)(targetHealth*(1+(targetDefence/10f)+(targetDamage/10f)));
            }
            WorldBaseComponent worldComponent = SoulForge.getWorldComponent(player.getWorld());
            expIncrease = (int)(worldComponent.getExpMultiplier() * expIncrease);
            if (this.isMobOrPlayer()) {
                if (this.isPlayer()) {
                    if (soulData.getPlayerSouls().containsKey(this.getUuidAsString())) {
                        expIncrease = (int)(MathHelper.clamp(1f-soulData.getPlayerSouls().get(this.getUuidAsString())/3f, 0f, 1f) * expIncrease);
                    }
                } else {
                    if (soulData.getMonsterSouls().containsKey(this.getType().getUntranslatedName())) {
                        expIncrease = (int)(MathHelper.clamp(1f-soulData.getMonsterSouls().get(this.getType().getUntranslatedName())/50f, 0.2f, 1f) * expIncrease);
                    }
                }
            }
            soulData.setEXP(soulData.getEXP() + expIncrease);
            if (this.isMobOrPlayer()) {
                if (this.isPlayer()) soulData.addPlayerSoul(this.getUuidAsString(), 1);
                else soulData.addMonsterSoul(this, 1);
            }
        }
    }

    @ModifyReturnValue(method="getJumpVelocity", at=@At("RETURN"))
    private float addJumpVelocityIncrease(float original) {
        float multiplier = 1f;
        LivingEntity entity = (LivingEntity)(Object)this;
        if (entity instanceof PlayerEntity player) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            if (playerSoul.hasValue("jumpBoost")) {
                multiplier *= playerSoul.getValue("jumpBoost");
            }
            if (playerSoul.hasValue("slamJumpTimer") && playerSoul.hasValue("slamJump")) {
                if (playerSoul.getValue("slamJumpTimer") > 0) multiplier *= (float)Math.sqrt(playerSoul.getValue("slamJump"));
            }
        }
        if (entity.isOnGround()) {
            Box box = this.getBoundingBox();
            for (Entity other : entity.getEntityWorld().getOtherEntities(entity, box.expand(0.0001))) {
                if (entity.collidesWith(other)) {
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

    /* frost floor no longer exists but i'm still keeping this code just in case i need to slip and slide at home
    @ModifyVariable(method = "travel", at=@At(value="STORE"), ordinal = 0)
    private float modifySlipperiness(float slipperiness) {
        float maxSlip = -1;
        LivingEntity living = (LivingEntity)(Object)this;
        if (!(living instanceof PlayerEntity)) {
            for (PlayerEntity player : this.getWorld().getPlayers()) {
                if (this.distanceTo(player) > 0.001f) {
                    SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                    for (AbilityBase ability : playerSoul.getActiveAbilities()) {
                        if (ability instanceof Snowglobe frostFloor) {
                            if (this.getPos().distanceTo(frostFloor.origin) < 15 + playerSoul.getLV()) {
                                float slip = 0.6f + 0.02f * playerSoul.getLV();
                                if (slip > maxSlip) maxSlip = slip;
                            }
                        }
                        if (ability instanceof FrozenGrasp frozenGrasp) {
                            if (frozenGrasp.target == living && frozenGrasp.timer > 0) {
                                maxSlip = 1f/0.91f;
                            }
                        }
                    }
                }
            }
        } else {
            SoulComponent playerSoul = SoulForge.getPlayerSoul((PlayerEntity)living);
            if (playerSoul.hasValue("slip")) {
                if (playerSoul.getValue("slip") > 0) return Math.min(1f, Math.max(playerSoul.getValue("slip"), slipperiness));
            }
        }
        if (maxSlip != -1) {
            return Math.min(1f, Math.max(maxSlip, slipperiness));
        }
        return slipperiness;
    }*/

    @Inject(method = "isImmobile", at=@At(value="HEAD"), cancellable = true)
    protected void isImmobile(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity living = (LivingEntity)(Object)this;
        if (living instanceof PlayerEntity player) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            if (playerSoul != null) {
                if (playerSoul.hasTag("immobile")) {
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @ModifyConstant(method = "travel", constant = @Constant(doubleValue = 0.08))
    private double modifyEntityGravity(double baseGravity) {
        LivingEntity living = (LivingEntity)(Object)this;
        if (living instanceof PlayerEntity player) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            if (playerSoul.hasTag("immobile")) return 0f;
            if (playerSoul.hasCast("Repulsion Field") || playerSoul.hasCast("Fearless Instincts") || playerSoul.hasCast("Accelerated Pellet Aura")) {
                baseGravity += 0.04;
            }
            if (playerSoul.hasTag("antigravityZoneAffected")) {
                baseGravity -= 0.065;
            }
        }
        return baseGravity;
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
            if (playerSoul != null) {
                if (playerSoul.hasCast("Fearless Instincts")) {
                    Vec3d vec3d = this.getVelocity();
                    float angle = (float) (Math.atan2(-vec3d.z, -vec3d.x) + Math.PI / 2);
                    this.setVelocity(this.getVelocity().add((-MathHelper.sin(angle) * 0.2F), 0.0, (MathHelper.cos(angle) * 0.2F)));
                    return;
                }
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
                .add(SoulForgeAttributes.AIR_SPEED_BECAUSE_MOJANG_SUCKS);
    }

    @ModifyReturnValue(method = "getOffGroundSpeed", at=@At("RETURN"))
    protected float modifyOffGroundSpeed(float original) {
        LivingEntity living = (LivingEntity)(Object)this;
        if (living.getAttributes().hasAttribute(SoulForgeAttributes.AIR_SPEED_BECAUSE_MOJANG_SUCKS)) {
            return (float)(original * living.getAttributeValue(SoulForgeAttributes.AIR_SPEED_BECAUSE_MOJANG_SUCKS));
        }
        return original;
    }

    /*@Unique
    private boolean wasUnchained = false;

    @ModifyReturnValue(method = "getStatusEffects", at = @At("RETURN"))
    public Collection<StatusEffectInstance> getStatusEffects(Collection<StatusEffectInstance> original) {
        LivingEntity living = (LivingEntity)(Object)this;
        if (living instanceof PlayerEntity player) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            boolean isUnchained = playerSoul.hasCast("Unchained Soul");
            int totalCount = 0;
            int duration = 10000;
            for (StatusEffectInstance effect : original) {
                totalCount += effect.getAmplifier();
                if (effect.getDuration() < duration) duration = effect.getDuration();
            }
            if (isUnchained != wasUnchained) {
                for (StatusEffectInstance effect : original) {
                    if (isUnchained) effect.getEffectType().onRemoved(player, player.getAttributes(), effect.getAmplifier());
                    else effect.getEffectType().onApplied(player, player.getAttributes(), effect.getAmplifier());
                }
            }
            wasUnchained = isUnchained;
            if (isUnchained && totalCount != 0) {
                return Set.of(new StatusEffectInstance(SoulForgeEffects.UNCHAINED_EFFECT, duration, totalCount));
            }
        }
        return original;
    }

    @ModifyReturnValue(method = "getActiveStatusEffects", at = @At("RETURN"))
    public Map<StatusEffect, StatusEffectInstance> getActiveStatusEffects(Map<StatusEffect, StatusEffectInstance> original) {
        LivingEntity living = (LivingEntity)(Object)this;
        if (living instanceof PlayerEntity player) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            boolean isUnchained = playerSoul.hasCast("Unchained Soul");
            int totalCount = 0;
            int duration = 10000;
            for (StatusEffectInstance effect : original.values()) {
                totalCount += effect.getAmplifier();
                if (effect.getDuration() < duration) duration = effect.getDuration();
            }
            if (isUnchained != wasUnchained) {
                for (StatusEffectInstance effect : original.values()) {
                    if (isUnchained) effect.getEffectType().onRemoved(player, player.getAttributes(), effect.getAmplifier());
                    else effect.getEffectType().onApplied(player, player.getAttributes(), effect.getAmplifier());
                }
            }
            wasUnchained = isUnchained;
            if (isUnchained && totalCount != 0) {
                return Map.of(SoulForgeEffects.UNCHAINED_EFFECT, new StatusEffectInstance(SoulForgeEffects.UNCHAINED_EFFECT, duration, totalCount));
            }
        }
        return original;
    }*/

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
        if (source.getAttacker() instanceof PlayerEntity player) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            if (playerSoul.hasValue("shieldBreak")) {
                return original * playerSoul.getValue("shieldBreak");
            }
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

    HashMap<String, Float> floatVals = new HashMap<>();
    HashMap<String, Integer> intVals = new HashMap<>();
    HashMap<String, Boolean> boolVals = new HashMap<>();
    HashMap<String, Vec3d> vecVals = new HashMap<>();

    public float getFloat(String key) {
        return floatVals.get(key);
    }
    public void setFloat(String key, float value) {
        floatVals.put(key, value);
    }
    public void removeFloat(String key) {
        floatVals.remove(key);
    }
    public boolean hasFloat(String key) {
        return floatVals.containsKey(key);
    }
    public int getInt(String key) {
        return intVals.get(key);
    }
    public void setInt(String key, int value) {
        intVals.put(key, value);
    }
    public void removeInt(String key) {
        intVals.remove(key);
    }
    public boolean hasInt(String key) {
        return intVals.containsKey(key);
    }
    public boolean getBool(String key) {
        return boolVals.get(key);
    }
    public void setBool(String key, boolean value) {
        boolVals.put(key, value);
    }
    public void removeBool(String key) {
        boolVals.remove(key);
    }
    public boolean hasBool(String key) {
        return boolVals.containsKey(key);
    }
    public Vec3d getVec(String key) {
        return vecVals.get(key);
    }
    public void setVec(String key, Vec3d value) {
        vecVals.put(key, value);
    }
    public void removeVec(String key) {
        vecVals.remove(key);
    }
    public boolean hasVec(String key) {
        return vecVals.containsKey(key);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void soulforge$addCustomData(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound floatNbt = new NbtCompound();
        for (Map.Entry<String, Float> entry : floatVals.entrySet()) {
            floatNbt.putFloat(entry.getKey(), entry.getValue());
        }
        nbt.put("floatVals", floatNbt);

        NbtCompound intNbt = new NbtCompound();
        for (Map.Entry<String, Integer> entry : intVals.entrySet()) {
            intNbt.putInt(entry.getKey(), entry.getValue());
        }
        nbt.put("intVals", intNbt);

        NbtCompound boolNbt = new NbtCompound();
        for (Map.Entry<String, Boolean> entry : boolVals.entrySet()) {
            boolNbt.putBoolean(entry.getKey(), entry.getValue());
        }
        nbt.put("boolVals", boolNbt);

        NbtCompound vecNbt = new NbtCompound();
        for (Map.Entry<String, Vec3d> entry : vecVals.entrySet()) {
            vecNbt.put(entry.getKey(), Utils.vectorToNbt(entry.getValue()));
        }
        nbt.put("vecVals", vecNbt);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void soulforge$readCustomData(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound floatNbt = nbt.getCompound("floatVals");
        floatVals = new HashMap<>();
        for (String key : floatNbt.getKeys()) {
            floatVals.put(key, floatNbt.getFloat(key));
        }

        NbtCompound intNbt = nbt.getCompound("intVals");
        intVals = new HashMap<>();
        for (String key : intNbt.getKeys()) {
            intVals.put(key, intNbt.getInt(key));
        }

        NbtCompound boolNbt = nbt.getCompound("boolVals");
        boolVals = new HashMap<>();
        for (String key : boolNbt.getKeys()) {
            boolVals.put(key, boolNbt.getBoolean(key));
        }

        NbtCompound vecNbt = nbt.getCompound("vecVals");
        vecVals = new HashMap<>();
        for (String key : floatNbt.getKeys()) {
            vecVals.put(key, Utils.nbtToVector(vecNbt.getList(key, NbtElement.DOUBLE_TYPE)));
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void soulforge$onEntityTick(CallbackInfo ci) {
        LivingEntityTick.tick((LivingEntity)(Object)this);
    }

    @Inject(method = "tickCramming", at = @At("HEAD"), cancellable = true)
    private void canTickCramming(CallbackInfo ci) {
        if (!((HasTickManager)this.getWorld()).getTickManager().shouldTick()) ci.cancel();
    }
}

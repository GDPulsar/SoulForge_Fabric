package com.pulsar.soulforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.kindness.PainSplit;
import com.pulsar.soulforge.ability.pures.MartyrsTouch;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.components.TemporaryModifierComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.item.weapons.MagicSweepingSwordItem;
import com.pulsar.soulforge.networking.SoulForgeNetworking;
import com.pulsar.soulforge.shield.ShieldDisabledCallback;
import com.pulsar.soulforge.siphon.Siphon;
import com.pulsar.soulforge.siphon.Siphon.Type;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.tag.SoulForgeTags;
import com.pulsar.soulforge.trait.Traits;
import com.pulsar.soulforge.util.TeamUtils;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.ArrayList;
import java.util.List;

@Debug(export = true)
@Mixin(PlayerEntity.class)
abstract class PlayerEntityMixin extends LivingEntity {
    @Shadow public abstract boolean damage(DamageSource source, float amount);

    @Shadow public abstract float getAttackCooldownProgress(float baseTime);

    @Shadow @Final private ItemCooldownManager itemCooldownManager;

    @Shadow public abstract float getDamageTiltYaw();

    @Shadow @Final private PlayerAbilities abilities;

    @Shadow protected abstract void dropShoulderEntities();

    @Shadow public abstract ItemCooldownManager getItemCooldownManager();

    @Shadow public abstract Arm getMainArm();

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method="jump", at=@At("HEAD"), cancellable = true)
    protected void modifyJump(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (playerSoul.hasTag("disableJump")) ci.cancel();
    }

    @Inject(method="travel", at=@At("HEAD"), cancellable = true)
    protected void modifyTravel(Vec3d movementInput, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (playerSoul.hasTag("disableMovement")) ci.cancel();
    }

    @ModifyVariable(method = "travel", at=@At("HEAD"), argsOnly = true)
    private Vec3d modifyMovement(Vec3d movementInput) {
        PlayerEntity player = ((PlayerEntity)(Object)this);
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        float stepHeight = 0.6f;
        if (playerSoul.hasCast("Repulsion Field")) stepHeight += 1f;
        if (playerSoul.hasCast("Fearless Instincts")) stepHeight += 1f;
        if (playerSoul.hasCast("Accelerated Pellet Aura")) stepHeight += 1f;
        if (playerSoul.hasCast("Warpspeed")) stepHeight += 3f;
        if (playerSoul.hasTag("sliding")) stepHeight = -1f;
        player.setStepHeight(stepHeight);
        if (playerSoul.hasCast("Warpspeed")) return new Vec3d(0f, 0f, 1f);
        if (playerSoul.hasTag("forcedRunning")) return new Vec3d(0f, 0f, 1f);
        return movementInput;
    }

    @ModifyVariable(method="handleFallDamage", at=@At("HEAD"),ordinal=1, argsOnly = true)
    public float modifyFallDamage(float damageMultiplier) {
        PlayerEntity player = ((PlayerEntity)(Object)this);
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (playerSoul.hasTag("shatterdrill")) {
            playerSoul.removeTag("shatterdrill");
            for (Entity entity : player.getEntityWorld().getOtherEntities(player, Box.of(player.getPos(), 8, 4, 8))) {
                if (entity instanceof LivingEntity target) {
                    float dist = (float) target.getPos().distanceTo(player.getPos());
                    if (dist <= 4f) {
                        Vec3d launchDir = target.getPos().subtract(player.getPos()).withAxis(Direction.Axis.Y, 0).normalize().multiply(dist / 3f);
                        target.damage(player.getDamageSources().playerAttack(player), 4f);
                        target.setVelocity(launchDir.add(0f, 1.25f, 0f));
                    }
                }
            }
            if (player.getServer() != null) {
                ServerWorld serverWorld = player.getServer().getWorld(player.getWorld().getRegistryKey());
                if (serverWorld != null) {
                    serverWorld.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, player.getSteppingBlockState()),
                            player.getX(), player.getY(), player.getZ(), 50, 3, 0.2, 3, 1);
                    serverWorld.playSoundFromEntity(null, player, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1f, 0.5f);
                }
            } else {
                ParticleEffect particle = new BlockStateParticleEffect(ParticleTypes.BLOCK, player.getSteppingBlockState());
                for (int i = 0; i < 50; i++) {
                    Vec3d pos = player.getPos().add(new Vec3d(Math.random() * 3f - 1.5f, Math.random() * 0.1f, Math.random() * 3f - 1.5f));
                    getWorld().addParticle(particle,
                            pos.x, pos.y, pos.z, 0, 0, 0);
                }
            }
            return 0;
        }
        if (playerSoul.hasTag("fallImmune")) {
            return 0;
        }
        if (playerSoul.hasTag("groundSlam")) {
            playerSoul.removeTag("groundSlam");
            playerSoul.setValue("slamJump", player.fallDistance);
            playerSoul.setValue("slamJumpTimer", 10);
            return 0;
        }
        return damageMultiplier;
    }

    @ModifyExpressionValue(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private Item soulforge$allowSweepingFor(Item original, @Local ItemStack stack) {
        if (stack.getItem() instanceof TridentItem) {
            if (stack.getOrCreateNbt().contains("Siphon")) {
                Siphon.Type siphonType = Siphon.Type.getSiphon(stack.getOrCreateNbt().getString("Siphon"));
                if (siphonType == Type.BRAVERY) {
                    return Items.GOLDEN_SWORD;
                }
            }
        }
        if (stack.getItem() instanceof MagicSweepingSwordItem) {
            return Items.GOLDEN_SWORD;
        }
        return original;
    }

    @ModifyVariable(method = "attack", at = @At(value = "STORE", ordinal = 1), ordinal = 0)
    private float soulforge$modifyDamage(float original, @Local Entity target) {
        if (this.hasStatusEffect(StatusEffects.STRENGTH)) {
            original += (this.getStatusEffect(StatusEffects.STRENGTH).getAmplifier() + 1) * 3;
        }
        if (this.hasStatusEffect(StatusEffects.WEAKNESS)) {
            original += (this.getStatusEffect(StatusEffects.WEAKNESS).getAmplifier() + 1) * -4;
        }
        if (this.isUsingRiptide()) {
            ItemStack tridentStack = this.getMainHandStack().isOf(Items.TRIDENT) ? this.getMainHandStack() : this.getOffHandStack();
            if (tridentStack.getOrCreateNbt().contains("Siphon")) {
                Siphon.Type siphonType = Siphon.Type.getSiphon(tridentStack.getOrCreateNbt().getString("Siphon"));
                if (siphonType == Type.BRAVERY) {
                    original *= 1.25f;
                }
            }
        }
        if (target instanceof LivingEntity living) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul((PlayerEntity) (Object) this);
            if (playerSoul.hasValue("rampageTimer") && playerSoul.hasValue("rampageActive")) {
                if (playerSoul.getValue("rampageActive") == 3) {
                    TemporaryModifierComponent modifiers = SoulForge.getTemporaryModifiers(living);
                    original *= (modifiers.getModifierCount() * 0.05f) + 1f;
                }
            }
        }
        return original;
    }

    @Inject(method = "readCustomDataFromNbt", at=@At("TAIL"))
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        if (player.getInventory().selectedSlot == 9) player.getInventory().selectedSlot = 0;
    }

    @ModifyArgs(method = "attack", at=@At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;takeKnockback(DDD)V"))
    private void addTridentKnockback(Args args) {
        if (this.getMainHandStack().isOf(Items.TRIDENT)) {
            if (this.getMainHandStack().getOrCreateNbt().contains("Siphon")) {
                Siphon.Type siphonType = Siphon.Type.getSiphon(this.getMainHandStack().getOrCreateNbt().getString("Siphon"));
                if (siphonType == Type.KINDNESS) {
                    args.set(0, (float)args.get(0) * 2f);
                }
            }
        }
    }

    @Inject(method = "attack", at=@At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;takeKnockback(DDD)V", shift = At.Shift.AFTER))
    private void modifyKnockback(Entity target, CallbackInfo ci) {
        addJusticeKnockback(target);
    }

    @Inject(method = "attack", at=@At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", shift = At.Shift.AFTER))
    private void modifyDamageKnockback(Entity target, CallbackInfo ci) {
        addJusticeKnockback(target);
    }

    @Unique
    private void addJusticeKnockback(Entity target) {
        ItemStack heldItem = this.getMainHandStack();
        if (heldItem.getItem() instanceof SwordItem || heldItem.getItem() instanceof ToolItem) {
            if (target instanceof LivingEntity living) {
                if (heldItem.getNbt() != null) {
                    if (heldItem.getNbt().contains("Siphon")) {
                        Siphon.Type type = Siphon.Type.getSiphon(heldItem.getNbt().getString("Siphon"));
                        if (type == Siphon.Type.JUSTICE || type == Siphon.Type.SPITE) {
                            float h = this.getAttackCooldownProgress(0.5f);
                            boolean bl = h > 0.9f;
                            int i = 0;
                            i += EnchantmentHelper.getKnockback(this);
                            if (this.isSprinting() && bl) {
                                ++i;
                            }
                            float damageDealt = (float) this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue();
                            if (i > 0) {
                                living.takeKnockback((float) i * 0.5 * (1 + damageDealt * 0.05), MathHelper.sin(this.getYaw() * ((float) Math.PI / 180)), -MathHelper.cos(this.getYaw() * ((float) Math.PI / 180)));
                            } else {
                                living.takeKnockback(0.4 * (1 + damageDealt * 0.05), MathHelper.sin(this.getYaw() * ((float) Math.PI / 180)), -MathHelper.cos(this.getYaw() * ((float) Math.PI / 180)));
                            }
                        }
                    }
                }
            }
        }
    }

    @ModifyExpressionValue(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;expand(DDD)Lnet/minecraft/util/math/Box;"))
    private Box soulforge$modifySweepingBox(Box original) {
        if (this.getMainHandStack().isOf(SoulForgeItems.COLOSSAL_CLAYMORE)) {
            return original.stretch(2f, 2f, 2f);
        }
        return original;
    }

    @ModifyExpressionValue(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getSweepingMultiplier(Lnet/minecraft/entity/LivingEntity;)F"))
    private float soulforge$modifySweepingMultiplier(float original) {
        if (this.getMainHandStack().isOf(SoulForgeItems.COLOSSAL_CLAYMORE)) {
            return 2f/3f;
        }
        if (this.getMainHandStack().isOf(SoulForgeItems.BRAVERY_SPEAR)) {
            return 1f/2f;
        }
        return original;
    }

    @ModifyArgs(method = "spawnSweepAttackParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnParticles(Lnet/minecraft/particle/ParticleEffect;DDDIDDDD)I"))
    private void soulforge$modifySweepParticleScale(Args args) {
        if (this.getMainHandStack().isOf(SoulForgeItems.COLOSSAL_CLAYMORE)) {
            args.set(5, -2);
            args.set(8, 1);
        }
    }

    @ModifyVariable(method = "damage", at=@At("HEAD"), ordinal = 0, argsOnly = true)
    protected float modifyDamage(float amount, @Local DamageSource source) {
        if (source.isOf(SoulForgeDamageTypes.PAIN_SPLIT_DAMAGE_TYPE)) return amount;
        List<PlayerEntity> toShare = new ArrayList<>();
        for (ServerPlayerEntity target : this.getServer().getPlayerManager().getPlayerList()) {
            if (this.distanceTo(target) <= 300f) {
                SoulComponent targetSoul = SoulForge.getPlayerSoul(target);
                for (AbilityBase ability : targetSoul.getActiveAbilities()) {
                    if (ability instanceof PainSplit painSplit) {
                        if (painSplit.target == (PlayerEntity) (Object) this) {
                            toShare.add(target);
                        }
                    }
                    if (ability instanceof MartyrsTouch martyrsTouch) {
                        if (martyrsTouch.players != null) {
                            if (martyrsTouch.players.contains((PlayerEntity) (Object) this)) {
                                target.damage(SoulForgeDamageTypes.of(this.getWorld(), SoulForgeDamageTypes.PAIN_SPLIT_DAMAGE_TYPE), amount);
                                return 0f;
                            }
                        }
                    }
                }
            }
        }
        for (PlayerEntity shared : toShare) {
            shared.damage(SoulForgeDamageTypes.of(this.getWorld(), SoulForgeDamageTypes.PAIN_SPLIT_DAMAGE_TYPE), amount/(toShare.size()+1f));
        }
        return amount/(toShare.size()+1f);
    }

    @Inject(method = "damage", at=@At("HEAD"), cancellable = true)
    private void onDamaged(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        if (!player.blockedByShield(source) && isUsingItem() && getActiveItem().isOf(SoulForgeItems.COLOSSAL_CLAYMORE)) {
            getItemCooldownManager().set(SoulForgeItems.COLOSSAL_CLAYMORE, 100);
            stopUsingItem();
            getWorld().playSound(null, getX(), getY(), getZ(), SoundEvents.ITEM_SHIELD_BREAK, SoundCategory.PLAYERS, 1f, 1f, 0);
            cir.setReturnValue(false);
        }
        if (source.getAttacker() instanceof PlayerEntity attacker) {
            if (!TeamUtils.canDamageEntity(player.getServer(), player, attacker)) {
                cir.setReturnValue(false);
                return;
            }
        }
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (playerSoul.hasValue("parry")) {
            if (playerSoul.getValue("parry") > 0) {
                if (source.getAttacker() != null) {
                    this.getWorld().playSoundFromEntity(null, player, SoulForgeSounds.PARRY_EVENT, SoundCategory.PLAYERS, 1f, 1f);
                    if (source.getAttacker() instanceof LivingEntity living)
                        living.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.VULNERABILITY, playerSoul.hasCast("Furioso") ? 160 : 80, 2));
                    if (amount < 30f) {
                        source.getAttacker().damage(SoulForgeDamageTypes.of(player, SoulForgeDamageTypes.PARRY_DAMAGE_TYPE), amount);
                    } else {
                        source.getAttacker().damage(SoulForgeDamageTypes.of(player, SoulForgeDamageTypes.PARRY_DAMAGE_TYPE), 30);
                        if (!(this.isInvulnerableTo(source) || (this.abilities.invulnerable && !source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)))) {
                            this.despawnCounter = 0;
                            if (!this.isDead()) {
                                if (!this.getWorld().isClient) this.dropShoulderEntities();
                                if (source.isScaledWithDifficulty()) {
                                    if (this.getWorld().getDifficulty() == Difficulty.PEACEFUL) amount = 0.0F;
                                    if (this.getWorld().getDifficulty() == Difficulty.EASY)
                                        amount = Math.min(amount / 2.0F + 1.0F, amount);
                                    if (this.getWorld().getDifficulty() == Difficulty.HARD)
                                        amount = amount * 3.0F / 2.0F;
                                }
                                if (amount != 0.0F) super.damage(source, amount);
                            }
                        }
                    }
                    cir.setReturnValue(false);
                }
            }
        }
    }

    @Inject(method = "createPlayerAttributes", require = 1, allow = 1, at = @At("RETURN"))
    private static void addAttributes(final CallbackInfoReturnable<DefaultAttributeContainer.Builder> info) {
        info.getReturnValue()
                .add(SoulForgeAttributes.MAGIC_COOLDOWN)
                .add(SoulForgeAttributes.MAGIC_COST)
                .add(SoulForgeAttributes.MAGIC_POWER);
    }

    @ModifyReturnValue(method = "getBlockBreakingSpeed", at=@At("RETURN"))
    private float modifyMiningSpeed(float original) {
        ItemStack stack = this.getMainHandStack();
        if (stack.getNbt() != null) {
            if (stack.getNbt().contains("Siphon")) {
                Siphon.Type type = Siphon.Type.getSiphon(stack.getNbt().getString("Siphon"));
                if (type == Siphon.Type.DETERMINATION || type == Siphon.Type.SPITE) {
                    SoulComponent playerSoul = SoulForge.getPlayerSoul((PlayerEntity)(Object)this);
                    if (playerSoul.getMagic() > 60f) {
                        return original * 1.2f;
                    } else {
                        return original * 0.8f;
                    }
                }
                if (type == Siphon.Type.PATIENCE || type == Siphon.Type.SPITE) {
                    return original * 0.65f;
                }
            }
        }
        return original;
    }

    @Inject(method = "disableShield", at=@At("HEAD"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void disableShieldHead(boolean sprinting, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        ItemStack activeItemStack = player.getActiveItem();
        Item activeItem = activeItemStack.getItem();

        ShieldDisabledCallback.EVENT.invoker().disable(player, player.getActiveHand(), activeItemStack);

        if (activeItemStack.isIn(SoulForgeTags.SHIELDS)) {
            itemCooldownManager.set(activeItem, 100);
        }
    }

    @Inject(method = "takeShieldHit", at = @At("HEAD"), cancellable = true)
    private void soulforge$onShieldHit(LivingEntity attacker, CallbackInfo ci) {
        if (this.getMainHandStack().isOf(SoulForgeItems.GUNBLADES)) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul((PlayerEntity)(Object)this);
            if (playerSoul.hasCast("Furioso")) {
                attacker.takeKnockback(1, MathHelper.sin(this.getYaw() * MathHelper.RADIANS_PER_DEGREE), -MathHelper.cos(this.getYaw() * MathHelper.RADIANS_PER_DEGREE));
                ci.cancel();
            }
        }
    }

    @ModifyExpressionValue(method = "interact", at=@At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;interact(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"))
    public ActionResult interact(ActionResult original, @Local Entity entity, @Local Hand hand) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        if (!original.isAccepted()) {
            if (!player.getWorld().isClient) {
                SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                if (!(playerSoul.hasTrait(Traits.bravery) && playerSoul.hasTrait(Traits.integrity)))
                    return original;
                if (!playerSoul.hasCast("Valiant Heart")) return original;
                if (!playerSoul.hasValue("parryCooldown")) playerSoul.setValue("parryCooldown", 0f);
                if (playerSoul.getValue("parryCooldown") <= 0f) {
                    player.getItemCooldownManager().set(player.getMainHandStack().getItem(), 25);
                    playerSoul.setValue("parryCooldown", 25f);
                    playerSoul.setValue("parry", 5f);
                    PacketByteBuf buf = PacketByteBufs.create().writeUuid(player.getUuid()).writeString("parry");
                    buf.writeBoolean(false);
                    SoulForgeNetworking.broadcast(null, player.getServer(), SoulForgeNetworking.PERFORM_ANIMATION, buf);
                    return TypedActionResult.consume(player.getStackInHand(hand)).getResult();
                }
            }
        }
        return original;
    }

    @ModifyReturnValue(method = "getEntityName", at=@At("RETURN"))
    public String getEntityName(String original) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (playerSoul.getDisguise() != null) {
            if (playerSoul.getDisguise() != player) {
                return playerSoul.getDisguise().getEntityName();
            }
        }
        return original;
    }

    @ModifyReturnValue(method = "getName", at=@At("RETURN"))
    public Text getName(Text original) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (playerSoul.getDisguise() != null) {
            if (playerSoul.getDisguise() != player) {
                return playerSoul.getDisguise().getName();
            }
        }
        return original;
    }

    @ModifyReturnValue(method = "shouldDamagePlayer", at=@At("RETURN"))
    private boolean modifyShouldDamagePlayer(boolean original, @Local PlayerEntity player) {
        return original && TeamUtils.canDamageEntity(getServer(), (PlayerEntity)(Object)this, player);
    }

    @ModifyReturnValue(method = "getOffGroundSpeed", at=@At("RETURN"))
    protected float modifyOffGroundSpeed(float original) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        if (player.getAttributes().hasAttribute(SoulForgeAttributes.AIR_SPEED_BECAUSE_MOJANG_SUCKS)) {
            return (float)(original * player.getAttributeValue(SoulForgeAttributes.AIR_SPEED_BECAUSE_MOJANG_SUCKS));
        }
        return original;
    }

    @Inject(method = "wakeUp(ZZ)V", at = @At("HEAD"), cancellable = true)
    private void soulforge$canWakeUp(CallbackInfo ci) {
        if (this.hasStatusEffect(SoulForgeEffects.EEPY)) {
            if (this.isSleepingInBed()) {
                ci.cancel();
            }
        }
    }
}

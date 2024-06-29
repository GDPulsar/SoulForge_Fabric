package com.pulsar.soulforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.kindness.PainSplit;
import com.pulsar.soulforge.ability.patience.FrozenGrasp;
import com.pulsar.soulforge.ability.pures.MartyrsTouch;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.components.WorldConfigComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.event.EventType;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.networking.SoulForgeNetworking;
import com.pulsar.soulforge.shield.ShieldDisabledCallback;
import com.pulsar.soulforge.siphon.Siphon;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.tag.SoulForgeTags;
import com.pulsar.soulforge.trait.Traits;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.BlockState;
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
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;

@Mixin(PlayerEntity.class)
abstract class PlayerEntityMixin extends LivingEntity {
    @Shadow public abstract boolean damage(DamageSource source, float amount);

    @Shadow public abstract float getAttackCooldownProgress(float baseTime);

    @Shadow @Final private ItemCooldownManager itemCooldownManager;

    @Shadow public abstract float getDamageTiltYaw();

    @Shadow @Final private PlayerAbilities abilities;

    @Shadow protected abstract void dropShoulderEntities();

    @Shadow public abstract ItemCooldownManager getItemCooldownManager();

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method="handleFallDamage", at=@At("HEAD"), cancellable = true)
    protected void modifyFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = ((PlayerEntity)(Object)this);
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (playerSoul != null) {
            if (playerSoul.hasTag("fallImmune") || playerSoul.hasCast("Antigravity Zone")) {
                cir.setReturnValue(false);
                playerSoul.handleEvent(EventType.FALL_IMMUNITY);
                return;
            }
            if (player.getServer() != null) {
                for (ServerPlayerEntity target : player.getServer().getPlayerManager().getPlayerList()) {
                    SoulComponent targetSoul = SoulForge.getPlayerSoul(target);
                    if (target.distanceTo(player) < 15f) {
                        if (targetSoul.hasCast("Antigravity Zone")) {
                            cir.setReturnValue(false);
                            playerSoul.handleEvent(EventType.FALL_IMMUNITY);
                            return;
                        }
                    }
                }
            }
        }
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

    @Inject(method="onKilledOther", at=@At("HEAD"))
    protected void onKillEntity(ServerWorld world, LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = ((PlayerEntity)(Object)this);

        SoulComponent soulData = SoulForge.getPlayerSoul(player);
        if (soulData == null) return;
        float targetHealth;
        if (target.getAttributes().hasAttribute(EntityAttributes.GENERIC_MAX_HEALTH)) targetHealth = (float) target.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
        else targetHealth = 0f;

        float targetDefence;
        if (target.getAttributes().hasAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE)) targetDefence = (float) target.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        else targetDefence = 0f;

        float targetDamage;
        if (target.getAttributes().hasAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE)) targetDamage = (float) target.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        else targetDamage = 0f;

        int expIncrease;
        if (target instanceof PlayerEntity targetPlayer) {
            SoulComponent targetSoul = SoulForge.getPlayerSoul(targetPlayer);
            expIncrease = (int)(250*(1+(targetDefence/10)*(targetSoul.getLV()/4)));
        } else if (target.getType() == EntityType.ENDER_DRAGON) {
            expIncrease = 3000;
        } else if (target.getType() == EntityType.WITHER) {
            expIncrease = 1500;
        } else if (target.getType() == EntityType.ELDER_GUARDIAN) {
            expIncrease = 500;
        } else if (target.getType() == EntityType.EVOKER) {
            expIncrease = 250;
        } else if (target.getType() == EntityType.WARDEN) {
            expIncrease = 1000;
        } else if (target.getType() == EntityType.PIGLIN_BRUTE) {
            expIncrease = 250;
        } else {
            expIncrease = (int)(targetHealth*(1+(targetDefence/10f)+(targetDamage/10f)));
        }
        WorldConfigComponent worldComponent = SoulForge.getWorldComponent(player.getWorld());
        expIncrease *= (int)worldComponent.getExpMultiplier();
        if (target.isMobOrPlayer()) {
            if (target.isPlayer()) {
                if (soulData.getPlayerSouls().containsKey(target.getUuidAsString())) {
                    expIncrease *= (int)MathHelper.clamp(1f-soulData.getPlayerSouls().get(target.getUuidAsString())/3f, 0f, 1f);
                }
            } else {
                if (soulData.getMonsterSouls().containsKey(target.getType().getUntranslatedName())) {
                    expIncrease *= (int)MathHelper.clamp(1f-soulData.getMonsterSouls().get(target.getType().getUntranslatedName())/50f, 0.2f, 1f);
                }
            }
        }
        soulData.setEXP(soulData.getEXP() + expIncrease);
        if (target.isMobOrPlayer()) {
            if (target.isPlayer()) soulData.addPlayerSoul(target.getUuidAsString(), 1);
            else soulData.addMonsterSoul(target, 1);
        }
    }

    @Redirect(method="attack", at= @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    protected boolean onDamageEntity(Entity target, DamageSource source, float damage) {
        if (target instanceof LivingEntity living) {
            PlayerEntity player = (PlayerEntity)(Object)this;
            if (target instanceof PlayerEntity targetPlayer) {
                if (!player.shouldDamagePlayer(targetPlayer)) return target.damage(source, damage);
            }
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            float targetDefence;
            if (living.getAttributes().hasAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE)) targetDefence = (float) living.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            else targetDefence = 0f;

            float targetDamage;
            if (living.getAttributes().hasAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE)) targetDamage = (float) living.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            else targetDamage = 0f;

            int expIncrease = (int)(damage * (1 + (targetDefence / 10) + (targetDamage / 10)));

            WorldConfigComponent worldComponent = SoulForge.getWorldComponent(player.getWorld());
            expIncrease *= (int)worldComponent.getExpMultiplier();
            playerSoul.setEXP(playerSoul.getEXP() + expIncrease);

            // siphon
            ItemStack held = player.getMainHandStack();
            if (held.getNbt() != null) {
                if (held.getNbt().contains("Siphon")) {
                    Siphon.Type type = Siphon.Type.getSiphon(held.getNbt().getString("Siphon"));
                    if (type == Siphon.Type.PATIENCE || type == Siphon.Type.SPITE) {
                        living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 1));
                        living.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 60, 0));
                    }
                    if (type == Siphon.Type.DETERMINATION || type == Siphon.Type.SPITE) {
                        playerSoul.setMagic(playerSoul.getMagic() + damage);
                    }
                    if (type == Siphon.Type.PERSEVERANCE || type == Siphon.Type.SPITE) {
                        if (player.getAttackCooldownProgress(0.5f) >= 0.99f) {
                            if (target instanceof PlayerEntity targetPlayer) {
                                SoulComponent targetSoul = SoulForge.getPlayerSoul(targetPlayer);
                                Utils.addAntiheal(0.6f, (int)(player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_SPEED).getValue() * 20), targetSoul);
                            }
                        }
                    }
                    if (type == Siphon.Type.KINDNESS || type == Siphon.Type.SPITE) {
                        if (player.getAbsorptionAmount() < 8f) player.setAbsorptionAmount(player.getAbsorptionAmount()+1f);
                    }
                }
            }

            boolean frostburn = playerSoul.getTraits().contains(Traits.bravery) && playerSoul.getTraits().contains(Traits.patience);
            // abilities
            for (AbilityBase ability : playerSoul.getActiveAbilities()) {
                if (ability instanceof FrozenGrasp frozenGrasp) {
                    if (!frozenGrasp.used) {
                        frozenGrasp.target = living;
                        frozenGrasp.used = true;
                        living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20 * playerSoul.getEffectiveLV(), MathHelper.ceil(playerSoul.getEffectiveLV() / 5f)));
                        living.addStatusEffect(new StatusEffectInstance(
                                frostburn ? SoulForgeEffects.FROSTBURN : SoulForgeEffects.FROSTBITE,
                                40 * playerSoul.getEffectiveLV(), 0));
                    }
                }
            }

            if (playerSoul.hasCast("Bravery Boost") && frostburn) {
                living.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.FROSTBURN, 100, 0));
            }
        }
        return target.damage(source, damage);
    }

    @ModifyVariable(method = "attack", at = @At("STORE"), ordinal = 0)
    private float modifyDamage(float original) {
        if (this.hasStatusEffect(StatusEffects.STRENGTH)) {
            original += this.getStatusEffect(StatusEffects.STRENGTH).getAmplifier() * 3;
        }
        if (this.hasStatusEffect(StatusEffects.WEAKNESS)) {
            original += this.getStatusEffect(StatusEffects.WEAKNESS).getAmplifier() * -4;
        }
        return original;
    }

    @Inject(method = "readCustomDataFromNbt", at=@At("TAIL"))
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        if (player.getInventory().selectedSlot == 9) player.getInventory().selectedSlot = 0;
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
        if (heldItem.getItem() instanceof SwordItem || heldItem.getItem() instanceof ToolItem || heldItem.getItem() instanceof TridentItem) {
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
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (playerSoul.hasValue("parry")) {
            if (playerSoul.getValue("parry") > 0) {
                if (source.getAttacker() != null) {
                    this.getWorld().playSoundFromEntity(null, player, SoulForgeSounds.PARRY_EVENT, SoundCategory.PLAYERS, 1f, 1f);
                    if (source.getAttacker() instanceof LivingEntity living)
                        living.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.VULNERABILITY, 80, 2));
                    if (amount < 30f) {
                        source.getAttacker().damage(this.getDamageSources().playerAttack(player), amount);
                    } else {
                        source.getAttacker().damage(this.getDamageSources().playerAttack(player), 30);
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

    @Inject(method = "getBlockBreakingSpeed", at=@At("RETURN"), cancellable = true)
    private void modifyMiningSpeed(BlockState block, CallbackInfoReturnable<Float> cir) {
        ItemStack stack = this.getMainHandStack();
        if (stack.getNbt() != null) {
            if (stack.getNbt().contains("Siphon")) {
                Siphon.Type type = Siphon.Type.getSiphon(stack.getNbt().getString("Siphon"));
                if (type == Siphon.Type.DETERMINATION || type == Siphon.Type.SPITE) {
                    SoulComponent playerSoul = SoulForge.getPlayerSoul((PlayerEntity)(Object)this);
                    if (playerSoul.getMagic() > 60f) {
                        cir.setReturnValue(cir.getReturnValue()*1.2f);
                    } else {
                        cir.setReturnValue(cir.getReturnValue()*0.8f);
                    }
                }
                if (type == Siphon.Type.PATIENCE || type == Siphon.Type.SPITE) {
                    cir.setReturnValue(cir.getReturnValue()*0.65f);
                }
            }
        }
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

    @Redirect(method = "interact", at=@At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;interact(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"))
    public ActionResult interact(Entity instance, PlayerEntity player, Hand hand) {
        ActionResult result = instance.interact(player, hand);
        if (!result.isAccepted()) {
            if (!player.getWorld().isClient) {
                SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                if (!(playerSoul.getTraits().contains(Traits.bravery) && playerSoul.getTraits().contains(Traits.integrity)))
                    return result;
                if (!playerSoul.hasCast("Valiant Heart")) return result;
                if (!playerSoul.hasValue("parryCooldown")) playerSoul.setValue("parryCooldown", 0f);
                if (playerSoul.getValue("parryCooldown") == 0f) {
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
        return result;
    }

    @Inject(method = "getEntityName", at=@At("HEAD"), cancellable = true)
    public void getEntityName(CallbackInfoReturnable<String> cir) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (playerSoul.getDisguise() != null) {
            if (playerSoul.getDisguise() != player) {
                cir.setReturnValue(playerSoul.getDisguise().getEntityName());
            }
        }
    }

    @Inject(method = "getName", at=@At("HEAD"), cancellable = true)
    public void getName(CallbackInfoReturnable<Text> cir) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (playerSoul.getDisguise() != null) {
            if (playerSoul.getDisguise() != player) {
                cir.setReturnValue(playerSoul.getDisguise().getName());
            }
        }
    }

    @ModifyReturnValue(method = "shouldDamagePlayer", at=@At("RETURN"))
    private boolean modifyShouldDamagePlayer(boolean original, @Local PlayerEntity player) {
        return original && TeamUtils.canDamagePlayer(getServer(), (PlayerEntity)(Object)this, player);
    }

    @ModifyReturnValue(method = "getOffGroundSpeed", at=@At("RETURN"))
    protected float modifyOffGroundSpeed(float original) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        if (player.getAttributes().hasAttribute(SoulForgeAttributes.AIR_SPEED_BECAUSE_MOJANG_SUCKS)) {
            return (float)(original * player.getAttributeValue(SoulForgeAttributes.AIR_SPEED_BECAUSE_MOJANG_SUCKS));
        }
        return original;
    }
}

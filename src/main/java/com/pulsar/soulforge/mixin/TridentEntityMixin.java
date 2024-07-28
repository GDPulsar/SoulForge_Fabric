package com.pulsar.soulforge.mixin;

import com.pulsar.soulforge.accessors.SiphonableEntity;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.siphon.Siphon;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(TridentEntity.class)
public abstract class TridentEntityMixin extends ProjectileEntity implements SiphonableEntity {
    @Shadow public ItemStack tridentStack;
    @Unique
    private static final TrackedData<String> SIPHON = DataTracker.registerData(TridentEntity.class, TrackedDataHandlerRegistry.STRING);

    public TridentEntityMixin(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;)V", at = @At("TAIL"))
    public void addSiphonData(World world, LivingEntity owner, ItemStack stack, CallbackInfo ci) {
        this.dataTracker.set(SIPHON, stack.getOrCreateNbt().contains("Siphon") ? stack.getOrCreateNbt().getString("Siphon") : "");
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    protected void addToTracker(CallbackInfo ci) {
        this.dataTracker.startTracking(SIPHON, "");
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void readSiphonData(NbtCompound nbt, CallbackInfo ci) {
        this.dataTracker.set(SIPHON, this.tridentStack.getOrCreateNbt().contains("Siphon") ? this.tridentStack.getOrCreateNbt().getString("Siphon") : "");
    }

    @Override
    public String getSiphonString() {
        return this.dataTracker.get(SIPHON);
    }

    @Inject(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/TridentEntity;onHit(Lnet/minecraft/entity/LivingEntity;)V"))
    private void addHitEffects(EntityHitResult entityHitResult, CallbackInfo ci) {
        if (!Objects.equals(getSiphonString(), "")) {
            Siphon.Type siphonType = Siphon.Type.getSiphon(getSiphonString());
            if (siphonType == Siphon.Type.KINDNESS || siphonType == Siphon.Type.SPITE) {
                if (entityHitResult.getEntity() instanceof LivingEntity living) {
                    living.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.VULNERABILITY, 400, 0));
                }
            }
            if (siphonType == Siphon.Type.PATIENCE || siphonType == Siphon.Type.SPITE) {
                if (entityHitResult.getEntity() instanceof LivingEntity living) {
                    int useLevel = this.tridentStack.getOrCreateNbt().contains("useLevel") ? this.tridentStack.getOrCreateNbt().getInt("useLevel") : 1;
                    living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 360, useLevel - 1));
                    if (useLevel >= 2) living.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 360, useLevel - 2));
                }
            }
        }
    }

    @Redirect(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private boolean modifyDamage(Entity instance, DamageSource source, float amount) {
        if (!Objects.equals(getSiphonString(), "")) {
            Siphon.Type siphonType = Siphon.Type.getSiphon(getSiphonString());
            if (siphonType == Siphon.Type.PERSEVERANCE || siphonType == Siphon.Type.SPITE) {
                return instance.damage(source, amount * 1.2f);
            }
        }
        return instance.damage(source, amount);
    }

    @Inject(method = "tryPickup", at = @At("HEAD"), cancellable = true)
    private void modifyTryPickup(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        TridentEntity trident = (TridentEntity)(Object)this;
        switch (trident.pickupType) {
            case CREATIVE_ONLY -> cir.setReturnValue(player.getAbilities().creativeMode);
            case DISALLOWED -> cir.setReturnValue(false);
        }
    }
}

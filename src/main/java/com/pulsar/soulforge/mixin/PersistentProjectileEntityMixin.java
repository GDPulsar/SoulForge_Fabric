package com.pulsar.soulforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.siphon.Siphon;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentProjectileEntity.class)
public class PersistentProjectileEntityMixin extends ProjectileEntity {
    @Unique
    private Siphon.Type siphonType;

    public PersistentProjectileEntityMixin(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "<init>(Lnet/minecraft/entity/EntityType;DDDLnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)V", at=@At("TAIL"))
    private void addInitData(EntityType type, double x, double y, double z, World world, ItemStack stack, ItemStack weapon, CallbackInfo ci) {
        if (weapon.get(SoulForgeItems.SIPHON_COMPONENT) != null) {
            siphonType = Siphon.Type.getSiphon(stack.get(SoulForgeItems.SIPHON_COMPONENT));
        }
    }

    @Inject(method = "writeCustomDataToNbt", at=@At("TAIL"))
    private void writeCustomNbt(NbtCompound nbt, CallbackInfo ci) {
        if (siphonType != null) nbt.putString("Siphon", siphonType.asString());
    }

    @Inject(method = "readCustomDataFromNbt", at=@At("HEAD"))
    private void readCustomNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("Siphon")) this.siphonType = Siphon.Type.getSiphon(nbt.getString("Siphon"));
    }

    @Inject(method = "onHit", at=@At("TAIL"))
    protected void modifyOnHit(LivingEntity target, CallbackInfo ci) {
        if (siphonType == Siphon.Type.DETERMINATION || siphonType == Siphon.Type.SPITE) {
            if (this.getOwner() instanceof LivingEntity owner) {
                owner.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 60, 2));
            }
        }
    }

    @ModifyReturnValue(method = "asItemStack", at=@At("RETURN"))
    public ItemStack asItemStack(ItemStack original) {
        original.remove(SoulForgeItems.SIPHON_COMPONENT);
        return original;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {}
}

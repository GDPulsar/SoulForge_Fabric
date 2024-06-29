package com.pulsar.soulforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.siphon.Siphon;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArrowEntity.class)
public abstract class ArrowEntityMixin extends PersistentProjectileEntity {
    @Unique
    private Siphon.Type siphonType;

    protected ArrowEntityMixin(EntityType<? extends PersistentProjectileEntity> entityType, World world) { super(entityType, world); }

    @Inject(method = "initFromStack", at=@At("HEAD"))
    private void addInitData(ItemStack stack, CallbackInfo ci) {
        if (stack.getNbt() != null) {
            if (stack.getNbt().contains("Siphon")) {
                siphonType = Siphon.Type.getSiphon(stack.getNbt().getString("Siphon"));
            }
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
        if (original.getNbt() != null) {
            original.getNbt().remove("Siphon");
        }
        return original;
    }
}

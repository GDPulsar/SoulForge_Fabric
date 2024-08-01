package com.pulsar.soulforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.sugar.Local;
import com.pulsar.soulforge.entity.BlastEntity;
import com.pulsar.soulforge.item.devices.machines.SiphonImbuer;
import com.pulsar.soulforge.item.special.CrushingRound;
import com.pulsar.soulforge.item.special.FrostbiteRound;
import com.pulsar.soulforge.item.special.PuncturingRound;
import com.pulsar.soulforge.item.special.SuppressingRound;
import com.pulsar.soulforge.siphon.Siphon;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(BowItem.class)
public abstract class BowItemMixin {
    @Shadow public abstract int getMaxUseTime(ItemStack stack);

    @ModifyArg(method = "onStoppedUsing", at=@At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;setVelocity(Lnet/minecraft/entity/Entity;FFFFF)V"), index = 4)
    private float modifyProjectileSpeed(float speed, @Local LivingEntity user) {
        if (user instanceof PlayerEntity player) {
            if (player.getMainHandStack().hasNbt() && player.getMainHandStack().getNbt().contains("Siphon")) {
                Siphon.Type type = Siphon.Type.getSiphon(player.getMainHandStack().getNbt().getString("Siphon"));
                if (type == Siphon.Type.BRAVERY || type == Siphon.Type.SPITE) {
                    return speed * 1.2f;
                }
            }
        }
        return speed;
    }

    @ModifyExpressionValue(method = "onStoppedUsing", at=@At(value="INVOKE", target="Lnet/minecraft/enchantment/EnchantmentHelper;getLevel(Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/item/ItemStack;)I", ordinal = 0))
    private int modifyInfinity(int original, @Local ItemStack stack) {
        if (stack.hasNbt() && stack.getNbt().contains("Siphon")) {
            Siphon.Type type = Siphon.Type.getSiphon(stack.getNbt().getString("Siphon"));
            if (type == Siphon.Type.JUSTICE || type == Siphon.Type.SPITE) {
                return 1;
            }
        }
        return original;
    }

    @ModifyReceiver(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z", ordinal = 0))
    private ItemStack modifyInfinity(ItemStack stack, Item item) {
        if (stack.hasNbt() && stack.getNbt().contains("Siphon")) {
            Siphon.Type type = Siphon.Type.getSiphon(stack.getNbt().getString("Siphon"));
            if (type == Siphon.Type.JUSTICE || type == Siphon.Type.SPITE) {
                return new ItemStack(item);
            }
        }
        return stack;
    }

    @Inject(method = "onStoppedUsing", at=@At(value = "INVOKE", target = "Lnet/minecraft/item/BowItem;getPullProgress(I)F", shift = At.Shift.AFTER), cancellable = true)
    private void doSiphonImbuer(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
        if (stack.getNbt() != null) {
            if (stack.getNbt().getBoolean("imbued")) {
                if (user instanceof PlayerEntity player) {
                    int useTicks = this.getMaxUseTime(stack) - remainingUseTicks;
                    float pullTime = (float) useTicks / 20.0F;
                    float strength = (pullTime * pullTime + pullTime * 2.0F) / 3.0F;
                    if (strength >= 1.25f) {
                        Vec3d end = user.getEyePos().add(user.getRotationVector().multiply(50f));
                        HitResult hit = user.getWorld().raycast(new RaycastContext(user.getEyePos(), user.getEyePos().add(user.getRotationVector().multiply(50f)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, user));
                        if (hit != null) end = hit.getPos().subtract(Utils.getArmPosition(player));
                        float damage = 15-((pullTime-6)*(pullTime-6))/2f;
                        if (pullTime > 6) damage = 15f;
                        BlastEntity blast = new BlastEntity(user.getWorld(), Utils.getArmPosition(player),
                                user, 0.25f, Vec3d.ZERO, end, damage, Color.YELLOW, true, 20);
                        blast.owner = user;
                        world.spawnEntity(blast);
                        world.playSoundFromEntity(null, player, SoulForgeSounds.UT_BLASTER_EVENT, SoundCategory.PLAYERS, 1f, 1f);
                        ItemStack imbuer = Utils.getImbuer(stack, player);
                        if (imbuer != null) ((SiphonImbuer)imbuer.getItem()).decreaseCharge(imbuer, MathHelper.ceil(damage));
                        ci.cancel();
                    }
                }
            }
        }
    }

    @Redirect(method = "onStoppedUsing", at=@At(value = "INVOKE", target = "Lnet/minecraft/item/ArrowItem;createArrow(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/entity/projectile/PersistentProjectileEntity;"))
    private PersistentProjectileEntity addProjectileEffects(ArrowItem instance, World world, ItemStack arrowStack, LivingEntity shooter) {
        ItemStack stack = shooter.getMainHandStack();
        if (!(stack.getItem() instanceof BowItem)) stack = shooter.getOffHandStack();
        if (!(stack.getItem() instanceof BowItem)) return instance.createArrow(world, arrowStack, shooter);
        NbtCompound arrowNbt = arrowStack.getNbt();
        if (arrowNbt == null) arrowNbt = new NbtCompound();
        if (stack.getNbt() != null) {
            if (stack.getNbt().contains("Siphon")) {
                arrowNbt.putString("Siphon", stack.getNbt().getString("Siphon"));
            }
        }
        ItemStack arrow = arrowStack.copy();
        arrow.setNbt(arrowNbt);
        PersistentProjectileEntity persistentProjectileEntity = null;
        if (stack.getOrCreateNbt().contains("reloaded")) {
            switch (stack.getOrCreateNbt().getString("reloaded")) {
                case "frostbite":
                    persistentProjectileEntity = FrostbiteRound.createProjectile(world, stack, shooter);
                    break;
                case "crushing":
                    persistentProjectileEntity = CrushingRound.createProjectile(world, stack, shooter);
                    break;
                case "puncturing":
                    persistentProjectileEntity = PuncturingRound.createProjectile(world, stack, shooter);
                    break;
                case "suppressing":
                    persistentProjectileEntity = SuppressingRound.createProjectile(world, stack, shooter);
                    break;
            }
            stack.getOrCreateNbt().remove("reloaded");
        } else {
            persistentProjectileEntity = instance.createArrow(world, arrowStack, shooter);
        }
        assert persistentProjectileEntity != null;
        if (stack.getNbt() != null) {
            if (stack.getNbt().contains("Siphon")) {
                Siphon.Type type = Siphon.Type.getSiphon(stack.getNbt().getString("Siphon"));
                if (type == Siphon.Type.PERSEVERANCE || type == Siphon.Type.SPITE) {
                    persistentProjectileEntity.setPierceLevel((byte) 1);
                }
                if (type == Siphon.Type.DETERMINATION || type == Siphon.Type.SPITE) {
                    persistentProjectileEntity.addCommandTag("Determination Siphon");
                }
                if (type == Siphon.Type.PATIENCE || type == Siphon.Type.SPITE) {
                    persistentProjectileEntity.addCommandTag("Patience Siphon");
                }
                if (type == Siphon.Type.KINDNESS || type == Siphon.Type.SPITE) {
                    persistentProjectileEntity.addCommandTag("Kindness Siphon");
                }
            }
        }
        return persistentProjectileEntity;
    }
}

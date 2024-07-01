package com.pulsar.soulforge.mixin;

import com.pulsar.soulforge.entity.BlastEntity;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.item.devices.machines.SiphonImbuer;
import com.pulsar.soulforge.item.special.CrushingRound;
import com.pulsar.soulforge.item.special.FrostbiteRound;
import com.pulsar.soulforge.item.special.PuncturingRound;
import com.pulsar.soulforge.item.special.SuppressingRound;
import com.pulsar.soulforge.siphon.Siphon;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.awt.*;
import java.util.List;

@Mixin(BowItem.class)
public abstract class BowItemMixin {
    @Shadow public abstract int getMaxUseTime(ItemStack stack, LivingEntity user);

    @Inject(method = "onStoppedUsing", at=@At(value = "INVOKE", target = "Lnet/minecraft/item/BowItem;getPullProgress(I)F", shift = At.Shift.AFTER), cancellable = true)
    private void doSiphonImbuer(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
        if (Boolean.TRUE.equals(stack.get(SoulForgeItems.IMBUED_COMPONENT))) {
            if (user instanceof PlayerEntity player) {
                int useTicks = this.getMaxUseTime(stack, user) - remainingUseTicks;
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

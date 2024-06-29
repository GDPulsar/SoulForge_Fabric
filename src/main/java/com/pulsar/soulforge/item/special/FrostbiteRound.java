package com.pulsar.soulforge.item.special;

import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.entity.JusticePelletProjectile;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TippedArrowItem;
import net.minecraft.potion.PotionUtil;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class FrostbiteRound extends TippedArrowItem {
    public FrostbiteRound(Settings settings) {
        super(settings);
    }

    public static PersistentProjectileEntity createProjectile(World world, ItemStack stack, LivingEntity shooter) {
        ArrowEntity arrowEntity = new ArrowEntity(world, shooter);
        arrowEntity.addEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 900, 2));
        arrowEntity.addEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 900, 1));
        arrowEntity.addEffect(new StatusEffectInstance(SoulForgeEffects.FROSTBITE, 900, 1));
        arrowEntity.addCommandTag("Frostbite Round");
        arrowEntity.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
        return arrowEntity;
    }

    public static JusticePelletProjectile createPellet(World world, LivingEntity shooter) {
        JusticePelletProjectile pellet = new JusticePelletProjectile(world, shooter, 2, living -> {
            Utils.addEffectDuration(living, StatusEffects.SLOWNESS, 900, 2);
            Utils.addEffectDuration(living, StatusEffects.WEAKNESS, 900, 1);
            Utils.addEffectDuration(living, SoulForgeEffects.FROSTBITE, 900, 1);
        });
        pellet.addCommandTag("Frostbite Round");
        return pellet;
    }

    public static JusticePelletProjectile createPellet(World world, LivingEntity shooter, float durationReduction) {
        JusticePelletProjectile pellet = new JusticePelletProjectile(world, shooter, 2, living -> {
            Utils.addEffectDuration(living, StatusEffects.SLOWNESS, (int)(900*durationReduction), 2);
            Utils.addEffectDuration(living, StatusEffects.WEAKNESS, (int)(900*durationReduction), 1);
            Utils.addEffectDuration(living, SoulForgeEffects.FROSTBITE, (int)(900*durationReduction), 1);
        });
        pellet.addCommandTag("Frostbite Round");
        return pellet;
    }
}

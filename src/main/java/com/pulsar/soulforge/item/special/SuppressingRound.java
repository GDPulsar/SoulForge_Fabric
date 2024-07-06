package com.pulsar.soulforge.item.special;

import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.entity.JusticePelletProjectile;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TippedArrowItem;
import net.minecraft.world.World;

public class SuppressingRound extends TippedArrowItem {
    public SuppressingRound(Settings settings) {
        super(settings);
    }

    public static PersistentProjectileEntity createProjectile(World world, ItemStack stack, LivingEntity shooter) {
        ArrowEntity arrowEntity = new ArrowEntity(world, shooter);
        arrowEntity.addEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 80, 4));
        arrowEntity.addEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 80, 3));
        arrowEntity.addEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 80, 4));
        arrowEntity.addEffect(new StatusEffectInstance(SoulForgeEffects.MANA_SICKNESS, 80, 4));
        arrowEntity.addCommandTag("Suppressing Round");
        arrowEntity.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
        return arrowEntity;
    }

    public static JusticePelletProjectile createPellet(World world, LivingEntity shooter) {
        JusticePelletProjectile pellet = new JusticePelletProjectile(world, shooter, 2, living -> {
            Utils.addEffectDuration(living, StatusEffects.SLOWNESS, 80, 4);
            Utils.addEffectDuration(living, StatusEffects.WEAKNESS, 80, 3);
            Utils.addEffectDuration(living, StatusEffects.MINING_FATIGUE, 80, 4);
            Utils.addEffectDuration(living, SoulForgeEffects.MANA_SICKNESS, 80, 4);
        });
        pellet.addCommandTag("Suppressing Round");
        return pellet;
    }

    public static JusticePelletProjectile createPellet(World world, LivingEntity shooter, float durationReduction) {
        JusticePelletProjectile pellet = new JusticePelletProjectile(world, shooter, 2, living -> {
            Utils.addEffectDuration(living, StatusEffects.SLOWNESS, (int)(80*durationReduction), 4);
            Utils.addEffectDuration(living, StatusEffects.WEAKNESS, (int)(80*durationReduction), 3);
            Utils.addEffectDuration(living, StatusEffects.MINING_FATIGUE, (int)(80*durationReduction), 4);
            Utils.addEffectDuration(living, SoulForgeEffects.MANA_SICKNESS, (int)(80*durationReduction), 4);
        });
        pellet.addCommandTag("Suppressing Round");
        return pellet;
    }
}

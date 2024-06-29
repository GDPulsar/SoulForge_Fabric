package com.pulsar.soulforge.item.special;

import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.entity.DeterminationArrowProjectile;
import com.pulsar.soulforge.entity.JusticePelletProjectile;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
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

public class CrushingRound extends TippedArrowItem {
    public CrushingRound(Settings settings) {
        super(settings);
    }

    public static PersistentProjectileEntity createProjectile(World world, ItemStack stack, LivingEntity shooter) {
        ArrowEntity arrowEntity = new ArrowEntity(world, shooter);
        arrowEntity.addEffect(new StatusEffectInstance(SoulForgeEffects.VULNERABILITY, 400, 2));
        arrowEntity.addEffect(new StatusEffectInstance(SoulForgeEffects.CRUSHED, 400, 0));
        arrowEntity.addCommandTag("Crushing Round");
        arrowEntity.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
        return arrowEntity;
    }

    public static JusticePelletProjectile createPellet(World world, LivingEntity shooter) {
        JusticePelletProjectile pellet = new JusticePelletProjectile(world, shooter, 2, living -> {
            Utils.addEffectDuration(living, SoulForgeEffects.VULNERABILITY, 400, 2);
            Utils.addEffectDuration(living, SoulForgeEffects.CRUSHED, 400, 0);
        });
        pellet.addCommandTag("Crushing Round");
        return pellet;
    }

    public static JusticePelletProjectile createPellet(World world, LivingEntity shooter, float durationReduction) {
        JusticePelletProjectile pellet = new JusticePelletProjectile(world, shooter, 2, living -> {
            Utils.addEffectDuration(living, SoulForgeEffects.VULNERABILITY, (int) (400*durationReduction), 2);
            Utils.addEffectDuration(living, SoulForgeEffects.CRUSHED, (int) (400*durationReduction), 0);
        });
        pellet.addCommandTag("Crushing Round");
        return pellet;
    }
}

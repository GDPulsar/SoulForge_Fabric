package com.pulsar.soulforge.item.special;

import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.entity.JusticePelletProjectile;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TippedArrowItem;
import net.minecraft.world.World;

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

    public static JusticePelletProjectile createPellet(World world, LivingEntity shooter, float damage) {
        return createPellet(world, shooter, damage, 1f);
    }

    public static JusticePelletProjectile createPellet(World world, LivingEntity shooter, float damage, float durationReduction) {
        JusticePelletProjectile pellet = new JusticePelletProjectile(world, shooter, damage, living -> {
            Utils.addEffectDuration(living, SoulForgeEffects.VULNERABILITY, (int) (400*durationReduction), 2);
            Utils.addEffectDuration(living, SoulForgeEffects.CRUSHED, (int) (400*durationReduction), 0);
        });
        pellet.addCommandTag("Crushing Round");
        return pellet;
    }
}

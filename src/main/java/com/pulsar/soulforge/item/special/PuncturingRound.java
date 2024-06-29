package com.pulsar.soulforge.item.special;

import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.entity.JusticePelletProjectile;
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

public class PuncturingRound extends TippedArrowItem {
    public PuncturingRound(Settings settings) {
        super(settings);
    }

    public static PersistentProjectileEntity createProjectile(World world, ItemStack stack, LivingEntity shooter) {
        ArrowEntity arrowEntity = new ArrowEntity(world, shooter);
        arrowEntity.setDamage(arrowEntity.getDamage()*1.5f);
        arrowEntity.setPierceLevel((byte)5);
        arrowEntity.addCommandTag("Puncturing Round");
        arrowEntity.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
        return arrowEntity;
    }

    public static JusticePelletProjectile createPellet(World world, LivingEntity shooter) {
        JusticePelletProjectile pellet = new JusticePelletProjectile(world, shooter);
        pellet.addCommandTag("Puncturing Round");
        return pellet;
    }

    public static JusticePelletProjectile createPellet(World world, LivingEntity shooter, float durationReduction) {
        JusticePelletProjectile pellet = new JusticePelletProjectile(world, shooter);
        pellet.addCommandTag("Puncturing Round");
        return pellet;
    }
}

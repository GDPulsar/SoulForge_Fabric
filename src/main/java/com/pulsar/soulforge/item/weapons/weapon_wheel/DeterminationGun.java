package com.pulsar.soulforge.item.weapons.weapon_wheel;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.entity.DeterminationShotProjectile;
import com.pulsar.soulforge.item.weapons.MagicRangedItem;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class DeterminationGun extends MagicRangedItem {
    public DeterminationGun() {}

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            NbtCompound stackNbt = stack.getOrCreateNbt();
            int ammo = stackNbt.contains("ammo") ? stackNbt.getInt("ammo") : 0;
            if (ammo > 0) {
                SoulComponent playerSoul = SoulForge.getPlayerSoul(user);
                float damage = 5f + playerSoul.getEffectiveLV()/2.5f;
                DeterminationShotProjectile projectile = new DeterminationShotProjectile(world, user, damage);
                projectile.setPos(user.getEyePos());
                projectile.setVelocity(user.getRotationVector().multiply(8));
                world.spawnEntity(projectile);
            }
            ammo--;
            stackNbt.putInt("ammo", ammo);
            user.getItemCooldownManager().set(this, 6);
            world.playSoundFromEntity(null, user, SoulForgeSounds.GUN_SHOOT_EVENT, SoundCategory.PLAYERS, 1f, 1f);
            return TypedActionResult.consume(user.getStackInHand(hand));
        }
        return TypedActionResult.fail(user.getStackInHand(hand));
    }

    @Override
    public int getRange() {
        return 32;
    }
}

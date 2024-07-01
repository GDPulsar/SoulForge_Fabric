package com.pulsar.soulforge.item.weapons.weapon_wheel;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.weapons.MagicRangedItem;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class DeterminationGun extends MagicRangedItem {
    public int ammo = 12;
    private int cooldown = 0;
    private int ammoCooldown = 50;

    public DeterminationGun() {}

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            if (ammo > 0 && cooldown <= 0) {
                EntityHitResult hit = Utils.getFocussedEntity(user, getRange());
                if (hit != null) {
                    if (hit.getEntity() instanceof LivingEntity target) {
                        SoulComponent playerSoul = SoulForge.getPlayerSoul(user);
                        target.damage(user.getDamageSources().playerAttack(user), 5f + playerSoul.getEffectiveLV()/2.5f);
                        ammo--;
                        cooldown = 10;
                        world.playSoundFromEntity(null, user, SoulForgeSounds.GUN_SHOOT_EVENT, SoundCategory.PLAYERS, 1f, 1f);
                        return TypedActionResult.consume(user.getStackInHand(hand));
                    }
                }
            }
        }
        return TypedActionResult.fail(user.getStackInHand(hand));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient) {
            if (cooldown > 0) cooldown--;
            if (ammo < 12) {
                if (ammoCooldown <= 0) {
                    ammo++;
                    ammoCooldown = 50;
                }
                ammoCooldown--;
            }
        }
    }

    @Override
    public int getRange() {
        return 32;
    }
}

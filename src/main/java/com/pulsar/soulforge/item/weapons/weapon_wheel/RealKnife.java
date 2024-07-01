package com.pulsar.soulforge.item.weapons.weapon_wheel;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.entity.SmallSlashProjectile;
import com.pulsar.soulforge.item.weapons.MagicSwordItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class RealKnife extends MagicSwordItem {
    public RealKnife() {
        super(0, 2f, 0.8f);
    }

    private List<SmallSlashProjectile> slashes = new ArrayList<>();

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(user);
            if (user.isSneaking()) {
                for (SmallSlashProjectile slash : slashes) {
                    slash.setVelocity(slash.getRotationVector());
                    slash.velocityModified = true;
                }
                slashes = new ArrayList<>();
            } else if (playerSoul.getMagic() >= 5f) {
                SmallSlashProjectile slash = new SmallSlashProjectile(world, user);
                slash.setOwner(user);
                slash.setPosition(user.getEyePos());
                slash.setPitch(user.getPitch());
                slash.setYaw(user.getYaw());
                slash.setVelocity(Vec3d.ZERO);
                world.spawnEntity(slash);
                slashes.add(slash);
                playerSoul.setMagic(playerSoul.getMagic() - 5f);
                playerSoul.resetLastCastTime();
            }
        }
        ItemStack stack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(stack);
    }
}

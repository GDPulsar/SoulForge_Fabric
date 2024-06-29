package com.pulsar.soulforge.item.weapons.weapon_wheel;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.weapons.MagicSwordItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DeterminationGauntlets extends MagicSwordItem {
    public DeterminationGauntlets() {
        super(3, 3.5f, 0.2f);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            if (user.isSneaking()) {
                SoulComponent playerSoul = SoulForge.getPlayerSoul(user);
                if (playerSoul.hasValue("dtGauntletsRush")) {
                    if (playerSoul.getValue("dtGauntletsRush") > 0) return TypedActionResult.pass(user.getStackInHand(hand));
                }
                playerSoul.setValue("dtGauntletsRush", 15);
                Vec3d flattened = new Vec3d(user.getRotationVector().x, 0f, user.getRotationVector().z).normalize();
                user.addVelocity(flattened.multiply(2.5f));
                user.velocityModified = true;
            }
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }
}

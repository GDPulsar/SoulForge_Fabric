package com.pulsar.soulforge.item.weapons.weapon_wheel;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.components.ValueComponent;
import com.pulsar.soulforge.item.weapons.MagicSwordItem;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DeterminationGauntlets extends MagicSwordItem {
    public DeterminationGauntlets() {
        super(3, 3.5f, 0.2f);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(user);
            ValueComponent values = SoulForge.getValues(user);
            if (user.isSneaking()) {
                values.setTimer("dtGauntletsRush", 15);
                Vec3d flattened = new Vec3d(user.getRotationVector().x, 0f, user.getRotationVector().z).normalize();
                user.addVelocity(flattened.multiply(2.5f));
                user.velocityModified = true;
                user.getItemCooldownManager().set(this, 60);
            } else {
                EntityHitResult hitResult = Utils.getFocussedEntity(user, 3);
                if (hitResult != null && hitResult.getEntity() instanceof LivingEntity target) {
                    user.addVelocity(0, 0.7, 0);
                    target.setFireTicks(10);
                    target.damage(user.getDamageSources().playerAttack(user), this.baseAttackDamage*2.5f);
                    target.timeUntilRegen = 15;
                    MinecraftServer server = world.getServer();
                    if (server != null) {
                        ServerWorld serverWorld = server.getWorld(world.getRegistryKey());
                        if (serverWorld != null) {
                            Entity serverEntity = serverWorld.getEntity(target.getUuid());
                            serverEntity.addVelocity(0, 0.3, 0);
                            serverEntity.velocityModified = true;
                        }
                    }
                    user.getItemCooldownManager().set(this, 30);
                    return TypedActionResult.success(user.getStackInHand(hand));
                }
            }
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }
}

package com.pulsar.soulforge.item.weapons;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.entity.PVHarpoonProjectile;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PerseveranceHarpoon extends MagicSwordItem {
    public PerseveranceHarpoon() {
        // attack damage, attack speed
        super(5, 1.2f, 0.2f);
        addAttribute(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE, new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, "pv_harpoon_reach"), 2f, EntityAttributeModifier.Operation.ADD_VALUE));
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (target instanceof PlayerEntity playerTarget) {
            SoulComponent targetSoul = SoulForge.getPlayerSoul(playerTarget);
            Utils.addAntiheal(0.2f, 200f, targetSoul);
        }
        Vec3d direction = target.getPos().subtract(attacker.getPos());
        target.takeKnockback(0.4f, direction.x, direction.z);
        return super.postHit(stack, target, attacker);
    }

    public PVHarpoonProjectile projectile = null;

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity playerEntity)) {
            return;
        }
        int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
        if (i < 10) {
            return;
        }
        if (!world.isClient) {
            if (projectile == null || projectile.isRemoved()) {
                projectile = new PVHarpoonProjectile(world, user);
                projectile.setPosition(user.getEyePos());
                projectile.setVelocity(user.getRotationVector().multiply(2));
                world.spawnEntity(projectile);
            }
        }
        playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }
}

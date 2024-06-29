package com.pulsar.soulforge.item.weapons;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.trait.Traits;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class BraveryGauntlets extends MagicSwordItem {
    public BraveryGauntlets() {
        super(3, 3.5f, 0.2f);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean frostburn = false;
        if (attacker instanceof PlayerEntity player) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            frostburn = playerSoul.getTraits().contains(Traits.bravery) && playerSoul.getTraits().contains(Traits.patience);
        }
        if (!frostburn) target.setFireTicks(10);
        else target.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.FROSTBURN, 10, 0));
        target.timeUntilRegen = 15;
        return super.postHit(stack, target, attacker);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(user);
        boolean frostburn = playerSoul.getTraits().contains(Traits.bravery) && playerSoul.getTraits().contains(Traits.patience);
        EntityHitResult hitResult = Utils.getFocussedEntity(user, 3);
        if (hitResult != null && hitResult.getEntity() instanceof LivingEntity target) {
            user.addVelocity(0, 0.7, 0);
            if (!frostburn) target.setFireTicks(10);
            else target.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.FROSTBURN, 10, 0));
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
        return TypedActionResult.pass(user.getStackInHand(hand));
    }
}

package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.util.TeamUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class LightningRodLightningEntity extends LightningEntity {
    private PlayerEntity owner;
    public LightningRodLightningEntity(PlayerEntity owner) {
        super(SoulForgeEntities.WEATHER_WARNING_LIGHTNING_ENTITY_TYPE, owner.getWorld());
        this.owner = owner;
    }

    public LightningRodLightningEntity(EntityType<LightningRodLightningEntity> type, World world) {
        super(type, world);
    }

    private int ambientTick = 2;

    @Override
    public void tick() {
        if (this.ambientTick == 2) {
            if (this.getWorld().isClient()) {
                this.getWorld().playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 10000.0F, 0.8F + this.random.nextFloat() * 0.2F, false);
                this.getWorld().playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.WEATHER, 2.0F, 0.5F + this.random.nextFloat() * 0.2F, false);
            }
        }

        --this.ambientTick;
        if (this.ambientTick >= 0) {
            if (!(this.getWorld() instanceof ServerWorld)) {
                this.getWorld().setLightningTicksLeft(2);
            } else {
                for (Entity entity : this.getWorld().getOtherEntities(this,
                        new Box(this.getX() - 3.0, this.getY() - 3.0, this.getZ() - 3.0,
                                this.getX() + 3.0, this.getY() + 6.0 + 3.0, this.getZ() + 3.0),
                        Entity::isAlive)) {
                    if (entity == this.owner) continue;
                    if (entity instanceof PlayerEntity targetPlayer && this.owner != null) {
                        if (!TeamUtils.canDamagePlayer(this.getServer(), this.owner, targetPlayer)) return;
                    }
                    entity.setFireTicks(entity.getFireTicks() + 1);
                    if (entity.getFireTicks() == 0) {
                        entity.setOnFireFor(8);
                    }
                    entity.timeUntilRegen = 0;
                    entity.damage(this.getDamageSources().lightningBolt(), 18.0F);
                }
            }
        } else {
            this.discard();
        }
    }
}

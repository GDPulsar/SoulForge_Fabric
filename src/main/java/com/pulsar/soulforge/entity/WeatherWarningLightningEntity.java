package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
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

public class WeatherWarningLightningEntity extends LightningEntity {
    private PlayerEntity owner;
    public WeatherWarningLightningEntity(PlayerEntity owner) {
        super(SoulForgeEntities.WEATHER_WARNING_LIGHTNING_ENTITY_TYPE, owner.getWorld());
        this.owner = owner;
    }

    public WeatherWarningLightningEntity(EntityType<WeatherWarningLightningEntity> type, World world) {
        super(type, world);
    }

    public boolean canUsePortals() {
        return false;
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
                float styleIncrease = 0f;
                for (Entity entity : this.getWorld().getOtherEntities(this,
                        new Box(this.getX() - 3.0, this.getY() - 3.0, this.getZ() - 3.0,
                                this.getX() + 3.0, this.getY() + 6.0 + 3.0, this.getZ() + 3.0),
                        Entity::isAlive)) {
                    if (entity == this.owner) continue;
                    if (entity instanceof PlayerEntity targetPlayer && this.owner != null) {
                        if (!TeamUtils.canDamagePlayer(this.getServer(), this.owner, targetPlayer)) return;
                    }
                    entity.onStruckByLightning((ServerWorld) this.getWorld(), this);
                    if (entity instanceof LivingEntity living) {
                        living.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.VULNERABILITY, 900, 2));
                        styleIncrease += 40f * (1f + Utils.getTotalDebuffLevel(living)/10f);
                    }
                }
                if (this.owner != null) {
                    SoulComponent playerSoul = SoulForge.getPlayerSoul(this.owner);
                    playerSoul.setStyle(playerSoul.getStyle() + (int)styleIncrease);
                }
            }
        } else {
            this.discard();
        }
    }
}

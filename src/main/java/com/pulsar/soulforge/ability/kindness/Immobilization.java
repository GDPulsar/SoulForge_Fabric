package com.pulsar.soulforge.ability.kindness;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.entity.ImmobilizationEntity;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.event.GameEvent;

public class Immobilization extends ToggleableAbilityBase {
    private ImmobilizationEntity entity;
    private LivingEntity target = null;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        if (!getActive()) {
            if (entity != null) {
                if (target != null) {
                    if (target.isPlayer()) {
                        SoulComponent targetSoul = SoulForge.getPlayerSoul((PlayerEntity)target);
                        targetSoul.removeTag("immobile");
                    } else if (target instanceof MobEntity mob) {
                        mob.setAiDisabled(true);
                    }
                    target.setInvulnerable(true);
                    target = null;
                    setActive(false);
                    return true;
                }
                entity.remove(Entity.RemovalReason.DISCARDED);
            }
            EntityHitResult result = Utils.getFocussedEntity(player, 10);
            if (result != null) {
                SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                if (result.getEntity() instanceof PlayerEntity playerTarget) {
                    target = playerTarget;
                    SoulComponent targetSoul = SoulForge.getPlayerSoul(playerTarget);
                    targetSoul.addTag("immobile");
                } else if (result.getEntity() instanceof MobEntity mob) {
                    target = mob;
                    mob.setAiDisabled(true);
                }
                if (target != null) {
                    player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoulForgeSounds.UT_REFLECT_EVENT, SoundCategory.PLAYERS, 1f, 1f);
                    entity = new ImmobilizationEntity(player.getWorld(), target.getPos(), playerSoul.getEffectiveLV() * 5, target);
                    entity.maxHealth = playerSoul.getEffectiveLV() * 5;
                    entity.health = playerSoul.getEffectiveLV() * 5;
                    entity.setPosition(target.getPos());
                    entity.setEntity(target);
                    entity.setSize((float)Math.max(target.getBoundingBox().getXLength(), target.getBoundingBox().getZLength()), (float)target.getBoundingBox().getYLength());
                    entity.calculateDimensions();
                    ServerWorld serverWorld = player.getServerWorld();
                    serverWorld.spawnEntity(entity);
                    serverWorld.emitGameEvent(GameEvent.ENTITY_PLACE, player.getPos(), GameEvent.Emitter.of(player));
                    target.setInvulnerable(true);
                    return super.cast(player);
                }
            }
        } else {
            entity.remove(Entity.RemovalReason.DISCARDED);
            entity = null;
            setActive(false);
        }
        return true;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        if (entity == null) return true;
        return entity.isRemoved() || !getActive();
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        setActive(false);
        if (target != null) {
            if (target.isPlayer()) {
                SoulComponent targetSoul = SoulForge.getPlayerSoul((PlayerEntity)target);
                targetSoul.removeTag("immobile");
            } else if (target instanceof MobEntity mob) {
                mob.setAiDisabled(false);
            }
            target.setInvulnerable(false);
        }
        return super.end(player);
    }

    public int getLV() { return 7; }

    public int getCost() { return 30; }

    public int getCooldown() { return 1200; }

    @Override
    public AbilityBase getInstance() {
        return new Immobilization();
    }
}

package com.pulsar.soulforge.ability.kindness;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.entity.ImmobilizationEntity;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.tag.SoulForgeTags;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.event.GameEvent;

public class Immobilization extends ToggleableAbilityBase {
    private ImmobilizationEntity entity = null;
    private LivingEntity target = null;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        if (!getActive()) {
            if (entity != null) {
                if (target != null) {
                    SoulForge.getValues(target).removeBool("Immobilized");
                    target.setInvulnerable(false);
                    target = null;
                    setActive(false);
                }
                entity.remove(Entity.RemovalReason.DISCARDED);
                return true;
            }
            EntityHitResult result = Utils.getFocussedEntity(player, 16);
            if (result != null) {
                SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                if (result.getEntity() instanceof LivingEntity living) {
                    if (living.getType().isIn(SoulForgeTags.BOSS_ENTITY)) return false;
                    target = living;
                    SoulForge.getValues(target).setTimer("Immobilized", 2);
                    if (target != null) {
                        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoulForgeSounds.UT_REFLECT_EVENT, SoundCategory.PLAYERS, 1f, 1f);
                        entity = new ImmobilizationEntity(player.getWorld(), target.getPos(), playerSoul.getEffectiveLV() * 5, target, player);
                        entity.maxHealth = playerSoul.getEffectiveLV() * 5;
                        entity.health = playerSoul.getEffectiveLV() * 5;
                        entity.setPosition(target.getPos());
                        entity.setEntity(target);
                        entity.setSize((float) Math.max(target.getBoundingBox().getXLength(), target.getBoundingBox().getZLength()), (float) target.getBoundingBox().getYLength());
                        entity.calculateDimensions();
                        ServerWorld serverWorld = player.getServerWorld();
                        serverWorld.spawnEntity(entity);
                        serverWorld.emitGameEvent(GameEvent.ENTITY_PLACE, player.getPos(), GameEvent.Emitter.of(player));
                        target.setInvulnerable(true);
                        return super.cast(player);
                    }
                }
            }
        } else {
            entity.remove(Entity.RemovalReason.DISCARDED);
            entity = null;
            setActive(false);
        }
        return false;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        if (target != null) {
            SoulForge.getValues(target).setTimer("Immobilized", 2);
        }
        if (entity == null) return true;
        return entity.isRemoved() || !getActive();
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        setActive(false);
        if (target != null) {
            SoulForge.getValues(target).removeBool("Immobilized");
            SoulForge.getValues(target).removeTimer("Immobilized");
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

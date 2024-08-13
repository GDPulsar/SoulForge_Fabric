package com.pulsar.soulforge.ability.patience;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.entity.TotalFrostbiteEntity;
import com.pulsar.soulforge.tag.SoulForgeTags;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.event.GameEvent;

public class TotalFrostbite extends AbilityBase {
    private TotalFrostbiteEntity entity;
    private LivingEntity target = null;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        if (!getActive()) {
            EntityHitResult result = Utils.getFocussedEntity(player, 10);
            if (result != null) {
                SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                if (result.getEntity() instanceof LivingEntity living) {
                    if (living.getType().isIn(SoulForgeTags.BOSS_ENTITY)) return false;
                    target = living;
                    SoulForge.getValues(target).setBool("Immobilized", true);
                    player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 1f, 1f);
                    entity = new TotalFrostbiteEntity(player.getWorld(), target.getPos(), 40, target, player);
                    entity.maxHealth = 40;
                    entity.health = 40;
                    entity.setPosition(target.getPos());
                    entity.setEntity(target);
                    entity.setSize((float) Math.max(target.getBoundingBox().getXLength(), target.getBoundingBox().getZLength()), (float) target.getBoundingBox().getYLength());
                    entity.calculateDimensions();
                    ServerWorld serverWorld = player.getServerWorld();
                    serverWorld.spawnEntity(entity);
                    serverWorld.emitGameEvent(GameEvent.ENTITY_PLACE, player.getPos(), GameEvent.Emitter.of(player));
                    target.setInvulnerable(true);
                    target.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.VULNERABILITY, 400, playerSoul.getEffectiveLV() / 5));
                    return super.cast(player);
                }
            }
        }
        return false;
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
            SoulForge.getValues(target).removeBool("Immobilized");
            target.setInvulnerable(false);
        }
        return super.end(player);
    }

    public int getLV() { return 17; }

    public int getCost() { return 70; }

    public int getCooldown() { return 900; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new TotalFrostbite();
    }
}

package com.pulsar.soulforge.ability.kindness;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.entity.ImmobilizationEntity;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.event.GameEvent;

public class Immobilization extends ToggleableAbilityBase {
    public final String name = "Immobilization";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "immobilization");
    public final int requiredLv = 7;
    public final int cost = 30;
    public final int cooldown = 1200;

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
                    } else {
                        NbtCompound nbt = new NbtCompound();
                        nbt = target.writeNbt(nbt);
                        nbt.putBoolean("NoAI", false);
                        target.readNbt(nbt);
                    }
                    target.setInvulnerable(false);
                    setActive(false);
                    return getActive();
                }
                entity.remove(Entity.RemovalReason.DISCARDED);
            }
            EntityHitResult result = Utils.getFocussedEntity(player, 10);
            if (result != null) {
                SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                if (result.getEntity() instanceof PlayerEntity targetPlayer) {
                    target = targetPlayer;
                    SoulComponent targetSoul = SoulForge.getPlayerSoul(targetPlayer);
                    targetSoul.addTag("immobile");
                } else if (result.getEntity() instanceof LivingEntity living) {
                    target = living;
                    NbtCompound nbt = new NbtCompound();
                    nbt = living.writeNbt(nbt);
                    nbt.putBoolean("NoAI", true);
                    living.readNbt(nbt);
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
                    setActive(true);
                    return getActive();
                }
            }
        } else {
            entity.remove(Entity.RemovalReason.DISCARDED);
            entity = null;
            setActive(false);
        }
        return getActive();
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
            } else {
                NbtCompound nbt = new NbtCompound();
                nbt = target.writeNbt(nbt);
                nbt.putBoolean("NoAI", false);
                target.readNbt(nbt);
            }
            target.setInvulnerable(false);
        }
        return true;
    }
    
    public String getName() { return name; }

    public Text getLocalizedText() { return Text.translatable("ability."+id.getPath()+".name"); }

    public Identifier getID() { return id; }

    public String getTooltip() { return Text.translatable("ability."+id.getPath()+".tooltip").getString(); }

    public int getLV() { return requiredLv; }

    public int getCost() { return cost; }

    public int getCooldown() { return cooldown; }

    @Override
    public AbilityBase getInstance() {
        return new Immobilization();
    }
}

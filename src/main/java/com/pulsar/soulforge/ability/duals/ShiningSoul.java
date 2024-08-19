package com.pulsar.soulforge.ability.duals;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.components.TemporaryModifierComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;

import java.util.Objects;

public class ShiningSoul extends AbilityBase {
    private int timer = 0;

    private final EntityAttributeModifier damageModifier = new EntityAttributeModifier("shining_soul", 0.35f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
    private final EntityAttributeModifier magicModifier = new EntityAttributeModifier("shining_soul", 0.35f, EntityAttributeModifier.Operation.ADDITION);
    private final EntityAttributeModifier costCooldownModifier = new EntityAttributeModifier("shining_soul", -0.25f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);

    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (playerSoul.getMagic() < 100f) return false;
        if (playerSoul.getStyleRank() < 5) {
            player.sendMessageToClient(Text.translatable(Math.random() < 0.01f ? "soulforge.style.get_real" : "soulforge.style.not_enough"), true);
            return false;
        }
        playerSoul.setMagic(0f);
        timer = 3600;
        player.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.MANA_OVERLOAD, 6000, 0));
        player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).addPersistentModifier(damageModifier);
        player.getAttributeInstance(SoulForgeAttributes.MAGIC_POWER).addPersistentModifier(magicModifier);
        return super.cast(player);
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        timer--;
        for (LivingEntity nearby : player.getWorld().getEntitiesByClass(LivingEntity.class, Box.of(player.getPos(), 24, 24,24), (entity) -> {
            return entity.distanceTo(player) < 12f && TeamUtils.canHealEntity(player.getServer(), player, entity) && entity != player;
        })) {
            TemporaryModifierComponent modifiers = SoulForge.getTemporaryModifiers(nearby);
            if (nearby instanceof PlayerEntity) {
                modifiers.addTemporaryModifier(SoulForgeAttributes.MAGIC_POWER, magicModifier, 2);
                modifiers.addTemporaryModifier(SoulForgeAttributes.MAGIC_COOLDOWN, costCooldownModifier, 2);
                modifiers.addTemporaryModifier(SoulForgeAttributes.MAGIC_COST, costCooldownModifier, 2);
            }
            modifiers.addTemporaryModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, damageModifier, 2);
        }
        ServerWorld serverWorld = player.getServerWorld();
        for (ServerPlayerEntity target : serverWorld.getPlayers()) {
            player.getServerWorld().spawnParticles(target, ParticleTypes.FLAME, false, player.getX(), player.getY(), player.getZ(), 3, 0.5, 1, 0.5, 0.1f);
            player.getServerWorld().spawnParticles(target, ParticleTypes.SNOWFLAKE, false, player.getX(), player.getY(), player.getZ(), 3, 0.5, 1, 0.5, 0.1f);
        }
        return timer <= 0;
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "shining_soul");
        Utils.clearModifiersByName(player, SoulForgeAttributes.MAGIC_POWER, "shining_soul");
        Utils.clearModifiersByName(player, SoulForgeAttributes.MAGIC_COST, "shining_soul");
        Utils.clearModifiersByName(player, SoulForgeAttributes.MAGIC_COOLDOWN, "shining_soul");
        return super.end(player);
    }

    public int getLV() { return 20; }

    public int getCost() { return 100; }

    public int getCooldown() { return 15600; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new ShiningSoul();
    }

    @Override
    public NbtCompound saveNbt(NbtCompound nbt) {
        nbt.putInt("timer", timer);
        return super.saveNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (!Objects.equals(nbt.getString("id"), getID().getPath())) return;
        timer = nbt.getInt("timer");
        super.readNbt(nbt);
    }
}

package com.pulsar.soulforge.ability.bravery;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class ValiantHeart extends AbilityBase {
    private int timer = 0;

    private final EntityAttributeModifier damageModifier = new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, "valiant_heart_damage"), 0.5f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    private final EntityAttributeModifier magicModifier = new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, "valiant_heart_magic"), 0.5f, EntityAttributeModifier.Operation.ADD_VALUE);

    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (playerSoul.getMagic() < 100f) return false;
        playerSoul.setMagic(0f);
        timer = 3600;
        player.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.VALIANT_HEART, 3600, 0));
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)).addPersistentModifier(damageModifier);
        Objects.requireNonNull(player.getAttributeInstance(SoulForgeAttributes.MAGIC_POWER)).addPersistentModifier(magicModifier);
        return true;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        timer--;
        ServerWorld serverWorld = player.getServerWorld();
        for (ServerPlayerEntity target : serverWorld.getPlayers()) {
            player.getServerWorld().spawnParticles(target, ParticleTypes.FLAME, false, player.getX(), player.getY(), player.getZ(), 5, 0.5, 1, 0.5, 0.1f);
        }
        return timer <= 0;
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)).removeModifier(damageModifier.id());
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)).removeModifier(magicModifier.id());
        player.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.MANA_OVERLOAD, 6000, 3));
        return true;
    }

    public String getName() { return "Valiant Heart"; }

    public int getLV() { return 20; }

    public int getCost() { return 100; }

    public int getCooldown() { return 15600; }

    @Override
    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new ValiantHeart();
    }

    @Override
    public NbtCompound saveNbt(NbtCompound nbt) {
        nbt.putInt("timer", timer);
        return super.saveNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (!Objects.equals(nbt.getString("id"), getID().getPath())) return;
        timer = nbt.getInt("timer");
    }
}

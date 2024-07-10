package com.pulsar.soulforge.ability.bravery;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.Objects;

public class ValiantHeart extends AbilityBase {
    private int timer = 0;

    private final EntityAttributeModifier damageModifier = new EntityAttributeModifier("valiant_heart", 0.5f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
    private final EntityAttributeModifier magicModifier = new EntityAttributeModifier("valiant_heart", 0.5f, EntityAttributeModifier.Operation.ADDITION);

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
        player.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.VALIANT_HEART, 3600, 0));
        player.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.MANA_OVERLOAD, 6000, 0));
        player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).addPersistentModifier(damageModifier);
        player.getAttributeInstance(SoulForgeAttributes.MAGIC_POWER).addPersistentModifier(magicModifier);
        return super.cast(player);
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
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "valiant_heart");
        Utils.clearModifiersByName(player, SoulForgeAttributes.MAGIC_POWER, "valiant_heart");
        return super.end(player);
    }

    public int getLV() { return 20; }

    public int getCost() { return 100; }

    public int getCooldown() { return 15600; }

    public AbilityType getType() { return AbilityType.SPECIAL; }

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
        if (!Objects.equals(nbt.getString("id"), getID().getPath())) return;
        timer = nbt.getInt("timer");
        super.readNbt(nbt);
    }
}

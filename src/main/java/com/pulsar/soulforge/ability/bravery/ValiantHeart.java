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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class ValiantHeart extends AbilityBase {
    public final String name = "Valiant Heart";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "valiant_heart");
    public final int requiredLv = 20;
    public final int cost = 100;
    public final int cooldown = 15600;
    public final AbilityType type = AbilityType.CAST;

    private int timer = 0;

    private final EntityAttributeModifier damageModifier = new EntityAttributeModifier("valiant_heart", 0.5f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
    private final EntityAttributeModifier magicModifier = new EntityAttributeModifier("valiant_heart", 0.5f, EntityAttributeModifier.Operation.ADDITION);

    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (playerSoul.getMagic() < 100f) return false;
        playerSoul.setMagic(0f);
        timer = 3600;
        player.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.VALIANT_HEART, 3600, 0));
        player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).addPersistentModifier(damageModifier);
        player.getAttributeInstance(SoulForgeAttributes.MAGIC_POWER).addPersistentModifier(magicModifier);
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
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "valiant_heart");
        Utils.clearModifiersByName(player, SoulForgeAttributes.MAGIC_POWER, "valiant_heart");
        player.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.MANA_OVERLOAD, 6000, 3));
        return true;
    }
    
    public String getName() { return name; }

    public Text getLocalizedText() { return Text.translatable("ability."+id.getPath()+".name"); }

    public Identifier getID() { return id; }

    public String getTooltip() { return Text.translatable("ability."+id.getPath()+".tooltip").getString(); }

    public int getLV() { return requiredLv; }

    public int getCost() { return cost; }

    public int getCooldown() { return cooldown; }

    public AbilityType getType() { return type; }

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
    }
}

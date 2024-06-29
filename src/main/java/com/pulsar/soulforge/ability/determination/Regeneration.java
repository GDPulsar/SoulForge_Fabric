package com.pulsar.soulforge.ability.determination;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.bravery.BraveryBoost;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.sound.SoundCategory;

public class Regeneration extends AbilityBase {
    public final String name = "Regeneration";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "regeneration");
    public final int requiredLv = 7;
    public final int cost = 50;
    public final int cooldown = 500;
    public final AbilityType type = AbilityType.CAST;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        float healAmount = playerSoul.getEffectiveLV();
        if (playerSoul.hasValue("antiheal")) {
            healAmount *= 1f-playerSoul.getValue("antiheal");
        }
        player.setHealth(player.getHealth() + healAmount);
        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoulForgeSounds.UT_HEAL_EVENT, SoundCategory.PLAYERS, 1f, 1f);
        for (int i = 0; i < 12; i++) {
            for (int j = -2; j < 3; j++) {
                double angle = (i/6f)*Math.PI;
                double posX = Math.sin(angle) + player.getX();
                double posY = j*0.1 + player.getY() + 1;
                double posZ = Math.cos(angle) + player.getZ();
                double velX = Math.sin(angle)*4;
                double velY = 0;
                double velZ = Math.cos(angle)*4;
                player.getServer().getWorld(player.getWorld().getRegistryKey()).addParticle(new DustParticleEffect(DustParticleEffect.RED, 1f), posX, posY, posZ, velX, velY, velZ);
            }
        }
        return true;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        return true;
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
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
        return new Regeneration();
    }
}

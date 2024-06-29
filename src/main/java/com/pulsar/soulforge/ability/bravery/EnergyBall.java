package com.pulsar.soulforge.ability.bravery;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.entity.EnergyBallProjectile;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class EnergyBall extends AbilityBase {
    public final String name = "Energy Ball";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "energy_ball");
    public final int requiredLv = 1;
    public final int cost = 20;
    public final int cooldown = 100;
    public final AbilityType type = AbilityType.CAST;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        World world = player.getWorld();
        EnergyBallProjectile projectile = new EnergyBallProjectile(world, player);
        projectile.setPosition(player.getEyePos());
        projectile.setVelocity(player.getRotationVector().multiply(2));
        world.spawnEntity(projectile);
        player.getServerWorld().playSoundFromEntity(null, player, SoulForgeSounds.PELLET_SUMMON_EVENT, SoundCategory.PLAYERS, 1f, 0.5f);
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
        return new EnergyBall();
    }
}

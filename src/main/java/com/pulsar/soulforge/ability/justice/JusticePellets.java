package com.pulsar.soulforge.ability.justice;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.bravery.BraveryBoost;
import com.pulsar.soulforge.entity.JusticePelletProjectile;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class JusticePellets extends AbilityBase {
    public final String name = "Justice Pellets";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "justice_pellets");
    public final int requiredLv = 1;
    public final int cost = 4;
    public final int cooldown = 2;
    public final AbilityType type = AbilityType.CAST;
    

    @Override
    public boolean cast(ServerPlayerEntity player) {
        World world = player.getWorld();
        JusticePelletProjectile projectile = new JusticePelletProjectile(world, player);
        projectile.setPos(player.getEyePos());
        projectile.setVelocity(player.getRotationVector().multiply(8));
        projectile.velocityModified = true;
        world.spawnEntity(projectile);
        world.playSoundFromEntity(null, player, SoulForgeSounds.PELLET_SUMMON_EVENT, SoundCategory.PLAYERS, 1f, 1f);
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
        return new JusticePellets();
    }
}

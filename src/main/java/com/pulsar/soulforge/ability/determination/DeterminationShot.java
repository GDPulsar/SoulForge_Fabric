package com.pulsar.soulforge.ability.determination;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.entity.DeterminationShotProjectile;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class DeterminationShot extends AbilityBase {
    public final String name = "Determination Shot";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "determination_shot");
    public final int requiredLv = 5;
    public final int cost = 10;
    public final int cooldown = 20;
    public final AbilityType type = AbilityType.CAST;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        World world = player.getWorld();
        DeterminationShotProjectile projectile = new DeterminationShotProjectile(world, player);
        projectile.setPosition(player.getEyePos());
        projectile.setVelocity(player.getRotationVector().multiply(4f));
        world.spawnEntity(projectile);
        player.getServerWorld().playSoundFromEntity(null, player, SoulForgeSounds.PELLET_SUMMON_EVENT, SoundCategory.PLAYERS, 1f, 0.75f);
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
        return new DeterminationShot();
    }
}

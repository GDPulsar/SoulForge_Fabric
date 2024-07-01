package com.pulsar.soulforge.ability.bravery;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Shatter extends AbilityBase {
    public boolean cast(ServerPlayerEntity player) {
        ServerWorld serverWorld = player.getServerWorld();
        for (int i = 0; i < 10; i++) {
            Vec3d pos = new Vec3d(6 * MathHelper.sin(i / 5f * MathHelper.PI) + player.getX(), player.getY() + 1f, 6 * MathHelper.cos(i / 5f * MathHelper.PI) + player.getZ());
            player.getWorld().createExplosion(player, pos.x, pos.y, pos.z, 3f, World.ExplosionSourceType.NONE);
        }
        if (serverWorld != null) serverWorld.playSoundFromEntity(null, player, SoulForgeSounds.UT_EXPLOSION_EVENT, SoundCategory.PLAYERS, 4f, 1f);
        return super.cast(player);
    }

    public String getName() { return "Shatter"; }

    public int getLV() { return 17; }

    public int getCost() { return 80; }

    public int getCooldown() { return 900; }

    @Override
    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new Shatter();
    }
}

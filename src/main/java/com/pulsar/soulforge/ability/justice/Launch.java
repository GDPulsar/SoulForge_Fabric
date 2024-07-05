package com.pulsar.soulforge.ability.justice;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class Launch extends AbilityBase {
    private int fallImmunityTime;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        fallImmunityTime = 0;
        playerSoul.addTag("fallImmune");
        player.addVelocity(new Vec3d(0, 2, 0));
        player.velocityModified = true;
        return super.cast(player);
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        fallImmunityTime++;
        return fallImmunityTime >= 140;
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        playerSoul.removeTag("fallImmune");
        return super.end(player);
    }

    public int getLV() { return 15; }

    public int getCost() { return 15; }

    public int getCooldown() { return 200; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new Launch();
    }
}

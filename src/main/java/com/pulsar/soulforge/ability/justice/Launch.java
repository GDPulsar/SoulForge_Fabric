package com.pulsar.soulforge.ability.justice;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class Launch extends AbilityBase {
    public final String name = "Launch";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "launch");
    public final int requiredLv = 15;
    public final int cost = 15;
    public final int cooldown = 200;
    public final AbilityType type = AbilityType.CAST;

    private int fallImmunityTime;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        fallImmunityTime = 0;
        playerSoul.addTag("fallImmune");
        player.addVelocity(new Vec3d(0, 2, 0));
        player.velocityModified = true;
        return true;
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
        return new Launch();
    }
}

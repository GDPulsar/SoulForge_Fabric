package com.pulsar.soulforge.ability.patience;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class FrozenGrasp extends AbilityBase {
    public final String name = "Frozen Grasp";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "frozen_grasp");
    public final int requiredLv = 5;
    public final int cost = 35;
    public final int cooldown = 500;
    public final AbilityType type = AbilityType.CAST;

    public boolean used = false;
    public LivingEntity target;
    public int timer = 0;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        used = false;
        target = null;
        timer = 60;
        return true;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        if (target != null && used) {
            timer--;
        }
        return !used && timer <= 0;
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        used = false;
        timer = 0;
        target = null;
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
        return new FrozenGrasp();
    }
}

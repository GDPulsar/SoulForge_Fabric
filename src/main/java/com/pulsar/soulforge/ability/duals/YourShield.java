package com.pulsar.soulforge.ability.duals;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.ability.kindness.PainSplit;
import com.pulsar.soulforge.components.SoulComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class YourShield extends AbilityBase {
    public final String name = "Your Shield";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "your_shield");
    public final int requiredLv = 15;
    public final int cost = 30;
    public final int cooldown = 200;
    public final AbilityType type = AbilityType.CAST;

    public boolean pullTarget = false;
    public PlayerEntity target;
    private int fallImmunityTime = 0;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        for (AbilityBase ability : playerSoul.getActiveAbilities()) {
            if (ability instanceof PainSplit painSplit) {
                if (painSplit.target != null) {
                    if (player.isSneaking()) {
                        pullTarget = true;
                        target = painSplit.target;
                        target.setVelocity(player.getPos().subtract(target.getPos()).normalize().multiply(2.5f));
                        target.velocityModified = true;
                        SoulComponent targetSoul = SoulForge.getPlayerSoul(target);
                        targetSoul.addTag("fallImmune");
                        fallImmunityTime = 0;
                    } else {
                        player.setVelocity(target.getPos().subtract(player.getPos()).normalize().multiply(2.5f));
                        player.velocityModified = true;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        if (pullTarget)  {
            fallImmunityTime++;
            return fallImmunityTime >= 80;
        }
        return true;
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        if (pullTarget) {
            SoulComponent targetSoul = SoulForge.getPlayerSoul(target);
            targetSoul.removeTag("fallImmune");
        }
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
        return new YourShield();
    }
}

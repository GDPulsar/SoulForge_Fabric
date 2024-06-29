package com.pulsar.soulforge.ability.kindness;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.bravery.BraveryBoost;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;

public class PainSplit extends AbilityBase {
    public final String name = "Pain Split";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "pain_split");
    public final int requiredLv = 15;
    public final int cost = 40;
    public final int cooldown = 200;
    public final AbilityType type = AbilityType.TOGGLE;

    public PlayerEntity target = null;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        if (target == null) {
            EntityHitResult hit = Utils.getFocussedEntity(player, 5);
            if (hit != null) {
                Entity entity = hit.getEntity();
                if (entity instanceof PlayerEntity targetPlayer) {
                    if (!TeamUtils.canHealPlayer(player.getServer(), player, targetPlayer)) return false;
                    target = targetPlayer;
                    player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoulForgeSounds.UT_HEAL_EVENT, SoundCategory.PLAYERS, 1f, 1f);
                    return true;
                }
            }
        } else {
            target = null;
        }
        return false;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        if (target != null) {
            if (target.distanceTo(player) >= 300f) target = null;
        }
        return target == null;
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
        return new PainSplit();
    }
}

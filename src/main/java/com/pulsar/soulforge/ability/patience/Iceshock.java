package com.pulsar.soulforge.ability.patience;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.bravery.BraveryBoost;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;

public class Iceshock extends AbilityBase {
    public final String name = "Iceshock";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "iceshock");
    public final int requiredLv = 1;
    public final int cost = 20;
    public final int cooldown = 200;
    public final AbilityType type = AbilityType.CAST;
    

    @Override
    public boolean cast(ServerPlayerEntity player) {
        EntityHitResult hit = Utils.getFocussedEntity(player, 10);
        if (hit != null) {
            if (hit.getEntity() instanceof LivingEntity target) {
                if (target instanceof PlayerEntity targetPlayer) {
                    if (!TeamUtils.canDamagePlayer(player.getServer(), player, targetPlayer)) return false;
                }
                player.getServerWorld().spawnParticles(ParticleTypes.SNOWFLAKE, target.getX(), target.getY(), target.getZ(), 20, 0.5, 1, 0.5, 0.1);
                player.getServerWorld().playSoundFromEntity(null, player, SoulForgeSounds.DR_ICESHOCK_EVENT, SoundCategory.PLAYERS, 1f, 1f);
                target.damage(SoulForgeDamageTypes.of(player.getWorld(), SoulForgeDamageTypes.ABILITY_PIERCE_DAMAGE_TYPE), 3f);
                return true;
            }
        }
        return false;
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
        return new Iceshock();
    }
}

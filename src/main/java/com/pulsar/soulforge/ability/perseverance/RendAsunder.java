package com.pulsar.soulforge.ability.perseverance;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.bravery.BraveryBoost;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RendAsunder extends AbilityBase {
    public final String name = "Rend Asunder";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "rend_asunder");
    public final int requiredLv = 3;
    public final int cost = 30;
    public final int cooldown = 200;
    public final AbilityType type = AbilityType.CAST;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        StatusEffectInstance effect = new StatusEffectInstance(SoulForgeEffects.VULNERABILITY, playerSoul.getEffectiveLV()*10, playerSoul.isPure() ? 1 : 0);
        DamageSource damageSource = SoulForgeDamageTypes.of(player.getWorld(), SoulForgeDamageTypes.ABILITY_DAMAGE_TYPE);
        for (LivingEntity target : Utils.getEntitiesInFrontOf(player, 1.5f, 2f, 1f, 2f)) {
            if (target instanceof PlayerEntity targetPlayer) {
                if (!TeamUtils.canDamagePlayer(player.getServer(), player, targetPlayer)) continue;
            }
            target.damage(damageSource, 0.5f*playerSoul.getEffectiveLV());
            target.addStatusEffect(effect, player);
            if (target instanceof PlayerEntity targetPlayer) {
                if (!TeamUtils.canDamagePlayer(player.getServer(), player, targetPlayer)) continue;
                SoulComponent targetSoul = SoulForge.getPlayerSoul(targetPlayer);
                Utils.addAntiheal(0.4f, playerSoul.getEffectiveLV()*20, targetSoul);
            }
        }
        for (int i = 0; i < 3; i ++) {
            ServerWorld serverWorld = ((ServerPlayerEntity) player).getServerWorld();
            Vec3d particlePos = new Vec3d(player.getRotationVector().x, 0.5f, player.getRotationVector().z).add(player.getPos());
            serverWorld.spawnParticles(ParticleTypes.SWEEP_ATTACK, particlePos.x, particlePos.y+i/2f, particlePos.z, 1, 0, 0, 0, 0);
        }
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
        return new RendAsunder();
    }
}

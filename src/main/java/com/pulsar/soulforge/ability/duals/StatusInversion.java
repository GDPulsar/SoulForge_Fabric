package com.pulsar.soulforge.ability.duals;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.components.TemporaryModifierComponent;
import com.pulsar.soulforge.util.Constants;
import com.pulsar.soulforge.util.Triplet;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;
import java.util.Set;

public class StatusInversion extends AbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        EntityHitResult hit = Utils.getFocussedEntity(player, 32f);
        if (hit != null) {
            if (hit.getEntity() instanceof LivingEntity target) {
                Collection<StatusEffectInstance> effects = target.getStatusEffects();
                TemporaryModifierComponent modifiers = SoulForge.getTemporaryModifiers(target);
                SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                playerSoul.setStyle(playerSoul.getStyle() + 4 * (effects.size() + modifiers.getModifierCount()));
                Constants.invertStatusEffects(target, 1f, 0.6f);
                for (Triplet<EntityAttributeModifier, EntityAttribute, Float> modifier : Set.copyOf(modifiers.getModifiers())) {
                    modifiers.removeTemporaryModifier(modifier.getSecond(), modifier.getFirst());
                    modifiers.addTemporaryModifier(modifier.getSecond(), new EntityAttributeModifier(
                            modifier.getFirst().getId(), modifier.getFirst().getName(), -modifier.getFirst().getValue(), modifier.getFirst().getOperation()
                    ), modifier.getThird());
                }
                Vec3d centerPos = target.getPos().add(0, 1, 0);
                for (int i = 0; i < 16; i++) {
                    Vec3d particlePos = new Vec3d(Math.sin(i*Math.PI/8), 0f, Math.cos(i*Math.PI/8));
                    player.getServerWorld().spawnParticles(player, new DustParticleEffect(Vec3d.unpackRgb(0x00FFFF).toVector3f(), 1f), true, particlePos.x + centerPos.x, centerPos.y + 0.3f, particlePos.z + centerPos.z, 1, 0, 0, 0, 0);
                    player.getServerWorld().spawnParticles(player, new DustParticleEffect(Vec3d.unpackRgb(0x00FF0).toVector3f(), 1f), true, particlePos.x + centerPos.x, centerPos.y, particlePos.z + centerPos.z, 1, 0, 0, 0, 0);
                    player.getServerWorld().spawnParticles(player, new DustParticleEffect(Vec3d.unpackRgb(0x00FFFF).toVector3f(), 1f), true, particlePos.x + centerPos.x, centerPos.y - 0.3f, particlePos.z + centerPos.z, 1, 0, 0, 0, 0);
                }
                return super.cast(player);
            }
        }
        return false;
    }

    public int getLV() { return 12; }

    public int getCost() { return 40; }

    public int getCooldown() { return 200; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new StatusInversion();
    }
}

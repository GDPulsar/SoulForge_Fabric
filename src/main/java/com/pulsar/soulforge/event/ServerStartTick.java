package com.pulsar.soulforge.event;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.patience.Snowglobe;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.entity.ShieldShardEntity;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.networking.SoulForgeNetworking;
import com.pulsar.soulforge.trait.Traits;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ServerStartTick implements ServerTickEvents.StartTick {
    private final HashMap<ServerPlayerEntity, Boolean> wasSneaking = new HashMap<>();
    private final HashMap<ServerPlayerEntity, Boolean> hadCreativeFlight = new HashMap<>();

    @Override
    public void onStartTick(MinecraftServer server) {
        long startTickTimer = System.currentTimeMillis();
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            /* frost floor no longer exists but i'm still keeping this code just in case i need to slip and slide at home
            float maxSlip = -1;
            for (ServerPlayerEntity target : server.getPlayerManager().getPlayerList()) {
                if (player.distanceTo(target) > 0.001f) {
                    SoulComponent targetSoul = SoulForge.getPlayerSoul(target);
                    for (AbilityBase ability : targetSoul.getActiveAbilities()) {
                        if (ability instanceof Snowglobe frostFloor) {
                            if (player.getPos().distanceTo(frostFloor.origin) < 15 + targetSoul.getLV()) {
                                float slip = 0.6f + 0.02f * targetSoul.getLV();
                                if (slip > maxSlip) maxSlip = slip;
                            }
                        }
                    }
                }
            }
            if (maxSlip != -1) {
                playerSoul.setValue("slip", maxSlip);
            } else {
                playerSoul.setValue("slip", 0);
            }*/

            // limit break
            Utils.clearModifiersByUUID(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, UUID.fromString("5d1370cc-507b-410a-a94a-b65e5f48f012"));
            if (playerSoul.hasAbility("Limit Break")) {
                float value = 0.5f * (1f - (player.getHealth() / player.getMaxHealth()));
                player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).addPersistentModifier(new EntityAttributeModifier(UUID.fromString("5d1370cc-507b-410a-a94a-b65e5f48f012"), "limit_break", value, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
            }

            boolean globing = playerSoul.hasCast("Snowglobe");
            if (!globing) Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "snowglobe");
            for (ServerPlayerEntity other : server.getPlayerManager().getPlayerList()) {
                if (player.distanceTo(other) > 4f) continue;
                if (!TeamUtils.canDamagePlayer(server, other, player)) continue;
                SoulComponent otherSoul = SoulForge.getPlayerSoul(other);
                for (AbilityBase ability : otherSoul.getActiveAbilities()) {
                    if (ability instanceof Snowglobe snowglobe) {
                        if (snowglobe.getActive()) {
                            player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).addPersistentModifier(new EntityAttributeModifier("snowglobe", -(otherSoul.getEffectiveLV() * 0.03f), EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
                        }
                    }
                }
            }

            // other
            if (playerSoul.hasTag("preventMove") || playerSoul.hasTag("immobile")) {
                player.setVelocity(0, 0, 0);
                player.velocityModified = true;
            }

            // magic tick + sync
            playerSoul.magicTick();
            PacketByteBuf soulBuf = playerSoul.toBuffer();
            if (soulBuf != null) {
                ServerPlayNetworking.send(player, SoulForgeNetworking.PLAYER_SOUL, soulBuf);
            }

            //platforms
            if (player.isSneaking() && wasSneaking.containsKey(player)) {
                if (!wasSneaking.get(player)) {
                    if (playerSoul.hasCast("Determination Platform") || playerSoul.hasCast("Platforms")) {
                        playerSoul.handleEvent(EventType.SPAWN_PLATFORM);
                    } else {
                        ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);
                        if (boots.isOf(SoulForgeItems.PLATFORM_BOOTS)) {
                            playerSoul.handleEvent(EventType.SPAWN_PLATFORM);
                        }
                    }
                }
            }
            wasSneaking.put(player, player.isSneaking());

            ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);
            if (boots.isOf(SoulForgeItems.PLATFORM_BOOTS)) {
                StatusEffectInstance statusEffectInstance = player.getStatusEffect(StatusEffects.JUMP_BOOST);
                float f = statusEffectInstance == null ? 0.0F : (float) (statusEffectInstance.getAmplifier() + 1);
                float damage = MathHelper.ceil((player.fallDistance - 3.0F - f));
                if (damage >= player.getHealth()) {
                    playerSoul.handleEvent(EventType.SPAWN_PLATFORM);
                }
            }

            if (playerSoul.getTraits().contains(Traits.kindness) && playerSoul.getTraits().contains(Traits.integrity)) {
                List<ShieldShardEntity> circlingShards = player.getEntityWorld().getEntitiesByClass(ShieldShardEntity.class, Box.of(player.getPos(), 15, 15, 15), (entity) -> entity.owner == player && entity.isCircling && !entity.isLaunched);
                for (ShieldShardEntity shard : player.getEntityWorld().getEntitiesByClass(ShieldShardEntity.class, Box.of(player.getPos(), 15, 15, 15), (entity) -> entity.owner == player && !entity.isLaunched)) {
                    if (circlingShards.size() >= 15) break;
                    if (shard.distanceTo(player) <= 10f) {
                        circlingShards.add(shard);
                        shard.isCircling = true;
                    }
                }
                float angleStep = (float)(2*Math.PI/circlingShards.size());
                for (int i = 0; i < circlingShards.size(); i++) {
                    Vec3d offset = new Vec3d(Math.sin(i*angleStep + player.getServer().getTicks()/10f), 1.2f, Math.cos(i*angleStep + player.getServer().getTicks()/10f));
                    circlingShards.get(i).setPos(player.getPos().add(offset));
                    circlingShards.get(i).setVelocity(Vec3d.ZERO);
                }
            }

            if (!player.isCreative() && !player.isSpectator()) {
                if (!hadCreativeFlight.containsKey(player)) hadCreativeFlight.put(player, false);
                if (player.hasStatusEffect(SoulForgeEffects.CREATIVE_ZONE)) {
                    player.getAbilities().allowFlying = true;
                    player.sendAbilitiesUpdate();
                    if (!hadCreativeFlight.get(player)) hadCreativeFlight.put(player, true);
                } else if (!player.hasStatusEffect(SoulForgeEffects.CREATIVE_ZONE) && hadCreativeFlight.get(player)) {
                    player.getAbilities().allowFlying = false;
                    player.getAbilities().flying = false;
                    player.sendAbilitiesUpdate();
                    hadCreativeFlight.put(player, false);
                }
            }
        }
        long tickDuration = System.currentTimeMillis() - startTickTimer;
        if (tickDuration >= 20) SoulForge.LOGGER.warn("AHHH SHIT ASS: {}", tickDuration);
    }
}

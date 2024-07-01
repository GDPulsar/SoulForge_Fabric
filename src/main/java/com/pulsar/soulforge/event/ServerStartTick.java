package com.pulsar.soulforge.event;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.client.networking.PlayerSoulPacket;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.entity.ShieldShardEntity;
import com.pulsar.soulforge.item.SoulForgeItems;
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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.List;

public class ServerStartTick implements ServerTickEvents.StartTick {
    private final HashMap<ServerPlayerEntity, Boolean> wasSneaking = new HashMap<>();
    private final HashMap<ServerPlayerEntity, Boolean> hadCreativeFlight = new HashMap<>();

    @Override
    public void onStartTick(MinecraftServer server) {
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
            Utils.removeModifier(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "limit_break");
            Utils.removeModifier(player, SoulForgeAttributes.MAGIC_POWER, "limit_break");
            if (playerSoul.hasAbility("Limit Break")) {
                float value = 0.5f * (1f - (player.getHealth() / player.getMaxHealth()));
                Utils.addModifier(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "limit_break", value, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                Utils.addModifier(player, SoulForgeAttributes.MAGIC_POWER, "limit_break", value, EntityAttributeModifier.Operation.ADD_VALUE);
            }

            boolean globing = playerSoul.hasCast("Snowglobe");
            if (!globing) Utils.removeModifier(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "snowglobe");
            for (ServerPlayerEntity other : server.getPlayerManager().getPlayerList()) {
                if (player == other) continue;
                if (player.distanceTo(other) > 4f) continue;
                if (!TeamUtils.canDamagePlayer(server, other, player)) continue;
                SoulComponent otherSoul = SoulForge.getPlayerSoul(other);
                if (otherSoul.hasCast("Snowglobe")) {
                    Utils.addModifier(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "snowglobe", -(otherSoul.getEffectiveLV() * 0.03f), EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                }
            }

            // other
            if (playerSoul.hasTag("preventMove") || playerSoul.hasTag("immobile")) {
                player.setVelocity(0, 0, 0);
                player.velocityModified = true;
            }

            // magic tick + sync
            playerSoul.magicTick();
            ServerPlayNetworking.send(player, new PlayerSoulPacket(playerSoul.getEXP(), playerSoul.getMagic(),
                    playerSoul.getAbilityLayout().toNbt(), playerSoul.getAbilityRow(), playerSoul.magicModeActive()));

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

            if (playerSoul.magicModeActive()) {
                AbilityBase currentAbility = playerSoul.getAbilityLayout().getSlot(playerSoul.getAbilityRow(), playerSoul.getAbilitySlot());
                if (currentAbility != null) {
                    currentAbility.displayTick(player);
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
    }
}

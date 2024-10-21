package com.pulsar.soulforge;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.advancement.SoulForgeCriterions;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.block.SoulForgeBlocks;
import com.pulsar.soulforge.client.ui.CreativeZoneScreenHandler;
import com.pulsar.soulforge.client.ui.SoulForgeScreenHandler;
import com.pulsar.soulforge.command.*;
import com.pulsar.soulforge.components.*;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.entity.SoulForgeEntities;
import com.pulsar.soulforge.event.ServerEndTick;
import com.pulsar.soulforge.event.ServerStartTick;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.networking.SoulForgeNetworking;
import com.pulsar.soulforge.particle.SoulForgeParticles;
import com.pulsar.soulforge.recipe.SoulForgeRecipes;
import com.pulsar.soulforge.registries.AbilityReloadListener;
import com.pulsar.soulforge.registries.TraitReloadListener;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.Constants;
import com.pulsar.soulforge.util.SoulForgeCustomTrades;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.resource.ResourceType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoulForge implements ModInitializer {
	public static final String MOD_ID = "soulforge";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static ScreenHandlerType<SoulForgeScreenHandler> SOUL_FORGE_SCREEN_HANDLER;
	public static ScreenHandlerType<CreativeZoneScreenHandler> CREATIVE_ZONE_SCREEN_HANDLER;

	@Override
	public void onInitialize() {
		LOGGER.info("Loading SoulForge v2.6.4");

		//registerResourceListeners();

		SoulForgeBlocks.registerBlocks();
		SoulForgeItems.registerItems();
		SoulForgeEffects.registerEffects();
		SoulForgeSounds.registerSounds();
		SoulForgeEntities.register();
		SoulForgeCriterions.registerCriterions();
		SoulForgeDamageTypes.register();
		SoulForgeRecipes.register();
		SoulForgeAttributes.register();
		SoulForgeCustomTrades.register();
		SoulForgeParticles.serverRegister();

		TeamUtils.checkForTeams();

		SOUL_FORGE_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(MOD_ID, "soul_forge_screen"), SoulForgeScreenHandler::new);
		CREATIVE_ZONE_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(new Identifier(MOD_ID, "creative_zone_screen"), CreativeZoneScreenHandler::new);

		ArgumentTypeRegistry.registerArgumentType(
				new Identifier(MOD_ID, "trait"),
				SoulForgeCommand.TraitArgumentType.class, ConstantArgumentSerializer.of(SoulForgeCommand.TraitArgumentType::trait));
		CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
			SoulForgeCommand.register(dispatcher);
			GhostBlockCommand.register(dispatcher, registryAccess);
			WormholeCommand.register(dispatcher, registryAccess);
			AntihealCommand.register(dispatcher);
			DisguiseCommand.register(dispatcher);
			MinionCommand.register(dispatcher, registryAccess);
			HateCommand.register(dispatcher);
			//TickCommand.register(dispatcher);
		}));

		SoulForgeNetworking.registerPackets();

		ServerTickEvents.START_SERVER_TICK.register(new ServerStartTick());
		ServerTickEvents.END_SERVER_TICK.register(new ServerEndTick());
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			SoulComponent playerSoul = getPlayerSoul(handler.getPlayer());
			if (playerSoul != null) {
				for (AbilityBase active : playerSoul.getActiveAbilities()) {
					if (Utils.abilityInstanceInList(active, Constants.endsOnDisconnect)) {
						active.end(handler.getPlayer());
					}
				}
			}
		});

		LootTableEvents.MODIFY.register(((resourceManager, lootManager, id, tableBuilder, source) -> {
			if (source.isBuiltin()) {
				for (EntityType<?> entity : Constants.essenceDrops.keySet()) {
					if (entity.getLootTableId().equals(id)) {
						LootPool.Builder poolBuilder = LootPool.builder()
								.with(ItemEntry.builder(Constants.essenceDrops.get(entity)))
								.conditionally(RandomChanceLootCondition.builder(0.1f));
						tableBuilder.pool(poolBuilder);
					}
				}
			}
		}));
	}

	public static void registerResourceListeners() {
		SoulForge.LOGGER.info("Registering resource listeners");

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(AbilityReloadListener.INSTANCE);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(TraitReloadListener.INSTANCE);
	}

	public static SoulComponent getPlayerSoul(PlayerEntity player) {
		SoulComponent playerSoul = EntityInitializer.SOUL.maybeGet(player).orElse(new SoulComponent(player));
		if (player.getWorld().isClient) {
			if (player == MinecraftClient.getInstance().player) playerSoul = SoulForgeClient.getPlayerData();
		}
		return playerSoul;
	}

	public static WorldComponent getWorldComponent(World world) {
		return WorldInitializer.WORLD_CONFIG.get(world);
	}

	public static ValueComponent getValues(LivingEntity living) {
		try {
			return EntityInitializer.VALUES.get(living);
		} catch (NullPointerException ignored) {
			return null;
		}
	}

	public static TemporaryModifierComponent getTemporaryModifiers(LivingEntity living) {
		try {
			return EntityInitializer.TEMPORARY_MODIFIERS.get(living);
		} catch (NullPointerException ignored) {
			return null;
		}
	}

	public static HateComponent getHate(LivingEntity living) {
		try {
			return EntityInitializer.HATE.get(living);
		} catch (NullPointerException ignored) {
			return null;
		}
	}
}
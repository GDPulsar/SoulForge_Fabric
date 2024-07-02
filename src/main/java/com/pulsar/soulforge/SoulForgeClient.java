package com.pulsar.soulforge;

import com.mojang.datafixers.util.Pair;
import com.pulsar.soulforge.ability.perseverance.ColossalClaymore;
import com.pulsar.soulforge.block.SoulForgeBlocks;
import com.pulsar.soulforge.client.block.CreativeZoneBlockRenderer;
import com.pulsar.soulforge.client.block.SoulJarEntityRenderer;
import com.pulsar.soulforge.client.entity.*;
import com.pulsar.soulforge.client.event.ClickEvent;
import com.pulsar.soulforge.client.event.ClientEndTick;
import com.pulsar.soulforge.client.event.ClientStartTick;
import com.pulsar.soulforge.client.event.KeyInputHandler;
import com.pulsar.soulforge.client.networking.ClientNetworkingHandler;
import com.pulsar.soulforge.client.render.SoulForgeRendering;
import com.pulsar.soulforge.client.ui.CreativeZoneScreen;
import com.pulsar.soulforge.client.ui.MagicHudOverlay;
import com.pulsar.soulforge.client.ui.SoulForgeScreen;
import com.pulsar.soulforge.client.ui.SoulResetOverlay;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.entity.SoulForgeEntities;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.item.weapons.JusticeCrossbow;
import com.pulsar.soulforge.item.weapons.weapon_wheel.DeterminationCrossbow;
import com.pulsar.soulforge.siphon.Siphon;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import me.x150.renderer.event.RenderEvents;
import me.x150.renderer.render.Renderer3d;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.LightningEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import squeek.appleskin.client.HUDOverlayHandler;

import java.awt.*;

public class SoulForgeClient implements ClientModInitializer {
	public static EntityModelLayer MODEL_FROZEN_ENERGY_LAYER = new EntityModelLayer(new Identifier(SoulForge.MOD_ID, "frozen_energy"), "Frozen Energy");

	private DisplaySlot survivalWeaponSlot;

	public static boolean appleSkin = false;
	public static boolean appleSkinApplied = false;

	public static void appleSkinLoad() {
		if (HUDOverlayHandler.INSTANCE != null) {
			HUDOverlayHandler.INSTANCE.FOOD_BAR_HEIGHT += 22;
			SoulForgeClient.appleSkinApplied = true;
		}
	}

	@Override
	public void onInitializeClient() {
		if (FabricLoader.getInstance().isModLoaded("appleskin")) {
			appleSkin = true;
			appleSkinLoad();
		}

		KeyInputHandler.register();
		KeyInputHandler.registerKeyInputs();
		if (MinecraftClient.getInstance().getSession().getUsername().equals("GDPulsar")) {
			KeyInputHandler.registerThePulsarFunnyThings();
		}

		EntityModelLayerRegistry.registerModelLayer(MODEL_FROZEN_ENERGY_LAYER, FrozenEnergyModel::getTexturedModelData);

		HandledScreens.register(SoulForge.SOUL_FORGE_SCREEN_HANDLER, SoulForgeScreen::new);
		HandledScreens.register(SoulForge.CREATIVE_ZONE_SCREEN_HANDLER, CreativeZoneScreen::new);

		BlockEntityRendererFactories.register(SoulForgeBlocks.CREATIVE_ZONE_ENTITY, CreativeZoneBlockRenderer::new);

		BlockEntityRendererRegistry.register(SoulForgeBlocks.SOUL_JAR_BLOCK_ENTITY, SoulJarEntityRenderer::new);

		BlockRenderLayerMap.INSTANCE.putBlock(SoulForgeBlocks.SOUL_FORGE_BLOCK, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SoulForgeBlocks.CREATIVE_ZONE, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SoulForgeBlocks.DOME_BLOCK, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SoulForgeBlocks.DETERMINATION_DOME_BLOCK, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SoulForgeBlocks.SOUL_JAR, RenderLayer.getTranslucent());

		EntityRendererRegistry.register(SoulForgeEntities.BRAVERY_SPEAR_ENTITY_TYPE, BraverySpearRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.DETERMINATION_SPEAR_ENTITY_TYPE, DeterminationSpearRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.SOJ_ENTITY_TYPE, SOJRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.JUSTICE_ARROW_ENTITY_TYPE, JusticeArrowRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.DETERMINATION_ARROW_ENTITY_TYPE, DeterminationArrowRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.ENERGY_BALL_ENTITY_TYPE, EnergyBallRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.JUSTICE_PELLET_ENTITY_TYPE, JusticePelletRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.DOME_ENTITY_TYPE, DomeEntityRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.DOME_PART_TYPE, DomePartRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.TURRET_ENTITY_TYPE, AutoTurretRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.SMALL_SLASH_ENTITY_TYPE, SmallSlashRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.BIG_SLASH_ENTITY_TYPE, BigSlashRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.HORIZONTAL_BLAST_ENTITY_TYPE, BlastRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.GUNLANCE_BLAST_ENTITY_TYPE, GunlanceBlastRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.DARK_FOUNTAIN_ENTITY_TYPE, DarkFountainEntityRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.SPECIAL_HELL_ENTITY_TYPE, SpecialHellEntityRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.ORBITAL_STRIKE_ENTITY_TYPE, OrbitalStrikeEntityRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.IMMOBILIZATION_ENTITY_TYPE, ImmobilizationEntityRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.FRAGMENTATION_GRENADE_ENTITY_TYPE, FragmentationGrenadeRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.FROZEN_ENERGY_ENTITY_TYPE, FrozenEnergyRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.HAIL_ENTITY_TYPE, HailRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.WEATHER_WARNING_LIGHTNING_ENTITY_TYPE, LightningEntityRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.PV_HARPOON_ENTITY_TYPE, PVHarpoonRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.DT_HARPOON_ENTITY_TYPE, DTHarpoonRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.DETERMINATION_PLATFORM_ENTITY_TYPE, DeterminationPlatformEntityRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.INTEGRITY_PLATFORM_ENTITY_TYPE, IntegrityPlatformEntityRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.SNOWGRAVE_PROJECTILE_TYPE, SnowgraveRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.DOME_EMITTER_ENTITY_TYPE, DomeEmitterRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.INCENDIARY_GRENADE_ENTITY_TYPE, IncendiaryGrenadeRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.LIGHTNING_ROD_ENTITY_TYPE, LightningRodRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.DETONATOR_MINE_ENTITY_TYPE, DetonatorMineRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.POLARITY_BALL_ENTITY_TYPE, PolaritiesBallRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.JUSTICE_ARROW_TRINKET_TYPE, JusticeArrowTrinketRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.ANTIHEAL_DART_ENTITY_TYPE, AntihealDartRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.SHIELD_SHARD_ENTITY_TYPE, ShieldShardRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.BOUNCING_SHIELD_ENTITY_TYPE, BouncingShieldRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.GRAPPLE_HOOK_ENTITY_TYPE, GrappleHookRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.FIRE_TORNADO_ENTITY_TYPE, FireTornadoRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.JUSTICE_HARPOON_ENTITY_TYPE, JusticeHarpoonRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.WORMHOLE_ENTITY_TYPE, WormholeEntityRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.SWORD_SLASH_ENTITY_TYPE, SwordSlashRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.YOYO_ENTITY_TYPE, YoyoRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.RAILKILLER_ENTITY_TYPE, RailkillerRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.PLAYER_SOUL_ENTITY_TYPE, PlayerSoulRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.DETERMINATION_SHOT_ENTITY_TYPE, DeterminationShotRenderer::new);

		HudRenderCallback.EVENT.register(new MagicHudOverlay());
		HudRenderCallback.EVENT.register(new SoulResetOverlay());

		ModelPredicateProviderRegistry.register(SoulForgeItems.INTEGRITY_RAPIER, new Identifier("parrying"), (stack, world, entity, i) -> {
			if (entity instanceof PlayerEntity player) {
				SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
				if (playerSoul.hasValue("parry")) {
					if (playerSoul.getValue("parry") > 0) return 1f;
				}
			}
			return 0f;
		});
		ModelPredicateProviderRegistry.register(SoulForgeItems.DETERMINATION_RAPIER, new Identifier("parrying"), (stack, world, entity, i) -> {
			if (entity instanceof PlayerEntity player) {
				SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
				if (playerSoul.hasValue("parry")) {
					if (playerSoul.getValue("parry") > 0) return 1f;
				}
			}
			return 0f;
		});
		ModelPredicateProviderRegistry.register(SoulForgeItems.DETERMINATION_SWORD, new Identifier("blocking"), (stack, world, entity, i) ->
				entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1f : 0f);
		ModelPredicateProviderRegistry.register(SoulForgeItems.KINDNESS_SHIELD, new Identifier("blocking"), (stack, world, entity, i) ->
				entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1f : 0f);
		ModelPredicateProviderRegistry.register(SoulForgeItems.JUSTICE_BOW, new Identifier("pulling"), ((stack, world, entity, seed) -> {
			if (entity == null) return 0f;
			return entity.isUsingItem() && entity.getActiveItem() == stack ? 1f : 0f;
		}));
		ModelPredicateProviderRegistry.register(SoulForgeItems.JUSTICE_BOW, new Identifier("pull"), ((stack, world, entity, seed) -> {
			if (entity == null) return 0f;
			return entity.getActiveItem() != stack ? 0f : (stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 10f;
		}));
		ModelPredicateProviderRegistry.register(SoulForgeItems.DETERMINATION_BOW, new Identifier("pulling"), ((stack, world, entity, seed) -> {
			if (entity == null) return 0f;
			return entity.isUsingItem() && entity.getActiveItem() == stack ? 1f : 0f;
		}));
		ModelPredicateProviderRegistry.register(SoulForgeItems.DETERMINATION_BOW, new Identifier("pull"), ((stack, world, entity, seed) -> {
			if (entity == null) return 0f;
			return entity.getActiveItem() != stack ? 0f : (stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 10f;
		}));
		ModelPredicateProviderRegistry.register(SoulForgeItems.JUSTICE_CROSSBOW, new Identifier("pulling"), ((stack, world, entity, seed) -> {
			if (entity == null) return 0f;
			return entity.isUsingItem() && entity.getActiveItem() == stack ? 1f : 0f;
		}));
		ModelPredicateProviderRegistry.register(SoulForgeItems.JUSTICE_CROSSBOW, new Identifier("pull"), ((stack, world, entity, seed) -> {
			if (entity == null) return 0f;
			return entity.getActiveItem() != stack ? 0f : (stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 10f;
		}));
		ModelPredicateProviderRegistry.register(SoulForgeItems.JUSTICE_CROSSBOW, new Identifier("loaded"), ((stack, world, entity, seed) -> {
			if (entity == null) return 0f;
			return ((JusticeCrossbow)stack.getItem()).loaded ? 1f : 0f;
		}));
		ModelPredicateProviderRegistry.register(SoulForgeItems.DETERMINATION_CROSSBOW, new Identifier("pulling"), ((stack, world, entity, seed) -> {
			if (entity == null) return 0f;
			return entity.isUsingItem() && entity.getActiveItem() == stack ? 1f : 0f;
		}));
		ModelPredicateProviderRegistry.register(SoulForgeItems.DETERMINATION_CROSSBOW, new Identifier("pull"), ((stack, world, entity, seed) -> {
			if (entity == null) return 0f;
			return entity.getActiveItem() != stack ? 0f : (stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 10f;
		}));
		ModelPredicateProviderRegistry.register(SoulForgeItems.DETERMINATION_CROSSBOW, new Identifier("loaded"), ((stack, world, entity, seed) -> {
			if (entity == null) return 0f;
			return ((DeterminationCrossbow)stack.getItem()).loaded ? 1f : 0f;
		}));

		ClampedModelPredicateProvider siphonProvider = (stack, world, entity, seed) -> {
			if (stack.isIn(ItemTags.TRIMMABLE_ARMOR) || stack.isOf(Items.BOW) || stack.isOf(Items.CROSSBOW) || stack.isIn(ItemTags.TOOLS) || stack.isOf(Items.TRIDENT)) {
				if (stack.getNbt() != null) {
					if (world == null || !stack.getNbt().contains("Siphon")) return 0.0F;
					String siphonStr = stack.getNbt().getString("Siphon");
					Siphon.Type siphonType = Siphon.Type.getSiphon(siphonStr);
					if (siphonType == null) return 0.0F;
					return siphonType.getIndex();
				}
			}
			return Float.NEGATIVE_INFINITY;
		};
		ModelPredicateProviderRegistry.register(new Identifier("siphon_type"), siphonProvider);

		ClientNetworkingHandler.registerPackets();

		ClientRawInputEvent.MOUSE_CLICKED_PRE.register(new ClickEvent());

		ClientTickEvents.START_CLIENT_TICK.register(new ClientStartTick());
		ClientTickEvents.END_CLIENT_TICK.register(new ClientEndTick());

		RenderEvents.WORLD.register(stack -> {
			PlayerEntity player = MinecraftClient.getInstance().player;
			if (player != null) {
				SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
				if (playerSoul.hasCast("Colossal Claymore")) {
					if (((ColossalClaymore)playerSoul.getAbility("Colossal Claymore")).greaterSlash) {
						Vec3d position = new Vec3d(-4, -0.05, 1);
						stack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(player.getYaw()));
						//stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(player.getPitch()));
						stack.translate(player.getX() + position.x, player.getEyeY() + position.y, player.getZ() + position.z);
						Renderer3d.renderFilled(stack, new Color(128, 0, 255, 128), Vec3d.ZERO, new Vec3d(8, 0.1, 6));
					}
				}
			}
		});

		SoulForgeRendering.initializeShaders();
	}

	private static void drawInFrontOfPlayer(MatrixStack stack, Vec3d position, Vec3d size, Color color) {
		PlayerEntity player = MinecraftClient.getInstance().player;
		if (player != null) {
			stack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(player.getYaw()));
			stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(player.getPitch()));
			stack.translate(player.getX() + position.x, player.getEyeY() + position.y, player.getZ() + position.z);
			Renderer3d.renderFilled(stack, color, Vec3d.ZERO, size);
		}
	}

	public static class DisplaySlot extends Slot {
		private final PlayerEntity player;

		public DisplaySlot(PlayerEntity player, int x, int y) {
			super(new SimpleInventory(1), 0, x, y);
			this.player = player;
		}

		public ItemStack getStack() {
			SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
			return playerSoul.getWeapon();
		}

		@Nullable
		public Pair<Identifier, Identifier> getBackgroundSprite() {
			return null;
		}

		@Override
		public boolean canInsert(ItemStack stack) {
			return false;
		}

		@Override
		public boolean canTakeItems(PlayerEntity playerEntity) {
			return false;
		}
	}

	public static SoulComponent getPlayerData() {
		return ClientNetworkingHandler.playerSoul;
	}
}
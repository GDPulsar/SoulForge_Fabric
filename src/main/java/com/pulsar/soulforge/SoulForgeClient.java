package com.pulsar.soulforge;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.pulsar.soulforge.ability.perseverance.ColossalClaymore;
import com.pulsar.soulforge.block.SoulForgeBlocks;
import com.pulsar.soulforge.client.block.CreativeZoneBlockRenderer;
import com.pulsar.soulforge.client.block.SoulForgeBlockRenderer;
import com.pulsar.soulforge.client.block.SoulJarEntityRenderer;
import com.pulsar.soulforge.client.entity.*;
import com.pulsar.soulforge.client.event.ClickEvent;
import com.pulsar.soulforge.client.event.ClientEndTick;
import com.pulsar.soulforge.client.event.ClientStartTick;
import com.pulsar.soulforge.client.event.KeyInputHandler;
import com.pulsar.soulforge.client.networking.ClientNetworkingHandler;
import com.pulsar.soulforge.client.render.SoulForgeRendering;
import com.pulsar.soulforge.client.ui.*;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.entity.SoulForgeEntities;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.particle.SoulForgeParticles;
import com.pulsar.soulforge.shader.TestPostProcessor;
import com.pulsar.soulforge.siphon.Siphon;
import com.pulsar.soulforge.trait.Traits;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import ladysnake.satin.api.managed.ManagedFramebuffer;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import me.x150.renderer.event.RenderEvents;
import me.x150.renderer.render.Renderer3d;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.render.entity.LightningEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.sound.SoundInstance;
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
import team.lodestar.lodestone.systems.postprocess.PostProcessHandler;

import java.awt.*;
import java.io.IOException;
import java.io.UncheckedIOException;

import static net.minecraft.client.render.RenderPhase.*;
import static net.minecraft.client.render.VertexFormats.POSITION_COLOR_TEXTURE_LIGHT;

public class SoulForgeClient implements ClientModInitializer {
	public static EntityModelLayer MODEL_FROZEN_ENERGY_LAYER = new EntityModelLayer(new Identifier(SoulForge.MOD_ID, "frozen_energy"), "Frozen Energy");
	public static EntityModelLayer MODEL_ICE_SPIKE_LAYER = new EntityModelLayer(new Identifier(SoulForge.MOD_ID, "ice_spike"), "Ice Spike");
	public static EntityModelLayer MODEL_TOTAL_FROSTBITE_LAYER = new EntityModelLayer(new Identifier(SoulForge.MOD_ID, "total_frostbite"), "Total Frostbite");

	public static SoundInstance snowstormSound = null;
	public static SoundInstance heartbeatSound = null;

	public static RenderPhase.ShaderProgram energyBeamProgram;

	public static RenderLayer getBeamRenderLayer(Identifier texture) {
		RenderLayer.MultiPhaseParameters mpp = RenderLayer.MultiPhaseParameters.builder().program(energyBeamProgram)
				.texture(new RenderPhase.Texture(texture, false, false)).transparency(NO_TRANSPARENCY)
				.lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(false);
		RenderLayer layer = RenderLayer.of("energy_beam", POSITION_COLOR_TEXTURE_LIGHT, VertexFormat.DrawMode.QUADS,
						2048, false, true, mpp);
		return layer;
		//return energyBeamBuffer.getRenderLayer(layer);
	}

	public static RenderLayer getRiftLayer(Identifier texture) {
		RenderLayer.MultiPhaseParameters mpp = RenderLayer.MultiPhaseParameters.builder().program(POSITION_COLOR_TEXTURE_LIGHTMAP_PROGRAM)
				.texture(new RenderPhase.Texture(texture, false, false)).transparency(ADDITIVE_TRANSPARENCY)
				.lightmap(DISABLE_LIGHTMAP).build(false);
        return RenderLayer.of("rift", POSITION_COLOR_TEXTURE_LIGHT, VertexFormat.DrawMode.TRIANGLES,
				FabricLoader.getInstance().isModLoaded("sodium") ? 4194304 : 256, false, false, mpp);
	}

	public static final ManagedShaderEffect energyBeamEffect = ShaderEffectManager.getInstance().manage(new Identifier(SoulForge.MOD_ID, "shaders/post/energy_beam.json"));
	public static final ManagedFramebuffer energyBeamBuffer = energyBeamEffect.getTarget("final");

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

		HandledScreens.register(SoulForge.SOUL_FORGE_SCREEN_HANDLER, SoulForgeScreen::new);
		HandledScreens.register(SoulForge.CREATIVE_ZONE_SCREEN_HANDLER, CreativeZoneScreen::new);

		BlockEntityRendererFactories.register(SoulForgeBlocks.CREATIVE_ZONE_ENTITY, CreativeZoneBlockRenderer::new);
		BlockEntityRendererFactories.register(SoulForgeBlocks.SOUL_FORGE_BLOCK_ENTITY, SoulForgeBlockRenderer::new);
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
		EntityRendererRegistry.register(SoulForgeEntities.FEAR_BOMB_ENTITY_TYPE, FearBombRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.SLOWBALL_ENTITY_TYPE, SlowballRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.ICE_SPIKE_ENTITY_TYPE, IceSpikeRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.TOTAL_FROSTBITE_ENTITY_TYPE, TotalFrostbiteEntityRenderer::new);
		EntityRendererRegistry.register(SoulForgeEntities.ANTLER_ENTITY_TYPE, FlyingItemEntityRenderer::new);

		EntityModelLayerRegistry.registerModelLayer(MODEL_FROZEN_ENERGY_LAYER, FrozenEnergyModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(MODEL_ICE_SPIKE_LAYER, IceSpikeModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(MODEL_TOTAL_FROSTBITE_LAYER, TotalFrostbiteModel::getTexturedModelData);

		WormholeEntityRenderer.initialiseCrackRenderTypes();

		HudRenderCallback.EVENT.register(new MagicHudOverlay());
		HudRenderCallback.EVENT.register(new ValueHudOverlay());
		HudRenderCallback.EVENT.register(new SoulResetOverlay());

		SoulForgeParticles.clientRegister();

		ModelPredicateProviderRegistry.register(SoulForgeItems.INTEGRITY_RAPIER, new Identifier("parrying"), (stack, world, entity, i) ->
				entity != null && entity.isUsingItem() ? 1f : 0f);
		ModelPredicateProviderRegistry.register(SoulForgeItems.DETERMINATION_RAPIER, new Identifier("parrying"), (stack, world, entity, i) ->
				entity != null && entity.isUsingItem() ? 1f : 0f);
		ModelPredicateProviderRegistry.register(SoulForgeItems.DETERMINATION_SWORD, new Identifier("blocking"), (stack, world, entity, i) ->
				entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1f : 0f);
		ModelPredicateProviderRegistry.register(SoulForgeItems.KINDNESS_SHIELD, new Identifier("blocking"), (stack, world, entity, i) ->
				entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1f : 0f);
		ModelPredicateProviderRegistry.register(SoulForgeItems.DETERMINATION_SHIELD, new Identifier("blocking"), (stack, world, entity, i) ->
				entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1f : 0f);
		ModelPredicateProviderRegistry.register(SoulForgeItems.JUSTICE_BOW, new Identifier("pulling"), ((stack, world, entity, seed) -> {
			if (entity == null) return 0f;
			return entity.isUsingItem() && entity.getActiveItem() == stack ? 1f : 0f;
		}));
		ModelPredicateProviderRegistry.register(SoulForgeItems.JUSTICE_BOW, new Identifier("pull"), ((stack, world, entity, seed) -> {
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
			return stack.getOrCreateNbt().contains("loaded") && stack.getOrCreateNbt().getBoolean("loaded") ? 1f : 0f;
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
			return stack.getOrCreateNbt().contains("loaded") && stack.getOrCreateNbt().getBoolean("loaded") ? 1f : 0f;
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

		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
			int color = 0x000000;
			if (tintIndex == 0) {
				try {
					return Traits.get(stack.getOrCreateNbt().getString("trait1")).getColor();
				} catch (NullPointerException ignored) {}
			}
			if (tintIndex == 1) {
				try {
					return Traits.get(stack.getOrCreateNbt().getString("trait2")).getColor();
				} catch (NullPointerException ignored) {
					return 0xFF80FF;
				}
			}
			return color;
		}, SoulForgeItems.SOUL);

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

		PostProcessHandler.addInstance(TestPostProcessor.INSTANCE);

		CoreShaderRegistrationCallback.EVENT.register(ctx -> {
			SoulForgeRendering.initializeShaders(
					(id, vertexFormat, onLoaded) -> {
						try {
							ctx.register(id, vertexFormat, onLoaded);
						} catch (IOException e) {
							throw new UncheckedIOException(e);
						}
					});

			energyBeamProgram = new RenderPhase.ShaderProgram(SoulForgeRendering::energyBeam);
		});

		ShaderEffectRenderCallback.EVENT.register(tickDelta -> {
			energyBeamEffect.render(tickDelta);
			MinecraftClient.getInstance().getFramebuffer().beginWrite(true);
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
			energyBeamBuffer.draw(MinecraftClient.getInstance().getWindow().getFramebufferWidth(), MinecraftClient.getInstance().getWindow().getFramebufferHeight(), false);
			energyBeamBuffer.clear();
			MinecraftClient.getInstance().getFramebuffer().beginWrite(true);
			RenderSystem.disableBlend();
		});
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
		if (ClientNetworkingHandler.playerSoul == null) return new SoulComponent(MinecraftClient.getInstance().player);
		return ClientNetworkingHandler.playerSoul;
	}
}
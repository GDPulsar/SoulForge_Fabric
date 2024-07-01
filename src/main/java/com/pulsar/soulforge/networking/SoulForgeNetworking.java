package com.pulsar.soulforge.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.networking.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class SoulForgeNetworking {
    // C2S
    public static final Identifier CAST_ABILITY = Identifier.of(SoulForge.MOD_ID, "cast_ability");
    public static final Identifier SWITCH_MODE = Identifier.of(SoulForge.MOD_ID, "switch_mode");
    public static final Identifier ABILITY_HOTBAR_SCROLL = Identifier.of(SoulForge.MOD_ID, "ability_hotbar_scroll");
    public static final Identifier SET_WEAPON = Identifier.of(SoulForge.MOD_ID, "set_weapon");
    public static final Identifier RESET = Identifier.of(SoulForge.MOD_ID, "reset");
    public static final Identifier VEINMINE = Identifier.of(SoulForge.MOD_ID, "veinmine");
    public static final Identifier SET_ABILITY_LAYOUT = Identifier.of(SoulForge.MOD_ID, "set_ability_layout");
    public static final Identifier TOGGLE_MAGIC_MODE = Identifier.of(SoulForge.MOD_ID, "toggle_magic_mode");
    public static final Identifier START_SOUL_RESET = Identifier.of(SoulForge.MOD_ID, "start_soul_reset");
    public static final Identifier END_SOUL_RESET = Identifier.of(SoulForge.MOD_ID, "end_soul_reset");
    public static final Identifier LEFT_CLICK = Identifier.of(SoulForge.MOD_ID, "left_click");
    public static final Identifier RIGHT_CLICK = Identifier.of(SoulForge.MOD_ID, "right_click");
    public static final Identifier HOLD_ITEM = Identifier.of(SoulForge.MOD_ID, "hold_item");
    public static final Identifier CAST_WORMHOLE = Identifier.of(SoulForge.MOD_ID, "cast_wormhole");
    public static final Identifier SPAWN_WORMHOLE = Identifier.of(SoulForge.MOD_ID, "spawn_wormhole");
    public static final Identifier DETERMINE_SELECT = Identifier.of(SoulForge.MOD_ID, "determine_select");
    public static final Identifier RELOAD_SELECT = Identifier.of(SoulForge.MOD_ID, "reload_select");
    public static final Identifier DOMAIN_EXPANSION = Identifier.of(SoulForge.MOD_ID, "domain_expansion");

    // S2C
    public static final Identifier PLAYER_SOUL = Identifier.of(SoulForge.MOD_ID, "player_soul");
    public static final Identifier PERFORM_ANIMATION = Identifier.of(SoulForge.MOD_ID, "perform_animation");
    public static final Identifier POSITION_VELOCITY = Identifier.of(SoulForge.MOD_ID, "position_velocity");
    public static final Identifier SET_THIRD_PERSON = Identifier.of(SoulForge.MOD_ID, "set_third_person");
    public static final Identifier SET_SPOKEN_TEXT = Identifier.of(SoulForge.MOD_ID, "set_spoken_text");
    public static final Identifier DETERMINE_SCREEN = Identifier.of(SoulForge.MOD_ID, "determine_screen");

    public static void registerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(AbilityHotbarScrollPacket.ID, AbilityHotbarScrollPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(CastAbilityPacket.ID, CastAbilityPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(CastWormholePacket.ID, CastWormholePacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(DomainExpansionPacket.ID, DomainExpansionPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(EndSoulResetPacket.ID, EndSoulResetPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(LeftClickPacket.ID, LeftClickPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(ReloadSelectPacket.ID, ReloadSelectPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SwitchModePacket.ID, SwitchModePacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SetWeaponPacket.ID, SetWeaponPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(VeinminePacket.ID, VeinminePacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SetAbilityLayoutPacket.ID, SetAbilityLayoutPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(ToggleMagicModePacket.ID, ToggleMagicModePacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(StartSoulResetPacket.ID, StartSoulResetPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(RightClickPacket.ID, RightClickPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(HoldItemPacket.ID, HoldItemPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SpawnWormholePacket.ID, SpawnWormholePacket::receive);

        PayloadTypeRegistry.playC2S().register(AbilityHotbarScrollPacket.ID, AbilityHotbarScrollPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(CastAbilityPacket.ID, CastAbilityPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(CastWormholePacket.ID, CastWormholePacket.CODEC);
        PayloadTypeRegistry.playC2S().register(DomainExpansionPacket.ID, DomainExpansionPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(EndSoulResetPacket.ID, EndSoulResetPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(LeftClickPacket.ID, LeftClickPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(ReloadSelectPacket.ID, ReloadSelectPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(SwitchModePacket.ID, SwitchModePacket.CODEC);
        PayloadTypeRegistry.playC2S().register(SetWeaponPacket.ID, SetWeaponPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(VeinminePacket.ID, VeinminePacket.CODEC);
        PayloadTypeRegistry.playC2S().register(SetAbilityLayoutPacket.ID, SetAbilityLayoutPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(ToggleMagicModePacket.ID, ToggleMagicModePacket.CODEC);
        PayloadTypeRegistry.playC2S().register(StartSoulResetPacket.ID, StartSoulResetPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(RightClickPacket.ID, RightClickPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(HoldItemPacket.ID, HoldItemPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(SpawnWormholePacket.ID, SpawnWormholePacket.CODEC);

        PayloadTypeRegistry.playS2C().register(PerformAnimationPacket.ID, PerformAnimationPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(PlayerSoulPacket.ID, PlayerSoulPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(PositionVelocityPacket.ID, PositionVelocityPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(SetSpokenTextPacket.ID, SetSpokenTextPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(SetThirdPersonPacket.ID, SetThirdPersonPacket.CODEC);
    }

    public static <T extends CustomPayload> void broadcast(@Nullable PlayerEntity exclude, MinecraftServer server, T packet) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (exclude == null || player != exclude) {
                ServerPlayNetworking.send(player, packet);
            }
        }
    }
}

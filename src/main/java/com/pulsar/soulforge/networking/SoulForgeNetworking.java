package com.pulsar.soulforge.networking;

import com.pulsar.soulforge.SoulForge;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class SoulForgeNetworking {
    // C2S
    public static final Identifier CAST_ABILITY = new Identifier(SoulForge.MOD_ID, "cast_ability");
    public static final Identifier SET_WEAPON = new Identifier(SoulForge.MOD_ID, "set_weapon");
    public static final Identifier RESET = new Identifier(SoulForge.MOD_ID, "reset");
    public static final Identifier VEINMINE = new Identifier(SoulForge.MOD_ID, "veinmine");
    public static final Identifier SET_ABILITY_LAYOUT = new Identifier(SoulForge.MOD_ID, "set_ability_layout");
    public static final Identifier START_SOUL_RESET = new Identifier(SoulForge.MOD_ID, "start_soul_reset");
    public static final Identifier END_SOUL_RESET = new Identifier(SoulForge.MOD_ID, "end_soul_reset");
    public static final Identifier LEFT_CLICK = new Identifier(SoulForge.MOD_ID, "left_click");
    public static final Identifier RIGHT_CLICK = new Identifier(SoulForge.MOD_ID, "right_click");
    public static final Identifier HOLD_ITEM = new Identifier(SoulForge.MOD_ID, "hold_item");
    public static final Identifier CAST_WORMHOLE = new Identifier(SoulForge.MOD_ID, "cast_wormhole");
    public static final Identifier SPAWN_WORMHOLE = new Identifier(SoulForge.MOD_ID, "spawn_wormhole");
    public static final Identifier RELOAD_SELECT = new Identifier(SoulForge.MOD_ID, "reload_select");
    public static final Identifier DOMAIN_EXPANSION = new Identifier(SoulForge.MOD_ID, "domain_expansion");

    // S2C
    public static final Identifier PLAYER_SOUL = new Identifier(SoulForge.MOD_ID, "player_soul");
    public static final Identifier USE_MAGIC = new Identifier(SoulForge.MOD_ID, "use_magic");
    public static final Identifier PERFORM_ANIMATION = new Identifier(SoulForge.MOD_ID, "perform_animation");
    public static final Identifier POSITION_VELOCITY = new Identifier(SoulForge.MOD_ID, "position_velocity");
    public static final Identifier SET_THIRD_PERSON = new Identifier(SoulForge.MOD_ID, "set_third_person");

    public static void registerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(SoulForgeNetworking.CAST_ABILITY, CastAbilityPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SoulForgeNetworking.SET_WEAPON, SetWeaponPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SoulForgeNetworking.RESET, ResetPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SoulForgeNetworking.VEINMINE, VeinminePacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SoulForgeNetworking.SET_ABILITY_LAYOUT, SetAbilityLayoutPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SoulForgeNetworking.START_SOUL_RESET, StartSoulResetPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SoulForgeNetworking.END_SOUL_RESET, EndSoulResetPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SoulForgeNetworking.LEFT_CLICK, LeftClickPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SoulForgeNetworking.RIGHT_CLICK, RightClickPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SoulForgeNetworking.HOLD_ITEM, HoldItemPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SoulForgeNetworking.CAST_WORMHOLE, CastWormholePacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SoulForgeNetworking.SPAWN_WORMHOLE, SpawnWormholePacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SoulForgeNetworking.RELOAD_SELECT, ReloadSelectPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SoulForgeNetworking.DOMAIN_EXPANSION, DomainExpansionPacket::receive);
    }

    public static void broadcast(@Nullable PlayerEntity exclude, MinecraftServer server, Identifier packet, PacketByteBuf buf) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (exclude == null || player != exclude) {
                ServerPlayNetworking.send(player, packet, buf);
            }
        }
    }
}

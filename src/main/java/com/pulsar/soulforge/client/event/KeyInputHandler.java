package com.pulsar.soulforge.client.event;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.client.networking.ClientNetworkingHandler;
import com.pulsar.soulforge.client.ui.SoulScreen;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.networking.SoulForgeNetworking;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

public class KeyInputHandler {
    public static final String KEY_CATEGORY = "category.soulforge.soulforge";

    public static final String KEY_MAGIC_MODE = "key.soulforge.magic_mode";
    public static final String KEY_CYCLE_UP = "key.soulforge.cycle_up";
    public static final String KEY_CYCLE_DOWN = "key.soulforge.cycle_down";
    public static final String KEY_SOUL_RESET = "key.soulforge.soul_reset";
    public static final String KEY_CAST_ABILITY = "key.soulforge.cast_ability";
    public static final String KEY_ABILITY_SCREEN = "key.soulforge.ability_screen";
    public static final String KEY_WEAPON_SLOT = "key.soulforge.weapon_slot";

    public static KeyBinding MagicModeKey;
    public static KeyBinding CycleUpKey;
    public static KeyBinding CycleDownKey;
    public static KeyBinding SoulResetKey;
    public static KeyBinding CastAbilityKey;
    public static KeyBinding AbilityScreenKey;
    public static KeyBinding WeaponSlotKey;
    public static KeyBinding DomainExpansionKey;

    public static void registerKeyInputs() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (MagicModeKey.wasPressed()) {
                if (client.currentScreen instanceof InventoryScreen || client.currentScreen instanceof CreativeInventoryScreen) {
                    client.setScreen(new SoulScreen(client.currentScreen));
                } else if (client.currentScreen instanceof SoulScreen soulScreen) {
                    client.setScreen(soulScreen.parent);
                } else {
                    if (ClientNetworkingHandler.playerSoul != null) ClientNetworkingHandler.playerSoul.toggleMagicMode();
                }
            }
            while (AbilityScreenKey.wasPressed()) {
                if (client.currentScreen instanceof SoulScreen soulScreen) {
                    client.setScreen(soulScreen.parent);
                } else {
                    client.setScreen(new SoulScreen(client.currentScreen));
                }
            }
            while (CycleUpKey.wasPressed()) {
                if (ClientNetworkingHandler.playerSoul != null) {
                    ClientNetworkingHandler.playerSoul.setAbilityRow((ClientNetworkingHandler.playerSoul.getAbilityRow()+1)%4);
                }
            }
            while (CycleDownKey.wasPressed()) {
                if (ClientNetworkingHandler.playerSoul != null) {
                    ClientNetworkingHandler.playerSoul.setAbilityRow((ClientNetworkingHandler.playerSoul.getAbilityRow()+3)%4);
                }
            }
            while (SoulResetKey.wasPressed()) {
                ClientPlayNetworking.send(SoulForgeNetworking.START_SOUL_RESET, PacketByteBufs.create());
            }
            while (CastAbilityKey.wasPressed()) {
                SoulComponent playerSoul = ClientNetworkingHandler.playerSoul;
                if (playerSoul == null) break;
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBoolean(playerSoul.magicModeActive());
                if (playerSoul.magicModeActive()) {
                    AbilityBase ability = playerSoul.getLayoutAbility(playerSoul.getAbilityRow(), playerSoul.getAbilitySlot());
                    if (ability != null) {
                        buf.writeBoolean(true);
                        buf.writeString(ability.getName());
                        if (playerSoul.onCooldown(ability)) break;
                        ClientPlayNetworking.send(SoulForgeNetworking.CAST_ABILITY, buf);
                    }
                } else {
                    ClientPlayNetworking.send(SoulForgeNetworking.CAST_ABILITY, buf);
                }
            }
            if (Objects.equals(client.getSession().getUsername(), "GDPulsar")) {
                if (DomainExpansionKey != null) {
                    while (DomainExpansionKey.wasPressed()) {
                        ClientPlayNetworking.send(SoulForgeNetworking.DOMAIN_EXPANSION, PacketByteBufs.create());
                    }
                }
            }
            while (WeaponSlotKey.wasPressed()) {
                if (client.player != null) {
                    if (ClientNetworkingHandler.playerSoul != null) {
                        if (ClientNetworkingHandler.playerSoul.hasWeapon()) client.player.getInventory().selectedSlot = 9;
                    }
                }
            }
        });
    }

    public static void register() {
        MagicModeKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_MAGIC_MODE,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_ALT,
                KEY_CATEGORY
        ));
        CycleUpKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_CYCLE_UP,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_BRACKET,
                KEY_CATEGORY
        ));
        CycleDownKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_CYCLE_DOWN,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_BRACKET,
                KEY_CATEGORY
        ));
        SoulResetKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_SOUL_RESET,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_END,
                KEY_CATEGORY
        ));
        CastAbilityKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_CAST_ABILITY,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                KEY_CATEGORY
        ));
        AbilityScreenKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_ABILITY_SCREEN,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_I,
                KEY_CATEGORY
        ));
        WeaponSlotKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_WEAPON_SLOT,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_0,
                KEY_CATEGORY
        ));
    }

    public static void registerThePulsarFunnyThings() {
        DomainExpansionKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.soulforge.domain_expansion",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_MULTIPLY,
                KEY_CATEGORY
        ));
    }
}

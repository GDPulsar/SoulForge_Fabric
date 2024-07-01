package com.pulsar.soulforge.client.event;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.determination.WeaponWheel;
import com.pulsar.soulforge.ability.duals.Armory;
import com.pulsar.soulforge.ability.duals.Reload;
import com.pulsar.soulforge.ability.duals.Wormhole;
import com.pulsar.soulforge.ability.integrity.KineticBoost;
import com.pulsar.soulforge.ability.perseverance.MorphingWeaponry;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.client.ui.*;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.networking.*;
import com.pulsar.soulforge.trait.Traits;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.Vec3d;
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
            SoulComponent playerSoul = SoulForge.getPlayerSoul(client.player);
            while (MagicModeKey.wasPressed()) {
                if (client.currentScreen instanceof InventoryScreen || client.currentScreen instanceof CreativeInventoryScreen) {
                    client.setScreen(new SoulScreen(client.currentScreen));
                } else if (client.currentScreen instanceof SoulScreen soulScreen) {
                    client.setScreen(soulScreen.parent);
                } else {
                    playerSoul.toggleMagicMode();
                    ClientPlayNetworking.send(new ToggleMagicModePacket(playerSoul.magicModeActive()));
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
                ClientPlayNetworking.send(new SwitchModePacket(1));
            }
            while (CycleDownKey.wasPressed()) {
                ClientPlayNetworking.send(new SwitchModePacket(-1));
            }
            while (SoulResetKey.wasPressed()) {
                ClientPlayNetworking.send(new StartSoulResetPacket());
            }
            while (CastAbilityKey.wasPressed()) {
                if (playerSoul.magicModeActive()) {
                    AbilityBase ability = playerSoul.getLayoutAbility(playerSoul.getAbilityRow(), playerSoul.getAbilitySlot());
                    if (playerSoul.onCooldown(ability)) break;
                    if (ability instanceof WeaponWheel || ability instanceof Wormhole || ability instanceof Armory || ability instanceof Reload || ability instanceof MorphingWeaponry) {
                        float cost = ability.getCost();
                        if (client.player.getAttributeInstance(SoulForgeAttributes.MAGIC_COST) != null) {
                            cost *= (float)client.player.getAttributeInstance(SoulForgeAttributes.MAGIC_COST).getValue();
                        }
                        if (playerSoul.isStrong() && !playerSoul.getTraits().contains(Traits.determination)) cost /= 2f;
                        if (playerSoul.hasCast("Valiant Heart")) cost /= 2f;
                        if (cost <= playerSoul.getMagic()) {
                            if (ability instanceof WeaponWheel) client.setScreen(new WeaponWheelScreen());
                            else if (ability instanceof Wormhole) client.setScreen(new WormholeScreen());
                            else if (ability instanceof Armory) client.setScreen(new ArmoryScreen());
                            else if (ability instanceof Reload) client.setScreen(new ReloadScreen());
                            else if (ability instanceof MorphingWeaponry) client.setScreen(new MorphingWeaponryScreen());
                        }
                    }
                    if (ability instanceof KineticBoost) {
                        float cost = ability.getCost();
                        if (client.player.getAttributeInstance(SoulForgeAttributes.MAGIC_COST) != null) {
                            cost *= (float)client.player.getAttributeInstance(SoulForgeAttributes.MAGIC_COST).getValue();
                        }
                        if (playerSoul.isStrong() && !playerSoul.getTraits().contains(Traits.determination)) cost /= 2f;
                        if (playerSoul.hasCast("Valiant Heart")) cost /= 2f;
                        if (cost <= playerSoul.getMagic()) {
                            float horiz = playerSoul.getEffectiveLV()*0.15f;
                            float vert = playerSoul.getEffectiveLV()*0.02f;
                            Vec3d direction = new Vec3d(client.player.getRotationVector().x, 0f, client.player.getRotationVector().z).normalize().multiply(horiz);
                            client.player.addVelocityInternal(new Vec3d(direction.x, vert, direction.z));
                        }
                    }
                }
                ClientPlayNetworking.send(new CastAbilityPacket(playerSoul.magicModeActive(), playerSoul.getAbilitySlot()));
            }
            if (Objects.equals(client.getSession().getUsername(), "GDPulsar")) {
                if (DomainExpansionKey != null) {
                    while (DomainExpansionKey.wasPressed()) {
                        ClientPlayNetworking.send(new DomainExpansionPacket());
                    }
                }
            }
            while (WeaponSlotKey.wasPressed()) {
                if (client.player != null) client.player.getInventory().selectedSlot = 9;
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

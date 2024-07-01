package com.pulsar.soulforge.mixin;

import com.pulsar.soulforge.client.event.KeyInputHandler;
import com.pulsar.soulforge.client.ui.SoulScreen;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public class ScreenMixin {
    @Shadow @Nullable protected MinecraftClient client;

    @Inject(method = "keyPressed", at=@At("HEAD"))
    private void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        Screen screen = (Screen)(Object)this;
        if (screen instanceof InventoryScreen) {
            if (keyCode == KeyBindingHelper.getBoundKeyOf(KeyInputHandler.MagicModeKey).getCode()) {
                assert this.client != null;
                this.client.setScreen(new SoulScreen(this.client.currentScreen));
            }
        }
    }
}

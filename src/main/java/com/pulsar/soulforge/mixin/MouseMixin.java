package com.pulsar.soulforge.mixin;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.ValueComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Overlay;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Mouse.class)
public class MouseMixin {
    @Shadow @Final private MinecraftClient client;

    @Redirect(method = "onMouseButton", at= @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getOverlay()Lnet/minecraft/client/gui/screen/Overlay;", ordinal = 0))
    private Overlay modifyGetOverlay(MinecraftClient instance) {
        if (instance.player != null) {
            ValueComponent values = SoulForge.getValues(instance.player);
            if (values.getBool("resettingSoul") && this.client.currentScreen == null) {
                return new Overlay() {
                    @Override
                    public void render(DrawContext context, int mouseX, int mouseY, float delta) {

                    }
                };
            }
        }
        return instance.getOverlay();
    }
}

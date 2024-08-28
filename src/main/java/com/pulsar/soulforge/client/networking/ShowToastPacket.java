package com.pulsar.soulforge.client.networking;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Objects;

public class ShowToastPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        boolean hasTexture = buf.readBoolean();
        Identifier texture = hasTexture ? buf.readIdentifier() : null;
        boolean hasTitleText = buf.readBoolean();
        String titleText = hasTitleText ? buf.readString() : null;
        String text = buf.readString();
        client.getToastManager().add(new Toast() {
            @Override
            public Visibility draw(DrawContext context, ToastManager manager, long startTime) {
                context.drawTexture(TEXTURE, 0, 0, 0, 0, this.getWidth(), this.getHeight());
                if (titleText != null) {
                    context.drawText(manager.getClient().textRenderer, Text.literal(titleText), 30, 7, -1, false);
                    context.drawText(manager.getClient().textRenderer, Text.literal(text), 30, 18, -1, false);
                } else {
                    List<OrderedText> list = manager.getClient().textRenderer.wrapLines(Text.literal(text), 125);
                    Objects.requireNonNull(manager.getClient().textRenderer);
                    int l = this.getHeight() / 2 - list.size() * 9 / 2;
                    for (OrderedText text : list) {
                        l += 9;
                        context.drawText(manager.getClient().textRenderer, text, 30, l, -1, false);
                    }

                    if (texture != null) context.drawTexture(texture, 8, 8, 0, 0, 18, 18, 18, 18);
                }
                return (double)startTime >= 5000.0 * manager.getNotificationDisplayTimeMultiplier() ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
            }
        });
    }
}

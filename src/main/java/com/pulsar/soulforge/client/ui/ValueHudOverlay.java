package com.pulsar.soulforge.client.ui;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.TemporaryModifierComponent;
import com.pulsar.soulforge.components.ValueComponent;
import com.pulsar.soulforge.util.Triplet;
import com.pulsar.soulforge.util.Utils;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.*;

import static java.util.Map.entry;

public class ValueHudOverlay implements HudRenderCallback {
    public static HashMap<String, String> valueMappings = new HashMap<>(Map.ofEntries(
            entry("stockpiles", "Stockpiles")
    ));

    public HashMap<UUID, ValueOverlayEntry> entries = new HashMap<>();
    public HashMap<UUID, String> oldValues = new HashMap<>();
    public HashMap<UUID, EntityAttribute> oldModifiers = new HashMap<>();

    float lastDelta = 0f;
    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        float delta = tickDelta - lastDelta;
        if (delta < 0) delta = (tickDelta + 1f) - lastDelta;
        lastDelta = delta;
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            ValueComponent values = SoulForge.getValues(player);
            if (values != null) {
                for (String value : valueMappings.keySet()) {
                    if (values.hasInt(value) && !oldValues.containsValue(value)) {
                        UUID id = UUID.randomUUID();
                        addEntry(id, Text.literal(valueMappings.get(value)).append(": ").append(String.valueOf(values.getInt(value))));
                        oldValues.put(id, value);
                    } else if (values.hasInt(value)) {
                        UUID id = Utils.getKeyByValue(oldValues, value);
                        entries.get(id).text = Text.literal(valueMappings.get(value)).append(": ").append(String.valueOf(values.getInt(value)));
                    }
                }
                for (Map.Entry<UUID, String> entry : Set.copyOf(oldValues.entrySet())) {
                    if (!valueMappings.containsKey(entry.getValue())) {
                        oldValues.remove(entry.getKey());
                        removeEntry(entry.getKey());
                    }
                }
            }

            TemporaryModifierComponent modifiers = SoulForge.getTemporaryModifiers(player);
            if (modifiers != null) {
                for (Triplet<EntityAttributeModifier, EntityAttribute, Float> modifier : modifiers.getModifiers()) {
                    MutableText text = Text.translatable(modifier.getSecond().getTranslationKey()).append(": ");
                    EntityAttributeModifier.Operation operation = modifier.getFirst().getOperation();
                    if (operation == EntityAttributeModifier.Operation.ADDITION) text.append("+").append(String.valueOf(modifier.getFirst().getValue()));
                    if (operation == EntityAttributeModifier.Operation.MULTIPLY_BASE) text.append("x").append(String.valueOf(1f + modifier.getFirst().getValue()));
                    if (operation == EntityAttributeModifier.Operation.MULTIPLY_TOTAL) text.append("x").append(String.valueOf(1f + modifier.getFirst().getValue())).append("%");
                    if (!oldModifiers.containsKey(modifier.getFirst().getId())) {
                        addEntry(modifier.getFirst().getId(), text);
                        oldModifiers.put(modifier.getFirst().getId(), modifier.getSecond());
                    } else {
                        entries.get(modifier.getFirst().getId()).text = text;
                    }
                }
                for (Map.Entry<UUID, EntityAttribute> entry : Set.copyOf(oldModifiers.entrySet())) {
                    if (modifiers.getModifier(entry.getValue(), entry.getKey()) != null) {
                        oldModifiers.remove(entry.getKey());
                        removeEntry(entry.getKey());
                    }
                }
            }
        }

        for (ValueOverlayEntry entry : entries.values()) {
            entry.moveTimer += delta;
            drawValueEntry(context, entry);
        }
    }

    public void addEntry(UUID id, Text text) {
        ValueOverlayEntry entry = new ValueOverlayEntry(id, text, 0);
        for (ValueOverlayEntry other : entries.values()) {
            other.pos += 1;
        }
        entries.put(id, entry);
    }

    public void removeEntry(UUID id) {
        int removedPos = entries.get(id).pos;
        for (ValueOverlayEntry other : entries.values()) {
            if (removedPos > other.pos) other.pos -= 1;
        }
    }

    public static void drawValueEntry(DrawContext context, ValueOverlayEntry entry) {
        TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
        int windowWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int windowHeight = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int width = renderer.getWidth(entry.text);
        float moveDelta = MathHelper.sin((float)(Math.PI * (2f*entry.moveTimer/3f - 0.5f)) + 0.1f)/2f;
        if (entry.moveTimer < 0f) moveDelta = 0f;
        if (entry.moveTimer > 1.5f) moveDelta = 1f;
        float pos = MathHelper.lerp(entry.moveTimer, entry.lastPos * 15f, entry.pos * 15f);
        context.drawTextWithShadow(renderer, entry.text, windowWidth - width - 10, windowHeight - (int)pos, 0xFFFFFF);
    }

    public static class ValueOverlayEntry {
        public UUID id;
        public Text text;
        public int lastPos;
        public int pos;
        public float moveTimer = 0f;

        public ValueOverlayEntry(UUID id, Text text, int pos) {
            this.id = id;
            this.text = text;
            this.lastPos = 0;
            this.pos = pos;
        }
    }
}

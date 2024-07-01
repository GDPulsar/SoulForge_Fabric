package com.pulsar.soulforge.client.ui;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.item.SoulJarItem;
import com.pulsar.soulforge.networking.SoulForgeNetworking;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.trait.TraitBase;
import com.pulsar.soulforge.trait.Traits;
import com.pulsar.soulforge.util.ResetData;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec2f;
import org.joml.Vector2i;

import java.awt.*;
import java.util.*;
import java.util.List;

public class SoulResetOverlay implements HudRenderCallback {
    private int startTime = 0;
    private boolean wasResetting = false;
    private boolean gotLibmod = false;
    private int libmodIndex = 0;
    private boolean startedMusic = false;
    private boolean hasShuffled = false;
    private RerollType rerollType = null;
    private List<TraitBase> chosenTraits = new ArrayList<>();
    private boolean allowChosing = false;
    private List<TraitBase> chosen = new ArrayList<>();
    private int chosenPowerPrediction = 0;
    private int chosenPower = 0;
    private List<Integer> indexes = new ArrayList<>();
    private List<Vector2i> lastPositions = new ArrayList<>();
    private List<Vector2i> targetPositions = new ArrayList<>();
    private ItemStack soulJar = ItemStack.EMPTY;

    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        assert client.player != null;
        SoulComponent playerSoul = SoulForge.getPlayerSoul(client.player);
        if (playerSoul.hasTag("resettingSoul")) {
            client.mouse.unlockCursor();
            if (!wasResetting) {
                startTime = client.player.age + 30;
                chosenTraits = new ArrayList<>();
                chosenPower = 0;
                chosen = new ArrayList<>();
                soulJar = ItemStack.EMPTY;
                allowChosing = playerSoul.getResetData().bravery && playerSoul.getResetData().justice && playerSoul.getResetData().kindness
                        && playerSoul.getResetData().patience && playerSoul.getResetData().integrity && playerSoul.getResetData().perseverance
                        && playerSoul.getResetData().determination && playerSoul.getResetData().strongDual;
                rerollType = getRerollType(playerSoul);
                if (client.player.getMainHandStack().isOf(SoulForgeItems.SOUL_JAR)) {
                    if (Objects.equals(SoulJarItem.getOwner(client.player.getMainHandStack()), client.player.getName().getString())) {
                        soulJar = client.player.getMainHandStack();
                        allowChosing = false;
                        rerollType = RerollType.JAR;
                    }
                }
                if (rerollType == RerollType.NORMAL) {
                    gotLibmod = Math.random() <= 0.01f && !allowChosing;
                    chosenTraits = List.of(getNormalTrait(playerSoul));
                    chosenPower = getNormalPower(playerSoul);
                } else if (rerollType == RerollType.DUAL) {
                    gotLibmod = false;
                    chosenTraits = getDualTrait(playerSoul);
                    chosenPower = getDualPower(playerSoul);
                } else {
                    gotLibmod = false;
                    chosenTraits = List.of(Traits.determination);
                    chosenPower = 1;
                }
            }
            if (client.player.age - startTime >= 50 && allowChosing) {
                if (client.player.age - startTime == 51) startTime++;
                Vec2f mousePos = new Vec2f((float)(client.mouse.getX()/(float)client.getWindow().getWidth())*width, (float)(client.mouse.getY()/(float)client.getWindow().getHeight())*height);
                if (chosen.isEmpty()) {
                    context.drawTexture(new Identifier(SoulForge.MOD_ID, "textures/ui/power_mouse.png"), width / 2 - 20, 55, 0, 0, 39, 60, 39, 60);
                    if (mousePos.distanceSquared(new Vec2f(width / 2f, 85f)) < 600) {
                        context.drawCenteredTextWithShadow(client.textRenderer, "Left click for normal. Right click for strong. Middle click for pure.", width / 2, 20, 0xFFFFFF);
                        if (client.mouse.wasLeftButtonClicked() || client.mouse.wasMiddleButtonClicked() || client.mouse.wasRightButtonClicked())
                            chosen.add(Traits.bravery);
                    } else {
                        context.drawCenteredTextWithShadow(client.textRenderer, "Choose your trait.", width / 2, 20, 0xFFFFFF);
                    }
                }
                if (chosen.size() < 2 && !chosen.contains(Traits.determination)) {
                    if (mousePos.distanceSquared(new Vec2f(width / 2f, height / 2f + 100f)) < 600) {
                        context.drawCenteredTextWithShadow(client.textRenderer, "Bravery", width / 2, 230, 0xFF8000);
                        if (client.mouse.wasLeftButtonClicked() || client.mouse.wasMiddleButtonClicked() || client.mouse.wasRightButtonClicked())
                            chosen.add(Traits.bravery);
                    }
                    if (mousePos.distanceSquared(new Vec2f(width / 2f + 87f, height / 2f + 50f)) < 600) {
                        context.drawCenteredTextWithShadow(client.textRenderer, "Justice", width / 2, 230, 0xFFFF00);
                        if (client.mouse.wasLeftButtonClicked() || client.mouse.wasMiddleButtonClicked() || client.mouse.wasRightButtonClicked())
                            chosen.add(Traits.justice);
                    }
                    if (mousePos.distanceSquared(new Vec2f(width / 2f + 87f, height / 2f - 50f)) < 600) {
                        context.drawCenteredTextWithShadow(client.textRenderer, "Kindness", width / 2, 230, 0x00FF00);
                        if (client.mouse.wasLeftButtonClicked() || client.mouse.wasMiddleButtonClicked() || client.mouse.wasRightButtonClicked())
                            chosen.add(Traits.kindness);
                    }
                    if (mousePos.distanceSquared(new Vec2f(width / 2f, height / 2f - 100f)) < 600) {
                        context.drawCenteredTextWithShadow(client.textRenderer, "Patience", width / 2, 230, 0x00FFFF);
                        if (client.mouse.wasLeftButtonClicked() || client.mouse.wasMiddleButtonClicked() || client.mouse.wasRightButtonClicked())
                            chosen.add(Traits.patience);
                    }
                    if (mousePos.distanceSquared(new Vec2f(width / 2f - 87f, height / 2f - 50f)) < 600) {
                        context.drawCenteredTextWithShadow(client.textRenderer, "Integrity", width / 2, 230, 0x0000FF);
                        if (client.mouse.wasLeftButtonClicked() || client.mouse.wasMiddleButtonClicked() || client.mouse.wasRightButtonClicked())
                            chosen.add(Traits.integrity);
                    }
                    if (mousePos.distanceSquared(new Vec2f(width / 2f - 87f, height / 2f + 50f)) < 600) {
                        context.drawCenteredTextWithShadow(client.textRenderer, "Perseverance", width / 2, 230, 0x8000FF);
                        if (client.mouse.wasLeftButtonClicked() || client.mouse.wasMiddleButtonClicked() || client.mouse.wasRightButtonClicked())
                            chosen.add(Traits.perseverance);
                    }
                    if (mousePos.distanceSquared(new Vec2f(width / 2f, height / 2f)) < 600) {
                        context.drawCenteredTextWithShadow(client.textRenderer, "Determination", width / 2, 230, 0xFF0000);
                        if (client.mouse.wasLeftButtonClicked() || client.mouse.wasMiddleButtonClicked() || client.mouse.wasRightButtonClicked())
                            chosen.add(Traits.determination);
                    }
                }
                if (chosen.size() == 2) {
                    if (chosen.get(1) == chosen.get(0)) {
                        chosen.remove(1);
                    }
                }
                if (!chosen.isEmpty()) {
                    context.drawCenteredTextWithShadow(client.textRenderer, "Are you sure?", width / 2, 20, 0xFFFFFF);
                    MutableText traitText = Text.translatable("power.soulforge.normal");
                    if (chosenPowerPrediction == 1) traitText = Text.translatable("power.soulforge.strong");
                    if (chosenPowerPrediction == 2) traitText = Text.translatable("power.soulforge.pure");
                    traitText = traitText.append(" ").append(chosen.get(0).getLocalizedText());
                    if (chosen.size() == 2) traitText.append("-").append(chosen.get(1).getLocalizedText());
                    context.drawCenteredTextWithShadow(client.textRenderer, traitText, width / 2, 55, 0xFFFFFF);
                    context.drawCenteredTextWithShadow(client.textRenderer, "Confirm", width / 2 - 50, 80, 0xFFFFFF);
                    context.drawCenteredTextWithShadow(client.textRenderer, "Undo", width / 2 + 50, 80, 0xFFFFFF);
                    if (mousePos.distanceSquared(new Vec2f(width/2f - 50, 80)) < 600) {
                        if (client.mouse.wasLeftButtonClicked() || client.mouse.wasMiddleButtonClicked() || client.mouse.wasRightButtonClicked()) {
                            rerollType = RerollType.NORMAL;
                            chosenTraits = new ArrayList<>();
                            if (Math.random() <= 0.8 && chosen.get(0) != Traits.determination) {
                                chosenTraits.add(chosen.get(0));
                            }
                            else if (chosen.get(0) == Traits.determination && Math.random() <= 0.25) {
                                chosenTraits.add(chosen.get(0));
                                rerollType = RerollType.DT;
                                chosenPower = 1;
                            }
                            else chosenTraits.add(Traits.randomNormal());
                            if (chosen.size() == 2 && Math.random() <= 0.5) {
                                rerollType = RerollType.DUAL;
                                if (Math.random() <= 0.4) chosenTraits.add(chosen.get(1));
                                else {
                                    TraitBase trait2 = Traits.randomNormal();
                                    while (trait2 == chosenTraits.get(0)) {
                                        trait2 = Traits.randomNormal();
                                    }
                                    chosenTraits.add(trait2);
                                }
                            }
                            if (chosenTraits.get(0) != Traits.determination) {
                                chosenPower = 0;
                                if (chosenPowerPrediction >= 1 && Math.random() <= 0.5) chosenPower = 1;
                                if (chosenPowerPrediction >= 2 && chosenTraits.size() == 1 && Math.random() <= 0.25)
                                    chosenPower = 2;
                            }
                            allowChosing = false;
                        }
                    } else {
                        if (client.mouse.wasLeftButtonClicked()) chosenPowerPrediction = 0;
                        if (client.mouse.wasRightButtonClicked()) chosenPowerPrediction = 1;
                        if (client.mouse.wasMiddleButtonClicked()) chosenPowerPrediction = 2;
                        if (mousePos.distanceSquared(new Vec2f(width/2f + 50, 80)) < 600) {
                            if (client.mouse.wasLeftButtonClicked() || client.mouse.wasMiddleButtonClicked() || client.mouse.wasRightButtonClicked()) {
                                chosen = new ArrayList<>();
                            }
                        }
                    }
                }
            }
            int tickTimer = client.player.age - startTime;
            float timer = tickTimer + tickDelta;
            if (startTime == 0) return;
            if (timer >= 0) {
                if (timer < 15) {
                    int color = (int)timer * 167772160;
                    context.fill(0, 0, width, height, color);
                } else {
                    int color = timer <= (gotLibmod ? 430f : (rerollType != RerollType.NORMAL ? 175f : 150f)) ? 0x96000000 : (int)((gotLibmod ? (450f-timer)/20f : ((rerollType != RerollType.NORMAL ? 200f : 175f)-timer)/25f)*15f) * 167772160;
                    context.fill(0, 0, width, height, color);

                    int[] colors = new int[]{0xFF8000, 0xFFFF00, 0x00FF00, 0x00FFFF, 0x0000FF, 0x8000FF};
                    if (gotLibmod && timer >= 50) {
                        if (tickTimer == 50 && !startedMusic) {
                            client.player.playSound(SoulForgeSounds.LIBMO_EVENT, SoundCategory.MASTER, 0.5f, 1f);
                            libmodIndex = MathHelper.clamp(MathHelper.floor(6*Math.random()), 0, 5);
                            lastPositions = new ArrayList<>();
                            targetPositions = new ArrayList<>();
                            indexes = new ArrayList<>(List.of(0, 1, 2, 3, 4, 5));
                            for (int i = 0; i < 6; i++) {
                                float angle = MathHelper.PI/3f*i;
                                lastPositions.add(new Vector2i((int)(100*MathHelper.sin(angle)), (int)(100*MathHelper.cos(angle))));
                            }
                            targetPositions.addAll(lastPositions);
                            startedMusic = true;
                        }
                        if (timer < 256) {
                            colors = new int[]{0xAAAAAA, 0xAAAAAA, 0xAAAAAA, 0xAAAAAA, 0xAAAAAA, 0xAAAAAA};
                        }
                        if (timer < 270 && timer >= 256) {
                            int[] targetR = new int[]{0xFF, 0xFF, 0x00, 0x00, 0x00, 0x80};
                            int[] targetG = new int[]{0x80, 0xFF, 0xFF, 0xFF, 0x00, 0x00};
                            int[] targetB = new int[]{0x00, 0x00, 0x00, 0xFF, 0xFF, 0xFF};
                            for (int i = 0; i < 6; i++) {
                                float delta = (timer - 256)/14f;
                                int r = MathHelper.lerp(delta, 0xAA, targetR[i]);
                                int g = MathHelper.lerp(delta, 0xAA, targetG[i]);
                                int b = MathHelper.lerp(delta, 0xAA, targetB[i]);
                                color = (((r << 8) + g) << 8) + b;
                                colors[i] = color;
                            }
                        }
                        if (timer < 76) {
                            float delta = timer <= 63 ? (timer-50)/13f : (76-timer)/13f;
                            int a = MathHelper.lerp(delta, 0xAA, 0x00), b = MathHelper.lerp(delta, 0xAA, 0xFF);
                            color = (((a << 8) + b) << 8) + a;
                            colors[libmodIndex] = color;
                        }
                        if (timer > 76 && timer < 256 && tickTimer % 6 == 0) {
                            if (!hasShuffled) {
                                lastPositions = List.copyOf(targetPositions);
                                List<Pair<Integer, Vector2i>> toShuffle = new ArrayList<>();
                                for (int i = 0; i < 6; i++) {
                                    toShuffle.add(new Pair<>(indexes.get(i), targetPositions.get(i)));
                                }
                                Collections.shuffle(toShuffle);
                                indexes = new ArrayList<>();
                                targetPositions = new ArrayList<>();
                                for (int i = 0; i < 6; i++) {
                                    indexes.add(toShuffle.get(i).getLeft());
                                    targetPositions.add(toShuffle.get(i).getRight());
                                }
                            }
                            hasShuffled = true;
                        } else {
                            hasShuffled = false;
                        }
                        if (tickTimer == 256) {
                            lastPositions = List.copyOf(targetPositions);
                        }
                        for (int i = 0; i < 6; i++) {
                            color = colors[i] + 0xFF000000;
                            float angle = (MathHelper.PI/3f*i + (timer-256)*Math.min(0.1f, (timer-256)*0.01f)) % (MathHelper.PI*2f);
                            if (timer < 256) {
                                float delta = (timer % 6) / 6f;
                                delta = 1 - (1-delta)*(1-delta);
                                int lerpX = MathHelper.lerp(delta, lastPositions.get(i).x(), targetPositions.get(i).x());
                                int lerpY = MathHelper.lerp(delta, lastPositions.get(i).y(), targetPositions.get(i).y());
                                drawSoul(context, color, color, width / 2 + lerpX, height / 2 + lerpY, 0);
                            } else if (timer < 340) {
                                color = colors[indexes.get(i)] + 0xFF000000;
                                int posX = (int)(100*MathHelper.sin(angle));
                                int posY = (int)(100*MathHelper.cos(angle));
                                drawSoul(context, color, color, width / 2 + posX, height / 2 + posY, 0);
                                if (timer > 280) {
                                    context.drawCenteredTextWithShadow(client.textRenderer, String.valueOf(MathHelper.floor((340-tickTimer)/20f)), width / 2, height / 2 - 200, 0xFFFFFFFF);
                                }
                            } else {
                                int opacity = (int)MathHelper.clamp((355f-timer)*15f, 0, 255) * 16777216;
                                int posX = (int)(100*MathHelper.sin(angle));
                                int posY = (int)(100*MathHelper.cos(angle));
                                if (i == indexes.get(libmodIndex)) {
                                    opacity = 0xFF000000;
                                    posX = timer >= 355f ? 0 : MathHelper.lerp((timer-340f)/15f, posX, 0);
                                    posY = timer >= 355f ? -100 : MathHelper.lerp((timer-340f)/15f, posY, -100);
                                }
                                if (timer >= 400f) {
                                    opacity = MathHelper.lerp((timer-400f)/30f, 0xFF, 0);
                                }
                                color = colors[indexes.get(i)] + opacity;
                                if (timer >= 370f) {
                                    posY = timer >= 385f ? 0 : MathHelper.lerp((timer-370f)/15f, posY, 0);
                                }
                                drawSoulWithGlow(context, color, color, width / 2 + posX, height / 2 + posY, 0, chosenPower);
                            }
                        }
                        if (timer <= 385f) {
                            int opacity = timer <= 370f ? 0xFF : MathHelper.lerp((timer-370f)/15f, 0xFF, 0);
                            int leftColor = playerSoul.getTrait(0).getColor() + (((opacity << 8) << 8) << 8);
                            int rightColor = playerSoul.getTrait(playerSoul.getTraitCount() - 1).getColor() + (((opacity << 8) << 8) << 8);
                            drawSoulWithGlow(context, leftColor, rightColor, width / 2, height / 2, 0.33f * MathHelper.sin(timer / 30f), playerSoul.isPure() ? 2 : (playerSoul.isStrong() ? 1 : 0));
                        }
                        if (tickTimer == 450) {
                            PacketByteBuf buf = PacketByteBufs.create();
                            buf.writeString(chosenTraits.get(0).getName());
                            buf.writeString("");
                            buf.writeBoolean(chosenPower >= 1);
                            buf.writeBoolean(chosenPower == 2);
                            buf.writeVarInt(1);
                            buf.writeVarInt(0);
                            ClientPlayNetworking.send(SoulForgeNetworking.END_SOUL_RESET, buf);
                            playerSoul.removeTag("resettingSoul");
                        }
                    } else if (rerollType != RerollType.JAR) {
                        if (timer <= 150f) {
                            for (int i = 0; i < 6; i++) {
                                float angle = MathHelper.PI / 3f * i;
                                int opacity = (int) Math.min((timer - 14f) * 5f, 255) * 16777216;
                                float circleRad = 100f;
                                if (timer >= 75f && (!chosenTraits.contains(getTraitFromIndex(i)) && rerollType != RerollType.DT)) {
                                    opacity = (((new Color(0f, 0f, 0f, MathHelper.clamp((100f - timer) / 25f, 0f, 1f)).getAlpha() << 8) << 8) << 8);
                                }
                                if (timer >= 100f) {
                                    if (!chosenTraits.contains(getTraitFromIndex(i)) && rerollType != RerollType.DT) {
                                        opacity = 0;
                                    }
                                    float delta = MathHelper.clamp((timer - 100f) / 25f, 0f, 1f);
                                    circleRad = 50f*MathHelper.cos(delta * MathHelper.PI)+50f;
                                    if (rerollType != RerollType.NORMAL) {
                                        Color colorObj = new Color(colors[i]);
                                        colorObj = new Color(
                                                MathHelper.lerp(delta, colorObj.getRed(), 255),
                                                MathHelper.lerp(delta, colorObj.getGreen(), 255),
                                                MathHelper.lerp(delta, colorObj.getBlue(), 255));
                                        colors[i] = getColorValue(colorObj) & 0x00FFFFFF;
                                    }
                                }
                                color = opacity + colors[i];
                                drawSoul(context, color, color, width / 2 + (int) (circleRad * MathHelper.sin(angle)), height / 2 + (int) (circleRad * MathHelper.cos(angle)), 0);
                            }
                        }
                        if (timer <= 125f) {
                            int opacity = timer <= 100f ? 0xFF : MathHelper.lerp((timer-100f)/25f, 0xFF, 0);
                            int leftColor = playerSoul.getTrait(0).getColor() + opacity * 16777216;
                            int rightColor = playerSoul.getTrait(playerSoul.getTraitCount() - 1).getColor() + opacity * 16777216;
                            drawSoulWithGlow(context, leftColor, rightColor, width / 2, height / 2, 0.33f * MathHelper.sin(timer / 30f), playerSoul.isPure() ? 2 : (playerSoul.isStrong() ? 1 : 0));
                        }
                        if (timer > 125f) {
                            float delta = MathHelper.clamp(((timer-150f)/25f), 0, 1);
                            long opacity = MathHelper.lerp(MathHelper.clamp((timer-(rerollType != RerollType.NORMAL ? 200f : 175f))/25f, 0f, 1f), 0xFF, 0) * 16777216L;
                            opacity = Math.abs(opacity);
                            float powerDelta = MathHelper.clamp(((timer-(rerollType != RerollType.NORMAL ? 175f : 150f))/25f), 0f, 1f);
                            long powerOpacity = MathHelper.lerp(powerDelta, 0xFF, 0) * 16777216L;
                            Color leftColor = new Color(chosenTraits.get(0).getColor());
                            Color rightColor = new Color(chosenTraits.get(chosenTraits.size() - 1).getColor());
                            if (rerollType != RerollType.NORMAL) {
                                leftColor = new Color(
                                        MathHelper.lerp(delta, 255, leftColor.getRed()),
                                        MathHelper.lerp(delta, 255, leftColor.getGreen()),
                                        MathHelper.lerp(delta, 255, leftColor.getBlue())
                                );
                                rightColor = new Color(
                                        MathHelper.lerp(delta, 255, rightColor.getRed()),
                                        MathHelper.lerp(delta, 255, rightColor.getGreen()),
                                        MathHelper.lerp(delta, 255, rightColor.getBlue())
                                );
                            }
                            powerOpacity = Math.min(opacity, Math.abs(powerOpacity * 16777216));
                            drawSoul(context,
                                    (int) ((getColorValue(leftColor) & 0x00FFFFFF) + opacity),
                                    (int) ((getColorValue(rightColor) & 0x00FFFFFF) + opacity),
                                    width / 2, height / 2, 0);
                            drawSoulWithGlow(context,
                                    (int) ((getColorValue(leftColor) & 0x00FFFFFF) + powerOpacity),
                                    (int) ((getColorValue(rightColor) & 0x00FFFFFF) + powerOpacity),
                                    width / 2, height / 2, 0,
                                    chosenPower);
                        }
                        if (tickTimer == (rerollType != RerollType.NORMAL ? 200 : 175)) {
                            PacketByteBuf buf = PacketByteBufs.create();
                            buf.writeString(chosenTraits.get(0).getName());
                            buf.writeString(chosenTraits.size() == 2 ? chosenTraits.get(1).getName() : "");
                            buf.writeBoolean(chosenPower >= 1);
                            buf.writeBoolean(chosenPower == 2);
                            buf.writeVarInt(1);
                            buf.writeVarInt(0);
                            ClientPlayNetworking.send(SoulForgeNetworking.END_SOUL_RESET, buf);
                            playerSoul.removeTag("resettingSoul");
                        }
                    } else {
                        if (tickTimer < 150) {
                            Color leftColor = new Color(Traits.get(SoulJarItem.getTrait1(soulJar)).getColor());
                            Color rightColor = new Color(Traits.get(SoulJarItem.getTrait1(soulJar)).getColor());
                            if (!Objects.equals(SoulJarItem.getTrait2(soulJar), "")) rightColor = new Color(Traits.get(SoulJarItem.getTrait2(soulJar)).getColor());
                            float delta = MathHelper.clamp((Math.abs(90f-timer)/50f), 0, 1);
                            delta = (MathHelper.cos(delta * MathHelper.PI)+1f) / 2f;
                            long opacity = MathHelper.lerp(delta, 0, 0xFF) * 16777216L;
                            delta = MathHelper.clamp(((timer-90f)/25f), 0, 1);
                            delta = (MathHelper.cos(delta * MathHelper.PI)+1f) / 2f;
                            drawSoul(context,
                                    (int) ((getColorValue(leftColor) & 0x00FFFFFF) + opacity),
                                    (int) ((getColorValue(rightColor) & 0x00FFFFFF) + opacity),
                                    width / 2 + (int)((1f-delta) * 150f), height / 2, 0);
                            leftColor = new Color(playerSoul.getTrait(0).getColor());
                            rightColor = new Color(playerSoul.getTrait(playerSoul.getTraitCount() - 1).getColor());
                            drawSoul(context,
                                    (int) ((getColorValue(leftColor) & 0x00FFFFFF) + opacity),
                                    (int) ((getColorValue(rightColor) & 0x00FFFFFF) + opacity),
                                    width / 2 - (int)(delta * 150f), height / 2, 0);
                        }
                        if (tickTimer == 150) {
                            PacketByteBuf buf = PacketByteBufs.create();
                            buf.writeString(SoulJarItem.getTrait1(soulJar));
                            buf.writeString(SoulJarItem.getTrait2(soulJar));
                            buf.writeBoolean(SoulJarItem.getStrong(soulJar));
                            buf.writeBoolean(SoulJarItem.getPure(soulJar));
                            buf.writeVarInt(SoulJarItem.getLv(soulJar));
                            buf.writeVarInt(SoulJarItem.getExp(soulJar));
                            soulJar.decrement(1);
                            ItemStack newJar = new ItemStack(SoulForgeItems.SOUL_JAR);
                            SoulJarItem.setFromPlayer(newJar, client.player);
                            client.player.giveItemStack(newJar);
                            ClientPlayNetworking.send(SoulForgeNetworking.END_SOUL_RESET, buf);
                            playerSoul.removeTag("resettingSoul");
                        }
                    }
                }
            }
        } else {
            if (wasResetting) {
                client.mouse.lockCursor();
            }
        }
        wasResetting = playerSoul.hasTag("resettingSoul");
    }

    private TraitBase getTraitFromIndex(int index) {
        return switch (index) {
            case 0 -> Traits.bravery;
            case 1 -> Traits.justice;
            case 2 -> Traits.kindness;
            case 3 -> Traits.patience;
            case 4 -> Traits.integrity;
            case 5 -> Traits.perseverance;
            default -> Traits.determination;
        };
    }

    private void drawSoul(DrawContext context, int leftColor, int rightColor, int x, int y, float rotation) {
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);
        context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotation(rotation));
        context.fill(-21, -12, -15, 0, leftColor);
        context.fill(-15, -18, -9, 6, leftColor);
        context.fill(-9, -18, -3, 12, leftColor);
        context.fill(-3, -12, 0, 18, leftColor);
        context.fill(0, -12, 3, 18, rightColor);
        context.fill(3, -18, 9, 12, rightColor);
        context.fill(9, -18, 15, 6, rightColor);
        context.fill(15, -12, 21, 0, rightColor);
        context.getMatrices().pop();
    }

    private void drawSoul(DrawContext context, int leftColor, int rightColor, int x, int y, float rotation, float scale) {
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);
        context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotation(rotation));
        context.getMatrices().scale(scale, scale, scale);
        context.fill(-21, -12, -15, 0, leftColor);
        context.fill(-15, -18, -9, 6, leftColor);
        context.fill(-9, -18, -3, 12, leftColor);
        context.fill(-3, -12, 0, 18, leftColor);
        context.fill(0, -12, 3, 18, rightColor);
        context.fill(3, -18, 9, 12, rightColor);
        context.fill(9, -18, 15, 6, rightColor);
        context.fill(15, -12, 21, 0, rightColor);
        context.getMatrices().pop();
    }

    private int getColorValue(Color color) {
        return color.getRGB();
    }

    private void drawSoulWithGlow(DrawContext context, int leftColor, int rightColor, int x, int y, float rotation, int glowLevel) {
        drawSoul(context, leftColor, rightColor, x, y, rotation);
        if (glowLevel >= 1) {
            int leftGlow1 = leftColor & 0x00FFFFFF + Math.abs((int)(((leftColor & 0xFF000000) >> 24) * 0.5f) * 16777216);
            int rightGlow1 = rightColor & 0x00FFFFFF + Math.abs((int)(((rightColor & 0xFF000000) >> 24) * 0.5f) * 16777216);
            drawSoul(context, leftGlow1, rightGlow1, x, y, rotation, 1.2f);
        }
        if (glowLevel >= 2) {
            int leftGlow2 = leftColor & 0x00FFFFFF + Math.abs((int)(((leftColor & 0xFF000000) >> 24) * 0.2f) * 16777216);
            int rightGlow2 = rightColor & 0x00FFFFFF + Math.abs((int)(((rightColor & 0xFF000000) >> 24) * 0.2f) * 16777216);
            drawSoul(context, leftGlow2, rightGlow2, x, y, rotation, 1.3f);
        }
    }

    public enum RerollType {
        NORMAL,
        DUAL,
        DT,
        JAR
    }

    private RerollType getRerollType(SoulComponent playerSoul) {
        List<TraitBase> oldTraits = playerSoul.getTraits();
        if (oldTraits == null) oldTraits = new ArrayList<>(List.of(Traits.bravery, Traits.integrity, Traits.spite));
        Random rnd = new Random();
        int num = rnd.nextInt(100);
        ResetData resetData = playerSoul.getResetData();
        if (num <= resetData.resetsSinceDT && !oldTraits.contains(Traits.determination)) {
            return RerollType.DT;
        } else if (num <= resetData.resetsSinceDT + 5 * resetData.resetsSinceDual) {
            return RerollType.DUAL;
        } else {
            return RerollType.NORMAL;
        }
    }

    private TraitBase getNormalTrait(SoulComponent playerSoul) {
        java.util.List<TraitBase> oldTraits = playerSoul.getTraits();
        if (oldTraits == null) oldTraits = new ArrayList<>(List.of(Traits.bravery, Traits.integrity, Traits.spite));
        TraitBase trait = Traits.randomNormal();
        while (oldTraits.size() == 1 && oldTraits.contains(trait)) {
            trait = Traits.randomNormal();
        }
        return trait;
    }

    private List<TraitBase> getDualTrait(SoulComponent playerSoul) {
        java.util.List<TraitBase> oldTraits = playerSoul.getTraits();
        if (oldTraits == null) oldTraits = new ArrayList<>(List.of(Traits.bravery, Traits.integrity, Traits.spite));
        List<TraitBase> traits = new ArrayList<>();
        traits.add(Traits.randomNormal());
        traits.add(Traits.randomNormal());
        while (traits.get(1) == traits.get(0) && !oldTraits.equals(traits)) {
            traits.set(1, Traits.randomNormal());
        }
        return traits;
    }

    private int getNormalPower(SoulComponent playerSoul) {
        ResetData resetData = playerSoul.getResetData();
        Random rnd = new Random();
        int num = rnd.nextInt(50);
        if (num <= resetData.resetsSincePure) {
            resetData.resetsSincePure = 0;
            resetData.resetsSinceStrong = 0;
            return 2;
        } else if (num <= resetData.resetsSincePure + resetData.resetsSinceStrong * 5) {
            resetData.resetsSinceStrong = 0;
            resetData.resetsSincePure++;
            return 1;
        } else {
            resetData.resetsSinceStrong++;
            resetData.resetsSincePure++;
            return 0;
        }
    }

    private int getDualPower(SoulComponent playerSoul) {
        ResetData resetData = playerSoul.getResetData();
        Random rnd = new Random();
        int num = rnd.nextInt(10);
        if (num <= resetData.resetsSinceStrong) {
            resetData.resetsSinceStrong = 0;
            return 1;
        } else {
            resetData.resetsSinceStrong++;
            return 0;
        }
    }
}

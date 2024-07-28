package com.pulsar.soulforge.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

import java.util.Objects;

public class SpokenTextRenderer {
    private int timer = 0;
    private float speed = 0;
    private int endTime = 0;
    private String text = "";

    public void setText(String text, float speed, int timeToDisappear) {
        this.text = text;
        this.timer = 0;
        this.speed = 5f / speed;
        this.endTime = (int)(text.length()*speed) + timeToDisappear;
    }

    public void setText(String text) {
        this.setText(text, 4, 40);
    }

    public String getText() {
        return text;
    }

    public void clearText() {
        this.text = "";
        this.timer = 0;
        this.speed = 0;
        this.endTime = 0;
    }

    public String toRender() {
        if (Objects.equals(text, "") || this.timer == 0 || this.speed == 0 || this.endTime == 0) return "";
        StringBuilder toRender = new StringBuilder();
        float t = 0;
        for (char c : text.toCharArray()) {
            float getCharacterTimer = this.speed;
            if (c == '.' || c == '?' || c == '!') getCharacterTimer *= 3;
            if (c == ',') getCharacterTimer *= 2;
            if (timer >= t) {
                toRender.append(c);
                t += getCharacterTimer;
            } else {
                return toRender.toString();
            }
        }
        return toRender.toString();
    }

    public void tick() {
        if (!Objects.equals(text, "")) {
            timer++;
            if (this.timer >= this.endTime) {
                clearText();
            }
        }
    }

    public NbtCompound writeNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("text", text);
        nbt.putInt("timer", timer);
        nbt.putFloat("speed", speed);
        nbt.putInt("endTime", endTime);
        return nbt;
    }

    public void readNbt(NbtCompound nbt) {
        text = nbt.getString("text");
        timer = nbt.getInt("timer");
        speed = nbt.getFloat("speed");
        endTime = nbt.getInt("endTime");
    }

    public void writeBuffer(PacketByteBuf buf) {
        buf.writeString(text);
        buf.writeVarInt(timer);
        buf.writeFloat(speed);
        buf.writeVarInt(endTime);
    }

    public void readBuffer(PacketByteBuf buf) {
        text = buf.readString();
        timer = buf.readVarInt();
        speed = buf.readFloat();
        endTime = buf.readVarInt();
    }
}

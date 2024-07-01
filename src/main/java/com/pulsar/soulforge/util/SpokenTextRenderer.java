package com.pulsar.soulforge.util;

import com.pulsar.soulforge.SoulForge;

import java.util.Objects;

public class SpokenTextRenderer {
    private int timer = 0;
    private int speed = 0;
    private int endTime = 0;
    private String text = "";

    public void setText(String text, int speed, int timeToDisappear) {
        this.text = text;
        this.timer = 0;
        this.speed = speed;
        this.endTime = text.length()*speed + timeToDisappear;
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
        int t = 0;
        for (char c : text.toCharArray()) {
            int getCharacterTimer = this.speed;
            if (c == '.' || c == '?' || c == '!') getCharacterTimer *= 3;
            if (c == ',') getCharacterTimer *= 2;
            if (t >= timer + getCharacterTimer) {
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
}

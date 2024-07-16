package com.pulsar.soulforge.client.ui;

import com.pulsar.soulforge.sounds.SoulForgeSounds;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.random.Random;

public class ANOTHERHIM extends TitleScreen {
    private SoundInstance ANOTHERHIMSOUND = new ANOTHERHIMSOUND();

    @Override
    protected void init() {
        this.client.setOverlay(null);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.getMatrices().scale(4f, 4f, 1f);
        context.drawTextWithShadow(this.textRenderer, "ANOTHER HIM", this.width / 8, this.height / 8, 0x010101);
        context.getMatrices().scale(0.25f, 0.25f, 1f);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public void tick() {
        boolean ANOTHERHIM = this.client.getSoundManager().isPlaying(ANOTHERHIMSOUND);
        if (!ANOTHERHIM) {
            this.client.getSoundManager().play(ANOTHERHIMSOUND);
        }
    }

    private class ANOTHERHIMSOUND extends AbstractSoundInstance {
        protected ANOTHERHIMSOUND() {
            super(SoulForgeSounds.ANOTHERHIM_EVENT, SoundCategory.MASTER, Random.create());
            this.repeat = true;
        }
    }
}

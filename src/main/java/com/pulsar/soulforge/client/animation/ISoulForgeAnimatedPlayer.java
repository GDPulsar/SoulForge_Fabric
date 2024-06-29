package com.pulsar.soulforge.client.animation;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;

public interface ISoulForgeAnimatedPlayer {
    /**
     * Use your mod ID in the method name to avoid collisions with other mods
     * @return Mod animation container
     */
    ModifierLayer<IAnimation> soulforge_getModAnimation();
}

package com.pulsar.soulforge.components;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;

public interface WorldConfigComponent extends AutoSyncedComponent {
    float getExpMultiplier();
    void setExpMultiplier(float multiplier);
}

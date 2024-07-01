package com.pulsar.soulforge.components;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public interface WorldBaseComponent extends AutoSyncedComponent {
    float getExpMultiplier();
    void setExpMultiplier(float multiplier);
    List<BlockPos> getActiveCreativeZones();

    boolean isInRangeOfActiveCreativeZone(BlockPos pos);

    void addActiveCreativeZone(BlockPos pos);
    void removeActiveCreativeZone(BlockPos pos);
}

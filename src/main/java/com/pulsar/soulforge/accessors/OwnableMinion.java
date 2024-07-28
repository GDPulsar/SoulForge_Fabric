package com.pulsar.soulforge.accessors;

import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public interface OwnableMinion {
    UUID getOwnerUUID();
    void setOwnerUUID(UUID ownerUUID);

    Vec3d getTargetPos();
    void setTargetPos(Vec3d targetPos);

    UUID getTargetUUID();
    void setTargetUUID(UUID target);
}

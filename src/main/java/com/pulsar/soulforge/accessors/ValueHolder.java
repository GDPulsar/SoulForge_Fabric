package com.pulsar.soulforge.accessors;

import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public interface ValueHolder {
    float getFloat(String key);
    void setFloat(String key, float value);
    void removeFloat(String key);
    boolean hasFloat(String key);
    int getInt(String key);
    void setInt(String key, int value);
    void removeInt(String key);
    boolean hasInt(String key);
    boolean getBool(String key);
    void setBool(String key, boolean value);
    void removeBool(String key);
    boolean hasBool(String key);
    Vec3d getVec(String key);
    void setVec(String key, Vec3d value);
    void removeVec(String key);
    boolean hasVec(String key);
    UUID getUUID(String key);
    void setUUID(String key, UUID value);
    void removeUUID(String key);
    boolean hasUUID(String key);
}
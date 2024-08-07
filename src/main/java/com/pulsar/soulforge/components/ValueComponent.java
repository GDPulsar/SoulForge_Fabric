package com.pulsar.soulforge.components;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.util.Utils;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ValueComponent implements AutoSyncedComponent {
    HashMap<String, Float> floatVals;
    HashMap<String, Integer> intVals;
    HashMap<String, Boolean> boolVals;
    HashMap<String, Vec3d> vecVals;
    HashMap<String, UUID> uuidVals;

    final LivingEntity entity;

    public ValueComponent(LivingEntity living) {
        this.entity = living;
        floatVals = new HashMap<>();
        intVals = new HashMap<>();
        boolVals = new HashMap<>();
        vecVals = new HashMap<>();
        uuidVals = new HashMap<>();
    }

    public float getFloat(String key) {
        return floatVals.get(key);
    }
    public void setFloat(String key, float value) {
        floatVals.put(key, value);
        sync();
    }
    public void removeFloat(String key) {
        floatVals.remove(key);
        sync();
    }
    public boolean hasFloat(String key) {
        return floatVals.containsKey(key);
    }
    public int getInt(String key) {
        return intVals.get(key);
    }
    public void setInt(String key, int value) {
        intVals.put(key, value);
        sync();
    }
    public void removeInt(String key) {
        intVals.remove(key);
        sync();
    }
    public boolean hasInt(String key) {
        return intVals.containsKey(key);
    }
    public boolean getBool(String key) {
        if (!boolVals.containsKey(key)) return false;
        return boolVals.get(key);
    }
    public void setBool(String key, boolean value) {
        boolVals.put(key, value);
        sync();
    }
    public void removeBool(String key) {
        boolVals.remove(key);
        sync();
    }
    public boolean hasBool(String key) {
        return boolVals.containsKey(key);
    }
    public Vec3d getVec(String key) {
        return vecVals.get(key);
    }
    public void setVec(String key, Vec3d value) {
        vecVals.put(key, value);
        sync();
    }
    public void removeVec(String key) {
        vecVals.remove(key);
        sync();
    }
    public boolean hasVec(String key) {
        return vecVals.containsKey(key);
    }
    public UUID getUUID(String key) {
        return uuidVals.get(key);
    }
    public void setUUID(String key, UUID value) {
        uuidVals.put(key, value);
        sync();
    }
    public void removeUUID(String key) {
        uuidVals.remove(key);
        sync();
    }
    public boolean hasUUID(String key) {
        return uuidVals.containsKey(key);
    }

    void sync() {
        EntityInitializer.VALUES.sync(entity);
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
        SoulForge.LOGGER.info("reading: {}, entity: {}", nbt, entity);

        NbtCompound floatNbt = nbt.getCompound("floatVals");
        floatVals = new HashMap<>();
        for (String key : floatNbt.getKeys()) {
            floatVals.put(key, floatNbt.getFloat(key));
        }

        NbtCompound intNbt = nbt.getCompound("intVals");
        intVals = new HashMap<>();
        for (String key : intNbt.getKeys()) {
            intVals.put(key, intNbt.getInt(key));
        }

        NbtCompound boolNbt = nbt.getCompound("boolVals");
        boolVals = new HashMap<>();
        for (String key : boolNbt.getKeys()) {
            boolVals.put(key, boolNbt.getBoolean(key));
        }

        NbtCompound vecNbt = nbt.getCompound("vecVals");
        vecVals = new HashMap<>();
        for (String key : vecNbt.getKeys()) {
            vecVals.put(key, Utils.nbtToVector(vecNbt.getList(key, NbtElement.DOUBLE_TYPE)));
        }

        NbtCompound uuidNbt = nbt.getCompound("uuidVals");
        uuidVals = new HashMap<>();
        for (String key : uuidNbt.getKeys()) {
            uuidVals.put(key, uuidNbt.getUuid(key));
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
        NbtCompound floatNbt = new NbtCompound();
        for (Map.Entry<String, Float> entry : floatVals.entrySet()) {
            floatNbt.putFloat(entry.getKey(), entry.getValue());
        }
        nbt.put("floatVals", floatNbt);

        NbtCompound intNbt = new NbtCompound();
        for (Map.Entry<String, Integer> entry : intVals.entrySet()) {
            intNbt.putInt(entry.getKey(), entry.getValue());
        }
        nbt.put("intVals", intNbt);

        NbtCompound boolNbt = new NbtCompound();
        for (Map.Entry<String, Boolean> entry : boolVals.entrySet()) {
            boolNbt.putBoolean(entry.getKey(), entry.getValue());
        }
        nbt.put("boolVals", boolNbt);

        NbtCompound vecNbt = new NbtCompound();
        for (Map.Entry<String, Vec3d> entry : vecVals.entrySet()) {
            vecNbt.put(entry.getKey(), Utils.vectorToNbt(entry.getValue()));
        }
        nbt.put("vecVals", vecNbt);

        NbtCompound uuidNbt = new NbtCompound();
        for (Map.Entry<String, UUID> entry : uuidVals.entrySet()) {
            uuidNbt.putUuid(entry.getKey(), entry.getValue());
        }
        nbt.put("uuidVals", uuidNbt);

        SoulForge.LOGGER.info("writing: {}, entity: {}", nbt, entity);
    }
}

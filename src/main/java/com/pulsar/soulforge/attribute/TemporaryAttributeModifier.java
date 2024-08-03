package com.pulsar.soulforge.attribute;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Supplier;

public class TemporaryAttributeModifier extends EntityAttributeModifier {
    private int duration;

    public TemporaryAttributeModifier(String name, double value, Operation operation, int duration) {
        this(MathHelper.randomUuid(Random.createLocal()), () -> name, value, operation, duration);
    }

    public TemporaryAttributeModifier(UUID uuid, String name, double value, Operation operation, int duration) {
        this(uuid, () -> name, value, operation, duration);
    }

    public TemporaryAttributeModifier(UUID uuid, Supplier<String> nameGetter, double value, Operation operation, int duration) {
        super(uuid, nameGetter, value, operation);
        this.duration = duration;
    }

    public int getDuration() { return this.duration; }

    public boolean tickDuration() {
        this.duration--;
        return this.duration <= 0;
    }

    @Override
    public NbtCompound toNbt() {
        NbtCompound nbt = super.toNbt();
        nbt.putInt("Duration", duration);
        return nbt;
    }

    @Nullable
    public static TemporaryAttributeModifier fromNbt(NbtCompound nbt) {
        try {
            UUID uuid = nbt.getUuid("UUID");
            Operation operation = EntityAttributeModifier.Operation.fromId(nbt.getInt("Operation"));
            return new TemporaryAttributeModifier(uuid, nbt.getString("Name"), nbt.getDouble("Amount"), operation, nbt.getInt("Duration"));
        } catch (Exception var3) {
            return null;
        }
    }
}

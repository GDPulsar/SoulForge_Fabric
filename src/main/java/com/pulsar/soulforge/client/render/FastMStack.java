package com.pulsar.soulforge.client.render;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * A reimplementation of {@link MatrixStack}, containing a few optimizations.
 * Shamelessly stolen from https://github.com/0x3C50/Renderer/blob/master/src/main/java/me/x150/renderer/util/FastMStack.java#L17
 */
public class FastMStack extends MatrixStack {
    private static final MethodHandle MATRIXSTACK_ENTRY_CTOR;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(MatrixStack.Entry.class,
                    MethodHandles.lookup());
            MATRIXSTACK_ENTRY_CTOR = lookup.findConstructor(MatrixStack.Entry.class,
                    MethodType.methodType(void.class, Matrix4f.class, Matrix3f.class));
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private final ObjectArrayList<com.pulsar.soulforge.client.render.FastMStack.Entry> fEntries = new ObjectArrayList<>(8);
    private com.pulsar.soulforge.client.render.FastMStack.Entry top;
    public FastMStack() {
        fEntries.add(top = new com.pulsar.soulforge.client.render.FastMStack.Entry(new Matrix4f(), new Matrix3f()));
    }

    @Override
    public void translate(float x, float y, float z) {
        top.positionMatrix.translate(x, y, z);
    }

    @Override
    public void scale(float x, float y, float z) {
        top.positionMatrix.scale(x, y, z);
        if (x == y && y == z) {
            // normal matrix is normalized, if all elements are uniform, we can just scale it based on the sign of the
            // elements. (positive / zero = no effect, negative = flip it)
            if (x != 0) {
                top.normalMatrix.scale(Math.signum(x));
            }
            return;
        }
        float inverseX = 1.0f / x;
        float inverseY = 1.0f / y;
        float inverseZ = 1.0f / z;
        // cbrt is faster than the pure java approximation these days
        float scalar = (float) (1f / Math.cbrt(inverseX * inverseY * inverseZ));
        top.normalMatrix.scale(scalar * inverseX, scalar * inverseY, scalar * inverseZ);
    }

    @Override
    public void multiply(Quaternionf quaternion) {
        top.positionMatrix.rotate(quaternion);
        top.normalMatrix.rotate(quaternion);
    }

    @Override
    public void multiply(Quaternionf quaternion, float originX, float originY, float originZ) {
        top.positionMatrix.rotateAround(quaternion, originX, originY, originZ);
        top.normalMatrix.rotate(quaternion);
    }

    @Override
    public void multiplyPositionMatrix(Matrix4f matrix) {
        top.positionMatrix.mul(matrix);
    }

    @Override
    public void push() {
        fEntries.add(top = new com.pulsar.soulforge.client.render.FastMStack.Entry(new Matrix4f(top.positionMatrix), new Matrix3f(top.normalMatrix)));
    }

    @Override
    public void pop() {
        if (fEntries.size() == 1) {
            throw new IllegalStateException("Trying to pop an empty stack");
        }
        fEntries.pop();
        top = fEntries.top();
    }

    @Override
    public MatrixStack.Entry peek() {
        // ugly hack but needed to interop with the original stack api
        try {
            return (MatrixStack.Entry) MATRIXSTACK_ENTRY_CTOR.invoke(top.positionMatrix, top.normalMatrix);
        } catch (Throwable e) {
            return null;
        }
    }

    @Override
    public boolean isEmpty() {
        return fEntries.size() == 1;
    }

    @Override
    public void loadIdentity() {
        top.positionMatrix.identity();
        top.normalMatrix.identity();
    }

    record Entry(Matrix4f positionMatrix, Matrix3f normalMatrix) {
    }
}

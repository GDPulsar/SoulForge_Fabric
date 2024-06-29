package com.pulsar.soulforge.client.render;

import com.pulsar.soulforge.SoulForge;
import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class SphereRenderer {
    private static final int SPHERE_SEGMENTS = 16;
    private static final int SPHERE_RINGS = 8;

    public static void renderSphere(Matrix4f model, VertexConsumer vertexConsumer, float size) {
        float phiStep = (float) (Math.PI / SPHERE_RINGS);
        float thetaStep = (float) (2.0 * Math.PI / SPHERE_SEGMENTS);

        for (int i = 0; i < SPHERE_RINGS; i++) {
            float phi1 = i * phiStep;
            float phi2 = (i + 1) * phiStep;

            for (int j = 0; j < SPHERE_SEGMENTS; j++) {
                float theta1 = j * thetaStep;
                float theta2 = (j + 1) * thetaStep;

                // Calculate the vertices for the current quad
                Vector3f p1 = calculateSpherePoint(phi1, theta1, size);
                Vector3f p2 = calculateSpherePoint(phi1, theta2, size);
                Vector3f p3 = calculateSpherePoint(phi2, theta2, size);
                Vector3f p4 = calculateSpherePoint(phi2, theta1, size);

                // Render the quad
                renderQuad(model, vertexConsumer, p1, p2, p3, p4);
            }
        }
    }

    private static Vector3f calculateSpherePoint(float phi, float theta, float mul) {
        float x = (float) (Math.sin(phi) * Math.cos(theta));
        float y = (float) Math.cos(phi);
        float z = (float) (Math.sin(phi) * Math.sin(theta));
        return new Vector3f(x, y, z).normalize().mul(mul);
    }

    private static void renderQuad(
            Matrix4f model,
            VertexConsumer vertices,
            Vector3f p1,
            Vector3f p2,
            Vector3f p3,
            Vector3f p4
    ) {
        vertices.vertex(model, p1.x, p1.y, p1.z).color(0.2f, 0.9f, 0.2f, 0.6f).texture(0, 0).overlay(15).light(255).normal(0, 1, 0).next();
        vertices.vertex(model, p2.x, p2.y, p2.z).color(0.2f, 0.9f, 0.2f, 0.6f).texture(0, 0).overlay(15).light(255).normal(0, 1, 0).next();
        vertices.vertex(model, p3.x, p3.y, p3.z).color(0.2f, 0.9f, 0.2f, 0.6f).texture(0, 0).overlay(15).light(255).normal(0, 1, 0).next();
        vertices.vertex(model, p4.x, p4.y, p4.z).color(0.2f, 0.9f, 0.2f, 0.6f).texture(0, 0).overlay(15).light(255).normal(0, 1, 0).next();
    }
}

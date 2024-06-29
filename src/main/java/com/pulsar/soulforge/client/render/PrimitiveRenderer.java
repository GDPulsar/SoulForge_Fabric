package com.pulsar.soulforge.client.render;

import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.awt.*;

public class PrimitiveRenderer {
    public static void renderCube(Matrix4f model, VertexConsumer vertexConsumer, Vector3f min, Vector3f max, Color color) {
        vertexConsumer.fixedColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        Vector3f xyz = new Vector3f(min.x, min.y, min.z);
        Vector3f Xyz = new Vector3f(max.x, min.y, min.z);
        Vector3f xYz = new Vector3f(min.x, max.y, min.z);
        Vector3f xyZ = new Vector3f(min.x, min.y, max.z);
        Vector3f XYz = new Vector3f(max.x, max.y, min.z);
        Vector3f XyZ = new Vector3f(max.x, min.y, max.z);
        Vector3f xYZ = new Vector3f(min.x, max.y, max.z);
        Vector3f XYZ = new Vector3f(max.x, max.y, max.z);
        renderQuad(model, vertexConsumer, xyz, xYz, XYz, Xyz);
        renderQuad(model, vertexConsumer, xyz, Xyz, XyZ, xyZ);
        renderQuad(model, vertexConsumer, Xyz, XYz, XYZ, XyZ);
        renderQuad(model, vertexConsumer, xYz, xYZ, XYZ, XYz);
        renderQuad(model, vertexConsumer, xYZ, xyZ, xyz, xYz);
        renderQuad(model, vertexConsumer, XYZ, xYZ, xyZ, XyZ);
        vertexConsumer.unfixColor();
    }

    private static void renderQuad(
            Matrix4f model,
            VertexConsumer vertices,
            Vector3f p1,
            Vector3f p2,
            Vector3f p3,
            Vector3f p4
    ) {
        vertices.vertex(model, p1.x, p1.y, p1.z).texture(0, 0).overlay(0).light(255).normal(0, 1, 0).next();
        vertices.vertex(model, p2.x, p2.y, p2.z).texture(0, 0).overlay(0).light(255).normal(0, 1, 0).next();
        vertices.vertex(model, p3.x, p3.y, p3.z).texture(0, 0).overlay(0).light(255).normal(0, 1, 0).next();
        vertices.vertex(model, p4.x, p4.y, p4.z).texture(0, 0).overlay(0).light(255).normal(0, 1, 0).next();
    }

    private static final int CYLINDER_SEGMENTS = 16;

    public static void renderCylinder(Matrix4f model, VertexConsumer vertexConsumer, Vector3f a, Vector3f b, float radius, Color color, int overlay, int light) {
        float thetaStep = (float) (2.0 * Math.PI / CYLINDER_SEGMENTS);

        Vector3f direction = new Vector3f(b.x - a.x, b.y - a.y, b.z - a.z).normalize();
        Vector3f perp1 = new Vector3f(direction.z, direction.z, -(direction.x + direction.y));
        Vector3f perp2 = new Vector3f(
                direction.y * perp1.z - direction.z * perp1.y,
                direction.z * perp1.x - direction.x * perp1.z,
                direction.x * perp1.y - direction.y * perp1.x).normalize();
        vertexConsumer.fixedColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        for (int j = 0; j < CYLINDER_SEGMENTS; j++) {
            float theta1 = j * thetaStep;
            float theta2 = (j + 1) * thetaStep;

            // Calculate the vertices for the current quad
            Vector3f p1 = calculateCylinderPoint(theta1, perp1, perp2, radius).add(a);
            Vector3f p2 = calculateCylinderPoint(theta2, perp1, perp2, radius).add(a);
            Vector3f p3 = calculateCylinderPoint(theta2, perp1, perp2, radius).add(b);
            Vector3f p4 = calculateCylinderPoint(theta1, perp1, perp2, radius).add(b);

            // Render the quad
            renderQuad(model, vertexConsumer, p1, p2, p3, p4, overlay, light);
            renderTri(model, vertexConsumer, p2, p1, a, overlay, light);
            renderTri(model, vertexConsumer, p4, p3, b, overlay, light);
        }
        vertexConsumer.unfixColor();
    }

    private static Vector3f calculateCylinderPoint(float theta, Vector3f perp1, Vector3f perp2, float radius) {
        return new Vector3f(
                (float) (radius*(Math.cos(theta) * perp1.x + Math.sin(theta) * perp2.x)),
                (float) (radius*(Math.cos(theta) * perp1.y + Math.sin(theta) * perp2.y)),
                (float) (radius*(Math.cos(theta) * perp1.z + Math.sin(theta) * perp2.z))
        );
    }

    private static void renderQuad(
            Matrix4f model,
            VertexConsumer vertices,
            Vector3f p1,
            Vector3f p2,
            Vector3f p3,
            Vector3f p4,
            int overlay,
            int light
    ) {
        vertices.vertex(model, p1.x, p1.y, p1.z).texture(0, 0).overlay(overlay).light(light).normal(0, 1, 0).next();
        vertices.vertex(model, p2.x, p2.y, p2.z).texture(0, 0).overlay(overlay).light(light).normal(0, 1, 0).next();
        vertices.vertex(model, p3.x, p3.y, p3.z).texture(0, 0).overlay(overlay).light(light).normal(0, 1, 0).next();
        vertices.vertex(model, p4.x, p4.y, p4.z).texture(0, 0).overlay(overlay).light(light).normal(0, 1, 0).next();
    }

    private static void renderTri(
            Matrix4f model,
            VertexConsumer vertices,
            Vector3f p1,
            Vector3f p2,
            Vector3f p3,
            int overlay,
            int light
    ) {
        vertices.vertex(model, p1.x, p1.y, p1.z).texture(0, 0).overlay(overlay).light(light).normal(0, 1, 0).next();
        vertices.vertex(model, p2.x, p2.y, p2.z).texture(0, 0).overlay(overlay).light(light).normal(0, 1, 0).next();
        vertices.vertex(model, p3.x, p3.y, p3.z).texture(0, 0).overlay(overlay).light(light).normal(0, 1, 0).next();
        vertices.vertex(model, p3.x, p3.y, p3.z).texture(0, 0).overlay(overlay).light(light).normal(0, 1, 0).next();
    }

    public static void renderCylinder(VertexConsumer vertexConsumer, Vector3f a, Vector3f b, float radius, Color color, int overlay, int light) {
        float thetaStep = (float) (2.0 * Math.PI / CYLINDER_SEGMENTS);

        Vector3f direction = new Vector3f(b.x - a.x, b.y - a.y, b.z - a.z).normalize();
        Vector3f perp1 = new Vector3f(direction.z, direction.z, -(direction.x + direction.y));
        Vector3f perp2 = new Vector3f(
                direction.y * perp1.z - direction.z * perp1.y,
                direction.z * perp1.x - direction.x * perp1.z,
                direction.x * perp1.y - direction.y * perp1.x).normalize();
        vertexConsumer.fixedColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        for (int j = 0; j < CYLINDER_SEGMENTS; j++) {
            float theta1 = j * thetaStep;
            float theta2 = (j + 1) * thetaStep;

            // Calculate the vertices for the current quad
            Vector3f p1 = calculateCylinderPoint(theta1, perp1, perp2, radius).add(a);
            Vector3f p2 = calculateCylinderPoint(theta2, perp1, perp2, radius).add(a);
            Vector3f p3 = calculateCylinderPoint(theta2, perp1, perp2, radius).add(b);
            Vector3f p4 = calculateCylinderPoint(theta1, perp1, perp2, radius).add(b);

            // Render the quad
            renderQuad(vertexConsumer, p1, p2, p3, p4, overlay, light);
            renderTri(vertexConsumer, p2, p1, a, overlay, light);
            renderTri(vertexConsumer, p4, p3, b, overlay, light);
        }
        vertexConsumer.unfixColor();
    }

    public static void renderCylinder(VertexConsumer vertexConsumer, Vector3f a, Vector3f b, float radiusA, float radiusB, Color color) {
        float thetaStep = (float) (2.0 * Math.PI / CYLINDER_SEGMENTS);

        Vector3f direction = new Vector3f(b.x - a.x, b.y - a.y, b.z - a.z).normalize();
        Vector3f perp1 = new Vector3f(direction.z, direction.z, -(direction.x + direction.y));
        Vector3f perp2 = new Vector3f(
                direction.y * perp1.z - direction.z * perp1.y,
                direction.z * perp1.x - direction.x * perp1.z,
                direction.x * perp1.y - direction.y * perp1.x).normalize();
        vertexConsumer.fixedColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        for (int j = 0; j < CYLINDER_SEGMENTS; j++) {
            float theta1 = j * thetaStep;
            float theta2 = (j + 1) * thetaStep;

            // Calculate the vertices for the current quad
            Vector3f p1 = calculateCylinderPoint(theta1, perp1, perp2, radiusA).add(a);
            Vector3f p2 = calculateCylinderPoint(theta2, perp1, perp2, radiusA).add(a);
            Vector3f p3 = calculateCylinderPoint(theta2, perp1, perp2, radiusB).add(b);
            Vector3f p4 = calculateCylinderPoint(theta1, perp1, perp2, radiusB).add(b);

            // Render the quad
            renderQuad(vertexConsumer, p1, p2, p3, p4, 0, 255);
            renderTri(vertexConsumer, p2, p1, a, 0, 255);
            renderTri(vertexConsumer, p4, p3, b, 0, 255);
        }
        vertexConsumer.unfixColor();
    }

    private static void renderQuad(
            VertexConsumer vertices,
            Vector3f p1,
            Vector3f p2,
            Vector3f p3,
            Vector3f p4,
            int overlay,
            int light
    ) {
        vertices.vertex(p1.x, p1.y, p1.z).texture(0, 0).overlay(overlay).light(light).normal(0, 1, 0).next();
        vertices.vertex(p2.x, p2.y, p2.z).texture(0, 0).overlay(overlay).light(light).normal(0, 1, 0).next();
        vertices.vertex(p3.x, p3.y, p3.z).texture(0, 0).overlay(overlay).light(light).normal(0, 1, 0).next();
        vertices.vertex(p4.x, p4.y, p4.z).texture(0, 0).overlay(overlay).light(light).normal(0, 1, 0).next();
    }

    private static void renderTri(
            VertexConsumer vertices,
            Vector3f p1,
            Vector3f p2,
            Vector3f p3,
            int overlay,
            int light
    ) {
        vertices.vertex(p1.x, p1.y, p1.z).texture(0, 0).overlay(overlay).light(light).normal(0, 1, 0).next();
        vertices.vertex(p2.x, p2.y, p2.z).texture(0, 0).overlay(overlay).light(light).normal(0, 1, 0).next();
        vertices.vertex(p3.x, p3.y, p3.z).texture(0, 0).overlay(overlay).light(light).normal(0, 1, 0).next();
        vertices.vertex(p3.x, p3.y, p3.z).texture(0, 0).overlay(overlay).light(light).normal(0, 1, 0).next();
    }

    public static void renderCylinder(VertexConsumer vertexConsumer, Vector3f a, Vector3f b, float radius, Color color) {
        float thetaStep = (float) (2.0 * Math.PI / CYLINDER_SEGMENTS);

        Vector3f direction = new Vector3f(b.x - a.x, b.y - a.y, b.z - a.z).normalize();
        Vector3f perp1 = new Vector3f(direction.z, direction.z, -(direction.x + direction.y));
        Vector3f perp2 = new Vector3f(
                direction.y * perp1.z - direction.z * perp1.y,
                direction.z * perp1.x - direction.x * perp1.z,
                direction.x * perp1.y - direction.y * perp1.x).normalize();
        vertexConsumer.fixedColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        for (int j = 0; j < CYLINDER_SEGMENTS; j++) {
            float theta1 = j * thetaStep;
            float theta2 = (j + 1) * thetaStep;

            // Calculate the vertices for the current quad
            Vector3f p1 = calculateCylinderPoint(theta1, perp1, perp2, radius).add(a);
            Vector3f p2 = calculateCylinderPoint(theta2, perp1, perp2, radius).add(a);
            Vector3f p3 = calculateCylinderPoint(theta2, perp1, perp2, radius).add(b);
            Vector3f p4 = calculateCylinderPoint(theta1, perp1, perp2, radius).add(b);

            // Render the quad
            renderQuad(vertexConsumer, p1, p2, p3, p4, 0, 255);
            renderTri(vertexConsumer, p2, p1, a, 0, 255);
            renderTri(vertexConsumer, p4, p3, b, 0, 255);
        }
        vertexConsumer.unfixColor();
    }

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
}

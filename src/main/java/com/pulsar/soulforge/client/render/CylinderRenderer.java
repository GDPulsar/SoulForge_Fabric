package com.pulsar.soulforge.client.render;

import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.awt.*;

public class CylinderRenderer {
    private static final int CYLINDER_SEGMENTS = 16;

    public CylinderRenderer() {
    }

    public static void renderCylinder(Matrix4f model, VertexConsumer vertexConsumer, Vector3f a, Vector3f b, float radius, Color color, int overlay, int light, boolean dualFace) {
        float thetaStep = 0.3926991F;
        Vector3f direction = (new Vector3f(b.x - a.x, b.y - a.y, b.z - a.z)).normalize();
        Vector3f perp1 = (new Vector3f(direction.z, direction.z, -(direction.x + direction.y))).normalize();
        Vector3f perp2 = (new Vector3f(direction.y * perp1.z - direction.z * perp1.y, direction.z * perp1.x - direction.x * perp1.z, direction.x * perp1.y - direction.y * perp1.x)).normalize();
        vertexConsumer.fixedColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());

        for(int j = 0; j < 16; ++j) {
            float theta1 = (float)j * thetaStep;
            float theta2 = (float)(j + 1) * thetaStep;
            Vector3f p1 = calculateCylinderPoint(theta1, perp1, perp2, radius).add(a);
            Vector3f p2 = calculateCylinderPoint(theta2, perp1, perp2, radius).add(a);
            Vector3f p3 = calculateCylinderPoint(theta2, perp1, perp2, radius).add(b);
            Vector3f p4 = calculateCylinderPoint(theta1, perp1, perp2, radius).add(b);
            renderQuad(model, vertexConsumer, p1, p2, p3, p4, overlay, light);
            renderTri(model, vertexConsumer, p2, p1, a, overlay, light);
            renderTri(model, vertexConsumer, p4, p3, b, overlay, light);
            if (dualFace) {
                renderQuad(model, vertexConsumer, p4, p3, p2, p1, overlay, light);
                renderTri(model, vertexConsumer, a, p1, p2, overlay, light);
                renderTri(model, vertexConsumer, b, p3, p4, overlay, light);
            }
        }

        vertexConsumer.unfixColor();
    }

    public static void renderCylinder(Matrix4f model, VertexConsumer vertexConsumer, Vector3f a, Vector3f b, float startRad, float endRad, Color color, int overlay, int light) {
        float thetaStep = 0.3926991F;
        Vector3f direction = (new Vector3f(b.x - a.x, b.y - a.y, b.z - a.z)).normalize();
        Vector3f perp1 = (new Vector3f(direction.z, direction.z, -(direction.x + direction.y))).normalize();
        Vector3f perp2 = (new Vector3f(direction.y * perp1.z - direction.z * perp1.y, direction.z * perp1.x - direction.x * perp1.z, direction.x * perp1.y - direction.y * perp1.x)).normalize();
        vertexConsumer.fixedColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());

        for(int j = 0; j < 16; ++j) {
            float theta1 = (float)j * thetaStep;
            float theta2 = (float)(j + 1) * thetaStep;
            Vector3f p1 = calculateCylinderPoint(theta1, perp1, perp2, startRad).add(a);
            Vector3f p2 = calculateCylinderPoint(theta2, perp1, perp2, startRad).add(a);
            Vector3f p3 = calculateCylinderPoint(theta2, perp1, perp2, endRad).add(b);
            Vector3f p4 = calculateCylinderPoint(theta1, perp1, perp2, endRad).add(b);
            renderQuad(model, vertexConsumer, p1, p2, p3, p4, overlay, light);
            renderTri(model, vertexConsumer, p2, p1, a, overlay, light);
            renderTri(model, vertexConsumer, p4, p3, b, overlay, light);
        }

        vertexConsumer.unfixColor();
    }

    private static Vector3f calculateCylinderPoint(float theta, Vector3f perp1, Vector3f perp2, float radius) {
        return new Vector3f((float)((double)radius * (Math.cos((double)theta) * (double)perp1.x + Math.sin((double)theta) * (double)perp2.x)), (float)((double)radius * (Math.cos((double)theta) * (double)perp1.y + Math.sin((double)theta) * (double)perp2.y)), (float)((double)radius * (Math.cos((double)theta) * (double)perp1.z + Math.sin((double)theta) * (double)perp2.z)));
    }

    private static void renderQuad(Matrix4f model, VertexConsumer vertices, Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4, int overlay, int light) {
        vertices.vertex(model, p1.x, p1.y, p1.z).texture(0.0F, 0.0F).overlay(overlay).light(light).normal(0.0F, 1.0F, 0.0F).next();
        vertices.vertex(model, p2.x, p2.y, p2.z).texture(0.0F, 0.0F).overlay(overlay).light(light).normal(0.0F, 1.0F, 0.0F).next();
        vertices.vertex(model, p3.x, p3.y, p3.z).texture(0.0F, 0.0F).overlay(overlay).light(light).normal(0.0F, 1.0F, 0.0F).next();
        vertices.vertex(model, p4.x, p4.y, p4.z).texture(0.0F, 0.0F).overlay(overlay).light(light).normal(0.0F, 1.0F, 0.0F).next();
    }

    private static void renderTri(Matrix4f model, VertexConsumer vertices, Vector3f p1, Vector3f p2, Vector3f p3, int overlay, int light) {
        vertices.vertex(model, p1.x, p1.y, p1.z).texture(0.0F, 0.0F).overlay(overlay).light(light).normal(0.0F, 1.0F, 0.0F).next();
        vertices.vertex(model, p2.x, p2.y, p2.z).texture(0.0F, 0.0F).overlay(overlay).light(light).normal(0.0F, 1.0F, 0.0F).next();
        vertices.vertex(model, p3.x, p3.y, p3.z).texture(0.0F, 0.0F).overlay(overlay).light(light).normal(0.0F, 1.0F, 0.0F).next();
        vertices.vertex(model, p3.x, p3.y, p3.z).texture(0.0F, 0.0F).overlay(overlay).light(light).normal(0.0F, 1.0F, 0.0F).next();
    }

    public static void renderCylinder(VertexConsumer vertexConsumer, Vector3f a, Vector3f b, float radius, Color color, int overlay, int light) {
        float thetaStep = 0.3926991F;
        Vector3f direction = (new Vector3f(b.x - a.x, b.y - a.y, b.z - a.z)).normalize();
        Vector3f perp1 = (new Vector3f(direction.z, direction.z, -(direction.x + direction.y))).normalize();
        Vector3f perp2 = (new Vector3f(direction.y * perp1.z - direction.z * perp1.y, direction.z * perp1.x - direction.x * perp1.z, direction.x * perp1.y - direction.y * perp1.x)).normalize();
        vertexConsumer.fixedColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());

        for(int j = 0; j < 16; ++j) {
            float theta1 = (float)j * thetaStep;
            float theta2 = (float)(j + 1) * thetaStep;
            Vector3f p1 = calculateCylinderPoint(theta1, perp1, perp2, radius).add(a);
            Vector3f p2 = calculateCylinderPoint(theta2, perp1, perp2, radius).add(a);
            Vector3f p3 = calculateCylinderPoint(theta2, perp1, perp2, radius).add(b);
            Vector3f p4 = calculateCylinderPoint(theta1, perp1, perp2, radius).add(b);
            renderQuad(vertexConsumer, p1, p2, p3, p4, overlay, light);
            renderTri(vertexConsumer, p2, p1, a, overlay, light);
            renderTri(vertexConsumer, p4, p3, b, overlay, light);
        }

        vertexConsumer.unfixColor();
    }

    public static void renderCylinder(VertexConsumer vertexConsumer, Vector3f a, Vector3f b, float radius, Color color) {
        renderCylinder(vertexConsumer, a, b, radius, color, 0, 255);
    }

    private static void renderQuad(VertexConsumer vertices, Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4, int overlay, int light) {
        vertices.vertex((double)p1.x, (double)p1.y, (double)p1.z).texture(0.0F, 0.0F).overlay(overlay).light(light).normal(0.0F, 1.0F, 0.0F).next();
        vertices.vertex((double)p2.x, (double)p2.y, (double)p2.z).texture(0.0F, 0.0F).overlay(overlay).light(light).normal(0.0F, 1.0F, 0.0F).next();
        vertices.vertex((double)p3.x, (double)p3.y, (double)p3.z).texture(0.0F, 0.0F).overlay(overlay).light(light).normal(0.0F, 1.0F, 0.0F).next();
        vertices.vertex((double)p4.x, (double)p4.y, (double)p4.z).texture(0.0F, 0.0F).overlay(overlay).light(light).normal(0.0F, 1.0F, 0.0F).next();
    }

    private static void renderTri(VertexConsumer vertices, Vector3f p1, Vector3f p2, Vector3f p3, int overlay, int light) {
        vertices.vertex((double)p1.x, (double)p1.y, (double)p1.z).texture(0.0F, 0.0F).overlay(overlay).light(light).normal(0.0F, 1.0F, 0.0F).next();
        vertices.vertex((double)p2.x, (double)p2.y, (double)p2.z).texture(0.0F, 0.0F).overlay(overlay).light(light).normal(0.0F, 1.0F, 0.0F).next();
        vertices.vertex((double)p3.x, (double)p3.y, (double)p3.z).texture(0.0F, 0.0F).overlay(overlay).light(light).normal(0.0F, 1.0F, 0.0F).next();
        vertices.vertex((double)p3.x, (double)p3.y, (double)p3.z).texture(0.0F, 0.0F).overlay(overlay).light(light).normal(0.0F, 1.0F, 0.0F).next();
    }
}
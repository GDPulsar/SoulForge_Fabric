package com.pulsar.soulforge.client.render;

import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.awt.*;

public class CubeRenderer {
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
        renderQuad(model, vertexConsumer, xYZ, xYz, xyz, xyZ);
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
}

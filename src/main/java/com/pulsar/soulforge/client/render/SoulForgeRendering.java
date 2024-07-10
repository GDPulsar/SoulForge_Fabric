package com.pulsar.soulforge.client.render;

import com.google.common.collect.ImmutableMap;
import com.pulsar.soulforge.SoulForge;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.function.Consumer;

import static net.minecraft.client.render.VertexFormats.*;

public class SoulForgeRendering {
    private static ShaderProgram energyBeam;
    public static VertexFormat POSITION_COLOR_TEXTURE_OVERLAY_LIGHT = new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder()
            .put("Position", POSITION_ELEMENT)
            .put("Color", COLOR_ELEMENT).put("UV0", TEXTURE_ELEMENT)
            .put("UV1", OVERLAY_ELEMENT).put("UV2", LIGHT_ELEMENT)
            .put("Padding", PADDING_ELEMENT).build());

    public static void initializeShaders(TriConsumer<Identifier, VertexFormat, Consumer<ShaderProgram>> registrations) {
        registrations.accept(
                new Identifier(SoulForge.MOD_ID, "energy_beam"),
                VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
                inst -> energyBeam = inst
        );
    }

    public static ShaderProgram energyBeam() {
        return energyBeam;
    }
}

package com.pulsar.soulforge.shader;

import com.google.gson.JsonSyntaxException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.LifecycledResourceManagerImpl;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidHierarchicalFileException;

import java.io.IOException;
import java.util.List;

import static net.minecraft.resource.ResourceType.CLIENT_RESOURCES;

public class SoulForgeShaders {
    private static final StopException STOP = new StopException();
    private static boolean reloading = false;
    private static boolean stopReloading = false;
    private static ResourceReloader shaderLoader;

    private static void printShaderException(Exception exception, boolean builtin) {
        var client = MinecraftClient.getInstance();
        var throwable = (Throwable) exception;
        while (!(throwable instanceof InvalidHierarchicalFileException)) {
            var cause = throwable.getCause();
            if (cause != null) throwable = cause;
            else {
                throwable.printStackTrace();
                return;
            }
        }
        client.inGameHud.getChatHud().addMessage(Text.literal(throwable.getMessage()).formatted(Formatting.GRAY));
    }

    public static ShaderProgram onLoadShaders$new(ResourceFactory factory, String name, VertexFormat format) throws IOException {
        try {
            return new ShaderProgram(factory, name, format);
        } catch (IOException e) {
            printShaderException(e, false);
            if (reloading) throw STOP;
        }
        try {
            var defaultPack = MinecraftClient.getInstance().getDefaultResourcePack();
            return new ShaderProgram(defaultPack.getFactory(), name, format);
        } catch (IOException e) {
            printShaderException(e, true);
            throw e;
        }
    }

    public static PostEffectProcessor onLoadShader$new(TextureManager textureManager, ResourceManager resourceManager, Framebuffer framebuffer, Identifier location) throws IOException {
        try {
            return new PostEffectProcessor(textureManager, resourceManager, framebuffer, location);
        } catch (IOException | JsonSyntaxException e) {
            printShaderException(e, false);
            stopReloading = true;
        }
        try {
            var defaultPack = MinecraftClient.getInstance().getDefaultResourcePack();
            resourceManager = new LifecycledResourceManagerImpl(CLIENT_RESOURCES, List.of(defaultPack));
            return new PostEffectProcessor(textureManager, resourceManager, framebuffer, location);
        } catch (IOException | JsonSyntaxException e) {
            printShaderException(e, true);
            throw e;
        }
    }

    public static void onLoadShader$end() {
        if (reloading && stopReloading) throw STOP;
    }

    public static void setShaderLoader(ResourceReloader returnValue) {
        shaderLoader = returnValue;
    }

    private static class StopException extends RuntimeException {
        private StopException() {}
    }
}

package com.pulsar.soulforge.siphon;

import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Nullable;

public class Siphon {
    public enum Type implements StringIdentifiable {
        BRAVERY("bravery", 0.1f),
        JUSTICE("justice", 0.2f),
        KINDNESS("kindness", 0.3f),
        PATIENCE("patience", 0.4f),
        INTEGRITY("integrity", 0.5f),
        PERSEVERANCE("perseverance", 0.6f),
        DETERMINATION("determination", 0.7f),
        SPITE("spite", 0.8f);

        private final String name;
        private final float index;

        Type(String name, float index) {
            this.name = name;
            this.index = index;
        }

        @Override
        public String asString() {
            return this.name;
        }

        public float getIndex() { return this.index; }

        @Nullable
        public static Type getSiphon(String name) {
            return switch (name) {
                case "bravery" -> Type.BRAVERY;
                case "justice" -> Type.JUSTICE;
                case "kindness" -> Type.KINDNESS;
                case "patience" -> Type.PATIENCE;
                case "integrity" -> Type.INTEGRITY;
                case "perseverance" -> Type.PERSEVERANCE;
                case "determination" -> Type.DETERMINATION;
                case "spite" -> Type.SPITE;
                default -> null;
            };
        }
    }

    private final Type type;

    public Siphon(Type type) {
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }
}

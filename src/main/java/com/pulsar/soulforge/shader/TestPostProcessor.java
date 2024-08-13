package com.pulsar.soulforge.shader;

import com.pulsar.soulforge.SoulForge;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import team.lodestar.lodestone.systems.postprocess.PostProcessor;

public class TestPostProcessor extends PostProcessor {
    public static final TestPostProcessor INSTANCE = new TestPostProcessor();

    static {
        INSTANCE.setActive(false);
    }

    @Override
    public Identifier getPostChainLocation() {
        return new Identifier(SoulForge.MOD_ID, "test_post");
    }

    @Override
    public void beforeProcess(MatrixStack viewModelStack) {

    }

    @Override
    public void afterProcess() {

    }
}

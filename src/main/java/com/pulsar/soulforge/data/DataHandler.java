package com.pulsar.soulforge.data;

import com.pulsar.soulforge.SoulForge;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

public class DataHandler extends PersistentState {

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        return null;
    }

    public static DataHandler createFromNbt(NbtCompound tag) {
        DataHandler handler = new DataHandler();
        return handler;
    }

    public static DataHandler getServerData(MinecraftServer server) {
        PersistentStateManager manager = server.getWorld(World.OVERWORLD).getPersistentStateManager();

        DataHandler handler = manager.getOrCreate(DataHandler::createFromNbt, DataHandler::new, SoulForge.MOD_ID);

        handler.markDirty();

        return handler;
    }
}

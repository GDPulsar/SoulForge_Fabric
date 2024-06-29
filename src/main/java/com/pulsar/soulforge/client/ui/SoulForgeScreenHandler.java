package com.pulsar.soulforge.client.ui;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.SoulForgeClient;
import com.pulsar.soulforge.block.SoulForgeBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class SoulForgeScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    public SoulForgeBlockEntity blockEntity;

    public SoulForgeScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        this(syncId, inventory, inventory.player.getWorld().getBlockEntity(buf.readBlockPos()),
                new ArrayPropertyDelegate(3));
    }

    public SoulForgeScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        this(syncId, playerInventory, inventory, new ArrayPropertyDelegate(3));
    }

    public SoulForgeScreenHandler(int syncId, PlayerInventory inventory) {
        this(syncId, inventory, new SimpleInventory(8), new ArrayPropertyDelegate(3));
    }

    public SoulForgeScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate arrayPropertyDelegate) {
        super(SoulForge.SOUL_FORGE_SCREEN_HANDLER, syncId);
        checkSize(inventory, 8);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);
        this.propertyDelegate = arrayPropertyDelegate;

        this.addSlot(new Slot(this.inventory, 0, 47, 35));
        this.addSlot(new Slot(this.inventory, 1, 26, 35));
        this.addSlot(new Slot(this.inventory, 2, 37, 14));
        this.addSlot(new Slot(this.inventory, 3, 58, 14));
        this.addSlot(new Slot(this.inventory, 4, 67, 35));
        this.addSlot(new Slot(this.inventory, 5, 58, 56));
        this.addSlot(new Slot(this.inventory, 6, 37, 56));
        this.addSlot(new Slot(this.inventory, 7, 120, 35));
        addPlayerInventory(playerInventory);

        addProperties(propertyDelegate);
    }

    public SoulForgeScreenHandler(int syncId, PlayerInventory inventory, BlockEntity blockEntity, PropertyDelegate arrayPropertyDelegate) {
        this(syncId, inventory, (Inventory)blockEntity, arrayPropertyDelegate);
        this.blockEntity = (SoulForgeBlockEntity)blockEntity;
    }

    public boolean isCrafting() {
        return propertyDelegate.get(0) > 0;
    }

    public int getScaledProgress() {
        int progress = this.propertyDelegate.get(0);
        int maxProgress = this.propertyDelegate.get(1);
        int progressSize = 14;

        return maxProgress != 0 && progress != 0 ? progress * progressSize / maxProgress : 0;
    }

    public int getScaledLava() {
        int lava = this.propertyDelegate.get(2);
        int maxLava = 4000;
        int scaleSize = 69;

        return lava != 0 ? lava * scaleSize / maxLava : 0;
    }

    private void addPlayerInventory(PlayerInventory inventory) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(inventory, j + i*9 + 9, 8 + j*18, 84 + i*18));
            }
        }
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(inventory, i, 8 + i*18, 142));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }
}

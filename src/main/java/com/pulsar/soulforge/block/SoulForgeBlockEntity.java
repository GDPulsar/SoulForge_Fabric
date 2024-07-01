package com.pulsar.soulforge.block;

import com.pulsar.soulforge.client.ui.SoulForgeScreenHandler;
import com.pulsar.soulforge.recipe.SoulForgeRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SoulForgeBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(8, ItemStack.EMPTY);

    private static final int OUTPUT_SLOT = 7;

    protected final PropertyDelegate propertyDelegate;
    private int progress;
    private int maxProgress = 100;
    private int lavaMb = 0;

    public SoulForgeBlockEntity(BlockPos pos, BlockState state) {
        super(SoulForgeBlocks.SOUL_FORGE_BLOCK_ENTITY, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> SoulForgeBlockEntity.this.progress;
                    case 1 -> SoulForgeBlockEntity.this.maxProgress;
                    case 2 -> SoulForgeBlockEntity.this.lavaMb;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> SoulForgeBlockEntity.this.progress = value;
                    case 1 -> SoulForgeBlockEntity.this.maxProgress = value;
                    case 2 -> SoulForgeBlockEntity.this.lavaMb = value;
                };
            }

            @Override
            public int size() {
                return 3;
            }
        };
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("blockentity.soulforge.soul_forge");
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
        nbt.putInt("soulforge.progress", progress);
        nbt.putInt("soulforge.lavaMb", lavaMb);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, inventory, registryLookup);
        progress = nbt.getInt("soulforge.progress");
        lavaMb = nbt.getInt("soulforge.lavaMb");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new SoulForgeScreenHandler(syncId, playerInventory, (BlockEntity) this, this.propertyDelegate);
    }

    public void addLava() { lavaMb = Math.min(lavaMb + 1000, 4000); }
    public boolean canInsertLava() { return lavaMb <= 3000; }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;

        if(isOutputEmptyOrReceivable() && lavaMb >= 25) {
            if(this.hasRecipe()) {
                progress++;
                markDirty(world, pos, state);
                if (progress >= maxProgress) {
                    this.craftItem();
                    lavaMb -= 25;
                    progress = 0;
                }
            } else {
                progress = 0;
            }
        } else {
            progress = 0;
            markDirty(world, pos, state);
        }
    }

    private Optional<SoulForgeRecipe> getCurrentRecipe() {
        SimpleInventory inv = new SimpleInventory(7);
        for (int i = 0; i < 7; i++) {
            inv.setStack(i, this.getStack(i));
        }
        return getWorld().getRecipeManager().getFirstMatch(SoulForgeRecipe.Type.INSTANCE, new SoulForgeRecipe.Input(inv), getWorld()).map(RecipeEntry::value);
    }

    private void craftItem() {
        Optional<SoulForgeRecipe> recipe = getCurrentRecipe();

        for (int i = 0; i < 7; i++) this.removeStack(i, 1);
        this.setStack(OUTPUT_SLOT, new ItemStack(recipe.get().getResult(null).getItem(), getStack(OUTPUT_SLOT).getCount() + recipe.get().getResult(null).getCount()));
    }

    private boolean isOutputEmptyOrReceivable() {
        return this.getStack(OUTPUT_SLOT).isEmpty() ||
                this.getStack(OUTPUT_SLOT).getCount() < this.getStack(OUTPUT_SLOT).getMaxCount();
    }

    private boolean hasRecipe() {
        Optional<SoulForgeRecipe> recipe = getCurrentRecipe();

        return recipe.isPresent() && (this.getStack(OUTPUT_SLOT).getItem() == recipe.get().getResult(null).getItem() || this.getStack(OUTPUT_SLOT).isEmpty()) &&
                (this.getStack(OUTPUT_SLOT).getCount() + recipe.get().getResult(null).getCount() <= this.getStack(OUTPUT_SLOT).getMaxCount());
    }
}

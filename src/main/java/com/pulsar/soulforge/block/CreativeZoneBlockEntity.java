package com.pulsar.soulforge.block;

import com.pulsar.soulforge.client.ui.CreativeZoneScreenHandler;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.item.SoulForgeItems;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.RenderUtils;

public class CreativeZoneBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, GeoBlockEntity, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);

    protected final PropertyDelegate propertyDelegate;
    public int fuel;

    public CreativeZoneBlockEntity(BlockPos pos, BlockState state) {
        super(SoulForgeBlocks.CREATIVE_ZONE_ENTITY, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                if (index == 0) return CreativeZoneBlockEntity.this.fuel;
                return 0;
            }

            @Override
            public void set(int index, int value) {
                if (index == 0) {
                    CreativeZoneBlockEntity.this.fuel = value;
                }
            }

            @Override
            public int size() {
                return 1;
            }
        };
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;

        if (isActive(state)) {
            if (fuel > 0) {
                fuel--;
                if (fuel % 20 == 0) applyEffect(world, pos);
            }
            ItemStack stack = this.getStack(0);
            if (stack.isOf(SoulForgeItems.KINDNESS_ARNICITE) || stack.isOf(SoulForgeItems.INTEGRITY_ARNICITE)) {
                fuel += 1000;
                stack.decrement(1);
            } else if (stack.isOf(SoulForgeItems.KINDNESS_ARNICITE_HEART) || stack.isOf(SoulForgeItems.INTEGRITY_ARNICITE_HEART)) {
                fuel += 2000;
                stack.decrement(1);
            } else if (stack.isOf(SoulForgeItems.KINDNESS_ARNICITE_CORE) || stack.isOf(SoulForgeItems.INTEGRITY_ARNICITE_CORE)) {
                fuel += 20000;
                stack.decrement(1);
            }
        }
    }

    public boolean isActive(BlockState state) {
        return (Boolean)state.getEntries().get(CreativeZoneBlock.ACTIVE) && fuel > 0;
    }

    private static void applyEffect(World world, BlockPos pos) {
        Box box = (new Box(pos)).expand(40).stretch(0.0, world.getHeight(), 0.0);
        for (PlayerEntity player : world.getNonSpectatingEntities(PlayerEntity.class, box)) {
            player.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.CREATIVE_ZONE, 150, 0));
        }
    }

    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<CreativeZoneBlockEntity> animationState) {
        animationState.getController().setAnimation(RawAnimation.begin().then("SPINNN", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object blockEntity) {
        return RenderUtils.getCurrentTick();
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.soulforge.creative_zone_block");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new CreativeZoneScreenHandler(syncId, playerInventory, (Inventory)this, this.propertyDelegate);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    public int getFuel() {
        if (this.fuel != 0) return fuel;
        if (this.propertyDelegate.get(0) != 0) return this.propertyDelegate.get(0);
        return 0;
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
        nbt.putInt("creative_zone.fuel", fuel);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, inventory, registryLookup);
        fuel = nbt.getInt("creative_zone.fuel");
    }

    @Override
    public Object getScreenOpeningData(ServerPlayerEntity player) {
        return this.getPos();
    }
}

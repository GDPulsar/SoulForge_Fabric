package com.pulsar.soulforge.item.devices.machines;

import com.pulsar.soulforge.client.item.GeoMagicItemRenderer;
import com.pulsar.soulforge.entity.DetonatorMine;
import com.pulsar.soulforge.entity.RailkillerEntity;
import com.pulsar.soulforge.item.devices.DeviceBase;
import com.pulsar.soulforge.trait.Traits;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Railkiller extends DeviceBase implements GeoItem {
    public Railkiller() {
        super(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC), 1000, Traits.justice);
    }


    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (!(world instanceof ServerWorld)) {
            return ActionResult.SUCCESS;
        } else {
            ItemStack itemStack = context.getStack();
            BlockPos blockPos = context.getBlockPos();
            Direction direction = context.getSide();
            BlockState blockState = world.getBlockState(blockPos);
            BlockPos blockPos2;
            if (blockState.getCollisionShape(world, blockPos).isEmpty()) {
                blockPos2 = blockPos;
            } else {
                blockPos2 = blockPos.offset(direction);
            }
            HitResult hit = world.raycast(new RaycastContext(blockPos2.toCenterPos(), blockPos2.toCenterPos().subtract(0, 2, 0), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, context.getPlayer()));
            RailkillerEntity railkillerEntity = new RailkillerEntity(world, hit.getPos(), context.getPlayer().getRotationVector().withAxis(Direction.Axis.Y, 0), itemStack.copy());
            railkillerEntity.setPos(hit.getPos());
            railkillerEntity.setStack(itemStack.copy());
            railkillerEntity.setDirection(context.getPlayer().getRotationVector().withAxis(Direction.Axis.Y, 0));
            world.spawnEntity(railkillerEntity);
            itemStack.decrement(1);
            return ActionResult.CONSUME;
        }
    }

    public AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private final GeoMagicItemRenderer<Railkiller> renderer = new GeoMagicItemRenderer<>("railkiller", "railkiller");

            @Override
            public BuiltinModelItemRenderer getCustomRenderer() {
                return this.renderer;
            }
        });
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return renderProvider;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, (animationState) -> PlayState.STOP));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}

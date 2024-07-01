package com.pulsar.soulforge.item.devices.trinkets;

import com.pulsar.soulforge.client.item.GeoMagicItemRenderer;
import com.pulsar.soulforge.entity.WormholeEntity;
import com.pulsar.soulforge.item.SoulForgeItems;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class WarpDiamond extends Item implements GeoItem {

    public WarpDiamond() {
        super(new Item.Settings().maxCount(4));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            if (stack.contains(SoulForgeItems.WORLD_COMPONENT)) {
                user.fallDistance = 0f;
                user.setVelocity(0f, 0f, 0f);
                user.velocityModified = true;
                ServerWorld savedWorld = null;
                for (RegistryKey<World> registryKey : user.getServer().getWorldRegistryKeys()) {
                    if (Objects.equals(registryKey.toString(), stack.get(SoulForgeItems.WORLD_COMPONENT))) {
                        savedWorld = user.getServer().getWorld(registryKey);
                        break;
                    }
                }
                Vec3d end = stack.get(SoulForgeItems.POSITION_COMPONENT);
                WormholeEntity startWormhole = new WormholeEntity(savedWorld, user.getPos().add(0, 1.25f, 0), savedWorld, end, user.getRotationVector().withAxis(Direction.Axis.Y, 0).normalize());
                user.teleport(savedWorld, end.x, end.y, end.z, PositionFlag.VALUES, user.getYaw(), user.getPitch());
                savedWorld.spawnEntity(startWormhole);
                stack.decrement(1);
                world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_ENDER_EYE_DEATH, SoundCategory.PLAYERS, 0.5f, 1f);
            } else {
                ItemStack savedDiamond = new ItemStack(SoulForgeItems.WARP_DIAMOND, 1);
                savedDiamond.set(SoulForgeItems.POSITION_COMPONENT, user.getPos());
                savedDiamond.set(SoulForgeItems.WORLD_COMPONENT, world.getRegistryKey().toString());
                user.giveItemStack(savedDiamond);
                String posString = "(" + String.format("%.2f", user.getX()) + ", " + String.format("%.2f", user.getY()) + ", " + String.format("%.2f", user.getZ()) + ")";
                user.sendMessage(Text.translatable("item.soulforge.warp_diamond.saved").append(posString).formatted(Formatting.GREEN), true);
                stack.decrement(1);
            }
        }

        return TypedActionResult.success(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if (stack.contains(SoulForgeItems.POSITION_COMPONENT)) {
            Vec3d pos = stack.getOrDefault(SoulForgeItems.POSITION_COMPONENT, Vec3d.ZERO);
            String posString = "(" + String.format("%.2f", pos.x) + ", " + String.format("%.2f", pos.y) + ", " + String.format("%.2f", pos.z) + ")";
            tooltip.add(Text.translatable("item.soulforge.warp_diamond.saved").append(posString));
        }
    }

    public AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private final GeoMagicItemRenderer<WarpDiamond> renderer = new GeoMagicItemRenderer<>("warp_diamond", "warp_diamond");

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

package com.pulsar.soulforge.item.devices.machines;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.item.GeoMagicItemRenderer;
import com.pulsar.soulforge.item.devices.DeviceBase;
import com.pulsar.soulforge.tag.SoulForgeTags;
import com.pulsar.soulforge.trait.Traits;
import com.pulsar.soulforge.util.Utils;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SiphonImbuer extends DeviceBase implements GeoItem {
    public SiphonImbuer() {
        super(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC), 1000, Traits.perseverance);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        TypedActionResult<ItemStack> result = super.use(world, user, hand);
        if (result.getResult().isAccepted()) return result;
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            if (getCharge(stack) <= 0) return super.use(world, user, hand);
            stack.getOrCreateNbt();
            if (!stack.getNbt().contains("imbuedId")) {
                if (hand == Hand.MAIN_HAND) {
                    int slot = (user.getInventory().selectedSlot + 1) % 9;
                    if (user.getInventory().getStack(slot).isEmpty()) return super.use(world, user, hand);
                    ItemStack slotStack = user.getInventory().getStack(slot);
                    Item item = slotStack.getItem();
                    if (!(item instanceof CrossbowItem || item instanceof SwordItem || item instanceof TridentItem || item instanceof ShieldItem || item instanceof AxeItem || item instanceof BowItem)) return super.use(world, user, hand);
                    slotStack.getNbt().putBoolean("imbued", true);
                    UUID uuid = UUID.randomUUID();
                    slotStack.getNbt().putUuid("imbuedId", uuid);
                    stack.getNbt().putUuid("imbuedId", uuid);
                    user.sendMessage(Text.literal("Successfully bound to ").append(slotStack.getName()));
                    return TypedActionResult.success(stack);
                }
            } else {
                ItemStack lStack = Utils.getImbuedById(stack.getNbt().getUuid("imbuedId"), user);
                if (lStack != null && lStack.getNbt() != null) {
                    lStack.getNbt().remove("imbued");
                    lStack.getNbt().remove("imbuedId");
                }
                if (stack.getNbt() != null) stack.getNbt().remove("imbuedId");
                user.sendMessage(Text.literal("Unbound"));
                return TypedActionResult.success(stack);
            }
        }
        return TypedActionResult.fail(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        stack.getOrCreateNbt();
        if (stack.getNbt().contains("imbuedId")) {
            if (getCharge(stack) <= 0) {
                ItemStack lStack = Utils.getImbuedById(stack.getNbt().getUuid("imbuedId"), (PlayerEntity)entity);
                if (lStack != null && lStack.getNbt() != null) {
                    lStack.getNbt().remove("imbued");
                    lStack.getNbt().remove("imbuedId");
                }
                if (stack.getNbt() != null) stack.getNbt().remove("imbuedId");
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(Text.literal("Infuses the item in your next slot."));
    }

    public AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private final GeoMagicItemRenderer<SiphonImbuer> renderer = new GeoMagicItemRenderer<>("siphon_imbuer", "siphon_imbuer");

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

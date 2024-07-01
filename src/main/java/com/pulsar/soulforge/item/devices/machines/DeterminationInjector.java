package com.pulsar.soulforge.item.devices.machines;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.client.item.GeoMagicItemRenderer;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.item.devices.DeviceBase;
import com.pulsar.soulforge.trait.Traits;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class DeterminationInjector extends DeviceBase implements GeoItem {
    public DeterminationInjector() {
        super(new Item.Settings().maxCount(1).rarity(Rarity.EPIC), 1000, Traits.determination);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        TypedActionResult<ItemStack> result = super.use(world, user, hand);
        if (result.getResult().isAccepted()) return result;
        ItemStack stack = user.getStackInHand(hand);
        if (getCharge(stack) > 1) {
            if (!world.isClient) {
                int timer = stack.getOrDefault(SoulForgeItems.TIMER_COMPONENT, 0);
                SoulComponent playerSoul = SoulForge.getPlayerSoul(user);
                boolean active = stack.getOrDefault(SoulForgeItems.ACTIVE_COMPONENT, false);
                if (active) {
                    playerSoul.removeTag("injected");
                } else {
                    if (playerSoul.hasTag("injected")) {
                        return super.use(world, user, hand);
                    }
                    playerSoul.addTag("injected");
                }
                stack.set(SoulForgeItems.ACTIVE_COMPONENT, !active);
                if (playerSoul.hasTag("injected")) {
                    Utils.removeModifier(user, SoulForgeAttributes.MAGIC_POWER, "dt_injector");
                    Utils.addModifier(user, SoulForgeAttributes.MAGIC_POWER, "dt_injector", 0.5f, EntityAttributeModifier.Operation.ADD_VALUE);
                    if (!playerSoul.getTraits().contains(Traits.determination)) {
                        Utils.removeModifier(user, EntityAttributes.GENERIC_MAX_HEALTH, "dt_injector_health");
                        Utils.removeModifier(user, EntityAttributes.GENERIC_ATTACK_DAMAGE, "dt_injector_strength");
                        Utils.addModifier(user, EntityAttributes.GENERIC_MAX_HEALTH, "dt_injector_health", 5f, EntityAttributeModifier.Operation.ADD_VALUE);
                        Utils.addModifier(user, EntityAttributes.GENERIC_ATTACK_DAMAGE, "dt_injector_strength", 0.175f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                    }
                    stack.set(SoulForgeItems.TIMER_COMPONENT, 0);
                } else {
                    if (!playerSoul.getTraits().contains(Traits.determination)) {
                        user.damage(user.getDamageSources().magic(), timer / 500f);
                        user.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.MANA_OVERLOAD, timer/4, timer/4000));
                        user.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, timer/4, timer/4000));
                        user.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, timer/4, timer/4000));
                    }
                    stack.set(SoulForgeItems.TIMER_COMPONENT, 0);
                    Utils.removeModifier(user, SoulForgeAttributes.MAGIC_POWER, "dt_injector");
                    Utils.removeModifier(user, EntityAttributes.GENERIC_MAX_HEALTH, "dt_injector_health");
                    Utils.removeModifier(user, EntityAttributes.GENERIC_ATTACK_DAMAGE, "dt_injector_strength");
                }
            }
        }
        return TypedActionResult.fail(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient) {
            if (stack.getOrDefault(SoulForgeItems.ACTIVE_COMPONENT, false)) {
                int timer = stack.getOrDefault(SoulForgeItems.TIMER_COMPONENT, 0);
                if (timer > 0 && timer % 20 == 0) {
                    decreaseCharge(stack, 1);
                    if (getCharge(stack) <= 0) {
                        stack.decrement(1);
                        world.createExplosion(entity, entity.getX(), entity.getY(), entity.getZ(), 5f, World.ExplosionSourceType.NONE);
                        entity.kill();
                    }
                }
                stack.set(SoulForgeItems.TIMER_COMPONENT, timer + 1);
            }
        }
    }

    public AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private final GeoMagicItemRenderer<DeterminationInjector> renderer = new GeoMagicItemRenderer<>("determination_injector", "determination_injector");

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

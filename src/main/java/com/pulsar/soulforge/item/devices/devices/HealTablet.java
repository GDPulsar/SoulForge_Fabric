package com.pulsar.soulforge.item.devices.devices;

import com.pulsar.soulforge.client.item.GeoMagicItemRenderer;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.item.devices.DeviceBase;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.trait.Traits;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
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

public class HealTablet extends DeviceBase implements GeoItem {
    public HealTablet() {
        super(new Item.Settings().maxCount(1).rarity(Rarity.RARE), 300, Traits.kindness);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        TypedActionResult<ItemStack> result = super.use(world, user, hand);
        if (result.getResult().isAccepted()) return result;
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            if (getCharge(stack) >= 25f) {
                if (!user.isSneaking()) {
                    EntityHitResult hit = Utils.getFocussedEntity(user, 10f);
                    if (hit != null) {
                        if (hit.getEntity() instanceof LivingEntity target) {
                            stack.set(SoulForgeItems.TARGET_ID_COMPONENT, target.getUuid());
                            stack.set(SoulForgeItems.TIMER_COMPONENT, 200);
                            user.getWorld().playSound(null, user.getX(), user.getY(), user.getZ(), SoulForgeSounds.HEAL_EVENT, SoundCategory.PLAYERS, 1f, 1f);
                            for (int i = 0; i < 20; i++) {
                                user.getWorld().addParticle(new DustParticleEffect(Vec3d.unpackRgb(0x00FF00).toVector3f(), 1f),
                                        target.getX() + Math.random() * 2 - 1,
                                        target.getY() + Math.random() * 2 - 1,
                                        target.getZ() + Math.random() * 2 - 1,
                                        0, 0, 0);
                            }
                            decreaseCharge(stack, 25);
                            user.getItemCooldownManager().set(this, 300);
                            return TypedActionResult.success(stack);
                        }
                    }
                } else {
                    stack.set(SoulForgeItems.TARGET_ID_COMPONENT, user.getUuid());
                    stack.set(SoulForgeItems.TIMER_COMPONENT, 200);
                    user.getWorld().playSound(null, user.getX(), user.getY(), user.getZ(), SoulForgeSounds.HEAL_EVENT, SoundCategory.PLAYERS, 1f, 1f);
                    for (int i = 0; i < 20; i++) {
                        user.getWorld().addParticle(new DustParticleEffect(Vec3d.unpackRgb(0x00FF00).toVector3f(), 1f),
                                user.getX() + Math.random() * 2 - 1,
                                user.getY() + Math.random() * 2 - 1,
                                user.getZ() + Math.random() * 2 - 1,
                                0, 0, 0);
                    }
                    decreaseCharge(stack, 25);
                    user.getItemCooldownManager().set(this, 500);
                    return TypedActionResult.success(stack);
                }
            }
        }
        return TypedActionResult.fail(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient) {
            if (stack.contains(SoulForgeItems.TARGET_ID_COMPONENT) && stack.contains(SoulForgeItems.TIMER_COMPONENT)) {
                LivingEntity target = (LivingEntity) ((ServerWorld) world).getEntity(stack.get(SoulForgeItems.TARGET_ID_COMPONENT));
                if (target != null) {
                    if (target.age - target.getLastAttackedTime() >= 0 && target.age - target.getLastAttackedTime() <= 1) {
                        stack.remove(SoulForgeItems.TARGET_ID_COMPONENT);
                        stack.remove(SoulForgeItems.TIMER_COMPONENT);
                        return;
                    }
                    int timer = stack.get(SoulForgeItems.TIMER_COMPONENT);
                    if (timer <= 0) return;
                    stack.set(SoulForgeItems.TIMER_COMPONENT, timer - 1);
                    if (timer % 7 == 0) target.heal(1f);
                }
            }
        }
    }

    public AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private final GeoMagicItemRenderer<HealTablet> renderer = new GeoMagicItemRenderer<>("heal_tablet", "heal_tablet");

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

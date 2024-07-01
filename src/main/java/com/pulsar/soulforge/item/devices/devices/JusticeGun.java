package com.pulsar.soulforge.item.devices.devices;

import com.pulsar.soulforge.client.item.GeoMagicItemRenderer;
import com.pulsar.soulforge.entity.JusticePelletProjectile;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.item.devices.DeviceBase;
import com.pulsar.soulforge.item.special.CrushingRound;
import com.pulsar.soulforge.item.special.FrostbiteRound;
import com.pulsar.soulforge.item.special.PuncturingRound;
import com.pulsar.soulforge.item.special.SuppressingRound;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.trait.Traits;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
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

public class JusticeGun extends DeviceBase implements GeoItem {
    public JusticeGun() {
        super(new Item.Settings().maxCount(1).rarity(Rarity.RARE), 300, Traits.justice);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        TypedActionResult<ItemStack> result = super.use(world, user, hand);
        if (result.getResult().isAccepted()) return result;
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            int ammo = stack.getOrDefault(SoulForgeItems.AMMO_COMPONENT, 0);
            if (stack.contains(SoulForgeItems.RELOAD_COUNT_COMPONENT)) {
                ammo = Math.max(ammo, stack.get(SoulForgeItems.RELOAD_COUNT_COMPONENT));
            }
            if (ammo > 0) {
                JusticePelletProjectile projectile = new JusticePelletProjectile(world, user);
                if (stack.contains(SoulForgeItems.RELOADED_COMPONENT)) {
                    if (stack.get(SoulForgeItems.RELOAD_COUNT_COMPONENT) > 0) {
                        projectile = switch (stack.get(SoulForgeItems.RELOAD_COMPONENT)) {
                            case "frostbite" -> FrostbiteRound.createPellet(world, user, 1f/6f);
                            case "crushing" -> CrushingRound.createPellet(world, user, 1f/6f);
                            case "puncturing" -> PuncturingRound.createPellet(world, user, 1f/6f);
                            case "suppressing" -> SuppressingRound.createPellet(world, user, 1f/6f);
                            default -> projectile;
                        };
                        stack.set(SoulForgeItems.RELOAD_COUNT_COMPONENT, stack.get(SoulForgeItems.RELOAD_COUNT_COMPONENT)-1);
                        if (stack.get(SoulForgeItems.RELOAD_COUNT_COMPONENT) <= 0) {
                            stack.set(SoulForgeItems.RELOADED_COMPONENT, false);
                        }
                    }
                }
                projectile.setPos(user.getEyePos());
                projectile.setVelocity(user.getRotationVector().multiply(8));
                world.spawnEntity(projectile);
                world.playSoundFromEntity(null, user, SoulForgeSounds.GUN_SHOOT_EVENT, SoundCategory.PLAYERS, 1f, 1f);
                user.getItemCooldownManager().set(this, 10);
                stack.set(SoulForgeItems.AMMO_COMPONENT, ammo-1);
                return TypedActionResult.consume(user.getStackInHand(hand));
            }
            if (getCharge(stack) > 0) {
                reload(stack, user);
                return TypedActionResult.success(user.getStackInHand(hand));
            }
        }
        return TypedActionResult.fail(stack);
    }

    public void reload(ItemStack stack, PlayerEntity user) {
        if (getCharge(stack) > 0) {
            int ammo = stack.getOrDefault(SoulForgeItems.AMMO_COMPONENT, 0);
            int toRefill = 12-ammo;
            ammo += Math.min(toRefill, getCharge(stack));
            stack.set(SoulForgeItems.AMMO_COMPONENT, ammo);
            decreaseCharge(stack, Math.min(toRefill, getCharge(stack)));
            user.getItemCooldownManager().set(this, 20);
        }
    }

    public AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private final GeoMagicItemRenderer<JusticeGun> renderer = new GeoMagicItemRenderer<>("justice_gun", "justice");

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

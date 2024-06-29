package com.pulsar.soulforge.item.devices.devices;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.item.GeoMagicItemRenderer;
import com.pulsar.soulforge.entity.JusticePelletProjectile;
import com.pulsar.soulforge.item.devices.DeviceBase;
import com.pulsar.soulforge.item.devices.machines.DeterminationInjector;
import com.pulsar.soulforge.item.special.CrushingRound;
import com.pulsar.soulforge.item.special.FrostbiteRound;
import com.pulsar.soulforge.item.special.PuncturingRound;
import com.pulsar.soulforge.item.special.SuppressingRound;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.trait.TraitBase;
import com.pulsar.soulforge.trait.Traits;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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
        super(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE), 300, Traits.justice);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        TypedActionResult<ItemStack> result = super.use(world, user, hand);
        if (result.getResult().isAccepted()) return result;
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            int ammo = 0;
            NbtCompound stackNbt = stack.getOrCreateNbt();
            if (stackNbt.contains("ammo")) ammo = stack.getNbt().getInt("ammo");
            else stackNbt.putInt("ammo", 0);
            if (stackNbt.contains("reloadedCount")) {
                ammo = Math.max(stackNbt.getInt("reloadedCount"), ammo);
            }
            if (ammo > 0) {
                JusticePelletProjectile projectile = new JusticePelletProjectile(world, user);
                if (stackNbt.contains("reloaded") && stackNbt.contains("reloadedCount")) {
                    if (stackNbt.getInt("reloadedCount") > 0) {
                        projectile = switch (stack.getOrCreateNbt().getString("reloaded")) {
                            case "frostbite" -> FrostbiteRound.createPellet(world, user, 1f/6f);
                            case "crushing" -> CrushingRound.createPellet(world, user, 1f/6f);
                            case "puncturing" -> PuncturingRound.createPellet(world, user, 1f/6f);
                            case "suppressing" -> SuppressingRound.createPellet(world, user, 1f/6f);
                            default -> projectile;
                        };
                        stackNbt.putInt("reloadedCount", stackNbt.getInt("reloadedCount")-1);
                        if (stackNbt.getInt("reloadedCount") <= 0) {
                            stackNbt.putBoolean("reloaded", false);
                        }
                    }
                }
                projectile.setPos(user.getEyePos());
                projectile.setVelocity(user.getRotationVector().multiply(8));
                world.spawnEntity(projectile);
                world.playSoundFromEntity(null, user, SoulForgeSounds.GUN_SHOOT_EVENT, SoundCategory.PLAYERS, 1f, 1f);
                user.getItemCooldownManager().set(this, 10);
                stack.getNbt().putInt("ammo", ammo-1);
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
            int ammo = 0;
            stack.getOrCreateNbt();
            if (stack.getNbt().contains("ammo")) ammo = stack.getNbt().getInt("ammo");
            int toRefill = 12-ammo;
            ammo += Math.min(toRefill, getCharge(stack));
            stack.getNbt().putInt("ammo", ammo);
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

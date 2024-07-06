package com.pulsar.soulforge.item.weapons;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.item.GeoMagicItemRenderer;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.entity.BlastEntity;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BFRCMG extends MagicItem implements GeoItem {
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.soulforge.bfrcmg.tooltip"));
    }

    public void onRightClickAction(boolean mouseDown, ItemStack stack) {
        stack.getOrCreateNbt();
        if (stack.getNbt() != null) stack.getNbt().putBoolean("active", mouseDown);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (entity instanceof ServerPlayerEntity player) {
            if (!stack.hasNbt()) {
                stack.getOrCreateNbt();
                stack.getNbt().putInt("timer", 0);
                stack.getNbt().putInt("heat", 0);
                stack.getNbt().putBoolean("active", false);
            }
            if (stack.getNbt() != null) {
                if (selected) {
                    if (stack.getNbt().getBoolean("active")) {
                        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                        stack.getNbt().putInt("timer", stack.getNbt().getInt("timer") + 1);
                        if (stack.getNbt().getInt("timer") % 4 == 0) {
                            HitResult hit = world.raycast(new RaycastContext(player.getEyePos(), player.getRotationVector().multiply(75f).add(player.getPos()), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player));
                            Vec3d end = player.getRotationVector().multiply(75f);
                            if (hit != null) end = hit.getPos().subtract(Utils.getArmPosition(player));
                            BlastEntity blast = new BlastEntity(player.getWorld(), Utils.getArmPosition(player), player, 0.1f,
                                    Vec3d.ZERO, end, 2f + playerSoul.getLV() / 6f * (1 + 0.02f * stack.getNbt().getInt("heat")),
                                    Color.YELLOW, true, 4);
                            blast.owner = player;
                            ServerWorld serverWorld = player.getServerWorld();
                            serverWorld.spawnEntity(blast);
                            serverWorld.emitGameEvent(GameEvent.ENTITY_PLACE, player.getPos(), GameEvent.Emitter.of(player));
                            serverWorld.playSoundFromEntity(null, player, SoulForgeSounds.UT_BLASTER_EVENT, SoundCategory.PLAYERS, 0.2f, 1f);
                        }
                        if (stack.getNbt().getInt("timer") % 12 == 0) {
                            stack.getNbt().putInt("heat", stack.getNbt().getInt("heat") + 1);
                            stack.getNbt().putInt("timer", 0);
                        }
                        if (stack.getNbt().getInt("heat") == 20) {
                            playerSoul.removeWeapon(false);
                            player.getWorld().createExplosion(null, player.getDamageSources().explosion(player, player), new ExplosionBehavior(), player.getPos(), 1f, false, World.ExplosionSourceType.NONE);
                            player.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.MANA_SICKNESS, 1200, 1));
                        }
                    }
                }
                if (!stack.getNbt().getBoolean("active") && stack.getNbt().getInt("heat") > 0) {
                    if (stack.getNbt().getInt("timer") >= 40) {
                        stack.getNbt().putInt("heat", stack.getNbt().getInt("heat") - 1);
                        stack.getNbt().putInt("timer", 0);
                    } else {
                        stack.getNbt().putInt("timer", stack.getNbt().getInt("timer") + 1);
                    }
                }
            }
        }
    }

    public boolean isItemBarVisible(ItemStack stack) {
        return stack.getNbt() != null && stack.getNbt().getInt("heat") > 0;
    }

    public int getItemBarStep(ItemStack stack) {
        return stack.getNbt() != null ? Math.round(13.0F - (float)stack.getNbt().getInt("heat") * 13.0F / 20f) : 0;
    }

    public int getItemBarColor(ItemStack stack) {
        if (stack.getNbt() == null) return 0;
        float f = Math.min(20f, (float)stack.getNbt().getInt("heat") / 20f);
        return MathHelper.hsvToRgb((1f-f) / 3.0F, 1.0F, 1.0F);
    }

    public AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private final GeoItemRenderer<BFRCMG> renderer = new GeoMagicItemRenderer<>("bfrcmg", "justice");

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

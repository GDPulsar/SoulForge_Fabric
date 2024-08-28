package com.pulsar.soulforge.item.weapons;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.item.GeoMagicItemRenderer;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.entity.GunlanceBlastEntity;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
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
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Gunlance extends MagicSwordItem implements GeoItem {
    GunlanceBlastEntity blast = null;

    public Gunlance() {
        super(5, 0.5f, 0.75f);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }


    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul((PlayerEntity) attacker);
            Utils.addAntiheal(0.4f, playerSoul.getLV() * 20, target);
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!world.isClient) {
            if (user instanceof PlayerEntity player) {
                SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                int useTicks = getMaxUseTime(stack) - remainingUseTicks;
                if (useTicks > 20 && playerSoul.getMagic() >= 4f) {
                    Vec3d end = player.getEyePos().add(player.getRotationVector().multiply(50f));
                    HitResult hit = player.getWorld().raycast(new RaycastContext(player.getEyePos(), player.getEyePos().add(player.getRotationVector().multiply(50f)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player));
                    if (hit != null) end = hit.getPos().subtract(Utils.getArmPosition(player));
                    if (blast == null) {
                        blast = new GunlanceBlastEntity(world, Utils.getArmPosition(player), player, Vec3d.ZERO, end, playerSoul.getLV() * 0.75f);
                        blast.owner = player;
                    } else {
                        if (useTicks == 25) {
                            ServerWorld serverWorld = (ServerWorld) player.getWorld();
                            serverWorld.spawnEntity(blast);
                            serverWorld.emitGameEvent(GameEvent.ENTITY_PLACE, player.getPos(), GameEvent.Emitter.of(player));
                            serverWorld.playSoundFromEntity(null, player, SoulForgeSounds.UT_BLASTER_EVENT, SoundCategory.PLAYERS, 1f, 1f);
                        }
                        if (useTicks >= 25) {
                            blast.setPosition(Utils.getArmPosition(player));
                            blast.setEnd(end);
                            playerSoul.setMagic(playerSoul.getMagic() - 4f);
                            playerSoul.resetLastCastTime();
                        }
                    }
                }
                if (useTicks % 20 == 0) {
                    if (playerSoul.getMagic() < 4f) {
                        if (blast != null) {
                            blast.kill();
                            blast = null;
                        }
                        player.getItemCooldownManager().set(this, 1);
                    }
                }
            }
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!world.isClient) {
            if (user instanceof PlayerEntity player) {
                int useTicks = getMaxUseTime(stack) - remainingUseTicks;
                if (useTicks < 20) {
                    SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                    SoulForge.getValues(player).setBool("Immobilized", true);
                    playerSoul.setValue("clawGouge", 23);
                    player.getItemCooldownManager().set(this, 200);
                } else {
                    if (blast != null) {
                        blast.kill();
                        blast = null;
                    }
                }
            }
        }
    }

    public AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private final GeoItemRenderer<Gunlance> renderer = new GeoMagicItemRenderer<>("gunlance", "gunlance");

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

package com.pulsar.soulforge.item.weapons.weapon_wheel;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.item.GeoMagicItemRenderer;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.item.weapons.MagicItem;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
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

public class DeterminationStaff extends MagicItem implements GeoItem {
    private int iceshockCooldown = 0;
    private int sleepMistCooldown = 0;

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!user.isSneaking()) {
            if (iceshockCooldown == 0) {
                EntityHitResult hit = Utils.getFocussedEntity(user, 10);
                if (hit != null) {
                    if (hit.getEntity() instanceof LivingEntity target) {
                        if (!world.isClient) {
                            if (target instanceof PlayerEntity targetPlayer) {
                                if (!TeamUtils.canDamagePlayer(user.getServer(), user, targetPlayer))
                                    return TypedActionResult.pass(user.getStackInHand(hand));
                            }
                            world.playSoundFromEntity(null, user, SoulForgeSounds.DR_ICESHOCK_EVENT, SoundCategory.PLAYERS, 1f, 1f);
                            DamageSource damageSource = SoulForgeDamageTypes.of(user, SoulForgeDamageTypes.ABILITY_PIERCE_DAMAGE_TYPE);
                            if (target.damage(damageSource, 5f)) {
                                SoulComponent playerSoul = SoulForge.getPlayerSoul(user);
                                playerSoul.setStyle(playerSoul.getStyle() + (int)(5f * (1f + Utils.getTotalDebuffLevel(target) / 10f)));
                            }
                            iceshockCooldown = 100;
                            return TypedActionResult.success(user.getStackInHand(hand));
                        } else {
                            for (int i = 0; i < 20; i++) {
                                world.addParticle(ParticleTypes.SNOWFLAKE,
                                        target.getX() + Math.random() - 0.5f, target.getY() + Math.random() * 2f - 1f, target.getZ() + Math.random() - 0.5f,
                                        0, 0, 0);
                            }
                        }
                    }
                }
            }
        } else {
            if (sleepMistCooldown == 0) {
                Box searchBox = user.getBoundingBox().expand(10f);
                HitResult hit = ProjectileUtil.raycast(user, user.getEyePos(), user.getEyePos().add(user.getRotationVector().multiply(10f)), searchBox, entity -> true, 0);
                if (hit == null || hit.getType() == HitResult.Type.MISS) hit = user.raycast(10f, 0f, false);
                if (hit != null && hit.getType() != HitResult.Type.MISS) {
                    Vec3d pos = hit.getPos();
                    if (!world.isClient) {
                        SoulComponent playerSoul = SoulForge.getPlayerSoul(user);
                        float styleIncrease = 0f;
                        for (Entity target : user.getEntityWorld().getOtherEntities(user, new Box(pos.subtract(3, 3, 3), pos.add(3, 3, 3)))) {
                            if (target instanceof LivingEntity living) {
                                if (living instanceof PlayerEntity targetPlayer) {
                                    if (!TeamUtils.canDamagePlayer(user.getServer(), user, targetPlayer)) continue;
                                }
                                living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 30*playerSoul.getLV(), (int)(playerSoul.getLV()/5f) - 1));
                                living.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 30*playerSoul.getLV(), Math.max((int)(playerSoul.getLV()/5f) - 1, 1)));
                                styleIncrease += 5f * (1f + Utils.getTotalDebuffLevel(living)/10f);
                            }
                        }
                        playerSoul.setStyle(playerSoul.getStyle() + (int)styleIncrease);
                        sleepMistCooldown = 600;
                    } else {
                        for (int i = 0; i < 50; i++) {
                            world.addParticle(ParticleTypes.EFFECT,
                                    pos.getX() + Math.random() * 6f - 3f, pos.getY() + Math.random() * 6f - 3f, pos.getZ() + Math.random() * 6f - 3f,
                                    0, 0, 0);
                        }
                    }
                    return TypedActionResult.success(user.getStackInHand(hand));
                }
            }
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        return Math.round(13.0f - (200f-iceshockCooldown) * 13.0f / 200f);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return MathHelper.packRgb(0.4f, 0.4f, 1.0f);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient) {
            if (iceshockCooldown > 0) iceshockCooldown--;
            if (sleepMistCooldown > 0) sleepMistCooldown--;
        }
    }

    public AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private final GeoItemRenderer<DeterminationStaff> renderer = new GeoMagicItemRenderer<>("determination_staff", "determination");

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

package com.pulsar.soulforge.item.weapons.weapon_wheel;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.item.GeoMagicItemRenderer;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.entity.SpecialHellEntity;
import com.pulsar.soulforge.item.weapons.MagicSwordItem;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DeterminationHammer extends MagicSwordItem implements GeoItem {
    public DeterminationHammer() {
        super(9, 0.9f, 0.33f);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        BlockHitResult hit = world.raycast(new RaycastContext(user.getEyePos(), user.getEyePos().add(user.getRotationVector().multiply(ReachEntityAttributes.getReachDistance(user, 3f))), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, user));
        if (!world.isClient) {
            if (hit != null) {
                if (user.isSneaking()) {
                    SoulComponent playerSoul = SoulForge.getPlayerSoul(user);
                    if (playerSoul.getStyleRank() < 1) {
                        user.sendMessage(Text.translatable(Math.random() < 0.01f ? "soulforge.style.get_real" : "soulforge.style.not_enough"), true);
                        return TypedActionResult.pass(user.getStackInHand(hand));
                    }
                    if (playerSoul.getMagic() >= 100f) {
                        SpecialHellEntity specialHell = new SpecialHellEntity(world, user.getPos(), user);
                        specialHell.setPosition(user.getPos());
                        specialHell.owner = user;
                        world.spawnEntity(specialHell);
                        playerSoul.setMagic(0f);
                        playerSoul.setStyleRank(playerSoul.getStyleRank() - 1);
                        playerSoul.resetLastCastTime();
                        user.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.MANA_SICKNESS, 6000, 0));
                    }
                } else {
                    Vec3d pos = hit.getBlockPos().toCenterPos();
                    Explosion explosion = world.createExplosion(user, pos.x, pos.y, pos.z, 2f, World.ExplosionSourceType.NONE);
                    for (Map.Entry<PlayerEntity, Vec3d> player : explosion.getAffectedPlayers().entrySet()) {
                        Vec3d direction = player.getValue().subtract(pos);
                        direction = new Vec3d(direction.x, 0f, direction.z).normalize();
                        player.getKey().takeKnockback(1.5f, direction.x, direction.z);
                    }
                    user.getItemCooldownManager().set(this, 40);
                }
            }
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    public AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private final GeoItemRenderer<DeterminationHammer> renderer = new GeoMagicItemRenderer<>("bravery_hammer", "determination");

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

package com.pulsar.soulforge.item.weapons;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.item.GeoMagicItemRenderer;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.entity.JusticePelletProjectile;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Gunblades extends MagicSwordItem implements GeoItem {
    public int ammo;
    private int cooldown = 0;
    public Gunblades() {
        super(6, 1f, 0.2f);
        ammo = 6;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BLOCK;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }


    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul((PlayerEntity) attacker);
            if (target instanceof PlayerEntity player) {
                SoulComponent targetSoul = SoulForge.getPlayerSoul(player);
                Utils.addAntiheal(0.3f, playerSoul.getLV() * 20, targetSoul);
            }
            ammo = Math.min(ammo+2, playerSoul.getLV()+6);
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    public void shoot(PlayerEntity user, World world) {
        if (ammo > 0 && cooldown <= 0) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(user);
            JusticePelletProjectile projectile = new JusticePelletProjectile(world, user, 5 + playerSoul.getLV()/5f);
            projectile.setPos(user.getEyePos());
            projectile.setVelocity(user.getRotationVector().multiply(8));
            world.spawnEntity(projectile);
            ammo--;
            cooldown = 10;
            world.playSoundFromEntity(null, user, SoulForgeSounds.GUN_SHOOT_EVENT, SoundCategory.PLAYERS, 1f, 1f);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient) {
            if (cooldown > 0) cooldown--;
        }
    }

    public AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private final GeoItemRenderer<Gunblades> renderer = new GeoMagicItemRenderer<>("gunblades", "gunblades");

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

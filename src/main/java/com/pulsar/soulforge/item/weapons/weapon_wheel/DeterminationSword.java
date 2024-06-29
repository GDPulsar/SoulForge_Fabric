package com.pulsar.soulforge.item.weapons.weapon_wheel;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.item.DefaultedGeoItemRenderer;
import com.pulsar.soulforge.client.item.GeoMagicItemRenderer;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.entity.DarkFountainEntity;
import com.pulsar.soulforge.entity.SpecialHellEntity;
import com.pulsar.soulforge.item.weapons.BraverySpear;
import com.pulsar.soulforge.item.weapons.MagicSwordItem;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
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

public class DeterminationSword extends MagicSwordItem implements GeoItem {
    public DeterminationSword() {
        super(7, 1.5f, 0f);
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
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user.isSneaking()) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(user);
            if (playerSoul.getMagic() >= 100f && playerSoul.getLV() >= 20) {
                SpecialHellEntity specialHell = new SpecialHellEntity(world, user.getPos(), user);
                specialHell.setPosition(user.getPos());
                specialHell.owner = user;
                world.spawnEntity(specialHell);
                playerSoul.setMagic(0f);
                playerSoul.resetLastCastTime();
                user.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.MANA_OVERLOAD, 3600, 0));
            }
        }
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    public AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private final GeoItemRenderer<DeterminationSword> renderer = new GeoMagicItemRenderer<>("determination_sword", "determination");

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

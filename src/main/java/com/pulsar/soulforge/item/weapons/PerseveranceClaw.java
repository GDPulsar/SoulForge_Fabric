package com.pulsar.soulforge.item.weapons;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.item.GeoMagicItemRenderer;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
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

public class PerseveranceClaw extends MagicSwordItem implements GeoItem {
    public PerseveranceClaw() {
        super(5f, 0.6f, 0.75f);
        addAttribute(ReachEntityAttributes.ATTACK_RANGE, new EntityAttributeModifier("pv_claw_reach", -1f, EntityAttributeModifier.Operation.ADDITION));
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        PlayerEntity player = (PlayerEntity)attacker;
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (playerSoul.hasCast("Furioso")) {
            boolean isCritical = player.getAttackCooldownProgress(0.5F) > 0.9f && player.fallDistance > 0.0F
                    && !player.isOnGround() && !player.isClimbing() && !player.isTouchingWater()
                    && !player.hasStatusEffect(StatusEffects.BLINDNESS) && !player.hasVehicle() && !player.isSprinting();
            if (isCritical) {
                Utils.addAntiheal(0.9f, playerSoul.getLV()*15, target);
                return super.postHit(stack, target, attacker);
            }
        }
        Utils.addAntiheal(0.4f, playerSoul.getLV()*20, target);
        return super.postHit(stack, target, attacker);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(user);
            SoulForge.getValues(user).setBool("Immobilized", true);
            playerSoul.setValue("clawGouge", 23);
            user.getItemCooldownManager().set(this, 300);
        }
        return TypedActionResult.pass(stack);
    }

    public AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private final GeoItemRenderer<PerseveranceClaw> renderer = new GeoMagicItemRenderer<>("claw", "perseverance");

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

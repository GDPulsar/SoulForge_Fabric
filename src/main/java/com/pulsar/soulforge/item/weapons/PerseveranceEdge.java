package com.pulsar.soulforge.item.weapons;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.item.GeoMagicItemRenderer;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.components.TemporaryModifierComponent;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PerseveranceEdge extends MagicSweepingSwordItem implements GeoItem {
    public PerseveranceEdge() {
        super(6f, 1.6f, 0.25f);
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BLOCK;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul((PlayerEntity)attacker);
        float range = playerSoul.hasCast("Furioso") ? (float)ReachEntityAttributes.getAttackRange(attacker, 3f) : 2f;
        for (Entity entity : attacker.getEntityWorld().getOtherEntities(attacker, Box.of(attacker.getPos(), range * 2f, range * 2f, range * 2f))) {
            if (entity.distanceTo(attacker) > range) continue;
            if (entity instanceof LivingEntity living && living != target) {
                if (entity instanceof PlayerEntity targetPlayer && attacker instanceof PlayerEntity player) {
                    if (!TeamUtils.canDamageEntity(player.getServer(), player, targetPlayer)) continue;
                }
                if (living.damage(attacker.getDamageSources().playerAttack((PlayerEntity)attacker), (this.baseAttackDamage + this.lvIncrease * playerSoul.getLV()))) {
                    playerSoul.setStyle(playerSoul.getStyle() + (int)(this.baseAttackDamage + this.lvIncrease * playerSoul.getLV()));
                }
                if (living instanceof PlayerEntity player) {
                    SoulComponent targetSoul = SoulForge.getPlayerSoul(player);
                    Utils.addAntiheal(0.2f, playerSoul.getLV()*5, targetSoul);
                }
            }
        }
        if (target instanceof PlayerEntity player) {
            SoulComponent targetSoul = SoulForge.getPlayerSoul(player);
            Utils.addAntiheal(0.2f, playerSoul.getLV()*5, targetSoul);
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (selected && entity instanceof ServerPlayerEntity player) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            if (playerSoul.hasCast("Furioso")) {
                TemporaryModifierComponent modifers = SoulForge.getTemporaryModifiers(player);
                modifers.addTemporaryModifier(ReachEntityAttributes.ATTACK_RANGE, new EntityAttributeModifier(
                        UUID.fromString("4590a4be-3e5e-473c-a7d0-67f7668712d7"), "furioso_pv_edge",
                        2, EntityAttributeModifier.Operation.ADDITION
                ), 2f);
            }
        }
    }

    public AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private final GeoItemRenderer<PerseveranceEdge> renderer = new GeoMagicItemRenderer<>("edge", "perseverance");

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

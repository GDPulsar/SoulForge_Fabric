package com.pulsar.soulforge.item.weapons;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.entity.JusticeHarpoonProjectile;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class JusticeHarpoon extends MagicSwordItem/* implements GeoItem*/ {
    public JusticeHarpoon() {
        super(5, 1.2f, 0.2f);
        addAttribute(ReachEntityAttributes.ATTACK_RANGE, new EntityAttributeModifier("jt_harpoon_reach", 2f, EntityAttributeModifier.Operation.ADDITION));
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity playerEntity)) {
            return;
        }
        int i = this.getMaxUseTime(stack) - remainingUseTicks;
        if (i < 10) {
            return;
        }
        if (!world.isClient) {
            for (JusticeHarpoonProjectile harpoon : world.getEntitiesByClass(JusticeHarpoonProjectile.class, Box.of(user.getPos(), 200, 200, 200), (entity) -> entity.getOwner() == user)) {
                return;
            }
            SoulComponent playerSoul = SoulForge.getPlayerSoul(playerEntity);
            JusticeHarpoonProjectile projectile = new JusticeHarpoonProjectile(world, playerEntity);
            projectile.setOwner(playerEntity);
            projectile.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0f, playerSoul.hasCast("Furioso") ? 5f : 2.5f, 1.0f);
            if (playerSoul.hasCast("Furioso")) projectile.setNoGravity(true);
            world.spawnEntity(projectile);
            world.playSoundFromEntity(null, projectile, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0f, 1.0f);
        }
        playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        for (JusticeHarpoonProjectile harpoon : world.getEntitiesByClass(JusticeHarpoonProjectile.class, Box.of(user.getPos(), 200, 200, 200), (entity) -> entity.getOwner() == user)) {
            if (harpoon.stuckEntity != null) {
                if (user.isSneaking()) {
                    Vec3d offset = user.getPos().subtract(harpoon.stuckEntity.getPos());
                    harpoon.stuckEntity.addVelocity(offset.normalize().multiply(Math.sqrt(offset.length() * 1.5f)).add(0f, 1.25f, 0f));
                } else {
                    Vec3d offset = user.getPos().subtract(harpoon.stuckEntity.getPos());
                    harpoon.stuckEntity.addVelocity(offset.normalize().multiply(Math.sqrt(offset.length() * 4f)));
                    harpoon.stuckEntity.velocityModified = true;
                }
            }
            harpoon.kill();
        }
        return TypedActionResult.consume(stack);
    }

    /*
    public AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private final GeoItemRenderer<JusticeHarpoon> renderer = new GeoMagicItemRenderer<>("bravery_spear", "justice");

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
    }*/
}

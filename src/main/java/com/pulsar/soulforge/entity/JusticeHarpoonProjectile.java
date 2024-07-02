package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class JusticeHarpoonProjectile extends PersistentProjectileEntity implements GeoEntity {
    public LivingEntity stuckEntity = null;

    public JusticeHarpoonProjectile(World world, LivingEntity owner) {
        super(SoulForgeEntities.JUSTICE_HARPOON_ENTITY_TYPE, owner, world);
        this.pickupType = PickupPermission.DISALLOWED;
    }

    public JusticeHarpoonProjectile(EntityType<JusticeHarpoonProjectile> entityType, World world) {
        super(entityType, world);
        this.pickupType = PickupPermission.DISALLOWED;
    }

    public boolean canUsePortals() {
        return false;
    }

    @Override
    public void tick() {
        if (!this.getWorld().isClient) {
            if (!inGround) {
                if (stuckEntity != null) {
                    if (stuckEntity instanceof PlayerEntity target) {
                        SoulComponent targetSoul = SoulForge.getPlayerSoul(target);
                        Utils.addAntiheal(0.5f, 4f, targetSoul);
                    }
                    this.setPosition(stuckEntity.getPos().add(0f, stuckEntity.getHeight() / 2f, 0f).add(getRotationVector()));
                    this.setVelocity(Vec3d.ZERO);
                    if (this.stuckEntity.isDead() || this.stuckEntity.isRemoved()) {
                        this.setNoGravity(false);
                        this.stuckEntity = null;
                    }
                }
            }
        }
        super.tick();
    }

    @Override
    @Nullable
    protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
        if (inGround) {
            return null;
        }
        return super.getEntityCollision(currentPosition, nextPosition);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        float f = 5.0f;
        Entity entity2 = this.getOwner();
        DamageSource damageSource = this.getDamageSources().trident(this, entity2 == null ? this : entity2);
        if (entity.damage(damageSource, f)) {
            if (entity.getType() == EntityType.ENDERMAN) {
                return;
            }
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity2 = (LivingEntity)entity;
                if (entity2 instanceof LivingEntity) {
                    EnchantmentHelper.onUserDamaged(livingEntity2, entity2);
                    EnchantmentHelper.onTargetDamaged((LivingEntity)entity2, livingEntity2);
                }
                stuckEntity = livingEntity2;
                this.setNoGravity(true);
                this.onHit(livingEntity2);
            }
        }
    }

    @Override
    protected ItemStack asItemStack() {
        return new ItemStack(SoulForgeItems.JUSTICE_HARPOON);
    }

    @Override
    protected SoundEvent getHitSound() {
        return SoundEvents.ITEM_TRIDENT_HIT_GROUND;
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (!this.isOwner(player)) {
            super.onPlayerCollision(player);
        }
    }

    @Override
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        return true;
    }

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "main", 0, (event) -> PlayState.STOP));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}

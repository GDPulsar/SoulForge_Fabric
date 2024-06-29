package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.util.TeamUtils;
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
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

public class LightningRodProjectile extends PersistentProjectileEntity {
    public LightningRodProjectile(World world, LivingEntity owner) {
        super(SoulForgeEntities.LIGHTNING_ROD_ENTITY_TYPE, owner, world);
        this.pickupType = PickupPermission.ALLOWED;
    }

    public LightningRodProjectile(EntityType<LightningRodProjectile> braverySpearProjectileEntityType, World world) {
        super(braverySpearProjectileEntityType, world);
        this.pickupType = PickupPermission.ALLOWED;
    }

    public List<LivingEntity> damaged = new ArrayList<>();

    @Override
    public void tick() {
        if (!this.getWorld().isClient) {
            if (!this.inGround) {
                if (this.getOwner() instanceof PlayerEntity player) {
                    SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                    for (Entity entity : this.getEntityWorld().getOtherEntities(this, Box.of(this.getPos(), 4f, 4f, 4f))) {
                        if (entity == this.getOwner()) continue;
                        if (entity instanceof LivingEntity target) {
                            if (entity instanceof PlayerEntity targetPlayer) {
                                if (!TeamUtils.canDamagePlayer(this.getServer(), player, targetPlayer)) return;
                            }
                            float distance = target.distanceTo(this);
                            if (distance < 2f) {
                                if (!damaged.contains(target)) {
                                    target.damage(this.getDamageSources().playerAttack(player), playerSoul.getLV() / 4f * (1f - distance / 2f));
                                    damaged.add(target);
                                }
                            }
                        }
                    }
                }
            }
        }
        super.tick();
    }

    @Override
    @Nullable
    protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
        if (this.isOnGround()) {
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
            if (entity instanceof LivingEntity livingEntity) {
                if (entity2 instanceof LivingEntity) {
                    EnchantmentHelper.onUserDamaged(livingEntity, entity2);
                    EnchantmentHelper.onTargetDamaged((LivingEntity)entity2, livingEntity);
                }
                this.onHit(livingEntity);
            }
        }
    }

    @Override
    protected boolean tryPickup(PlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (!playerSoul.hasWeapon() && this.isOwner(player)) {
            playerSoul.setWeapon(new ItemStack(SoulForgeItems.LIGHTNING_ROD));
            return true;
        }
        return false;
    }

    @Override
    protected ItemStack asItemStack() {
        return new ItemStack(SoulForgeItems.LIGHTNING_ROD);
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
}

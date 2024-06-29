package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.util.TeamUtils;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

public class FireTornadoProjectile extends Entity implements GeoEntity {
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public PlayerEntity owner = null;

    public FireTornadoProjectile(EntityType<? extends FireTornadoProjectile> entityType, World world) {
        super(entityType, world);
    }

    public FireTornadoProjectile(World world, PlayerEntity owner, Vec3d pos) {
        super(SoulForgeEntities.FIRE_TORNADO_ENTITY_TYPE, world);
        this.owner = owner;
        this.setPosition(pos);
    }

    @Override
    public void tick() {
        calculateDimensions();
        if (age%4 == 0) {
            for (Entity entity : getEntityWorld().getOtherEntities(this, this.getBoundingBox(), Entity::canHit)) {
                if (entity == owner) continue;
                if (entity instanceof LivingEntity living) {
                    if (entity instanceof PlayerEntity targetPlayer && this.owner != null) {
                        if (!TeamUtils.canDamagePlayer(this.getServer(), this.owner, targetPlayer)) return;
                    }
                    float horizDist = (float) living.getPos().subtract(this.getPos()).withAxis(Direction.Axis.Y, 0).length();
                    float vertDist = (float) living.getPos().subtract(this.getPos()).getY();
                    float timer = (float) this.age / 20f;
                    float size = 2.5f + 15f * Math.min(timer, 45f) / 45f;
                    float height = 30f + 40f * Math.min(timer, 45f) / 45f;
                    if ((horizDist < size + (0.3f * size * vertDist) / height) && vertDist < height && vertDist >= -2f) {
                        float damage = 7.5f;
                        if (owner != null) {
                            SoulComponent playerSoul = SoulForge.getPlayerSoul(owner);
                            damage = playerSoul.getEffectiveLV()/4f;
                        }
                        living.damage(this.getDamageSources().onFire(), damage);
                        living.timeUntilRegen = 10;
                    }
                }
            }
        }
        if (age >= 1200) this.kill();
    }

    public EntityDimensions getDimensions(EntityPose pose) {
        float timer = (float)this.age/20f;
        float size = 2.5f + 15f*Math.min(timer,45f)/45f;
        float height = 30f + 40f*Math.min(timer,45f)/45f;
        return new EntityDimensions(size, height, false);
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {}

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        tAnimationState.getController().setAnimation(RawAnimation.begin().then("snowgrave.main", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}

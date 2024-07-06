package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.trait.Traits;
import com.pulsar.soulforge.util.TeamUtils;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BouncingShieldEntity extends ProjectileEntity implements GeoEntity {
    private static final TrackedData<Vector3f> POSITION = DataTracker.registerData(BouncingShieldEntity.class, TrackedDataHandlerRegistry.VECTOR3F);
    public PlayerEntity owner;

    public BouncingShieldEntity(PlayerEntity owner) {
        super(SoulForgeEntities.BOUNCING_SHIELD_ENTITY_TYPE, owner.getWorld());
        this.owner = owner;
        this.setPosition(owner.getEyePos());
        this.setPos(owner.getEyePos());
        this.setVelocity(owner.getRotationVector().multiply(1f));
    }

    public boolean canUsePortals() {
        return false;
    }

    public BouncingShieldEntity(PlayerEntity owner, Vec3d position, Vec3d velocity) {
        super(SoulForgeEntities.BOUNCING_SHIELD_ENTITY_TYPE, owner.getWorld());
        this.owner = owner;
        this.setPosition(position);
        this.setPos(position);
        this.setVelocity(velocity);
    }

    public BouncingShieldEntity(EntityType<BouncingShieldEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(POSITION, new Vector3f(0, 0, 0));
    }

    public void setPos(Vec3d pos) {
        this.dataTracker.set(POSITION, pos.toVector3f());
    }

    public Vec3d getPos() {
        Vector3f vec = this.dataTracker.get(POSITION);
        return new Vec3d(vec.x, vec.y, vec.z);
    }

    private int stallTimer = 0;

    @Override
    public void tick() {
        if (!this.getWorld().isClient) {
            HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
            if (hitResult.getType() != HitResult.Type.MISS) {
                this.onCollision(hitResult);
            }
        }

        this.checkBlockCollision();
        this.setYaw(this.getYaw()+10f);
        if (stallTimer <= 0) {
            this.setPos(this.getPos().add(this.getVelocity()));
        } else {
            stallTimer--;
        }
        this.setPosition(this.getPos());
    }

    protected boolean canHit(Entity entity) {
        if (entity instanceof PlayerEntity targetPlayer && owner != null) {
            if (!TeamUtils.canDamagePlayer(this.getServer(), owner, targetPlayer)) return false;
        }
        return !entity.noClip && super.canHit(entity) && this.owner != entity;
    }

    protected void onCollision(HitResult hitResult) {
        if (this.owner != null) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(this.owner);
            if (hitResult.getType() == HitResult.Type.ENTITY) {
                EntityHitResult hit = (EntityHitResult)hitResult;
                if (hit.getEntity() instanceof ProjectileEntity projectile) {
                    if (projectile.getOwner() != this.owner) projectile.kill();
                    else {
                        LivingEntity nearest = null;
                        for (LivingEntity living : this.getEntityWorld().getEntitiesByClass(LivingEntity.class, Box.of(projectile.getPos(), 40, 40, 40), (target) -> target != this.owner)) {
                            if (nearest == null) nearest = living;
                            else {
                                if (nearest.distanceTo(projectile) > living.distanceTo(projectile)) {
                                    nearest = living;
                                }
                            }
                        }
                        if (nearest != null) projectile.setVelocity(nearest.getPos().subtract(projectile.getPos()).normalize().multiply(projectile.getVelocity().length()*1.5f));
                        stallTimer = 4;
                    }
                } else if (hit.getEntity() instanceof LivingEntity living) {
                    living.damage(this.getDamageSources().thrown(this, this.owner), playerSoul.getEffectiveLV()*0.75f);
                }
            }
            if (playerSoul.getTraits().contains(Traits.kindness) && playerSoul.getTraits().contains(Traits.justice)) {
                Vec3d normal = hitResult.getPos().subtract(this.getPos()).normalize();
                if (hitResult instanceof BlockHitResult hit) {
                    Vector3f unitVec = hit.getSide().getOpposite().getUnitVector();
                    normal = new Vec3d(unitVec.x, unitVec.y, unitVec.z);
                }
                Vec3d reflect = this.getVelocity().subtract(normal.multiply(2f*this.getVelocity().dotProduct(normal)));
                this.setVelocity(reflect);
                LivingEntity target = getTarget();
                if (target != null) {
                    this.setVelocity(target.getPos().add(0f, target.getHeight()/2f, 0f).subtract(this.getPos()).normalize().multiply(1.5f));
                }
            }
        }
    }

    private LivingEntity getTarget() {
        for (Entity entity : this.getEntityWorld().getOtherEntities(this, this.getAttackBox())) {
            if (entity instanceof LivingEntity target) {
                float angle = MathHelper.wrapDegrees((float) (MathHelper.atan2(entity.getX() - this.getX(), entity.getZ() - this.getZ()) * 57.2957763671875));
                float thisAngle = MathHelper.wrapDegrees((float) (MathHelper.atan2(this.getVelocity().x, this.getVelocity().z) * 57.2957763671875));
                if (target.canTakeDamage() && this.canSee(target) && Math.abs(Math.abs(thisAngle) - Math.abs(angle)) <= 10f) {
                    return target;
                }
            }
        }
        return null;
    }

    private boolean canSee(LivingEntity entity) {
        if (entity.getWorld() != this.getWorld()) {
            return false;
        } else {
            Vec3d vec3d = new Vec3d(this.getX(), this.getEyeY(), this.getZ());
            Vec3d vec3d2 = new Vec3d(entity.getX(), entity.getEyeY(), entity.getZ());
            if (vec3d2.distanceTo(vec3d) > 128.0) {
                return false;
            } else {
                return this.getWorld().raycast(new RaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this)).getType() == HitResult.Type.MISS;
            }
        }
    }

    protected Box getAttackBox() {
        return Box.of(this.getPos(), 200, 10, 200);
    }

    @Override
    protected Box calculateBoundingBox() {
        return super.calculateBoundingBox().offset(0, -0.5f, 0);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {}
    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {}
    @Override
    public boolean canHit() { return true; }
    @Override
    public boolean damage(DamageSource source, float damage) {
        if (source.getAttacker() == this.owner) {
            this.setVelocity(this.owner.getRotationVector().normalize().multiply(1.5f));
        }
        return false;
    }
    @Override
    public EntityDimensions getDimensions(EntityPose pose) { return EntityDimensions.fixed(1.1f, 1.1f); }
    @Override
    public boolean shouldSave() { return false; }
    @Override
    public boolean shouldRender(double distance) { return true; }

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

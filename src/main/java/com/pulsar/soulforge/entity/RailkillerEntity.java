package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.item.devices.machines.Railkiller;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
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

import java.awt.*;

public class RailkillerEntity extends Entity implements GeoEntity {
    private static final TrackedData<ItemStack> STACK = DataTracker.registerData(RailkillerEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<Vector3f> DIRECTION = DataTracker.registerData(RailkillerEntity.class, TrackedDataHandlerRegistry.VECTOR3F);
    private static final TrackedData<Vector3f> POSITION = DataTracker.registerData(RailkillerEntity.class, TrackedDataHandlerRegistry.VECTOR3F);

    public RailkillerEntity(EntityType<RailkillerEntity> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(false);
    }

    public boolean canUsePortals() {
        return false;
    }

    public RailkillerEntity(World world, Vec3d position, Vec3d direction, ItemStack stack) {
        super(SoulForgeEntities.RAILKILLER_ENTITY_TYPE, world);
        this.dataTracker.set(STACK, stack);
        this.dataTracker.set(DIRECTION, direction.toVector3f());
        this.dataTracker.set(POSITION, position.toVector3f());
        this.setNoGravity(false);
    }

    public void setStack(ItemStack stack) {
        this.dataTracker.set(STACK, stack);
    }

    public void setDirection(Vec3d direction) {
        this.dataTracker.set(DIRECTION, direction.toVector3f());
    }

    public void setPos(Vec3d position) {
        this.dataTracker.set(POSITION, position.toVector3f());
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(STACK, ItemStack.EMPTY);
        this.dataTracker.startTracking(DIRECTION, new Vector3f());
        this.dataTracker.startTracking(POSITION, new Vector3f());
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

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        ItemStack stack = this.dataTracker.get(STACK);
        NbtCompound stackNbt = new NbtCompound();
        stack.writeNbt(stackNbt);
        nbt.put("stack", stackNbt);
        Vector3f direction = this.dataTracker.get(DIRECTION);
        NbtCompound directionNbt = new NbtCompound();
        directionNbt.putFloat("x", direction.x);
        directionNbt.putFloat("y", direction.y);
        directionNbt.putFloat("z", direction.z);
        nbt.put("direction", directionNbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("stack")) {
            this.dataTracker.set(STACK, ItemStack.fromNbt(nbt.getCompound("stack")));
        }
        if (nbt.contains("direction")) {
            NbtCompound directionNbt = nbt.getCompound("direction");
            this.dataTracker.set(DIRECTION, new Vector3f(directionNbt.getFloat("x"), directionNbt.getFloat("y"), directionNbt.getFloat("z")));
        }
    }

    BlastEntity blast = null;
    @Override
    public void baseTick() {
        if (!this.getWorld().isClient) {
            this.setPitch(0f);
            this.setPosition(Utils.vector3fToVec3d(this.dataTracker.get(POSITION)));
            Vec3d direction = Utils.vector3fToVec3d(this.dataTracker.get(DIRECTION));
            ItemStack stack = this.dataTracker.get(STACK);
            this.setYaw((float)(MathHelper.atan2(direction.x, direction.z) * 57.2957763671875));
            this.getWorld().breakBlock(this.getBlockPos(), true);
            if (this.age >= 20 && blast == null) {
                this.setNoGravity(true);
                Vec3d start = getPos().add(direction.multiply(0.5f)).add(0, 1, 0);
                Vec3d end = start.add(direction.multiply(75f));
                HitResult hit = getWorld().raycast(new RaycastContext(start, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
                if (hit != null) end = hit.getPos();
                blast = new BlastEntity(getWorld(), getPos(), null, 1f, Vec3d.ZERO, end.subtract(start), 25f, Color.YELLOW, true, getItemCharge()/3);
                blast.setPos(start);
                getWorld().spawnEntity(blast);
                getWorld().playSoundFromEntity(null, this, SoulForgeSounds.UT_BLASTER_EVENT, SoundCategory.MASTER, 1f, 1f);
            }
            this.setNoGravity(this.age >= 20);
            if (getItemCharge() <= 0) {
                ItemEntity droppedItem = new ItemEntity(getWorld(), getX(), getY(), getZ(), stack);
                droppedItem.setPosition(getPos());
                getWorld().spawnEntity(droppedItem);
                this.kill();
            }
            if (blast != null) {
                Vec3d start = getPos().add(direction.multiply(0.5f)).add(0, 1, 0);
                Vec3d end = start.add(direction.multiply(75f));
                HitResult hit = getWorld().raycast(new RaycastContext(start, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
                if (hit != null) end = hit.getPos();
                blast.setPos(start);
                blast.setEnd(end.subtract(start));
                boolean isGrounded = false;
                HitResult groundHit = getWorld().raycast(new RaycastContext(getPos(), getPos().subtract(0, 2, 0), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
                if (groundHit.getPos().distanceTo(getPos()) <= 0.25f) isGrounded = true;
                this.setPos(this.getPos().subtract(direction.normalize().multiply(isGrounded ? 0.05f : 1f)));
                setItemCharge(this.getItemCharge() - 3);
            }
        }
        super.baseTick();
    }

    public int getItemCharge() {
        ItemStack stack = this.dataTracker.get(STACK);
        if (!stack.isEmpty()) {
            Railkiller railkiller = (Railkiller)stack.getItem();
            return railkiller.getCharge(stack);
        }
        return 0;
    }

    public void setItemCharge(int charge) {
        ItemStack stack = this.dataTracker.get(STACK);
        if (!stack.isEmpty()) {
            Railkiller railkiller = (Railkiller)stack.getItem();
            railkiller.setCharge(stack, charge);
        }
    }

    @Override
    protected Box calculateBoundingBox() {
        return super.calculateBoundingBox().offset(0, 0.2f, 0);
    }
}

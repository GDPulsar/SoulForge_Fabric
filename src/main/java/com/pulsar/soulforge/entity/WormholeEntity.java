package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Set;

public class WormholeEntity extends Entity {
    private static final TrackedData<Float> SIZE = DataTracker.registerData(WormholeEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Vector3f> CRACK_MOD = DataTracker.registerData(WormholeEntity.class, TrackedDataHandlerRegistry.VECTOR3F);
    public ArrayList<Vec3d> cracks = new ArrayList<>();
    private Vec3d targetPos;
    private ServerWorld targetWorld;

    public WormholeEntity(World world, double x, double y, double z, ServerWorld targetWorld,
                          Vec3d targetPos, float size, Vec3d crackModifier) {
        super(SoulForgeEntities.WORMHOLE_ENTITY_TYPE, world);
        this.setPosition(x, y, z);
        this.setPos(x, y, z);
        this.targetPos = targetPos;
        this.targetWorld = targetWorld;
        this.setSize(size);
        this.setCrackMod(crackModifier);
    }

    public boolean canUsePortals() {
        return false;
    }

    public WormholeEntity(World world, Vec3d position, ServerWorld targetWorld, Vec3d targetPos) {
        super(SoulForgeEntities.WORMHOLE_ENTITY_TYPE, world);
        this.setPosition(position);
        this.targetPos = targetPos;
        this.targetWorld = targetWorld;
        this.ignoreCameraFrustum = true;
    }

    public WormholeEntity(EntityType<? extends Entity> type, World world) {
        super(type, world);
        this.ignoreCameraFrustum = true;
    }

    public float getSize() {
        return this.dataTracker.get(SIZE);
    }

    public void setSize(float size) {
        this.dataTracker.set(SIZE, size);
    }

    public Vec3d getCrackMod() {
        return Utils.vector3fToVec3d(this.dataTracker.get(CRACK_MOD));
    }

    public void setCrackMod(Vec3d mod) {
        this.dataTracker.set(CRACK_MOD, mod.toVector3f());
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(SIZE, 1f);
        this.dataTracker.startTracking(CRACK_MOD, new Vector3f(1f, 1f, 1f));
    }

    public int timer = 0;
    @Override
    public void tick() {
        if (targetWorld != null) {
            for (Entity entity : this.getWorld().getOtherEntities(this, Box.of(this.getPos(), 1, 1, 1))) {
                entity.teleport(targetWorld, targetPos.x, targetPos.y, targetPos.z, Set.of(), entity.getYaw(), entity.getPitch());
            }
            if (this.timer >= 200) this.kill();
            timer++;
        }
        super.tick();
        if (this.cracks.isEmpty()) {
            for(int i = 0; i < 5; ++i) {
                Vec3d add = Vec3d.ZERO;

                for(int tries = 0; add.length() < (new Vec3d(this.getCrackMod().x * (double)(4.0F * this.getSize()), this.getCrackMod().y * (double)(4.0F * this.getSize()), this.getCrackMod().z * (double)(4.0F * this.getSize()))).length() || !this.getWorld().getBlockState(new BlockPos((int)(this.getX() + add.getX()), (int) (this.getY() + add.getY()), (int) (this.getZ() + add.getZ()))).isAir(); ++tries) {
                    add = new Vec3d(this.getWorld().random.nextGaussian() * (double)(4.0F * this.getSize()) * this.getCrackMod().x, this.getWorld().random.nextGaussian() * (double)(4.0F * this.getSize()) * this.getCrackMod().y, this.getWorld().random.nextGaussian() * (double)(4.0F * this.getSize()) * this.getCrackMod().z);
                }

                this.cracks.add(add);
            }
        }

        if (this.age == 1) {
            this.getWorld().playSound(this, this.getBlockPos(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.MASTER, 5.0F, 1.0F);
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        NbtList posList = new NbtList();
        posList.add(NbtDouble.of(targetPos.x)); posList.add(NbtDouble.of(targetPos.y)); posList.add(NbtDouble.of(targetPos.y));
        nbt.put("targetPos", new NbtList());
        nbt.putFloat("size", getSize());
        NbtList crackModList = new NbtList();
        crackModList.add(NbtDouble.of(getCrackMod().x)); crackModList.add(NbtDouble.of(getCrackMod().y)); crackModList.add(NbtDouble.of(getCrackMod().y));
        nbt.put("crackMod", new NbtList());
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        NbtList posList = nbt.getList("targetPos", NbtElement.DOUBLE_TYPE);
        targetPos = new Vec3d(posList.getDouble(0), posList.getDouble(1), posList.getDouble(2));
        setSize(nbt.getInt("size"));
        NbtList crackModList = nbt.getList("crackMod", NbtElement.DOUBLE_TYPE);
        setCrackMod(new Vec3d(crackModList.getDouble(0), crackModList.getDouble(1), crackModList.getDouble(2)));
    }

    @Override
    public boolean shouldRender(double distance) {
        return true;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return EntityDimensions.fixed(0.5f, 2f);
    }
}

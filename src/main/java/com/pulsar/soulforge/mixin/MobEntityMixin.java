package com.pulsar.soulforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.pulsar.soulforge.accessors.OwnableMinion;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Arm;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity implements OwnableMinion {
    @Shadow @Final private DefaultedList<ItemStack> armorItems;
    @Shadow @Final private DefaultedList<ItemStack> handItems;

    @Shadow public abstract boolean isLeftHanded();

    @Shadow @Nullable private LivingEntity target;

    @Shadow public abstract void setPositionTarget(BlockPos target, int range);

    @Shadow public abstract EntityNavigation getNavigation();

    @Unique
    private static final TrackedData<Optional<UUID>> MINION_OWNER = DataTracker.registerData(MobEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    @Unique
    private static final TrackedData<Vector3f> MINION_TARGET_POS = DataTracker.registerData(MobEntity.class, TrackedDataHandlerRegistry.VECTOR3F);
    @Unique
    private static final TrackedData<Optional<UUID>> MINION_TARGET = DataTracker.registerData(MobEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);

    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    protected void addToTracker(CallbackInfo ci) {
        this.dataTracker.startTracking(MINION_OWNER, Optional.empty());
        this.dataTracker.startTracking(MINION_TARGET_POS, new Vector3f());
        this.dataTracker.startTracking(MINION_TARGET, Optional.empty());
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    public void writeMinionData(NbtCompound nbt, CallbackInfo ci) {
        if (this.dataTracker.get(MINION_OWNER).isPresent()) nbt.putUuid("minionOwner", this.dataTracker.get(MINION_OWNER).get());
        if (this.dataTracker.get(MINION_TARGET).isPresent()) nbt.putUuid("minionTarget", this.dataTracker.get(MINION_TARGET).get());
        nbt.put("minionTargetPos", Utils.vectorToNbt(Utils.vector3fToVec3d(this.dataTracker.get(MINION_TARGET_POS))));
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void readMinionData(NbtCompound nbt, CallbackInfo ci) {
        this.dataTracker.set(MINION_OWNER, nbt.contains("minionOwner") ? Optional.of(nbt.getUuid("minionOwner")) : Optional.empty());
        this.dataTracker.set(MINION_TARGET, nbt.contains("minionOwner") ? Optional.of(nbt.getUuid("minionOwner")) : Optional.empty());
        this.dataTracker.set(MINION_TARGET_POS, Utils.nbtToVector(nbt.getList("minionTargetPos", NbtElement.DOUBLE_TYPE)).toVector3f());
    }

    @Override
    public UUID getOwnerUUID() { return this.dataTracker.get(MINION_OWNER).orElse(null); }
    @Override
    public void setOwnerUUID(UUID ownerUUID) { this.dataTracker.set(MINION_OWNER, Optional.of(ownerUUID)); }
    @Override
    public Vec3d getTargetPos() { return Utils.vector3fToVec3d(this.dataTracker.get(MINION_TARGET_POS)); }
    @Override
    public void setTargetPos(Vec3d targetPos) {
        this.getNavigation().findPathTo(targetPos.x, targetPos.y, targetPos.z, (int)this.getPos().distanceTo(targetPos));
        this.dataTracker.set(MINION_TARGET_POS, targetPos.toVector3f());
    }
    @Override
    public UUID getTargetUUID() { return this.dataTracker.get(MINION_TARGET).orElse(null); }
    @Override
    public void setTargetUUID(UUID targetUUID) { this.dataTracker.set(MINION_TARGET, Optional.of(targetUUID)); }

    @ModifyReturnValue(method = "getTarget", at = @At("RETURN"))
    private LivingEntity modifyTarget(LivingEntity original) {
        if (getOwnerUUID() != null) {
            if (getTargetUUID() != null) {
                List<LivingEntity> target = this.getWorld().getEntitiesByClass(LivingEntity.class, Box.of(this.getPos(), 400, 400, 400), (entity) -> entity.getUuid().compareTo(getTargetUUID()) == 0);
                if (!target.isEmpty()) {
                    return target.get(0);
                }
            } else if (getTargetPos() != Vec3d.ZERO) {
                return null;
            }
        }
        return original;
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return this.armorItems;
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return switch (slot.getType()) {
            case HAND -> this.handItems.get(slot.getEntitySlotId());
            case ARMOR -> this.armorItems.get(slot.getEntitySlotId());
        };
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {
        this.processEquippedStack(stack);
        switch (slot.getType()) {
            case HAND -> this.onEquipStack(slot, this.handItems.set(slot.getEntitySlotId(), stack), stack);
            case ARMOR -> this.onEquipStack(slot, this.armorItems.set(slot.getEntitySlotId(), stack), stack);
        }
    }

    @Override
    public Arm getMainArm() {
        return this.isLeftHanded() ? Arm.LEFT : Arm.RIGHT;
    }
}

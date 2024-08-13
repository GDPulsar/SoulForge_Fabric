package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.components.TemporaryModifierComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Optional;
import java.util.UUID;

public class SnowgraveProjectile extends Entity implements GeoEntity {
    private static final TrackedData<Vector3f> POSITION = DataTracker.registerData(SnowgraveProjectile.class, TrackedDataHandlerRegistry.VECTOR3F);
    private static final TrackedData<Float> DAMAGE = DataTracker.registerData(SnowgraveProjectile.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(SnowgraveProjectile.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private PlayerEntity owner;
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public SnowgraveProjectile(EntityType<? extends SnowgraveProjectile> entityType, World world) {
        super(entityType, world);
    }

    public SnowgraveProjectile(PlayerEntity owner, World world, Vec3d pos) {
        super(SoulForgeEntities.SNOWGRAVE_PROJECTILE_TYPE, world);
        this.owner = owner;
        this.dataTracker.set(OWNER_UUID, Optional.of(owner.getUuid()));
        SoulComponent playerSoul = SoulForge.getPlayerSoul(owner);
        this.dataTracker.set(DAMAGE, (float)playerSoul.getEffectiveLV());
        this.dataTracker.set(POSITION, pos.toVector3f());
        this.setPosition(pos);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(POSITION, new Vector3f());
        this.dataTracker.startTracking(DAMAGE, 20f);
        this.dataTracker.startTracking(OWNER_UUID, Optional.empty());
    }

    public void setPos(Vec3d position) { this.dataTracker.set(POSITION, position.toVector3f()); }
    public Vec3d getPos() { return Utils.vector3fToVec3d(this.dataTracker.get(POSITION)); }

    @Override
    public void tick() {
        if (this.age % 10 == 0) {
            if (owner == null && this.dataTracker.get(OWNER_UUID).isPresent()) {
                owner = this.getWorld().getPlayerByUuid(this.dataTracker.get(OWNER_UUID).get());
            } else if (owner != null && this.dataTracker.get(OWNER_UUID).isPresent()) {
                if (this.dataTracker.get(OWNER_UUID).get().compareTo(owner.getUuid()) != 0) {
                    owner = this.getWorld().getPlayerByUuid(this.dataTracker.get(OWNER_UUID).get());
                }
            }
            if (owner != null) {
                for (Entity entity : getWorld().getOtherEntities(this, getBoundingBox())) {
                    if (entity instanceof LivingEntity living) {
                        DamageSource source = SoulForgeDamageTypes.of(owner, this.getWorld(), SoulForgeDamageTypes.ABILITY_PROJECTILE_DAMAGE_TYPE);
                        if (living.damage(source, this.dataTracker.get(DAMAGE)) && owner != null) {
                            SoulComponent playerSoul = SoulForge.getPlayerSoul(owner);
                            playerSoul.setStyle(playerSoul.getStyle() + this.dataTracker.get(DAMAGE).intValue());
                            TemporaryModifierComponent modifiers = SoulForge.getTemporaryModifiers(living);
                            modifiers.addStackingTemporaryModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, new EntityAttributeModifier(
                                    UUID.fromString("4e72a713-f496-41e2-a5d1-9ed15e75b4dc"), "proceed", -playerSoul.getEffectiveLV() * 0.02f,
                                    EntityAttributeModifier.Operation.MULTIPLY_BASE), 200);
                            modifiers.addStackingTemporaryModifier(SoulForgeAttributes.AIR_SPEED_BECAUSE_MOJANG_SUCKS, new EntityAttributeModifier(
                                    UUID.fromString("4e72a713-f496-41e2-a5d1-9ed15e75b4dc"), "proceed", -playerSoul.getEffectiveLV() * 0.02f,
                                    EntityAttributeModifier.Operation.MULTIPLY_BASE), 200);
                            modifiers.addStackingTemporaryModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, new EntityAttributeModifier(
                                    UUID.fromString("4e72a713-f496-41e2-a5d1-9ed15e75b4dc"), "proceed", -playerSoul.getEffectiveLV() * 0.02f,
                                    EntityAttributeModifier.Operation.MULTIPLY_TOTAL), 200);
                            modifiers.addStackingTemporaryModifier(SoulForgeAttributes.AIR_SPEED_BECAUSE_MOJANG_SUCKS, new EntityAttributeModifier(
                                    UUID.fromString("4e72a713-f496-41e2-a5d1-9ed15e75b4dc"), "proceed", -playerSoul.getEffectiveLV() * 0.02f,
                                    EntityAttributeModifier.Operation.MULTIPLY_TOTAL), 200);
                            modifiers.addStackingTemporaryModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(
                                    UUID.fromString("4e72a713-f496-41e2-a5d1-9ed15e75b4dc"), "proceed", -playerSoul.getEffectiveLV() * 0.02f,
                                    EntityAttributeModifier.Operation.MULTIPLY_TOTAL), 200);
                            if (this.age >= 55) {
                                living.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.FROSTBITE, playerSoul.getEffectiveLV() * 10, (int) (playerSoul.getEffectiveLV() / 5f)));
                                living.getWorld().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 1f, 1f);
                                TotalFrostbiteEntity frostbite = new TotalFrostbiteEntity(living.getWorld(), living.getPos(), 20, living, owner);
                                frostbite.maxHealth = 20;
                                frostbite.health = 20;
                                frostbite.setPosition(entity.getPos());
                                frostbite.setEntity(living);
                                frostbite.setSize((float) Math.max(entity.getBoundingBox().getXLength(), entity.getBoundingBox().getZLength()), (float) entity.getBoundingBox().getYLength());
                                ServerWorld serverWorld = this.getServer().getWorld(this.getWorld().getRegistryKey());
                                serverWorld.spawnEntity(frostbite);
                                entity.setInvulnerable(true);
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean canUsePortals() {
        return false;
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

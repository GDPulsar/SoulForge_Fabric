package com.pulsar.soulforge.util;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.components.EntityInitializer;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.trait.TraitBase;
import com.pulsar.soulforge.trait.Traits;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.Heightmap;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.Predicate;

public class Utils {
    public static Vec3d vector3fToVec3d(Vector3f vector3f) {
        return new Vec3d(vector3f.x, vector3f.y, vector3f.z);
    }

    public static EntityHitResult getFocussedEntity(PlayerEntity player, float distance) {
        Vec3d start = player.getEyePos();
        Vec3d direction = player.getRotationVector().multiply(10000);
        Box searchBox = player.getBoundingBox().expand(distance);
        return ProjectileUtil.getEntityCollision(player.getWorld(), player, start, start.add(direction), searchBox, EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR);
    }

    public static EntityHitResult getFocussedEntity(PlayerEntity player, float distance, Predicate<Entity> predicate) {
        Vec3d start = player.getEyePos();
        Vec3d direction = player.getRotationVector().multiply(distance);
        Box searchBox = player.getBoundingBox().expand(distance);
        return ProjectileUtil.getEntityCollision(player.getWorld(), player, start, start.add(direction), searchBox, predicate);
    }

    public static BlockPos getTopBlock(MinecraftServer server, World world, int x, int z) throws Exception {
        //shamelessly stolen from the execute command
        ServerWorld serverWorld = server.getWorld(world.getRegistryKey());
        if (!serverWorld.isChunkLoaded(ChunkSectionPos.getSectionCoord(x), ChunkSectionPos.getSectionCoord(z))) {
            throw new Exception("Chunk is not loaded.");
        } else {
            int i = serverWorld.getTopY(Heightmap.Type.MOTION_BLOCKING, x, z);
            return new BlockPos(x, i, z);
        }
    }

    public static void addAntiheal(float amount, float duration, SoulComponent targetSoul) {
        targetSoul.setAntiheal(amount, duration);
    }

    public static void clearModifiersByName(LivingEntity living, EntityAttribute attribute, String name) {
        if (living.getAttributeInstance(attribute) != null) {
            for (EntityAttributeModifier modifier : Set.copyOf(living.getAttributeInstance(attribute).getModifiers())) {
                if (Objects.equals(modifier.getName(), name)) {
                    living.getAttributeInstance(attribute).tryRemoveModifier(modifier.getId());
                }
            }
        }
    }

    public static void clearModifiersByUUID(LivingEntity living, EntityAttribute attribute, UUID uuid) {
        if (living.getAttributeInstance(attribute) != null) {
            living.getAttributeInstance(attribute).removeModifier(uuid);
        }
    }

    public static List<LivingEntity> getEntitiesInFrontOf(PlayerEntity player, float width, float depth, float distDown, float distUp) {
        float cos = MathHelper.cos(player.getYaw() * MathHelper.RADIANS_PER_DEGREE);
        float sin = MathHelper.sin(player.getYaw() * MathHelper.RADIANS_PER_DEGREE);
        float cos90 = -MathHelper.cos((player.getYaw() - 90) * MathHelper.RADIANS_PER_DEGREE);
        float sin90 = -MathHelper.sin((player.getYaw() - 90) * MathHelper.RADIANS_PER_DEGREE);
        int boxSize = MathHelper.floor(width+depth);
        Box box = new Box(player.getX() - boxSize, player.getY() - distDown, player.getZ() - boxSize, player.getX() + boxSize, player.getY() + distUp, player.getZ() + boxSize);
        Vec3d f = new Vec3d(-sin, 0, cos);
        Vec3d s = new Vec3d(sin90, 0, cos90);
        List<LivingEntity> targets = new ArrayList<>();
        for (Entity entity : player.getEntityWorld().getOtherEntities(player, box)) {
            if (entity instanceof LivingEntity living) {
                float df = (float) ((living.getX() - player.getX()) * f.x + (living.getZ() - player.getZ()) * f.z);
                float ds = (float) ((living.getX() - player.getX()) * s.x + (living.getZ() - player.getZ()) * s.z);
                if (df <= depth && df >= 0 && ds <= width && ds >= -width) {
                    targets.add(living);
                }
            }
        }
        return targets;
    }

    public static List<Entity> getEntitiesInDirection(Entity entity, Vec3d direction, float width, float depth, float distDown, float distUp) {
        int boxSize = MathHelper.floor(width+depth);
        Box box = new Box(entity.getX() - boxSize, entity.getY() - distDown, entity.getZ() - boxSize, entity.getX() + boxSize, entity.getY() + distUp, entity.getZ() + boxSize);
        Vec3d s = direction.rotateY(MathHelper.PI/2f);
        List<Entity> targets = new ArrayList<>();
        for (Entity target : entity.getEntityWorld().getOtherEntities(null, box)) {
            float df = (float) ((target.getX() - entity.getX()) * direction.x + (target.getZ() - entity.getZ()) * direction.z);
            float ds = (float) ((target.getX() - entity.getX()) * s.x + (target.getZ() - entity.getZ()) * s.z);
            if (df <= depth && df >= 0 && ds <= width && ds >= -width) {
                targets.add(target);
            }
        }
        return targets;
    }

    public static boolean abilityInstanceInList(AbilityBase ability, List<AbilityBase> list) {
        for (AbilityBase test : list) {
            if (ability.getID() == test.getID()) return true;
        }
        return false;
    }

    public static Text getTraitText(SoulComponent playerSoul) {
        MutableText text = Text.literal(playerSoul.getTrait(0).getName());
        text = text.setStyle(playerSoul.getTrait(0).getStyle());
        if (playerSoul.getTraitCount() >= 2) {
            text = text.append(Text.literal("-").formatted(Formatting.RESET));
            MutableText trait2 = Text.literal(playerSoul.getTrait(1).getName()).setStyle(playerSoul.getTrait(1).getStyle());
            text.append(trait2);
        }
        if (playerSoul.isPure()) text = Text.literal("Pure ").append(text);
        if (playerSoul.isStrong() || playerSoul.getTraits().contains(Traits.determination)) text = text.setStyle(text.getStyle().withFormatting(Formatting.BOLD));
        return text;
    }

    public static boolean isImbued(ItemStack stack, PlayerEntity player) {
        return getImbuer(stack, player) != null;
    }

    public static ItemStack getImbuer(ItemStack stack, PlayerEntity player) {
        if (stack.getNbt().contains("imbuedId")) {
            for (int i = 0; i < player.getInventory().size(); i++) {
                ItemStack check = player.getInventory().getStack(i);
                if (check.isOf(SoulForgeItems.SIPHON_IMBUER)) {
                    check.getOrCreateNbt();
                    if (stack.getNbt().getUuid("imbuedId").compareTo(check.getNbt().getUuid("imbuedId")) == 0) {
                        return check;
                    }
                }
            }
        }
        return null;
    }

    public static ItemStack getImbuedById(UUID uuid, PlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            stack.getOrCreateNbt();
            if (stack.getNbt().contains("imbued") && stack.getNbt().contains("imbuedId")) {
                if (stack.getNbt().getUuid("imbuedId").compareTo(uuid) == 0) return stack;
            }
        }
        return null;
    }

    public static List<Entity> visibleEntitiesInBox(Entity entity, Box box) {
        List<Entity> visible = new ArrayList<>();
        for (Entity aoe : entity.getEntityWorld().getOtherEntities(null, box)) {
            RaycastContext ctx = new RaycastContext(entity.getPos(), aoe.getPos(), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity);
            if (entity.getWorld().raycast(ctx).getPos().distanceTo(aoe.getPos()) < 0.05f) {
                visible.add(aoe);
            }
        }
        return visible;
    }

    public static Vec3d getArmPosition(PlayerEntity player) {
        float angle = player.getMainArm() == Arm.RIGHT ? (float)(-Math.PI/2f) : (float)(Math.PI/2f);
        return player.getEyePos().add(player.getRotationVector().rotateY(angle).withAxis(Direction.Axis.Y, 0).multiply(0.4f)).subtract(0, 0.3f, 0);
    }

    public static void addEffectDuration(LivingEntity target, StatusEffect effect, int duration, int amplifier) {
        if (target.hasStatusEffect(StatusEffects.SLOWNESS)) {
            if (target.getStatusEffect(StatusEffects.SLOWNESS).getAmplifier() >= amplifier) {
                duration += target.getStatusEffect(StatusEffects.SLOWNESS).getDuration();
            }
        }
        target.addStatusEffect(new StatusEffectInstance(effect, duration, amplifier));
    }

    public static int getTotalEffectLevel(LivingEntity target) {
        int val = 0;
        for (StatusEffectInstance effect : target.getStatusEffects()) {
            val += effect.getAmplifier();
        }
        return val;
    }

    public static int getTotalDebuffLevel(LivingEntity target) {
        int val = 0;
        for (StatusEffectInstance effect : target.getStatusEffects()) {
            if (!effect.getEffectType().isBeneficial()) val += effect.getAmplifier();
        }
        return val;
    }

    public static NbtList vectorToNbt(Vec3d vec) {
        NbtList list = new NbtList();
        list.add(NbtDouble.of(vec.x));
        list.add(NbtDouble.of(vec.y));
        list.add(NbtDouble.of(vec.z));
        return list;
    }

    public static Vec3d nbtToVector(NbtList list) {
        return new Vec3d(list.getDouble(0), list.getDouble(1), list.getDouble(2));
    }

    public static boolean isInverted(SoulComponent playerSoul) {
        if (playerSoul.getTraits().contains(Traits.fear) ||
                playerSoul.getTraits().contains(Traits.ineptitude) ||
                playerSoul.getTraits().contains(Traits.misery) ||
                playerSoul.getTraits().contains(Traits.anxiety) ||
                playerSoul.getTraits().contains(Traits.paranoia) ||
                playerSoul.getTraits().contains(Traits.despair)) {
            return true;
        }
        return false;
    }

    public static TraitBase getInvertedVariant(TraitBase trait) {
        if (trait == Traits.bravery) return Traits.fear;
        if (trait == Traits.justice) return Traits.ineptitude;
        if (trait == Traits.kindness) return Traits.misery;
        if (trait == Traits.patience) return Traits.anxiety;
        if (trait == Traits.integrity) return Traits.paranoia;
        if (trait == Traits.perseverance) return Traits.despair;
        if (trait == Traits.determination) return Traits.spite;
        return null;
    }

    public static void addTemporaryAttribute(LivingEntity entity, EntityAttribute attribute, EntityAttributeModifier modifier, int duration) {
        EntityInitializer.TEMPORARY_MODIFIERS.get(entity).addTemporaryModifier(attribute, modifier, duration);
    }

    public static void removeTemporaryAttribute(LivingEntity entity, EntityAttribute attribute, EntityAttributeModifier modifier) {
        EntityInitializer.TEMPORARY_MODIFIERS.get(entity).removeTemporaryModifier(attribute, modifier);
    }
}

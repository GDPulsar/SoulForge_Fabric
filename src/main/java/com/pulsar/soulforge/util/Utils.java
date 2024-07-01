package com.pulsar.soulforge.util;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.trait.Traits;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
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
        Vec3d direction = player.getRotationVector().multiply(10000);
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
        if (targetSoul.hasValue("antiheal")) targetSoul.setValue("antiheal", Math.max(targetSoul.getValue("antiheal"), amount));
        else targetSoul.setValue("antiheal", amount);
        if (targetSoul.hasValue("antihealDuration")) targetSoul.setValue("antihealDuration", Math.max(targetSoul.getValue("antihealDuration"), duration));
        else targetSoul.setValue("antihealDuration", duration);
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
        if (playerSoul.isPure()) text = Text.literal("Pure ").append(text);
        if (playerSoul.getTrait(0) == Traits.bravery) text = text.formatted(Formatting.GOLD);
        if (playerSoul.getTrait(0) == Traits.justice) text = text.formatted(Formatting.YELLOW);
        if (playerSoul.getTrait(0) == Traits.kindness) text = text.formatted(Formatting.GREEN);
        if (playerSoul.getTrait(0) == Traits.patience) text = text.formatted(Formatting.BLUE);
        if (playerSoul.getTrait(0) == Traits.integrity) text = text.formatted(Formatting.DARK_BLUE);
        if (playerSoul.getTrait(0) == Traits.perseverance) text = text.formatted(Formatting.DARK_PURPLE);
        if (playerSoul.getTrait(0) == Traits.determination) text = text.formatted(Formatting.DARK_RED);
        if (playerSoul.getTraitCount() >= 2) {
            text = text.append(Text.literal("-").formatted(Formatting.RESET));
            MutableText trait2 = Text.literal(playerSoul.getTrait(1).getName());
            if (playerSoul.getTrait(1) == Traits.bravery) trait2 = trait2.formatted(Formatting.GOLD);
            if (playerSoul.getTrait(1) == Traits.justice) trait2 = trait2.formatted(Formatting.YELLOW);
            if (playerSoul.getTrait(1) == Traits.kindness) trait2 = trait2.formatted(Formatting.GREEN);
            if (playerSoul.getTrait(1) == Traits.patience) trait2 = trait2.formatted(Formatting.BLUE);
            if (playerSoul.getTrait(1) == Traits.integrity) trait2 = trait2.formatted(Formatting.DARK_BLUE);
            if (playerSoul.getTrait(1) == Traits.perseverance) trait2 = trait2.formatted(Formatting.DARK_PURPLE);
            if (playerSoul.getTrait(1) == Traits.determination) trait2 = trait2.formatted(Formatting.DARK_RED);
            text.append(trait2);
        }
        if (playerSoul.isStrong() || playerSoul.getTraits().contains(Traits.determination)) text = text.formatted(Formatting.BOLD);
        return text;
    }

    public static boolean isImbued(ItemStack stack, PlayerEntity player) {
        return getImbuer(stack, player) != null;
    }

    public static ItemStack getImbuer(ItemStack stack, PlayerEntity player) {
        if (Boolean.TRUE.equals(stack.get(SoulForgeItems.IMBUED_COMPONENT))) {
            for (int i = 0; i < player.getInventory().size(); i++) {
                ItemStack check = player.getInventory().getStack(i);
                if (check.isOf(SoulForgeItems.SIPHON_IMBUER)) {
                    if (Objects.requireNonNull(stack.get(SoulForgeItems.IMBUED_ID_COMPONENT)).compareTo(Objects.requireNonNull(check.get(SoulForgeItems.IMBUED_ID_COMPONENT))) == 0) {
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
            if (Boolean.TRUE.equals(stack.get(SoulForgeItems.IMBUED_COMPONENT)) && stack.contains(SoulForgeItems.IMBUED_ID_COMPONENT)) {
                if (Objects.requireNonNull(stack.get(SoulForgeItems.IMBUED_ID_COMPONENT)).compareTo(uuid) == 0) return stack;
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

    public static void addEffectDuration(LivingEntity target, RegistryEntry<StatusEffect> effect, int duration, int amplifier) {
        if (target.hasStatusEffect(StatusEffects.SLOWNESS)) {
            if (Objects.requireNonNull(target.getStatusEffect(StatusEffects.SLOWNESS)).getAmplifier() >= amplifier) {
                duration += Objects.requireNonNull(target.getStatusEffect(StatusEffects.SLOWNESS)).getDuration();
            }
        }
        target.addStatusEffect(new StatusEffectInstance(effect, duration, amplifier));
    }

    public static Identifier convertAbilityNameToID(String abilityName) {
        return Identifier.of(SoulForge.MOD_ID, abilityName.toLowerCase().replace(" ", "_"));
    }

    public static void addModifier(PlayerEntity player, RegistryEntry<EntityAttribute> attribute, String id, float amount, EntityAttributeModifier.Operation operation) {
        player.getAttributeInstance(attribute).addPersistentModifier(new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, id), amount, operation));
    }

    public static void removeModifier(PlayerEntity player, RegistryEntry<EntityAttribute> attribute, String id) {
        player.getAttributeInstance(attribute).removeModifier(Identifier.of(SoulForge.MOD_ID, id));
    }
}

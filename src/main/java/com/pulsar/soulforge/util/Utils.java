package com.pulsar.soulforge.util;

import com.mojang.brigadier.context.CommandContext;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.components.*;
import com.pulsar.soulforge.entity.ShieldShardEntity;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.tag.SoulForgeTags;
import com.pulsar.soulforge.trait.TraitBase;
import com.pulsar.soulforge.trait.Traits;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtList;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
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
        return ProjectileUtil.getEntityCollision(player.getWorld(), player, start, start.add(direction), searchBox, (entity) ->
                (EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(entity) ||
                        (entity instanceof ShieldShardEntity shieldShard && shieldShard.owner != player)) &&
                        (entity instanceof LivingEntity || entity instanceof ShieldShardEntity));
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

    public static final UUID antihealModifierID = UUID.fromString("860f5ef8-87a6-47c7-9af5-8ecd553338c9");
    public static void addAntiheal(double amount, int duration, LivingEntity target) {
        TemporaryModifierComponent modifiers = SoulForge.getTemporaryModifiers(target);
        if (modifiers != null) {
            Triplet<EntityAttributeModifier, EntityAttribute, Float> modifier = modifiers.getModifierEntry(SoulForgeAttributes.ANTIHEAL, antihealModifierID);
            if (modifier != null) {
                if (modifier.getFirst().getValue() < amount) {
                    modifiers.removeTemporaryModifier(SoulForgeAttributes.ANTIHEAL, modifier.getFirst());
                    modifiers.addTemporaryModifier(SoulForgeAttributes.ANTIHEAL, new EntityAttributeModifier(antihealModifierID,
                            "antiheal", amount, EntityAttributeModifier.Operation.ADDITION), duration);
                } else if (modifier.getFirst().getValue() == amount && modifier.getThird() < duration) {
                    modifiers.removeTemporaryModifier(SoulForgeAttributes.ANTIHEAL, modifier.getFirst());
                    modifiers.addTemporaryModifier(SoulForgeAttributes.ANTIHEAL, new EntityAttributeModifier(antihealModifierID,
                            "antiheal", amount, EntityAttributeModifier.Operation.ADDITION), duration);
                }
            } else {
                modifiers.addTemporaryModifier(SoulForgeAttributes.ANTIHEAL, new EntityAttributeModifier(antihealModifierID,
                        "antiheal", amount, EntityAttributeModifier.Operation.ADDITION), duration);
            }
        }
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
        if (playerSoul.isStrong() || playerSoul.hasTrait(Traits.determination)) text = text.setStyle(text.getStyle().withFormatting(Formatting.BOLD));
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
        TemporaryModifierComponent modifiers = EntityInitializer.TEMPORARY_MODIFIERS.get(target);
        val += modifiers.getModifierCount();
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
        if (playerSoul.hasTrait(Traits.fear) ||
                playerSoul.hasTrait(Traits.ineptitude) ||
                playerSoul.hasTrait(Traits.misery) ||
                playerSoul.hasTrait(Traits.anxiety) ||
                playerSoul.hasTrait(Traits.paranoia) ||
                playerSoul.hasTrait(Traits.despair)) {
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

    public static List<String> omegagamers = List.of("GDPulsar", "lolteddii", "AmbrosialPhoenix", "KoriOfAllTrades", "KDMHX2");

    public static boolean canAccessInverteds(CommandContext<ServerCommandSource> context) {
        return context.getSource().isExecutedByPlayer() && (omegagamers.contains(context.getSource().getPlayer().getName().getString()) || FabricLoader.getInstance().isDevelopmentEnvironment());
    }

    public static boolean canAccessInverteds(ServerCommandSource source) {
        return source.isExecutedByPlayer() && (omegagamers.contains(source.getPlayer().getName().getString()) || FabricLoader.getInstance().isDevelopmentEnvironment());
    }

    public static float getHate(LivingEntity entity) {
        if (entity == null) return 0f;
        HateComponent hate = SoulForge.getHate(entity);
        if (hate == null) return 0f;
        return hate.getHatePercent();
    }

    public static boolean hasHate(LivingEntity entity) {
        if (entity == null) return false;
        HateComponent hate = SoulForge.getHate(entity);
        if (hate == null) return false;
        return hate.hasHate();
    }

    public static void setHate(LivingEntity entity, float amount) {
        if (entity == null) return;
        HateComponent hate = SoulForge.getHate(entity);
        if (hate == null) return;
        hate.setHatePercent(amount);
    }

    public static void addHate(LivingEntity entity, float amount) {
        if (entity == null) return;
        HateComponent hate = SoulForge.getHate(entity);
        if (hate == null) return;
        hate.addHatePercent(amount);
    }

    public static void setHasHate(LivingEntity entity, boolean hasHate) {
        if (entity == null) return;
        HateComponent hate = SoulForge.getHate(entity);
        if (hate == null) return;
        hate.setHasHate(hasHate);
    }

    public static <T, V> T getKeyByValue(HashMap<T, V> oldValues, V value) {
        for (Map.Entry<T, V> entry : oldValues.entrySet()) {
            if (entry.getValue() == value) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static boolean isParrying(LivingEntity entity) {
        if (entity.isUsingItem()) {
            if (entity.getActiveItem().isIn(SoulForgeTags.PARRY_ITEMS)) return true;
        }
        if (entity instanceof PlayerEntity player) {
            if (!player.getWorld().isClient) {
                SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                if (playerSoul.hasTrait(Traits.bravery) && playerSoul.hasTrait(Traits.integrity)) {
                    if (playerSoul.hasCast("Valiant Heart")) {
                        // do the funny bravteg parry somehow
                    }
                }
            }
        }
        return false;
    }

    public static float getPlayerKillCountExpMultiplier(LivingEntity living, PlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (living.isMobOrPlayer()) {
            if (living.isPlayer()) {
                if (playerSoul.getPlayerSouls().containsKey(living.getUuidAsString())) {
                    return MathHelper.clamp(1f-playerSoul.getPlayerSouls().get(living.getUuidAsString())/3f, 0f, 1f);
                }
            } else {
                String mobId = Registries.ENTITY_TYPE.getId(living.getType()).toString();
                if (playerSoul.getMonsterSouls().containsKey(mobId)) {
                    return MathHelper.clamp(1f-playerSoul.getMonsterSouls().get(mobId)/50f, 0.2f, 1f);
                }
            }
        }
        return 1f;
    }

    public static float getEntityExpMultiplier(LivingEntity living) {
        float targetDefence = 0f;
        if (living.getAttributes().hasAttribute(EntityAttributes.GENERIC_ARMOR)) targetDefence = (float)living.getAttributeValue(EntityAttributes.GENERIC_ARMOR);

        float targetDamage = 0f;
        if (living.getAttributes().hasAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE)) targetDamage = (float)living.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);

        return 1f + (targetDefence / 10f) + (targetDamage / 10f);
    }

    public static float getKillExpOverride(LivingEntity living) {
        switch(living.getType()){
            case EntityType.EVOKER:
            case EntityType.PIGLIN_BRUTE:
                return 250;
            case EntityType.ELDER_GUARDIAN:
                return 500;
            case EntityType.WARDEN:
                return 1000;
            case EntityType.WITHER:
                return 1500;
            case EntityType.ENDER_DRAGON:
                return 3000;
            default:
                break;
        }
        if (!living.isPlayer()) return 0;
        float targetDefence = 0f;
        if (living.getAttributes().hasAttribute(EntityAttributes.GENERIC_ARMOR)) targetDefence = (float)living.getAttributeValue(EntityAttributes.GENERIC_ARMOR);
        PlayerEntity targetPlayer = (PlayerEntity)living;
        SoulComponent targetSoul = SoulForge.getPlayerSoul(targetPlayer);
        return 250f*(1+(targetDefence/10f)*(targetSoul.getLV()/4f));
    }

    public static int getKillExp(LivingEntity living, PlayerEntity player) {
        float exp = Utils.getKillExpOverride(living);
        if (exp == 0f) exp = living.getMaxHealth()*getEntityExpMultiplier(living);

        WorldComponent worldComponent = SoulForge.getWorldComponent(player.getWorld());
        exp *= worldComponent.getExpMultiplier();
        exp *= Utils.getPlayerKillCountExpMultiplier(living, player);
        return (int)exp;
    }

    public static int getDamageExp(LivingEntity living, PlayerEntity player, float damage) {
        float exp = damage * getEntityExpMultiplier(living);

        WorldComponent worldComponent = SoulForge.getWorldComponent(player.getWorld());
        exp *= worldComponent.getExpMultiplier();
        exp *= Utils.getPlayerKillCountExpMultiplier(living, player);
        return (int)exp;
    }
}

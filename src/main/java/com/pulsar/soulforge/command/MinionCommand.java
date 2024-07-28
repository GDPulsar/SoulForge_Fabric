package com.pulsar.soulforge.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.accessors.OwnableMinion;
import com.pulsar.soulforge.components.SoulComponent;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static net.minecraft.command.argument.EntityArgumentType.entity;
import static net.minecraft.command.argument.EntityArgumentType.getEntity;
import static net.minecraft.command.argument.RegistryEntryArgumentType.getRegistryEntry;
import static net.minecraft.command.argument.RegistryEntryArgumentType.registryEntry;
import static net.minecraft.server.command.CommandManager.literal;

public class MinionCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(
                literal("minion")
                        .requires(source -> source.hasPermissionLevel(4) && source.isExecutedByPlayer())
                        .then(literal("summon")
                                .then(RequiredArgumentBuilder.<ServerCommandSource, RegistryEntry.Reference<EntityType<?>>>argument("entity", registryEntry(registryAccess, RegistryKeys.ENTITY_TYPE))
                                        .suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                                        .then(RequiredArgumentBuilder.<ServerCommandSource, PosArgument>argument("position", Vec3ArgumentType.vec3())
                                                .executes(context -> {
                                                    SoulComponent playerSoul = SoulForge.getPlayerSoul(context.getSource().getPlayer());
                                                    RegistryEntry.Reference<EntityType<?>> entityType = getRegistryEntry(context, "entity", RegistryKeys.ENTITY_TYPE);
                                                    if (playerSoul.getMonsterSouls().containsKey(entityType.value().getUntranslatedName())) {
                                                        if (playerSoul.getMonsterSouls().get(entityType.value().getUntranslatedName()) > 0) {
                                                            Vec3d pos = Vec3ArgumentType.getVec3(context, "position");
                                                            BlockPos blockPos = BlockPos.ofFloored(pos);
                                                            if (!World.isValid(blockPos)) {
                                                                throw new SimpleCommandExceptionType(Text.translatable("commands.summon.invalidPosition")).create();
                                                            } else {
                                                                NbtCompound nbt = new NbtCompound();
                                                                nbt.putString("id", entityType.registryKey().getValue().toString());
                                                                ServerWorld serverWorld = context.getSource().getWorld();
                                                                Entity entity = EntityType.loadEntityWithPassengers(nbt, serverWorld, (entityx) -> {
                                                                    entityx.refreshPositionAndAngles(pos.x, pos.y, pos.z, entityx.getYaw(), entityx.getPitch());
                                                                    return entityx;
                                                                });
                                                                if (entity == null) {
                                                                    throw new SimpleCommandExceptionType(Text.translatable("commands.summon.failed")).create();
                                                                } else {
                                                                    if (entity instanceof MobEntity mobEntity) {
                                                                        SpawnReason reason = SpawnReason.COMMAND;
                                                                        if (mobEntity instanceof WardenEntity) reason = SpawnReason.TRIGGERED;
                                                                        mobEntity.initialize(context.getSource().getWorld(), context.getSource().getWorld().getLocalDifficulty(entity.getBlockPos()), reason, null, null);
                                                                        if (entity instanceof ZombieEntity zombie) {
                                                                            zombie.setBaby(false);
                                                                            zombie.setCanPickUpLoot(true);
                                                                        }
                                                                        mobEntity.getArmorItems().forEach(stack -> stack.decrement(1));
                                                                        mobEntity.getHandItems().forEach(stack -> {
                                                                            if (!(mobEntity instanceof AbstractSkeletonEntity) && stack.isOf(Items.BOW)) stack.decrement(1);
                                                                            if (!(mobEntity instanceof VindicatorEntity) && stack.isOf(Items.IRON_AXE)) stack.decrement(1);
                                                                            if (!(mobEntity instanceof PillagerEntity) && stack.isOf(Items.CROSSBOW)) stack.decrement(1);
                                                                        });
                                                                    }

                                                                    if (!serverWorld.spawnNewEntityAndPassengers(entity)) {
                                                                        throw new SimpleCommandExceptionType(Text.translatable("commands.summon.failed.uuid")).create();
                                                                    }
                                                                }
                                                                if (entity instanceof MobEntity mobEntity) {
                                                                    ((OwnableMinion)mobEntity).setOwnerUUID(context.getSource().getPlayer().getUuid());
                                                                }
                                                            }
                                                            playerSoul.getMonsterSouls().put(entityType.value().getUntranslatedName(),
                                                                    playerSoul.getMonsterSouls().get(entityType.value().getUntranslatedName()) - 1);
                                                            return 1;
                                                        }
                                                    }
                                                    context.getSource().sendError(Text.literal("You do not have a soul of that kind of entity!"));
                                                    return 1;
                                                })
                                        )
                                )
                        ).then(literal("target")
                                .then(RequiredArgumentBuilder.<ServerCommandSource, EntitySelector>argument("entity", entity())
                                        .executes(context -> {
                                            PlayerEntity owner = context.getSource().getPlayer();
                                            World world = context.getSource().getWorld();
                                            for (MobEntity minion : world.getEntitiesByClass(MobEntity.class, Box.of(owner.getPos(), 400, 400, 400),
                                                    entity -> ((OwnableMinion)entity).getOwnerUUID() != null &&
                                                            ((OwnableMinion)entity).getOwnerUUID().compareTo(owner.getUuid()) == 0)) {
                                                ((OwnableMinion)minion).setTargetUUID(getEntity(context, "entity").getUuid());
                                            }
                                            return 1;
                                        })
                                )
                                .then(RequiredArgumentBuilder.<ServerCommandSource, PosArgument>argument("position", Vec3ArgumentType.vec3())
                                        .executes(context -> {
                                            PlayerEntity owner = context.getSource().getPlayer();
                                            World world = context.getSource().getWorld();
                                            for (MobEntity minion : world.getEntitiesByClass(MobEntity.class, Box.of(owner.getPos(), 400, 400, 400),
                                                    entity -> ((OwnableMinion)entity).getOwnerUUID() != null &&
                                                            ((OwnableMinion)entity).getOwnerUUID().compareTo(owner.getUuid()) == 0)) {
                                                ((OwnableMinion)minion).setTargetPos(Vec3ArgumentType.getVec3(context, "position"));
                                            }
                                            return 1;
                                        })
                                )
                        )
        );
    }
}

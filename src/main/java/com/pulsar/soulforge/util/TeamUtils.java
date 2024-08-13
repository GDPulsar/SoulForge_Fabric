package com.pulsar.soulforge.util;

import com.pulsar.soulforge_teams.SoulForgeTeams;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;

public class TeamUtils {
    public static boolean isTeamsInstalled = false;

    public static void checkForTeams() {
        if (FabricLoader.getInstance().isModLoaded("soulforge-teams")) {
            isTeamsInstalled = true;
        }
    }

    public static boolean canDamageEntity(MinecraftServer server, PlayerEntity attacker, LivingEntity target) {
        if (server == null || attacker == null || target == null) return true;
        if (isTeamsInstalled) {
            SoulForgeTeams.Team team = SoulForgeTeams.getPlayerTeam(server, attacker);
            if (team != null) {
                if (target instanceof PlayerEntity targetPlayer) {
                    if (team.isTeamMember(targetPlayer)) {
                        return team.getOptions().allowTeamPvP;
                    } else {
                        SoulForgeTeams.Team targetTeam = SoulForgeTeams.getPlayerTeam(server, targetPlayer);
                        if (targetTeam != null) {
                            if (team.isAllyTeam(targetTeam)) {
                                return team.getOptions().allowOffensiveAllyTargeting;
                            }
                        }
                    }
                } else if (target instanceof TameableEntity tameable) {
                    if (tameable.getOwner() == attacker) {
                        return team.getOptions().allowTeamPvP;
                    } else {
                        if (tameable.getOwner() instanceof PlayerEntity targetPlayer) {
                            if (team.isTeamMember(targetPlayer)) {
                                return team.getOptions().allowTeamPvP;
                            } else {
                                SoulForgeTeams.Team targetTeam = SoulForgeTeams.getPlayerTeam(server, targetPlayer);
                                if (targetTeam != null) {
                                    if (team.isAllyTeam(targetTeam)) {
                                        return team.getOptions().allowOffensiveAllyTargeting;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public static boolean canHealEntity(MinecraftServer server, PlayerEntity attacker, LivingEntity target) {
        if (server == null || attacker == null || target == null) return true;
        if (isTeamsInstalled) {
            SoulForgeTeams.Team team = SoulForgeTeams.getPlayerTeam(server, attacker);
            if (team != null) {
                if (target instanceof PlayerEntity targetPlayer) {
                    if (team.isTeamMember(targetPlayer)) {
                        return true;
                    } else {
                        SoulForgeTeams.Team targetTeam = SoulForgeTeams.getPlayerTeam(server, targetPlayer);
                        if (targetTeam != null) {
                            if (team.isEnemyTeam(targetTeam)) {
                                return team.getOptions().allowDefensiveEnemyTargeting;
                            }
                        }
                    }
                } else if (target instanceof TameableEntity tameable) {
                    if (tameable.getOwner() == attacker) {
                        return true;
                    } else {
                        if (tameable.getOwner() instanceof PlayerEntity targetPlayer) {
                            if (team.isTeamMember(targetPlayer)) {
                                return true;
                            } else {
                                SoulForgeTeams.Team targetTeam = SoulForgeTeams.getPlayerTeam(server, targetPlayer);
                                if (targetTeam != null) {
                                    if (team.isEnemyTeam(targetTeam)) {
                                        return team.getOptions().allowDefensiveEnemyTargeting;
                                    }
                                }
                            }
                        }
                    }
                }
                return false;
            }
        }
        return true;
    }
}

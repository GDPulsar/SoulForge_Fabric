package com.pulsar.soulforge.util;

import com.pulsar.soulforge_teams.SoulForgeTeams;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;

public class TeamUtils {
    public static boolean isTeamsInstalled = false;

    public static void checkForTeams() {
        if (FabricLoader.getInstance().isModLoaded("soulforge-teams")) {
            isTeamsInstalled = true;
        }
    }

    public static boolean canDamagePlayer(MinecraftServer server, PlayerEntity attacker, PlayerEntity target) {
        if (server == null || attacker == null || target == null) return true;
        if (isTeamsInstalled) {
            SoulForgeTeams.Team team = SoulForgeTeams.getPlayerTeam(server, attacker);
            if (team != null) {
                if (team.isTeamMember(target)) {
                    return team.getOptions().allowTeamPvP;
                } else {
                    SoulForgeTeams.Team targetTeam = SoulForgeTeams.getPlayerTeam(server, target);
                    if (targetTeam != null) {
                        if (team.isAllyTeam(targetTeam)) {
                            return team.getOptions().allowOffensiveAllyTargeting;
                        }
                    }
                }
            }
        }
        return true;
    }

    public static boolean canHealPlayer(MinecraftServer server, PlayerEntity attacker, PlayerEntity target) {
        if (server == null || attacker == null || target == null) return true;
        if (isTeamsInstalled) {
            SoulForgeTeams.Team team = SoulForgeTeams.getPlayerTeam(server, attacker);
            if (team != null) {
                if (team.isTeamMember(target)) {
                    return true;
                } else {
                    SoulForgeTeams.Team targetTeam = SoulForgeTeams.getPlayerTeam(server, target);
                    if (targetTeam != null) {
                        if (team.isEnemyTeam(targetTeam)) {
                            return team.getOptions().allowDefensiveEnemyTargeting;
                        }
                    }
                }
            }
        }
        return true;
    }
}

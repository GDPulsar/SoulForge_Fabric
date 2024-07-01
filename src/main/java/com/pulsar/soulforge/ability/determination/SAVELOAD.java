package com.pulsar.soulforge.ability.determination;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;

public class SAVELOAD extends ToggleableAbilityBase {
    public int timer = 0;
    public Vec3d savedPosition = Vec3d.ZERO;
    public boolean self = false;
    public LivingEntity saved = null;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        if (player.getPitch() < -89.5f && player.isSneaking()) {
            setActive(true);
            savedPosition = player.getPos();
            self = true;
            timer = 600;
        } else {
            EntityHitResult hit = Utils.getFocussedEntity(player, (float) ReachEntityAttributes.getAttackRange(player, 3.0));
            if (hit != null && hit.getEntity() instanceof LivingEntity living) {
                setActive(true);
                savedPosition = living.getPos();
                self = false;
                timer = 600;
                saved = living;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        return timer < 0 || !isActive();
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        if (timer >= 0) {
            if (self) {
                player.teleport(player.getServerWorld(), savedPosition.x, savedPosition.y, savedPosition.z, player.getYaw(), player.getPitch());
            } else if (saved != null) {
                saved.teleport(player.getServerWorld(), savedPosition.x, savedPosition.y, savedPosition.z, PositionFlag.VALUES, saved.getYaw(), saved.getPitch());
            }
        }
        return true;
    }

    public String getName() { return "SAVE/LOAD"; }
    public Identifier getId() { return Identifier.of(SoulForge.MOD_ID, "save_load"); }
    public int getLV() { return 13; }
    public int getCost() { return 40; }
    public int getCooldown() { return 600; }
    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new SAVELOAD();
    }
}

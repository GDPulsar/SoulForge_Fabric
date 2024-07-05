package com.pulsar.soulforge.ability.determination;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;

public class SAVELOAD extends AbilityBase {
    public int timer = 0;
    public Vec3d savedPosition = Vec3d.ZERO;
    public boolean self = false;
    public LivingEntity saved = null;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        if (!getActive()) {
            if (player.getPitch() > 89.5f && player.isSneaking()) {
                super.cast(player);
                savedPosition = player.getPos();
                self = true;
                timer = 600;
                player.playSound(SoulForgeSounds.UT_SAVE_EVENT, SoundCategory.MASTER, 1f, 1f);
            } else {
                EntityHitResult hit = Utils.getFocussedEntity(player, 5f);
                if (hit != null && hit.getEntity() instanceof LivingEntity living) {
                    super.cast(player);
                    savedPosition = living.getPos();
                    self = false;
                    timer = 600;
                    saved = living;
                    player.playSound(SoulForgeSounds.UT_SAVE_EVENT, SoundCategory.MASTER, 1f, 1f);
                }
            }
        } else {
            setActive(false);
        }
        return getActive();
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        timer--;
        return timer < 0 || !getActive();
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        if (timer >= 0) {
            if (self) {
                player.teleport(savedPosition.x, savedPosition.y, savedPosition.z);
            } else if (saved != null) {
                saved.teleport(savedPosition.x, savedPosition.y, savedPosition.z);
            }
        }
        return super.end(player);
    }
    
    public String getName() { return "SAVE/LOAD"; }

    public Identifier getID() { return new Identifier(SoulForge.MOD_ID, "save_load"); }

    public int getLV() { return 13; }

    public int getCost() { return 40; }

    public int getCooldown() { return 600; }

    @Override
    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new SAVELOAD();
    }
}

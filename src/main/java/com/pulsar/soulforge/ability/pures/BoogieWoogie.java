package com.pulsar.soulforge.ability.pures;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;

import java.util.Set;

public class BoogieWoogie extends AbilityBase {
    public final String name = "Boogie Woogie";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "boogie_woogie");
    public final int requiredLv = 10;
    public final int cost = 20;
    public final int cooldown = 100;
    public final AbilityType type = AbilityType.CAST;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        EntityHitResult target = Utils.getFocussedEntity(player, 100f, entity -> (entity instanceof ProjectileEntity || entity instanceof LivingEntity) && !entity.isInvisible());
        if (target != null) {
            if (target.getEntity() != null) {
                Entity entity = target.getEntity();
                Vec3d targetPos = entity.getPos();
                Vec3d targetVel = entity.getVelocity();
                float targetYaw = entity.getYaw();
                float targetPitch = entity.getPitch();
                entity.teleport(player.getServerWorld(), player.getPos().x, player.getPos().y, player.getPos().z, Set.of(), player.getYaw(), player.getPitch());
                entity.setVelocity(player.getVelocity());
                entity.velocityModified = true;
                if (entity instanceof ProjectileEntity) {
                    player.teleport(player.getServerWorld(), targetPos.x, targetPos.y, targetPos.z, Set.of(), player.getYaw(), player.getPitch());
                } else {
                    player.teleport(player.getServerWorld(), targetPos.x, targetPos.y, targetPos.z, Set.of(), targetYaw, targetPitch);
                }
                player.setVelocity(targetVel);
                player.velocityModified = true;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        return true;
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        return true;
    }

    public String getName() { return name; }

    public Text getLocalizedText() { return Text.translatable("ability."+id.getPath()+".name"); }

    public Identifier getID() { return id; }

    public String getTooltip() { return Text.translatable("ability."+id.getPath()+".tooltip").getString(); }

    public int getLV() { return requiredLv; }

    public int getCost() { return cost; }

    public int getCooldown() { return cooldown; }

    public AbilityType getType() { return type; }

    @Override
    public AbilityBase getInstance() {
        return new BoogieWoogie();
    }
}

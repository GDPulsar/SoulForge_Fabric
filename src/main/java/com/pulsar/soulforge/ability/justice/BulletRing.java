package com.pulsar.soulforge.ability.justice;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.bravery.BraveryBoost;
import com.pulsar.soulforge.entity.JusticePelletProjectile;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class BulletRing extends AbilityBase {
    public final String name = "Bullet Ring";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "bullet_ring");
    public final int requiredLv = 5;
    public final int cost = 15;
    public final int cooldown = 60;
    public final AbilityType type = AbilityType.CAST;

    private Vec3d targetPos;
    private List<JusticePelletProjectile> projectiles = new ArrayList<>();
    private int timer = 0;
    private LivingEntity target;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        EntityHitResult hit = Utils.getFocussedEntity(player, 50);
        if (hit != null && hit.getEntity() instanceof LivingEntity) {
            if (hit.getEntity() instanceof PlayerEntity targetPlayer) {
                if (!TeamUtils.canDamagePlayer(player.getServer(), player, targetPlayer)) return false;
            }
            target = (LivingEntity)hit.getEntity();
            targetPos = target.getPos();
            projectiles = new ArrayList<>();
            timer = 16;
            return true;
        }
        return false;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        if (target == null) return true;
        if (timer > 10) {
            JusticePelletProjectile pellet = new JusticePelletProjectile(target.getWorld(), player);
            pellet.setPos(targetPos.add(new Vec3d(5 * MathHelper.sin((float) (timer * Math.PI / 3f)), 1f, 5 * MathHelper.cos((float) (timer * Math.PI / 3f)))));
            projectiles.add(pellet);
            player.getWorld().spawnEntity(pellet);
            pellet.playSound(SoulForgeSounds.UT_A_BULLET_EVENT, 0.5f, 1f);
        }
        timer--;
        return timer <= 0;
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        if (target == null) return true;
        for (JusticePelletProjectile pellet : projectiles) {
            pellet.setVelocity(targetPos.add(0f, target.getHeight()/2f, 0f).subtract(pellet.getPos()).normalize().multiply(2f));
            pellet.velocityModified = true;
        }
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
        return new BulletRing();
    }
}

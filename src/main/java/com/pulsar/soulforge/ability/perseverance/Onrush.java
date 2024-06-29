package com.pulsar.soulforge.ability.perseverance;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.event.EventType;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class Onrush extends AbilityBase {
    public final String name = "Onrush";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "onrush");
    public final int requiredLv = 7;
    public final int cost = 30;
    public final int cooldown = 200;
    public final AbilityType type = AbilityType.CAST;

    private int timer = 0;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        damaged = new ArrayList<>();
        timer = 10;
        Vec3d vel = new Vec3d(player.getRotationVector().x, 0f, player.getRotationVector().z);
        player.addVelocity(vel.normalize());
        player.velocityModified = true;
        return true;
    }

    List<LivingEntity> damaged = new ArrayList<>();

    @Override
    public boolean tick(ServerPlayerEntity player) {
        timer--;
        boolean hit = false;
        float damage = (float)player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue();
        damage *= 1.5f;
        DamageSource source = SoulForgeDamageTypes.of(player.getWorld(), SoulForgeDamageTypes.ABILITY_DAMAGE_TYPE);
        for (Entity target : player.getEntityWorld().getOtherEntities(player, Box.of(player.getPos(), 1, 1, 1))) {
            if (target instanceof LivingEntity living) {
                if (target instanceof PlayerEntity targetPlayer) {
                    if (!TeamUtils.canDamagePlayer(player.getServer(), player, targetPlayer)) continue;
                }
                if (!damaged.contains(living)) {
                    living.damage(source, damage);
                    living.timeUntilRegen += 10;
                    damaged.add(living);
                    hit = true;
                }
            }
        }
        if (hit) {
            timer = 10;
            Vec3d vel = new Vec3d(player.getRotationVector().x, 0f, player.getRotationVector().z);
            player.addVelocity(vel.normalize());
            player.velocityModified = true;
        }
        return timer <= 0;
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
        return new Onrush();
    }
}

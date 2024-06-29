package com.pulsar.soulforge.ability.determination;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class SAVELOAD extends ToggleableAbilityBase {
    public final String name = "SAVE/LOAD";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "save_load");
    public final int requiredLv = 13;
    public final int cost = 40;
    public final int cooldown = 600;
    public final AbilityType type = AbilityType.CAST;

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
        return new SAVELOAD();
    }
}

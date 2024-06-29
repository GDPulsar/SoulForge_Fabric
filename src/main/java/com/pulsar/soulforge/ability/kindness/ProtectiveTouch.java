package com.pulsar.soulforge.ability.kindness;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;

public class ProtectiveTouch extends AbilityBase {
    public final String name = "Protective Touch";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "protective_touch");
    public final int requiredLv = 17;
    public final int cost = 75;
    public final int cooldown = 600;
    public final AbilityType type = AbilityType.CAST;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        EntityHitResult hit = Utils.getFocussedEntity(player, 10);
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (hit != null && hit.getEntity() instanceof LivingEntity) {
            LivingEntity target = (LivingEntity)hit.getEntity();
            if (target.isPlayer()) {
                if (target instanceof PlayerEntity targetPlayer) {
                    if (!TeamUtils.canHealPlayer(player.getServer(), player, targetPlayer)) return false;
                }
                target.setAbsorptionAmount(Math.max(target.getAbsorptionAmount(), (float)(playerSoul.getEffectiveLV())));
                player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoulForgeSounds.UT_HEAL_EVENT, SoundCategory.PLAYERS, 1f, 1f);
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
        return new ProtectiveTouch();
    }
}

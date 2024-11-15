package com.pulsar.soulforge.ability.duals;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;

import java.util.UUID;

public class Ward extends AbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        float newAbsorptionAmount = Math.max(player.getAbsorptionAmount(), (float)(playerSoul.getEffectiveLV()));
        float absorptionIncrease = newAbsorptionAmount - player.getAbsorptionAmount();
        player.setAbsorptionAmount(newAbsorptionAmount);
        float armorBonus = playerSoul.getEffectiveLV() / 5f;
        if (player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).getModifier(UUID.fromString("52b19d82-dd09-4ed4-87d6-84aa8bb12247")) != null) {
            armorBonus = (float)Math.max(armorBonus, player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).getModifier(UUID.fromString("52b19d82-dd09-4ed4-87d6-84aa8bb12247")).getValue());
        }
        player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).tryRemoveModifier(UUID.fromString("52b19d82-dd09-4ed4-87d6-84aa8bb12247"));
        player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).addPersistentModifier(new EntityAttributeModifier(UUID.fromString("52b19d82-dd09-4ed4-87d6-84aa8bb12247"), "ward", armorBonus, EntityAttributeModifier.Operation.ADDITION));
        playerSoul.setStyle(playerSoul.getStyle() + (int)absorptionIncrease);
        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoulForgeSounds.UT_HEAL_EVENT, SoundCategory.PLAYERS, 1f, 1f);
        return super.cast(player);
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        return player.getAbsorptionAmount() <= 0f;
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).tryRemoveModifier(UUID.fromString("52b19d82-dd09-4ed4-87d6-84aa8bb12247"));
        return super.end(player);
    }

    public int getLV() { return 17; }

    public int getCost() { return 75; }

    public int getCooldown() { return 900; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new Ward();
    }
}

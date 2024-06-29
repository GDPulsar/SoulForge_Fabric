package com.pulsar.soulforge.ability.bravery;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.entity.BlastEntity;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.event.GameEvent;

import java.awt.*;

public class EnergyWave extends ToggleableAbilityBase {
    public final String name = "Energy Wave";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "energy_wave");
    public final int requiredLv = 3;
    public final int cost = 20;
    public final int cooldown = 0;
    public final AbilityType type = AbilityType.CAST;

    private int chargeTimer = 0;
    private int chargeLevel = 0;
    private boolean overcharged = false;

    private final EntityAttributeModifier modifier = new EntityAttributeModifier("energy_wave", -0.7, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);

    @Override
    public boolean cast(ServerPlayerEntity player) {
        if (!getActive()) {
            player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).addPersistentModifier(modifier);
            chargeLevel = 0;
            chargeTimer = 0;
            setActive(true);
        } else {
            setActive(false);
        }
        return getActive();
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        playerSoul.resetLastCastTime();
        boolean valiantHeart = playerSoul.hasCast("Valiant Heart");
        chargeTimer++;
        if (chargeTimer % (valiantHeart ? 10 : 20) == 0) {
            if (playerSoul.getMagic() >= 5f) {
                playerSoul.setMagic(playerSoul.getMagic()-5f);
            } else {
                if (playerSoul.getLV() >= 10) {
                    if (playerSoul.hasValue("overchargeCooldown")) {
                        if (playerSoul.getValue("overchargeCooldown") > 0) return true;
                    }
                    playerSoul.setMagic(0f);
                    overcharged = true;
                } else {
                    return true;
                }
            }
            chargeLevel += 1;
        }
        return !getActive();
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        setActive(false);
        Vec3d end = player.getEyePos().add(player.getRotationVector().multiply(30f));
        HitResult hit = player.getWorld().raycast(new RaycastContext(player.getEyePos(), player.getEyePos().add(player.getRotationVector().multiply(30f)), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player));
        if (hit != null) end = hit.getPos().subtract(Utils.getArmPosition(player));
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        float damage = 0.4f * playerSoul.getEffectiveLV();
        float size = 0.25f;
        if (overcharged) {
            damage = 1.6f * playerSoul.getEffectiveLV();
            size = 0.5f;
            player.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.MANA_OVERLOAD, 2400, 1));
            playerSoul.setValue("overchargeCooldown", 6000);
        }
        BlastEntity blast = new BlastEntity(player.getWorld(), Utils.getArmPosition(player),
                player, size, Vec3d.ZERO, end, damage, Color.ORANGE);
        blast.owner = player;
        ServerWorld serverWorld = (ServerWorld)player.getWorld();
        serverWorld.spawnEntity(blast);
        serverWorld.emitGameEvent(GameEvent.ENTITY_PLACE, player.getPos(), GameEvent.Emitter.of(player));
        serverWorld.playSoundFromEntity(null, player, SoulForgeSounds.UT_BLASTER_EVENT, SoundCategory.PLAYERS, 1f, 1f);
        playerSoul.setCooldown(this, 100+20*chargeLevel);
        playerSoul.setValue("energyWaveCooldown", 100 + 20*chargeLevel);
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "energy_wave");
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
        return new EnergyWave();
    }
}

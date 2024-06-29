package com.pulsar.soulforge.ability.integrity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.networking.SoulForgeNetworking;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.awt.*;
import java.util.Objects;

public class GravityAnchor extends AbilityBase {
    public final String name = "Gravity Anchor";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "gravity_anchor");
    public final int requiredLv = 5;
    public final int cost = 40;
    public final int cooldown = 400;
    public final AbilityType type = AbilityType.CAST;

    private int timer = 0;
    private LivingEntity target;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        EntityHitResult result = Utils.getFocussedEntity(player, 15);
        if (result != null && result.getEntity() instanceof LivingEntity living) {
            if (living instanceof PlayerEntity targetPlayer) {
                if (!TeamUtils.canDamagePlayer(player.getServer(), player, targetPlayer)) return false;
            }
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            target = living;
            if (result.getEntity() instanceof PlayerEntity targetPlayer) {
                SoulComponent targetSoul = SoulForge.getPlayerSoul(targetPlayer);
                targetSoul.addTag("disableJump");
            }
            for (int i = 0; i < 10; i++) {
                float x = MathHelper.sin((float)(i/5*Math.PI));
                float z = MathHelper.cos((float)(i/5*Math.PI));
                player.getWorld().addParticle(
                        new DustParticleEffect(Vec3d.unpackRgb(0x0000FF).toVector3f(), 1f),
                        target.getX()+x, target.getY(), target.getZ()+z, 1, 0, 0.2);
            }
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 2));
            timer = Math.round(playerSoul.getEffectiveLV()*0.75f);
            return true;
        }
        return false;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        timer--;
        if (target != null) {
            target.addVelocity(0, -5, 0);
        }
        return timer == 0;
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        if (target != null) {
            if (target instanceof PlayerEntity targetPlayer) {
                SoulComponent targetSoul = SoulForge.getPlayerSoul(targetPlayer);
                targetSoul.removeTag("disableJump");
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
        return new GravityAnchor();
    }

    @Override
    public NbtCompound saveNbt(NbtCompound nbt) {
        nbt.putInt("timer", timer);
        return super.saveNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (!Objects.equals(nbt.getString("id"), getID().getPath())) return;
        timer = nbt.getInt("timer");
    }
}

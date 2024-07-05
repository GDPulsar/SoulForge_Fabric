package com.pulsar.soulforge.ability.perseverance;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.networking.SoulForgeNetworking;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.HashSet;
import java.util.Objects;

public class ColossalClaymore extends AbilityBase {
    int timer = 0;
    public boolean greaterSlash = false;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        greaterSlash = false;
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (player.getMainHandStack().isOf(SoulForgeItems.COLOSSAL_CLAYMORE)) {
            if (playerSoul.getMagic() < 100f) return false;
            playerSoul.setMagic(0f);
            greaterSlash = true;
            for (LivingEntity target : Utils.getEntitiesInFrontOf(player, 5f, 8f, 2f, 2f)) {
                if (target instanceof PlayerEntity targetPlayer) {
                    if (!TeamUtils.canDamagePlayer(player.getServer(), player, targetPlayer)) continue;
                }
                target.damage(player.getDamageSources().playerAttack(player), playerSoul.getEffectiveLV()*4f);
            }
            player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoulForgeSounds.UT_BOMBSPLOSION_EVENT, SoundCategory.MASTER, 1f, 1f);
            PacketByteBuf buf = PacketByteBufs.create().writeUuid(player.getUuid()).writeString("greater_slash");
            buf.writeBoolean(false);
            if (player.getServer() != null) SoulForgeNetworking.broadcast(null, player.getServer(), SoulForgeNetworking.PERFORM_ANIMATION, buf);
            player.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.MANA_OVERLOAD, 6000, 2));
        }
        timer = greaterSlash ? 5 : 30;
        return super.cast(player);
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        timer--;
        if (greaterSlash) return timer <= 0;
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        float g = -(player.getYaw() + 90f) * 0.017453292F;
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        Vec3d velocity = new Vec3d(i*2.5f, 1f, h*2.5f);
        for (LivingEntity target : Utils.getEntitiesInFrontOf(player, 1f, 5f, 1f, 1f)) {
            if (target instanceof PlayerEntity targetPlayer) {
                if (!TeamUtils.canDamagePlayer(player.getServer(), player, targetPlayer)) continue;
            }
            target.damage(player.getDamageSources().playerAttack(player), playerSoul.getEffectiveLV()*0.8f);
            target.setVelocity(velocity);
            target.velocityModified = true;
        }
        player.teleport(player.getServerWorld(), player.getX(), player.getY(), player.getZ(), new HashSet<>(), player.getYaw()+25f, player.getPitch());
        return timer <= 0;
    }

    @Override
    public void displayTick(PlayerEntity player) {
        if (player.getMainHandStack().isOf(SoulForgeItems.COLOSSAL_CLAYMORE)) {
            return;
        }
        Vec3d centerPos = player.getPos();
        for (int i = 0; i < 32; i++) {
            Vec3d particlePos = new Vec3d(Math.sin(i*Math.PI/16), 0f, Math.cos(i*Math.PI/16)).multiply(5f);
            player.getWorld().addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xFF00FF).toVector3f(), 1f), particlePos.x + centerPos.x, centerPos.y, particlePos.z + centerPos.z, 0, 0, 0);
        }
    }

    public int getLV() { return 12; }

    public int getCost() { return 40; }

    public int getCooldown() { return 400; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new ColossalClaymore();
    }

    @Override
    public NbtCompound saveNbt(NbtCompound nbt) {
        nbt.putBoolean("greaterSlash", greaterSlash);
        return super.saveNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (!Objects.equals(nbt.getString("id"), getID().getPath())) return;
        greaterSlash = nbt.getBoolean("greaterSlash");
        super.readNbt(nbt);
    }
}

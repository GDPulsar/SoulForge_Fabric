package com.pulsar.soulforge.ability.bravery;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.TeamUtils;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;

import java.util.ArrayList;
import java.util.List;

public class Shatter extends AbilityBase {
    public final String name = "Shatter";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "shatter");
    public final int requiredLv = 17;
    public final int cost = 80;
    public final int cooldown = 900;
    public final AbilityType type = AbilityType.CAST;
    
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        ServerWorld serverWorld = player.getServerWorld();
        List<Entity> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Vec3d pos = new Vec3d(6 * MathHelper.sin(i / 5f * MathHelper.PI) + player.getX(), player.getY() + 1f, 6 * MathHelper.cos(i / 5f * MathHelper.PI) + player.getZ());
            int k = MathHelper.floor(pos.x - 7);
            int l = MathHelper.floor(pos.x + 7);
            int r = MathHelper.floor(pos.y - 7);
            int s = MathHelper.floor(pos.y + 7);
            int t = MathHelper.floor(pos.z - 7);
            int u = MathHelper.floor(pos.z + 7);
            list.addAll(player.getWorld().getOtherEntities(player, new Box(k, r, t, l, s, u)));
            if (serverWorld != null) serverWorld.spawnParticles((ParticleEffect)ParticleTypes.EXPLOSION, pos.x, pos.y, pos.z, 1, 1.0, 0.0, 0.0, 0f);
        }
        if (serverWorld != null) serverWorld.playSoundFromEntity(null, player, SoulForgeSounds.UT_EXPLOSION_EVENT, SoundCategory.PLAYERS, 4f, 1f);
        Vec3d vec3d = new Vec3d(player.getX(), player.getY(), player.getZ());
        for (Entity entity : list) {
            if (entity instanceof PlayerEntity targetPlayer) {
                if (!TeamUtils.canDamagePlayer(player.getServer(), player, targetPlayer)) continue;
            }
            double ac;
            double y;
            double x;
            double w;
            double z;
            float v;
            if (entity.isImmuneToExplosion() || !((v = MathHelper.sqrt((float)entity.squaredDistanceTo(vec3d)) / 6f) <= 1.0) || (z = Math.sqrt((w = entity.getX() - player.getX()) * w + (x = (entity instanceof TntEntity ? entity.getY() : entity.getEyeY()) - player.getY()) * x + (y = entity.getZ() - player.getZ()) * y)) == 0.0) continue;
            w /= z;
            x /= z;
            y /= z;
            float aa = Explosion.getExposure(vec3d, entity);
            float ab = (1f - v) * aa;
            entity.damage(player.getDamageSources().explosion(player, entity), (int)((ab * ab + ab) / 2f * playerSoul.getEffectiveLV() * 1.5f));
            if (entity instanceof LivingEntity livingEntity) {
                ac = ProtectionEnchantment.transformExplosionKnockback(livingEntity, ab);
            } else {
                ac = ab;
            }
            Vec3d vec3d2 = new Vec3d(w * ac, x * ac, y * ac);
            entity.setVelocity(entity.getVelocity().add(vec3d2));
        }
        return true;
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
        return new Shatter();
    }
}

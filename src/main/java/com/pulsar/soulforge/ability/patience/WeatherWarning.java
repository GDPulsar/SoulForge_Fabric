package com.pulsar.soulforge.ability.patience;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.advancement.SoulForgeCriterions;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.entity.FrozenEnergyProjectile;
import com.pulsar.soulforge.entity.HailProjectile;
import com.pulsar.soulforge.entity.WeatherWarningLightningEntity;
import com.pulsar.soulforge.trait.Traits;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class WeatherWarning extends AbilityBase {
    public final String name = "Weather Warning";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "weather_warning");
    public final int requiredLv = 17;
    public final int cost = 40;
    public final int cooldown = 600;
    public final AbilityType type = AbilityType.CAST;

    public boolean active = false;
    public BlockPos origin = null;
    public int stage = 0;
    public int timer = 0;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        if (!active) {
            HitResult hitResult = player.raycast(32f, 1f, false);
            if (hitResult != null) {
                if (hitResult instanceof BlockHitResult blockHitResult) {
                    origin = blockHitResult.getBlockPos();
                    if (player.getWorld().isThundering()) stage = 2;
                    else if (player.getWorld().isRaining()) stage = 1;
                    else stage = 0;
                    timer = 6000;
                    active = true;
                    player.getServerWorld().setWeather(0, timer, true, stage >= 1);
                    return true;
                }
            }
        } else {
            if (stage < 2) stage++;
            timer = 6000;
            player.getServerWorld().setWeather(0, timer, true, stage >= 1);
            HitResult hitResult = player.raycast(32f, 1f, false);
            if (hitResult != null) {
                if (hitResult instanceof BlockHitResult blockHitResult) {
                    origin = blockHitResult.getBlockPos();
                }
            }
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            float cost = this.cost;
            if (player.getAttributeInstance(SoulForgeAttributes.MAGIC_COST) != null) {
                cost *= (float)player.getAttributeInstance(SoulForgeAttributes.MAGIC_COST).getValue();
            }
            if (playerSoul.isStrong() && !playerSoul.getTraits().contains(Traits.determination)) cost /= 2f;
            if (playerSoul.hasCast("Valiant Heart")) cost /= 2f;
            playerSoul.setMagic(playerSoul.getMagic()-cost);
            int cooldown = this.cooldown;
            if (player.getAttributeInstance(SoulForgeAttributes.MAGIC_COOLDOWN) != null) cooldown = (int)(cooldown * player.getAttributeInstance(SoulForgeAttributes.MAGIC_COOLDOWN).getValue());
            if (playerSoul.isPure()) cooldown /= 2;
            if (playerSoul.hasCast("Valiant Heart")) cooldown /= 2;
            playerSoul.setCooldown(this, cooldown);
            playerSoul.resetLastCastTime();
            return false;
        }
        return false;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        timer--;
        if (timer > 5800) {
            World world = player.getWorld();
            if (stage >= 0) {
                HailProjectile projectile = new HailProjectile(world, player, true);
                projectile.setPosition(origin.toCenterPos().add(Math.random() * 75f - 27.5f, 70f, Math.random() * 75 - 42.5f));
                Vec3d velocity = new Vec3d(-0.25f, -2f, 0.5f);
                projectile.setVelocity(velocity);
                world.spawnEntity(projectile);
            }
            if (stage >= 1) {
                if (timer % 2 == 0) {
                    HailProjectile projectile = new HailProjectile(world, player, true);
                    projectile.setPosition(origin.toCenterPos().add(Math.random() * 75f - 27.5f, 70f, Math.random() * 75 - 42.5f));
                    Vec3d velocity = new Vec3d(-0.25f, -2f, 0.5f);
                    projectile.setVelocity(velocity);
                    world.spawnEntity(projectile);
                }
            }
            if (stage >= 2) {
                if (timer % 15 == 0) {
                    Vec3d position = new Vec3d((Math.random() * 60 - 30) + origin.getX(), 320f, ((Math.random() * 60 - 30) + origin.getZ()));
                    BlockHitResult top = world.raycast(new RaycastContext(position, position.subtract(0f, 300f, 0f), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.SOURCE_ONLY, player));
                    WeatherWarningLightningEntity lightning = new WeatherWarningLightningEntity(player);
                    lightning.setPosition(top.getBlockPos().toCenterPos());
                    world.spawnEntity(lightning);
                }
            }
        }
        return timer <= 0;
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        origin = null;
        stage = 0;
        active = false;
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
        return new WeatherWarning();
    }
}

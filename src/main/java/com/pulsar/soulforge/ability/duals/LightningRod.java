package com.pulsar.soulforge.ability.duals;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.entity.LightningRodLightningEntity;
import com.pulsar.soulforge.entity.LightningRodProjectile;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.util.TeamUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class LightningRod extends AbilityBase {
    public final String name = "Lightning Rod";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "lightning_rod");
    public final int requiredLv = 15;
    public final int cost = 20;
    public final int cooldown = 0;
    public final AbilityType type = AbilityType.WEAPON;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (!player.isSneaking()) {
            boolean struck = false;
            for (LightningRodProjectile lightningRod : player.getWorld().getEntitiesByClass(LightningRodProjectile.class, Box.of(player.getPos(), 300, 300, 300), projectile -> projectile.getOwner() == player)) {
                LightningRodLightningEntity lightning = new LightningRodLightningEntity(player);
                lightning.setPosition(lightningRod.getPos());
                player.getWorld().spawnEntity(lightning);
                Vec3d chainPos = lightningRod.getPos();
                List<LivingEntity> chained = new ArrayList<>();
                boolean found = true;
                while (found) {
                    LivingEntity closest = null;
                    for (Entity near : player.getWorld().getOtherEntities(lightningRod, Box.of(chainPos, 15, 15, 15))) {
                        if (near == player) continue;
                        if (near instanceof LivingEntity living) {
                            if (living instanceof PlayerEntity targetPlayer) {
                                if (!TeamUtils.canDamagePlayer(player.getServer(), player, targetPlayer)) continue;
                            }
                            if (chained.contains(living)) continue;
                            if (closest == null) {
                                closest = living;
                                continue;
                            }
                            if (chainPos.distanceTo(living.getPos()) < chainPos.distanceTo(living.getPos())) {
                                closest = living;
                            }
                        }
                    }
                    found = closest != null;
                    if (found) {
                        chained.add(closest);
                        chainPos = closest.getPos();
                        closest.damage(player.getDamageSources().playerAttack(player), 5 + playerSoul.getEffectiveLV() / 2f);
                    }
                }
                struck = true;
                break;
            }
            if (!struck) playerSoul.setWeapon(new ItemStack(SoulForgeItems.LIGHTNING_ROD));
            return true;
        } else {
            for (LightningRodProjectile lightningRod : player.getWorld().getEntitiesByClass(LightningRodProjectile.class, Box.of(player.getPos(), 300, 300, 300), projectile -> projectile.getOwner() == player)) {
                Vec3d fromPos = lightningRod.getPos();
                Vec3d toPos = player.getPos();
                int dist = MathHelper.floor(toPos.distanceTo(fromPos));
                for (int i = 0; i < dist; i++) {
                    Vec3d pos = fromPos.lerp(toPos, (double)i/dist);
                    for (Entity entity : player.getEntityWorld().getOtherEntities(player, Box.of(pos, 2, 2, 2))) {
                        if (entity instanceof LivingEntity living) {
                            if (living instanceof PlayerEntity targetPlayer) {
                                if (!TeamUtils.canDamagePlayer(player.getServer(), player, targetPlayer)) continue;
                            }
                            living.damage(player.getDamageSources().playerAttack(player), 5 + playerSoul.getEffectiveLV()/2f);
                        }
                    }
                }
                lightningRod.kill();
                playerSoul.setWeapon(new ItemStack(SoulForgeItems.LIGHTNING_ROD));
                return true;
            }
        }
        playerSoul.setWeapon(new ItemStack(SoulForgeItems.LIGHTNING_ROD));
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
        return new LightningRod();
    }
}

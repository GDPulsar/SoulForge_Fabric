package com.pulsar.soulforge.ability.perseverance;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.entity.PVHarpoonProjectile;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.networking.SoulForgeNetworking;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Furioso extends AbilityBase {
    public final String name = "Furisoso";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "furioso");
    public final int requiredLv = 20;
    public final int cost = 100;
    public final int cooldown = 6000;
    public final AbilityType type = AbilityType.SPECIAL;

    private PVHarpoonProjectile harpoon;
    private int timer = 0;
    private Vec3d castPos;
    private LivingEntity target;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        if (harpoon == null || harpoon.isRemoved()) {
            harpoon = new PVHarpoonProjectile(player.getWorld(), player);
            harpoon.setPosition(player.getEyePos());
            harpoon.setVelocity(player.getRotationVector().multiply(2));
            player.getWorld().spawnEntity(harpoon);
        }
        timer = 0;
        return true;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        if (!(harpoon == null || harpoon.isRemoved())) {
            if (harpoon.returning) {
                if (harpoon.hit == null) {
                    harpoon.kill();
                    harpoon = null;
                    player.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.MANA_OVERLOAD, 1800, 0));
                    return true;
                } else {
                    SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                    target = harpoon.hit;
                    if (target instanceof PlayerEntity targetPlayer) {
                        if (!TeamUtils.canDamagePlayer(player.getServer(), player, targetPlayer)) {
                            harpoon.kill();
                            harpoon = null;
                            player.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.MANA_OVERLOAD, 1800, 0));
                            return true;
                        }
                        SoulComponent targetSoul = SoulForge.getPlayerSoul(targetPlayer);
                        targetSoul.addTag("immobile");
                    } else if (target instanceof MobEntity targetMob) {
                        targetMob.setAiDisabled(true);
                    }
                    castPos = player.getPos();
                    playerSoul.addTag("immobile");
                    PacketByteBuf buf = PacketByteBufs.create().writeUuid(player.getUuid()).writeString("furioso_perseverance");
                    buf.writeBoolean(false);
                    if (player.getServer() != null) SoulForgeNetworking.broadcast(null, player.getServer(), SoulForgeNetworking.PERFORM_ANIMATION, buf);
                }
            }
            return false;
        }
        if (castPos == null || target == null) return true;
        timer++;
        doPlayerMovement(player, 0f, 0.425f, 10, 15);
        doPlayerMovement(player, 0.425f, 1.075f, 15, 30);
        doPlayerMovement(player, 1.075f, 1.35f, 30, 35);
        doPlayerMovement(player, 1.35f, 4.9f, 50, 53);
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        playerSoul.addTag("immobile");
        if (timer == 2) playerSoul.setWeapon(new ItemStack(SoulForgeItems.PERSEVERANCE_HARPOON), false);
        if (timer == 15) target.damage(player.getDamageSources().playerAttack(player), 9);
        if (timer == 22) playerSoul.removeWeapon();
        if (timer == 25) playerSoul.setWeapon(new ItemStack(SoulForgeItems.PERSEVERANCE_CLAW), false);
        if (timer == 34) target.damage(player.getDamageSources().playerAttack(player), 20);
        if (timer == 44) target.damage(player.getDamageSources().playerAttack(player), 20);
        if (timer == 45) playerSoul.removeWeapon();
        if (timer == 48) playerSoul.setWeapon(new ItemStack(SoulForgeItems.PERSEVERANCE_BLADES), false);
        if (timer == 52) target.damage(player.getDamageSources().playerAttack(player), 10);
        if (timer == 56) playerSoul.removeWeapon();
        if (timer == 60) playerSoul.setWeapon(new ItemStack(SoulForgeItems.PERSEVERANCE_EDGE), false);
        if (timer == 67) target.damage(player.getDamageSources().playerAttack(player), 7);
        if (timer == 82) target.damage(player.getDamageSources().playerAttack(player), 7);
        if (timer == 98) target.damage(player.getDamageSources().playerAttack(player), 7);
        if (timer == 110) {
            playerSoul.removeWeapon();
            player.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.MANA_OVERLOAD, 1800, 2));
        }
        return timer >= 110;
    }

    public void doPlayerMovement(PlayerEntity player, float distanceStart, float distanceEnd, int timerStart, int timerEnd) {
        if (timer < timerStart || timer > timerEnd) return;
        float lerpedDistance = MathHelper.lerp((float)(timer-timerStart)/(float)(timerEnd-timerStart), distanceStart, distanceEnd);
        player.teleport(castPos.x + player.getRotationVector().multiply(lerpedDistance).x, castPos.y, castPos.z + player.getRotationVector().multiply(lerpedDistance).z);
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        playerSoul.removeTag("immobile");
        if (target != null) {
            if (target instanceof PlayerEntity targetPlayer) {
                SoulComponent targetSoul = SoulForge.getPlayerSoul(targetPlayer);
                targetSoul.addTag("immobile");
            } else if (target instanceof MobEntity targetMob) {
                targetMob.setAiDisabled(true);
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
        return new Furioso();
    }
}

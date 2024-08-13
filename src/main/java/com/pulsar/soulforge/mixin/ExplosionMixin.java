package com.pulsar.soulforge.mixin;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.entity.DetonatorMine;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.TeamUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {
    @Shadow @Nullable public abstract Entity getEntity();

    @Shadow public float power;

    @Redirect(method = "collectBlocksAndDamageEntities", at=@At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    public boolean overwriteExplosionDamage(Entity instance, DamageSource source, float amount) {
        if (instance instanceof PlayerEntity player) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            if (playerSoul.hasValue("parry")) {
                if (playerSoul.getValue("parry") > 0) {
                    instance.playSound(SoulForgeSounds.PARRY_EVENT, 1f, 1f);
                    return false;
                }
            }
        }
        return instance.damage(source, amount);
    }

    @Redirect(method = "collectBlocksAndDamageEntities", at=@At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V"))
    public void overwriteEntityKnockback(Entity instance, Vec3d velocity) {
        if (instance instanceof PlayerEntity player) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            if (playerSoul.hasValue("parry")) {
                if (playerSoul.getValue("parry") > 0) {
                    return;
                }
            }
        }
        instance.setVelocity(instance.getVelocity().add(velocity));
    }

    @Redirect(method = "collectBlocksAndDamageEntities", at=@At(value = "INVOKE", target = "Lnet/minecraft/world/World;getOtherEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;)Ljava/util/List;"))
    private List<Entity> modifyEntityList(World instance, Entity entity, Box box) {
        return instance.getOtherEntities(entity, box, (target) -> {
            if (target instanceof PlayerEntity targetPlayer && entity instanceof PlayerEntity player) {
                return TeamUtils.canDamageEntity(instance.getServer(), player, targetPlayer);
            }
            return true;
        });
    }

    @Inject(method = "collectBlocksAndDamageEntities", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectArrayList;addAll(Ljava/util/Collection;)Z", shift = At.Shift.AFTER))
    private void modifyExplosionEntityDamage(CallbackInfo ci) {
        if (this.getEntity() instanceof DetonatorMine) {
            this.power = 2f;
        }
    }

    @Inject(method = "collectBlocksAndDamageEntities", at = @At("TAIL"))
    private void resetExplosionEntityDamage(CallbackInfo ci) {
        if (this.getEntity() instanceof DetonatorMine) {
            this.power = 7.5f;
        }
    }
}

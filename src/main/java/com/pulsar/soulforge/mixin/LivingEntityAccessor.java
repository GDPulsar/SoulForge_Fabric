package com.pulsar.soulforge.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Invoker(value = "blockedByShield")
    boolean invokeBlockedByShield(DamageSource source);

    @Invoker(value = "damageShield")
    void invokeDamageShield(float amount);

    @Invoker(value = "takeShieldHit")
    void invokeTakeShieldHit(LivingEntity attacker);
}

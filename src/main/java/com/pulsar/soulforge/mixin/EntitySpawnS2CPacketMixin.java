package com.pulsar.soulforge.mixin;

import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntitySpawnS2CPacket.class)
public class EntitySpawnS2CPacketMixin {
    @ModifyConstant(method = "<init>(ILjava/util/UUID;DDDFFLnet/minecraft/entity/EntityType;ILnet/minecraft/util/math/Vec3d;D)V", constant = @Constant(doubleValue = 3.9))
    private double soulforge$doWeirdAssMojankFixPositive(double constant) {
        return 20;
    }
    @ModifyConstant(method = "<init>(ILjava/util/UUID;DDDFFLnet/minecraft/entity/EntityType;ILnet/minecraft/util/math/Vec3d;D)V", constant = @Constant(doubleValue = -3.9))
    private double soulforge$doWeirdAssMojankFixNegative(double constant) {
        return -20;
    }
}

package com.pulsar.soulforge.mixin;

import com.pulsar.soulforge.accessors.OwnableMinion;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ActiveTargetGoal.class)
public abstract class ActiveTargetGoalMixin extends TrackTargetGoal {
    @Shadow @Final protected int reciprocalChance;

    public ActiveTargetGoalMixin(MobEntity mob, boolean checkVisibility) {
        super(mob, checkVisibility);
    }

    @Shadow protected abstract void findClosestTarget();

    @Shadow @Nullable protected LivingEntity targetEntity;

    @Inject(method = "findClosestTarget", at = @At("HEAD"), cancellable = true)
    private void modifyClosestTarget(CallbackInfo ci) {
        OwnableMinion minion = ((OwnableMinion)this.mob);
        if (minion.getOwnerUUID() != null) {
            if (minion.getTargetUUID() != null) {
                List<LivingEntity> target = this.mob.getWorld().getEntitiesByClass(LivingEntity.class, Box.of(this.mob.getPos(), 400, 400, 400), (entity) -> entity.getUuid().compareTo(minion.getTargetUUID()) == 0);
                if (!target.isEmpty()) {
                    this.targetEntity = target.get(0);
                    ci.cancel();
                }
            } else if (minion.getTargetPos() != Vec3d.ZERO) {
                ci.cancel();
            }
        }
    }

    @Override
    public boolean canStart() {
        if (this.reciprocalChance > 0 && this.mob.getRandom().nextInt(this.reciprocalChance) != 0) {
            return false;
        } else {
            this.findClosestTarget();
            return this.targetEntity != null;
        }
    }
}

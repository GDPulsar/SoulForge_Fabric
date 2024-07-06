package com.pulsar.soulforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.pulsar.soulforge.entity.DomeEntity;
import com.pulsar.soulforge.entity.DomePart;
import net.minecraft.entity.Entity;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.function.LazyIterationConsumer;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityLookup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Predicate;

@Mixin(World.class)
public abstract class WorldMixin {
    @Shadow protected abstract EntityLookup<Entity> getEntityLookup();

    @ModifyReturnValue(method = "getOtherEntities", at=@At("RETURN"))
    public List<Entity> getOtherEntities(List<Entity> list, @Local Box box) {
        getEntityLookup().forEachIntersects(box, entity -> {
            if (entity instanceof DomeEntity) {
                for (DomePart domePart : ((DomeEntity)entity).getParts()) {
                    if (box.contains(domePart.getPos())) {
                        list.add(domePart);
                    }
                }
            }
        });
        return list;
    }

    @Inject(method = "collectEntitiesByType(Lnet/minecraft/util/TypeFilter;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;Ljava/util/List;)V", at=@At("RETURN"))
    public <T extends Entity> void collectEntitiesByType(TypeFilter<Entity, T> filter, Box box, Predicate<Entity> predicate, List<Entity> result, CallbackInfo ci) {
        getEntityLookup().forEachIntersects(filter, box, entity -> {
            if (entity instanceof DomeEntity domeEntity) {
                for (DomePart domePart : domeEntity.getParts()) {
                    Entity entity2 = filter.downcast(domePart);
                    if (entity2 == null || !predicate.test(entity2)) continue;
                    result.add(entity2);
                }
            }
            return LazyIterationConsumer.NextIteration.CONTINUE;
        });
    }
}

package com.pulsar.soulforge.mixin;

import com.pulsar.soulforge.entity.DomeEntity;
import com.pulsar.soulforge.entity.DomePart;
import net.minecraft.entity.Entity;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.function.LazyIterationConsumer;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityLookup;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;

@Mixin(World.class)
public abstract class WorldMixin {
    @Shadow protected abstract EntityLookup<Entity> getEntityLookup();

    @Inject(method = "getOtherEntities", at=@At("RETURN"), cancellable = true)
    public void getOtherEntities(@Nullable Entity except, Box box, Predicate<? super Entity> predicate, CallbackInfoReturnable<List<Entity>> cir) {
        List<Entity> list = cir.getReturnValue();
        getEntityLookup().forEachIntersects(box, entity -> {
            if (entity instanceof DomeEntity) {
                for (DomePart domePart : ((DomeEntity)entity).getParts()) {
                    if (entity == except || !predicate.test(domePart)) continue;
                    list.add(domePart);
                }
            }
        });
        cir.setReturnValue(list);
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

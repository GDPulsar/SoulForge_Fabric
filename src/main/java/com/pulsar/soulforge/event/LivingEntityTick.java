package com.pulsar.soulforge.event;

import com.pulsar.soulforge.accessors.ValueHolder;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundCategory;

public class LivingEntityTick {
    public static void tick(LivingEntity living) {
        ValueHolder values = ((ValueHolder)living);
        if (values.hasInt("HangToAThreadTimer")) {
            values.setInt("HangToAThreadTimer", values.getInt("HangToAThreadTimer") - 1);
            if (!values.hasBool("HangToAThreadDamaging") || !values.getBool("HangToAThreadDamaging")) {
                if (values.getInt("HangToAThreadTimer") % 5 == 0) {
                    living.getWorld().playSound(null, living.getBlockPos(), SoulForgeSounds.UT_TICK_EVENT, SoundCategory.MASTER, 1f, 1f);
                }
                if (values.getInt("HangToAThreadTimer") == 0) {
                    values.setBool("HangToAThreadDamaging", true);
                    values.setInt("HangToAThreadTimer", 140);
                    values.setInt("HangToAThreadDamageCount", 0);
                    living.getWorld().playSound(null, living.getBlockPos(), SoulForgeSounds.UT_CHAINSAW_EVENT, SoundCategory.MASTER, 1f, 1f);
                }
            } else {
                if (140 - values.getInt("HangToAThreadTimer") >=
                        values.getInt("HangToAThreadDamageCount") * (140f / values.getFloat("HangToAThreadDamage"))) {
                    living.timeUntilRegen = 0;
                    living.damage(SoulForgeDamageTypes.of(living.getWorld(), SoulForgeDamageTypes.ABILITY_DAMAGE_TYPE), Math.max(1f, values.getFloat("HangToAThreadDamage") / 140f));
                    values.setInt("HangToAThreadDamageCount", values.getInt("HangToAThreadDamageCount") + 1);
                }
                if (values.getInt("HangToAThreadTimer") == 0) {
                    values.removeInt("HangToAThreadTimer");
                    values.removeFloat("HangToAThreadDamage");
                    values.removeBool("HangToAThreadDamaging");
                }
            }
        }
    }
}

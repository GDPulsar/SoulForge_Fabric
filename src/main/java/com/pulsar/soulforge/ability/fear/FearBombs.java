package com.pulsar.soulforge.ability.fear;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.entity.FearBombEntity;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class FearBombs extends AbilityBase {
    Vec3d pos = Vec3d.ZERO;
    Vec3d offset = Vec3d.ZERO;
    int summonCount = 0;
    float spread = 4f;
    List<FearBombEntity> toSummon = new ArrayList<>();
    int timer = 0;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        float magicCost = Math.max(20f * (1f - summonCount * 0.1f), 4f) * (playerSoul.isPure() ? 0.5f : 1.0f) * (float)player.getAttributeInstance(SoulForgeAttributes.MAGIC_COOLDOWN).getValue();
        if (playerSoul.getMagic() < magicCost) return false;
        playerSoul.setMagic(playerSoul.getMagic() - magicCost);
        playerSoul.resetLastCastTime();
        if (!getActive()) {
            HitResult result = Utils.getFocussedEntity(player, 20f);
            if (result == null) result = player.raycast(20f, 1f, false);
            if (result != null) {
                spread = 6f;
                pos = result.getPos();
                offset = new Vec3d(0f, 1f, 0f);
                summonCount = 0;
                timer = 0;
                toSummon = new ArrayList<>();
                if (result instanceof BlockHitResult blockHit)
                    offset = Utils.vector3fToVec3d(blockHit.getSide().getUnitVector());
                addBombs(player);
                return super.cast(player);
            }
        } else {
            spread = Math.min(spread * 1.2f, 12);
            summonCount++;
            addBombs(player);
            return false;
        }
        return false;
    }

    void addBombs(ServerPlayerEntity player) {
        for (int i = 0; i < Math.min(Math.pow(2, summonCount) * 5, 20); i++) {
            FearBombEntity bomb = new FearBombEntity(player.getWorld(), player);
            Vec3d posOffset = new Vec3d(Math.random()*spread*2 - spread, Math.random()*spread - spread/2f, Math.random()*spread*2 - spread);
            bomb.setPosition(pos.add(offset.multiply(spread/2)).add(posOffset));
            toSummon.add(bomb);
        }
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        float ticksToNextSummon = (float)Math.pow(2, -(Math.floor(toSummon.size()/5f)-1));
        int amountToSummon = Math.max((int)(1f/ticksToNextSummon), 1);
        timer++;
        if (ticksToNextSummon >= timer) {
            for (int i = 0; i < amountToSummon; i++) {
                if (!toSummon.isEmpty()) {
                    FearBombEntity bomb = toSummon.get((int) (Math.random() * toSummon.size()));
                    player.getWorld().spawnEntity(bomb);
                    player.getWorld().playSound(null, bomb.getBlockPos(), SoulForgeSounds.MINE_SUMMON_EVENT, SoundCategory.MASTER, 1f, 1f);
                    toSummon.remove(bomb);
                }
            }
            timer = 0;
        }
        return toSummon.isEmpty();
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        summonCount = 0;
        spread = 4f;
        timer = 0;
        return super.end(player);
    }

    public int getLV() { return 20; }

    public int getCost() { return 0; }

    public int getCooldown() { return 0; }

    public AbilityType getType() { return AbilityType.CAST; }
    @Override
    public AbilityBase getInstance() {
        return new FearBombs();
    }
}

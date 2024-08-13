package com.pulsar.soulforge.ability.patience;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.entity.SnowgraveProjectile;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;

import java.util.UUID;

public class Proceed extends ToggleableAbilityBase {
    int timer = 0;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        if (!getActive()) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            if (playerSoul.getStyleRank() < 5) {
                player.sendMessageToClient(Text.translatable(Math.random() < 0.01f ? "soulforge.style.get_real" : "soulforge.style.not_enough"), true);
                return false;
            }
            playerSoul.setStyleRank(playerSoul.getStyleRank() - 5);
            timer = 0;
        }
        return super.cast(player);
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        float elvBoost = (((timer/10f)*(timer/10f)*(timer/10f)) - ((6*timer/20f)*(2*timer/20f))+150)/100f;
        if (timer >= 60) elvBoost = 0.42f;
        EntityAttributeModifier modifier = new EntityAttributeModifier(UUID.fromString("ee2eb3dc-e9a2-4414-9c83-b745bc25563d"), "proceed", elvBoost, EntityAttributeModifier.Operation.ADDITION);
        player.getAttributeInstance(SoulForgeAttributes.MAGIC_POWER).tryRemoveModifier(modifier.getId());
        player.getAttributeInstance(SoulForgeAttributes.MAGIC_POWER).addPersistentModifier(modifier);
        return super.tick(player);
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        HitResult hit = ProjectileUtil.getCollision(player, (entity) -> true, 50f);
        Vec3d end = hit.getPos();
        SnowgraveProjectile snowgrave = new SnowgraveProjectile(player, player.getWorld(), end);
        ServerWorld serverWorld = player.getServerWorld();
        serverWorld.spawnEntity(snowgrave);
        serverWorld.emitGameEvent(GameEvent.ENTITY_PLACE, player.getPos(), GameEvent.Emitter.of(player));
        serverWorld.playSoundFromEntity(null, snowgrave, SoulForgeSounds.SNOWGRAVE_EVENT, SoundCategory.PLAYERS, 1f, 1f);
        player.getAttributeInstance(SoulForgeAttributes.MAGIC_POWER).removeModifier(UUID.fromString("ee2eb3dc-e9a2-4414-9c83-b745bc25563d"));
        return super.end(player);
    }

    public int getLV() { return 20; }

    public int getCost() { return 100; }

    public int getCooldown() { return 6000; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new Proceed();
    }
}

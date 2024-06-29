package com.pulsar.soulforge.ability.pures;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.entity.FireTornadoProjectile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.RaycastContext;

public class ResplendentPhoenix extends AbilityBase {
    public final String name = "Resplendent Phoenix";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "resplendent_phoenix");
    public final int requiredLv = 15;
    public final int cost = 80;
    public final int cooldown = 400;
    public final AbilityType type = AbilityType.CAST;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        BlockHitResult hitResult = player.getWorld().raycast(new RaycastContext(player.getEyePos(), player.getEyePos().add(player.getRotationVector().multiply(40f)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player));
        if (hitResult != null) {
            FireTornadoProjectile tornado = new FireTornadoProjectile(player.getWorld(), player, hitResult.getPos());
            tornado.owner = player;
            tornado.setPosition(hitResult.getPos());
            player.getWorld().spawnEntity(tornado);
        }
        return true;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        return true;
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
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
        return new ResplendentPhoenix();
    }
}

package com.pulsar.soulforge.ability.patience;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.entity.FrozenEnergyProjectile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SkewerWeakpoint extends AbilityBase {
    public final String name = "Skewer Weakpoint";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "skewer_weakpoint");
    public final int requiredLv = 15;
    public final int cost = 30;
    public final int cooldown = 200;
    public final AbilityType type = AbilityType.CAST;
    
    public int timer = 0;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        timer = 13;
        return true;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        timer--;
        if (timer % 3 == 0) {
            World world = player.getWorld();
            FrozenEnergyProjectile projectile = new FrozenEnergyProjectile(world, player);
            projectile.setPosition(player.getEyePos());
            float f = (float)((player.getPitch()+Math.random()*2.5f-1.25f) * 0.017453292F);
            float g = (float)(-(player.getYaw()+Math.random()*2.5f-1.25f) * 0.017453292F);
            float h = MathHelper.cos(g);
            float i = MathHelper.sin(g);
            float j = MathHelper.cos(f);
            float k = MathHelper.sin(f);
            Vec3d velocity = new Vec3d(i * j, -k, h * j);
            projectile.setVelocity(velocity.multiply(2));
            world.spawnEntity(projectile);
        }
        return timer <= 0;
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
        return new SkewerWeakpoint();
    }
}

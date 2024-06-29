package com.pulsar.soulforge.ability.justice;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.bravery.BraveryBoost;
import com.pulsar.soulforge.entity.FragmentationGrenadeProjectile;
import com.pulsar.soulforge.entity.JusticePelletProjectile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class FragmentationGrenade extends AbilityBase {
    public final String name = "FragmentationGrenade";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "fragmentation_grenade");
    public final int requiredLv = 17;
    public final int cost = 40;
    public final int cooldown = 300;
    public final AbilityType type = AbilityType.CAST;
    

    @Override
    public boolean cast(ServerPlayerEntity player) {
        World world = player.getWorld();
        FragmentationGrenadeProjectile projectile = new FragmentationGrenadeProjectile(world, player.getEyePos(), player);
        projectile.setPosition(player.getEyePos());
        projectile.setVelocity(player.getRotationVector().multiply(1.5f));
        projectile.setOwner(player);
        world.spawnEntity(projectile);
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
        return new FragmentationGrenade();
    }
}

package com.pulsar.soulforge.ability.kindness;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.SoulForgeItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class KindnessShield extends AbilityBase {
    public final String name = "Kindness Shield";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "kindness_shield");
    public final int requiredLv = 3;
    public final int cost = 20;
    public final int cooldown = 0;
    public final AbilityType type = AbilityType.WEAPON;
    

    @Override
    public boolean cast(ServerPlayerEntity player) {

        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        playerSoul.setWeapon(new ItemStack(SoulForgeItems.KINDNESS_SHIELD));
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
        return new KindnessShield();
    }
}

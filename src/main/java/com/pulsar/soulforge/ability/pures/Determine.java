package com.pulsar.soulforge.ability.pures;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.bravery.*;
import com.pulsar.soulforge.ability.justice.*;
import com.pulsar.soulforge.ability.kindness.*;
import com.pulsar.soulforge.ability.patience.*;
import com.pulsar.soulforge.ability.integrity.*;
import com.pulsar.soulforge.ability.perseverance.*;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.networking.SoulForgeNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.*;

public class Determine extends AbilityBase {
    public final String name = "Determine";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "determine");
    public final int requiredLv = 10;
    public int cost = 25;
    public int cooldown = 140;
    public final AbilityType type = AbilityType.CAST;

    public int currentTrait = 0;
    public AbilityBase selected = null;

    public static List<AbilityBase> getCurrentOptions(int currentTrait) {
        List<AbilityBase> abilities = new ArrayList<>();
        switch (currentTrait) {
            case 0:
                abilities.add(new EnergyBall());
                abilities.add(new EnergyWave());
                abilities.add(new Eruption());
                abilities.add(new Polarities());
                abilities.add(new Shatter());
                break;
            case 1:
                abilities.add(new BulletRing());
                abilities.add(new FragmentationGrenade());
                abilities.add(new JusticePellets());
                abilities.add(new Launch());
                abilities.add(new Railcannon());
                break;
            case 2:
                abilities.add(new AllyHeal());
                abilities.add(new ExpandingForce());
                abilities.add(new Immobilization());
                abilities.add(new KindnessDome());
                abilities.add(new ProtectiveTouch());
                abilities.add(new SelfHeal());
                break;
            case 3:
                abilities.add(new BlindingSnowstorm());
                abilities.add(new FrozenGrasp());
                abilities.add(new Iceshock());
                abilities.add(new SkewerWeakpoint());
                abilities.add(new Snowglobe());
                abilities.add(new WeatherWarning());
                break;
            case 4:
                abilities.add(new GravityAnchor());
                abilities.add(new KineticBoost());
                abilities.add(new TelekinesisEntity());
                abilities.add(new TelekineticShockwave());
                break;
            case 5:
                abilities.add(new ColossalClaymore());
                abilities.add(new Onrush());
                abilities.add(new RendAsunder());
                break;
        }
        return abilities;
    }


    @Override
    public boolean cast(ServerPlayerEntity player) {
        if (player.isSneaking()) {
            player.sendMessage(Text.translatable("ability.determine.reset"));
            selected = null;
            currentTrait = (currentTrait+1)%6;
            return false;
        }
        ServerPlayNetworking.send(player, SoulForgeNetworking.DETERMINE_SCREEN, PacketByteBufs.create().writeVarInt(currentTrait));
        return false;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        return true;
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (selected != null) {
            playerSoul.setCooldown(this, selected.getCooldown());
            playerSoul.setValue("determineCooldown", selected.getCooldown());
            currentTrait = (currentTrait+1)%6;
        }
        selected = null;
        return true;
    }

    @Override
    public void displayTick(PlayerEntity player) {
        if (selected != null) {
            selected.displayTick(player);
        }
    }

    public String getName() { return name; }

    public Text getLocalizedText() { return Text.translatable("ability."+id.getPath()+".name"); }

    public Identifier getID() { return id; }

    public String getTooltip() { return Text.translatable("ability."+id.getPath()+".tooltip").getString(); }

    public int getLV() { return requiredLv; }

    public int getCost() {
        if (selected != null) {
            return Math.max(selected.getCost(), cost);
        }
        return cost;
    }

    public int getCooldown() {
        if (selected != null) {
            return Math.max(selected.getCooldown(), cooldown);
        }
        return cooldown;
    }

    public AbilityType getType() { return type; }

    @Override
    public AbilityBase getInstance() {
        return new Determine();
    }

    @Override
    public NbtCompound saveNbt(NbtCompound nbt) {
        nbt.putInt("trait", currentTrait);
        return super.saveNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (!Objects.equals(nbt.getString("id"), getID().getPath())) return;
        currentTrait = nbt.getInt("trait");
    }
}

package com.pulsar.soulforge.ability.perseverance;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.trait.Traits;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class Furioso extends AbilityBase {
    int timer = 0;
    int switchTimer = 0;
    int weaponIndex = 0;
    public boolean usedClaymore = false;

    public void doSwitch(PlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        switchTimer = 200;
        List<Item> weapons = new ArrayList<>();
        if (!playerSoul.hasTrait(Traits.justice) || playerSoul.hasTrait(Traits.spite)) {
            weapons.addAll(List.of(
                    SoulForgeItems.PERSEVERANCE_BLADES, SoulForgeItems.PERSEVERANCE_EDGE,
                    SoulForgeItems.PERSEVERANCE_CLAW, SoulForgeItems.PERSEVERANCE_HARPOON
            ));
        }
        if ((playerSoul.isPure() || playerSoul.hasTrait(Traits.spite)) && !usedClaymore) {
            weapons.add(SoulForgeItems.COLOSSAL_CLAYMORE);
        }
        if (playerSoul.hasTrait(Traits.bravery) || playerSoul.hasTrait(Traits.spite)) {
            weapons.addAll(List.of(SoulForgeItems.BRAVERY_SPEAR, SoulForgeItems.BRAVERY_HAMMER, SoulForgeItems.BRAVERY_GAUNTLETS));
        }
        if (playerSoul.hasTrait(Traits.justice) || playerSoul.hasTrait(Traits.spite)) {
            weapons.addAll(List.of(
                    SoulForgeItems.GUNBLADES, SoulForgeItems.MUSKET_BLADE,
                    SoulForgeItems.GUNLANCE, SoulForgeItems.JUSTICE_HARPOON
            ));
        }
        if (playerSoul.hasTrait(Traits.kindness) || playerSoul.hasTrait(Traits.spite)) {
            weapons.add(SoulForgeItems.KINDNESS_SHIELD);
        }
        if (playerSoul.hasTrait(Traits.patience) || playerSoul.hasTrait(Traits.spite)) {
            weapons.add(SoulForgeItems.FREEZE_RING);
        }
        if (playerSoul.hasTrait(Traits.integrity) || playerSoul.hasTrait(Traits.spite)) {
            weapons.add(SoulForgeItems.INTEGRITY_RAPIER);
        }
        weaponIndex = (weaponIndex + 1) % weapons.size();
        playerSoul.setWeapon(new ItemStack(weapons.get(weaponIndex)));
    }

    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (playerSoul.getStyleRank() < 5) {
            player.sendMessageToClient(Text.translatable(Math.random() < 0.01f ? "soulforge.style.get_real" : "soulforge.style.not_enough"), true);
            return false;
        }
        usedClaymore = false;
        timer = 1800;
        switchTimer = 200;
        weaponIndex = -1;
        doSwitch(player);
        return super.cast(player);
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 2, 1));
        timer--;
        switchTimer--;
        if (switchTimer <= 0) doSwitch(player);
        return super.tick(player) && timer <= 0;
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        timer = 0;
        switchTimer = 0;
        weaponIndex = 0;
        player.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.MANA_OVERLOAD, 4800, 0));
        return super.end(player);
    }

    public int getLV() { return 20; }

    public int getCost() { return 100; }

    public int getCooldown() { return 4800; }

    public AbilityType getType() { return AbilityType.CAST; }

    public AbilityBase getInstance() { return new Furioso(); }

    public NbtCompound saveNbt(NbtCompound nbt) {
        nbt.putInt("timer", timer);
        nbt.putInt("switchTimer", switchTimer);
        nbt.putInt("weaponIndex", weaponIndex);
        return super.saveNbt(nbt);
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        timer = nbt.getInt("timer");
        switchTimer = nbt.getInt("switchTimer");
        weaponIndex = nbt.getInt("weaponIndex");
    }
}

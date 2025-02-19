package com.pulsar.soulforge.item.weapons;

import com.github.crimsondawn45.fabricshieldlib.lib.object.FabricShield;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.components.TemporaryModifierComponent;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class PerseveranceBlades extends MagicSwordItem implements FabricShield {
    public PerseveranceBlades() {
        super(6, 1.2f, 0.6f);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.soulforge.perseverance_blades.tooltip"));
    }


    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul((PlayerEntity)attacker);
        Utils.addAntiheal(0.3f, playerSoul.getLV()*20, target);
        return super.postHit(stack, target, attacker);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (selected && entity instanceof ServerPlayerEntity player) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            if (playerSoul.hasCast("Furioso")) {
                TemporaryModifierComponent modifers = SoulForge.getTemporaryModifiers(player);
                modifers.addTemporaryModifier(SoulForgeAttributes.DAMAGE_REDUCTION, new EntityAttributeModifier(
                        UUID.fromString("3030f14f-9e19-4955-8efa-aa141cf98ef5"), "furioso_pv_blades",
                        -0.2, EntityAttributeModifier.Operation.MULTIPLY_TOTAL
                ), 2f);
            }
        }
    }

    @Override
    public int getCoolDownTicks() {
        return 100;
    }

    @Override
    public boolean supportsBanner() {
        return false;
    }
}

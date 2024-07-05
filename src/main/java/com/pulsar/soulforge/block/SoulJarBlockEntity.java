package com.pulsar.soulforge.block;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.data.AbilityLayout;
import com.pulsar.soulforge.trait.TraitBase;
import com.pulsar.soulforge.trait.Traits;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SoulJarBlockEntity extends BlockEntity {
    public boolean hasSoul = false;

    public String owner = "";
    public String trait1 = "";
    public String trait2 = "";
    public boolean strong = false;
    public boolean pure = false;
    public int lv = 1;
    public int exp = 0;
    public AbilityLayout layout = new AbilityLayout();

    public SoulJarBlockEntity(BlockPos pos, BlockState state) {
        super(SoulForgeBlocks.SOUL_JAR_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putBoolean("hasSoul", hasSoul);
        if (hasSoul) {
            NbtCompound soul = new NbtCompound();
            soul.putString("owner", owner);
            soul.putString("trait1", trait1);
            soul.putString("trait2", trait2);
            soul.putBoolean("strong", strong);
            soul.putBoolean("pure", pure);
            soul.putInt("lv", lv);
            soul.putInt("exp", exp);
            soul.put("layout", layout.toNbt());
            nbt.put("soul", soul);
        }
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        hasSoul = nbt.getBoolean("hasSoul");
        if (hasSoul) {
            NbtCompound soul = nbt.getCompound("soul");
            owner = soul.getString("owner");
            trait1 = soul.getString("trait1");
            trait2 = soul.getString("trait2");
            strong = soul.getBoolean("strong");
            pure = soul.getBoolean("pure");
            lv = soul.getInt("lv");
            exp = soul.getInt("exp");
            List<TraitBase> traits = new ArrayList<>();
            if (!Objects.equals(trait1, "")) traits.add(Traits.get(trait1));
            if (!Objects.equals(trait2, "")) traits.add(Traits.get(trait2));
            List<AbilityBase> abilities = Traits.getAbilities(traits, lv, pure);
            layout = AbilityLayout.fromNbt(abilities, soul.getList("layout", NbtElement.COMPOUND_TYPE));
        }
        super.readNbt(nbt);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}

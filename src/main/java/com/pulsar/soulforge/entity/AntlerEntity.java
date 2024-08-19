package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.item.SoulForgeItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class AntlerEntity extends ThrownItemEntity {
    public AntlerEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public AntlerEntity(World world, PlayerEntity user) {
        super(SoulForgeEntities.ANTLER_ENTITY_TYPE, world);
    }

    @Override
    protected Item getDefaultItem() {
        return SoulForgeItems.ANTLER;
    }
}

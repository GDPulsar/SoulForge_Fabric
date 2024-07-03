package com.pulsar.soulforge.ability.kindness;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.block.SoulForgeBlocks;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.entity.DomeEntity;
import com.pulsar.soulforge.entity.DomePart;
import com.pulsar.soulforge.entity.ShieldShardEntity;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.trait.Traits;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.Objects;

public class KindnessDome extends ToggleableAbilityBase {
    public final String name = "Kindness Dome";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "kindness_dome");
    public final int requiredLv = 5;
    public final int cost = 40;
    public final int cooldown = 300;

    public DomeEntity entity;
    private BlockPos center = null;
    private int domeRadius = 4;

    private float lengthSq(int x, int y, int z) {
        return (x*x + y*y + z*z);
    }

    @Override
    public boolean cast(ServerPlayerEntity player) {
        if (!player.getWorld().isClient) {
            BlockHitResult hitResult = player.getWorld().raycast(new RaycastContext(player.getEyePos(), player.getEyePos().add(player.getRotationVector().multiply(40f)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player));
            if (hitResult != null) {
                toggleActive();
                if (getActive()) {
                    SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                    center = hitResult.getBlockPos();
                    domeRadius = MathHelper.floor(playerSoul.getEffectiveLV()/10f) + 4;
                    player.getWorld().playSoundFromEntity(null, player, SoulForgeSounds.DR_RUDEBUSTER_SWING_EVENT, SoundCategory.PLAYERS, 10f, 1f);
                    entity = new DomeEntity(player.getWorld(), player.getBlockPos().toCenterPos().subtract(0.5f, 0.5f, 0.5f), domeRadius,
                            playerSoul.getEffectiveLV() * 10, false, player, playerSoul.getTraits().contains(Traits.perseverance) && playerSoul.getTraits().contains(Traits.kindness));
                    entity.setPosition(player.getBlockPos().toCenterPos().subtract(0.5f, 0.5f, 0.5f));
                    double radius = domeRadius + 0.5;
                    double radSq = radius * radius;
                    double rad1Sq = (radius - 1.5) * (radius - 1.5);
                    int ceilRad = MathHelper.ceil(radius);
                    for (int x = 0; x <= ceilRad; x++) {
                        for (int y = 0; y < ceilRad; y++) {
                            for (int z = 0; z <= ceilRad; z++) {
                                double distanceSq = lengthSq(x, y, z);
                                if (distanceSq > radSq) continue;
                                if (distanceSq < rad1Sq) continue;

                                placeDomeBlock(x, y, z, player);
                                placeDomeBlock(-x, y, z, player);
                                placeDomeBlock(x, -y, z, player);
                                placeDomeBlock(-x, -y, z, player);
                                placeDomeBlock(x, y, -z, player);
                                placeDomeBlock(-x, y, -z, player);
                                placeDomeBlock(x, -y, -z, player);
                                placeDomeBlock(-x, -y, -z, player);
                            }
                        }
                    }
                }
            }
        }
        return getActive();
    }

    private void placeDomeBlock(int x, int y, int z, PlayerEntity player) {
        BlockPos pos = new BlockPos(x, y, z).add(center);
        World world = player.getWorld();
        if (!world.isClient) {
            if (pos.toCenterPos().distanceTo(center.toCenterPos()) <= domeRadius) {
                if (!world.getBlockState(pos).isSolid()) {
                    BlockState state = SoulForgeBlocks.DOME_BLOCK.getDefaultState();
                    world.setBlockState(pos, state);
                    DomePart part = new DomePart(entity, x+center.getX(), y+center.getY(), z+center.getZ());
                    player.getWorld().spawnEntity(part);
                    entity.addPart(part);
                }
            }
        }
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        if (entity != null) {
            return !getActive() || !entity.isAlive() || entity.isRemoved();
        }
        return !getActive();
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        for (int x = -domeRadius; x <= domeRadius; x++) {
            for (int y = -domeRadius; y <= domeRadius; y++) {
                for (int z = -domeRadius; z <= domeRadius; z++) {
                    BlockPos pos = new BlockPos(x, y, z).add(center);
                    if (player.getWorld().getBlockState(pos).isOf(SoulForgeBlocks.DOME_BLOCK)) {
                        player.getWorld().addBlockBreakParticles(pos, player.getWorld().getBlockState(pos));
                        player.getWorld().setBlockState(pos, Blocks.AIR.getDefaultState());
                    }
                }
            }
        }
        if (playerSoul.getTraits().contains(Traits.kindness) && playerSoul.getTraits().contains(Traits.integrity)) {
            for (int i = 0; i < 15; i++) {
                Vec3d velocity = new Vec3d(Math.random()-0.5f, Math.random()-0.5f, Math.random()-0.5f).normalize().multiply(2f);
                ShieldShardEntity shard = new ShieldShardEntity(player, center.toCenterPos(), velocity);
                shard.setPosition(center.toCenterPos());
                shard.setVelocity(velocity);
                player.getWorld().spawnEntity(shard);
            }
        }
        if (entity != null) {
            for (DomePart part : entity.getParts()) {
                if (!part.isRemoved()) part.remove(Entity.RemovalReason.KILLED);
            }
            if (!entity.isRemoved()) entity.remove(Entity.RemovalReason.KILLED);
        }
        entity = null;
        player.getWorld().playSoundFromEntity(null, player, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 10f, 1f);
        return true;
    }

    @Override
    public void displayTick(ClientPlayerEntity player) {
        BlockHitResult hitResult = player.getWorld().raycast(new RaycastContext(player.getEyePos(), player.getEyePos().add(player.getRotationVector().multiply(40f)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player));
        if (hitResult != null) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            domeRadius = MathHelper.floor(playerSoul.getEffectiveLV()/10f) + 4;
            Vec3d centerPos = hitResult.getPos();
            float phiStep = (float) (Math.PI / 16);
            float thetaStep = (float) (2.0 * Math.PI / 16);
            for (int i = 0; i < 16; i++) {
                float phi = i * phiStep;
                for (int j = 0; j < 16; j++) {
                    float theta = j * thetaStep;
                    float x = (float) (Math.sin(phi) * Math.cos(theta));
                    float y = (float) Math.cos(phi);
                    float z = (float) (Math.sin(phi) * Math.sin(theta));
                    Vector3f particlePos = new Vector3f(x, y, z).normalize().mul(domeRadius);
                    player.getWorld().addParticle(new DustParticleEffect(Vec3d.unpackRgb(0x00FF00).toVector3f(), 1f), particlePos.x + centerPos.x, particlePos.y + centerPos.y, particlePos.z + centerPos.z, 0, 0, 0);
                }
            }
        }
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
        return new KindnessDome();
    }

    @Override
    public NbtCompound saveNbt(NbtCompound nbt) {
        nbt.put("center", NbtHelper.fromBlockPos(center));
        return super.saveNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (!Objects.equals(nbt.getString("id"), getID().getPath())) return;
        super.readNbt(nbt);
        center = NbtHelper.toBlockPos(nbt.getCompound(("center")));
    }
}

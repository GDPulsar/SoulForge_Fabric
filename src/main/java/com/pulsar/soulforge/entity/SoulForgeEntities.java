package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.SoulForge;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SoulForgeEntities {
    public static EntityType<JusticeArrowProjectile> JUSTICE_ARROW_ENTITY_TYPE;
    public static EntityType<DeterminationArrowProjectile> DETERMINATION_ARROW_ENTITY_TYPE;
    public static EntityType<BraverySpearProjectile> BRAVERY_SPEAR_ENTITY_TYPE;
    public static EntityType<BlastEntity> HORIZONTAL_BLAST_ENTITY_TYPE;
    public static EntityType<GunlanceBlastEntity> GUNLANCE_BLAST_ENTITY_TYPE;
    public static EntityType<DeterminationSpearProjectile> DETERMINATION_SPEAR_ENTITY_TYPE;
    public static EntityType<SOJProjectile> SOJ_ENTITY_TYPE;
    public static EntityType<EnergyBallProjectile> ENERGY_BALL_ENTITY_TYPE;
    public static EntityType<JusticePelletProjectile> JUSTICE_PELLET_ENTITY_TYPE;
    public static EntityType<DomeEntity> DOME_ENTITY_TYPE;
    public static EntityType<DomePart> DOME_PART_TYPE;
    public static EntityType<AutoTurretEntity> TURRET_ENTITY_TYPE;
    public static EntityType<SmallSlashProjectile> SMALL_SLASH_ENTITY_TYPE;
    public static EntityType<BigSlashProjectile> BIG_SLASH_ENTITY_TYPE;
    public static EntityType<SpecialHellEntity> SPECIAL_HELL_ENTITY_TYPE;
    public static EntityType<OrbitalStrikeEntity> ORBITAL_STRIKE_ENTITY_TYPE;
    public static EntityType<FragmentationGrenadeProjectile> FRAGMENTATION_GRENADE_ENTITY_TYPE;
    public static EntityType<FrozenEnergyProjectile> FROZEN_ENERGY_ENTITY_TYPE;
    public static EntityType<HailProjectile> HAIL_ENTITY_TYPE;
    public static EntityType<PVHarpoonProjectile> PV_HARPOON_ENTITY_TYPE;
    public static EntityType<DTHarpoonProjectile> DT_HARPOON_ENTITY_TYPE;
    public static EntityType<ImmobilizationEntity> IMMOBILIZATION_ENTITY_TYPE;
    public static EntityType<DeterminationPlatformEntity> DETERMINATION_PLATFORM_ENTITY_TYPE;
    public static EntityType<IntegrityPlatformEntity> INTEGRITY_PLATFORM_ENTITY_TYPE;
    public static EntityType<SnowgraveProjectile> SNOWGRAVE_PROJECTILE_TYPE;
    public static EntityType<WeatherWarningLightningEntity> WEATHER_WARNING_LIGHTNING_ENTITY_TYPE;
    public static EntityType<LightningRodLightningEntity> LIGHTNING_ROD_LIGHTNING_ENTITY_TYPE;
    public static EntityType<DomeEmitterEntity> DOME_EMITTER_ENTITY_TYPE;
    public static EntityType<IncendiaryGrenadeEntity> INCENDIARY_GRENADE_ENTITY_TYPE;
    public static EntityType<LightningRodProjectile> LIGHTNING_ROD_ENTITY_TYPE;
    public static EntityType<DetonatorMine> DETONATOR_MINE_ENTITY_TYPE;
    public static EntityType<PolarityBallEntity> POLARITY_BALL_ENTITY_TYPE;
    public static EntityType<JusticeArrowTrinketProjectile> JUSTICE_ARROW_TRINKET_TYPE;
    public static EntityType<AntihealDartProjectile> ANTIHEAL_DART_ENTITY_TYPE;
    public static EntityType<ShieldShardEntity> SHIELD_SHARD_ENTITY_TYPE;
    public static EntityType<BouncingShieldEntity> BOUNCING_SHIELD_ENTITY_TYPE;
    public static EntityType<GrappleHookProjectile> GRAPPLE_HOOK_ENTITY_TYPE;
    public static EntityType<FireTornadoProjectile> FIRE_TORNADO_ENTITY_TYPE;
    public static EntityType<JusticeHarpoonProjectile> JUSTICE_HARPOON_ENTITY_TYPE;
    public static EntityType<WormholeEntity> WORMHOLE_ENTITY_TYPE;
    public static EntityType<SwordSlashProjectile> SWORD_SLASH_ENTITY_TYPE;
    public static EntityType<YoyoProjectile> YOYO_ENTITY_TYPE;
    public static EntityType<RailkillerEntity> RAILKILLER_ENTITY_TYPE;
    public static EntityType<PlayerSoulEntity> PLAYER_SOUL_ENTITY_TYPE;
    public static EntityType<DeterminationShotProjectile> DETERMINATION_SHOT_ENTITY_TYPE;
    public static EntityType<FearBombEntity> FEAR_BOMB_ENTITY_TYPE;
    public static EntityType<SlowballProjectile> SLOWBALL_ENTITY_TYPE;
    public static EntityType<IceSpikeProjectile> ICE_SPIKE_ENTITY_TYPE;
    public static EntityType<TotalFrostbiteEntity> TOTAL_FROSTBITE_ENTITY_TYPE;
    public static EntityType<AntlerEntity> ANTLER_ENTITY_TYPE;
    public static EntityType<SkullProjectile> SKULL_ENTITY_TYPE;

    public static void register() {
        SNOWGRAVE_PROJECTILE_TYPE = Registry.register(
                Registries.ENTITY_TYPE, new Identifier(SoulForge.MOD_ID, "snowgrave"),
                FabricEntityTypeBuilder.<SnowgraveProjectile>create(SpawnGroup.MISC, SnowgraveProjectile::new)
                        .trackRangeBlocks(100).trackedUpdateRate(40)
                        .dimensions(EntityDimensions.fixed(7f, 7f)).build()
        );

        JUSTICE_ARROW_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "justice_arrow"),
                FabricEntityTypeBuilder.<JusticeArrowProjectile>create(SpawnGroup.MISC, JusticeArrowProjectile::new)
                        .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                        .trackRangeBlocks(50).trackedUpdateRate(40)
                        .build()
        );

        DETERMINATION_ARROW_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "determination_arrow"),
                FabricEntityTypeBuilder.<DeterminationArrowProjectile>create(SpawnGroup.MISC, DeterminationArrowProjectile::new)
                        .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                        .trackRangeBlocks(50).trackedUpdateRate(40)
                        .build()
        );

        BRAVERY_SPEAR_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "bravery_spear"),
                FabricEntityTypeBuilder.<BraverySpearProjectile>create(SpawnGroup.MISC, BraverySpearProjectile::new)
                        .dimensions(EntityDimensions.fixed(0.25F, 0.25F))
                        .trackRangeBlocks(50).trackedUpdateRate(40)
                        .build()
        );

        HORIZONTAL_BLAST_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "horizontal_blast"),
                FabricEntityTypeBuilder.<BlastEntity>create(SpawnGroup.MISC, BlastEntity::new)
                        .dimensions(EntityDimensions.changing(1f, 1f))
                        .trackRangeBlocks(100).trackedUpdateRate(40)
                        .build()
        );

        GUNLANCE_BLAST_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "gunlance_blast"),
                FabricEntityTypeBuilder.<GunlanceBlastEntity>create(SpawnGroup.MISC, GunlanceBlastEntity::new)
                        .dimensions(EntityDimensions.changing(1f, 1f))
                        .trackRangeBlocks(100).trackedUpdateRate(40)
                        .build()
        );

        DETERMINATION_SPEAR_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "determination_spear"),
                FabricEntityTypeBuilder.<DeterminationSpearProjectile>create(SpawnGroup.MISC, DeterminationSpearProjectile::new)
                        .dimensions(EntityDimensions.fixed(0.25F, 0.25F))
                        .trackRangeBlocks(50).trackedUpdateRate(40)
                        .build()
        );

        SOJ_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "spear_of_justice"),
                FabricEntityTypeBuilder.<SOJProjectile>create(SpawnGroup.MISC, SOJProjectile::new)
                        .dimensions(EntityDimensions.fixed(0.25F, 0.25F))
                        .trackRangeBlocks(50).trackedUpdateRate(40)
                        .build()
        );

        ENERGY_BALL_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "energy_ball"),
                FabricEntityTypeBuilder.<EnergyBallProjectile>create(SpawnGroup.MISC, EnergyBallProjectile::new)
                        .dimensions(EntityDimensions.fixed(0.5F, 0.5F))
                        .trackRangeBlocks(50).trackedUpdateRate(40)
                        .build()
        );

        JUSTICE_PELLET_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "justice_pellet"),
                FabricEntityTypeBuilder.<JusticePelletProjectile>create(SpawnGroup.MISC, JusticePelletProjectile::new)
                        .dimensions(EntityDimensions.fixed(0.25F, 0.25F))
                        .trackRangeBlocks(50).trackedUpdateRate(40)
                        .build()
        );

        DOME_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "dome"),
                FabricEntityTypeBuilder.<DomeEntity>create(SpawnGroup.MISC, DomeEntity::new)
                        .dimensions(EntityDimensions.fixed(1f, 1f))
                        .trackRangeBlocks(50).trackedUpdateRate(40)
                        .build()
        );

        DOME_PART_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "dome_part"),
                FabricEntityTypeBuilder.<DomePart>create(SpawnGroup.MISC, DomePart::new)
                        .dimensions(EntityDimensions.fixed(1.1f, 1.1f))
                        .trackRangeBlocks(50).trackedUpdateRate(40)
                        .build()
        );

        TURRET_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "auto_turret"),
                FabricEntityTypeBuilder.<AutoTurretEntity>create(SpawnGroup.MISC, AutoTurretEntity::new)
                        .dimensions(EntityDimensions.changing(0.8f, 1.5f))
                        .trackRangeBlocks(50).trackedUpdateRate(40)
                        .build()
        );

        SMALL_SLASH_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "small_static_slash"),
                FabricEntityTypeBuilder.<SmallSlashProjectile>create(SpawnGroup.MISC, SmallSlashProjectile::new)
                        .dimensions(EntityDimensions.fixed(0.6f, 1.5f))
                        .trackRangeBlocks(50).trackedUpdateRate(40)
                        .build()
        );

        BIG_SLASH_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "big_static_slash"),
                FabricEntityTypeBuilder.<BigSlashProjectile>create(SpawnGroup.MISC, BigSlashProjectile::new)
                        .dimensions(EntityDimensions.fixed(0.6f, 1.5f))
                        .trackRangeBlocks(50).trackedUpdateRate(40)
                        .build()
        );

        SPECIAL_HELL_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "special_hell"),
                FabricEntityTypeBuilder.<SpecialHellEntity>create(SpawnGroup.MISC, SpecialHellEntity::new)
                        .dimensions(EntityDimensions.changing(20f, 100f))
                        .trackRangeBlocks(100).trackedUpdateRate(40)
                        .build()
        );

        ORBITAL_STRIKE_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "orbital_strike"),
                FabricEntityTypeBuilder.<OrbitalStrikeEntity>create(SpawnGroup.MISC, OrbitalStrikeEntity::new)
                        .dimensions(EntityDimensions.fixed(4f, 384f))
                        .trackRangeBlocks(300).trackedUpdateRate(40)
                        .build()
        );

        FRAGMENTATION_GRENADE_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "fragmentation_grenade"),
                FabricEntityTypeBuilder.<FragmentationGrenadeProjectile>create(SpawnGroup.MISC, FragmentationGrenadeProjectile::new)
                        .dimensions(EntityDimensions.changing(1f, 1f))
                        .trackRangeBlocks(50).trackedUpdateRate(40)
                        .build()
        );

        FROZEN_ENERGY_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "frozen_energy"),
                FabricEntityTypeBuilder.<FrozenEnergyProjectile>create(SpawnGroup.MISC, FrozenEnergyProjectile::new)
                        .dimensions(EntityDimensions.changing(1f, 1f))
                        .trackRangeBlocks(50).trackedUpdateRate(40)
                        .build()
        );

        HAIL_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "hail"),
                FabricEntityTypeBuilder.<HailProjectile>create(SpawnGroup.MISC, HailProjectile::new)
                        .dimensions(EntityDimensions.changing(1f, 1f))
                        .trackRangeBlocks(100).trackedUpdateRate(40)
                        .build()
        );

        PV_HARPOON_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "pv_harpoon"),
                FabricEntityTypeBuilder.<PVHarpoonProjectile>create(SpawnGroup.MISC, PVHarpoonProjectile::new)
                        .dimensions(EntityDimensions.changing(1f, 1f))
                        .trackRangeBlocks(50).trackedUpdateRate(40)
                        .build()
        );

        DT_HARPOON_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "dt_harpoon"),
                FabricEntityTypeBuilder.<DTHarpoonProjectile>create(SpawnGroup.MISC, DTHarpoonProjectile::new)
                        .dimensions(EntityDimensions.changing(1f, 1f))
                        .trackRangeBlocks(50).trackedUpdateRate(40)
                        .build()
        );

        IMMOBILIZATION_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE, new Identifier(SoulForge.MOD_ID, "immobilization_entity"),
                FabricEntityTypeBuilder.<ImmobilizationEntity>create(SpawnGroup.MISC, ImmobilizationEntity::new)
                        .dimensions(EntityDimensions.changing(3f, 3f))
                        .trackRangeBlocks(100).trackedUpdateRate(40)
                        .build()
        );

        DETERMINATION_PLATFORM_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "determination_platform"),
                FabricEntityTypeBuilder.<DeterminationPlatformEntity>create(SpawnGroup.MISC, DeterminationPlatformEntity::new)
                        .dimensions(EntityDimensions.changing(2.5f, 0.25f))
                        .trackRangeBlocks(100).trackedUpdateRate(40)
                        .build()
        );

        INTEGRITY_PLATFORM_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "integrity_platform"),
                FabricEntityTypeBuilder.<IntegrityPlatformEntity>create(SpawnGroup.MISC, IntegrityPlatformEntity::new)
                        .dimensions(EntityDimensions.changing(2.5f, 0.25f))
                        .trackRangeBlocks(100).trackedUpdateRate(40)
                        .build()
        );

        WEATHER_WARNING_LIGHTNING_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "weather_warning_lightning"),
                FabricEntityTypeBuilder.<WeatherWarningLightningEntity>create(SpawnGroup.MISC, WeatherWarningLightningEntity::new)
                        .dimensions(EntityDimensions.changing(2.5f, 0.25f))
                        .trackRangeBlocks(50).trackedUpdateRate(40)
                        .build()
        );

        LIGHTNING_ROD_LIGHTNING_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "lightning_rod_lightning"),
                FabricEntityTypeBuilder.<LightningRodLightningEntity>create(SpawnGroup.MISC, LightningRodLightningEntity::new)
                        .dimensions(EntityDimensions.changing(2.5f, 0.25f))
                        .trackRangeBlocks(50).trackedUpdateRate(40)
                        .build()
        );

        DOME_EMITTER_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "dome_emitter"),
                FabricEntityTypeBuilder.<DomeEmitterEntity>create(SpawnGroup.MISC, DomeEmitterEntity::new)
                        .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                        .trackRangeBlocks(50).trackedUpdateRate(40)
                        .build()
        );

        INCENDIARY_GRENADE_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "incendiary_grenade"),
                FabricEntityTypeBuilder.<IncendiaryGrenadeEntity>create(SpawnGroup.MISC, IncendiaryGrenadeEntity::new)
                        .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                        .trackRangeBlocks(50).trackedUpdateRate(40)
                        .build()
        );

        LIGHTNING_ROD_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "lightning_rod"),
                FabricEntityTypeBuilder.<LightningRodProjectile>create(SpawnGroup.MISC, LightningRodProjectile::new)
                        .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                        .trackRangeBlocks(100).trackedUpdateRate(40)
                        .build()
        );

        DETONATOR_MINE_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "detonator_mine"),
                FabricEntityTypeBuilder.<DetonatorMine>create(SpawnGroup.MISC, DetonatorMine::new)
                        .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                        .trackRangeBlocks(50).trackedUpdateRate(40)
                        .build()
        );

        POLARITY_BALL_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "polarity_ball"),
                FabricEntityTypeBuilder.<PolarityBallEntity>create(SpawnGroup.MISC, PolarityBallEntity::new)
                        .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                        .trackRangeBlocks(50).trackedUpdateRate(40)
                        .build()
        );

        JUSTICE_ARROW_TRINKET_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "justice_arrow_trinket"),
                FabricEntityTypeBuilder.<JusticeArrowTrinketProjectile>create(SpawnGroup.MISC, JusticeArrowTrinketProjectile::new)
                        .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                        .trackRangeBlocks(50).trackedUpdateRate(40)
                        .build()
        );

        ANTIHEAL_DART_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "antiheal_dart"),
                FabricEntityTypeBuilder.<AntihealDartProjectile>create(SpawnGroup.MISC, AntihealDartProjectile::new)
                        .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                        .trackRangeBlocks(50).trackedUpdateRate(40)
                        .build()
        );

        SHIELD_SHARD_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "shield_shard"),
                FabricEntityTypeBuilder.<ShieldShardEntity>create(SpawnGroup.MISC, ShieldShardEntity::new)
                        .dimensions(EntityDimensions.fixed(0.4f, 1.4f))
                        .trackRangeBlocks(50).trackedUpdateRate(40)
                        .build()
        );

        BOUNCING_SHIELD_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "bouncing_shield"),
                FabricEntityTypeBuilder.<BouncingShieldEntity>create(SpawnGroup.MISC, BouncingShieldEntity::new)
                        .dimensions(EntityDimensions.fixed(1f, 0.5f))
                        .trackRangeBlocks(50).trackedUpdateRate(40)
                        .build()
        );

        GRAPPLE_HOOK_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "grapple_hook"),
                FabricEntityTypeBuilder.<GrappleHookProjectile>create(SpawnGroup.MISC, GrappleHookProjectile::new)
                        .dimensions(EntityDimensions.fixed(0.4f, 0.4f))
                        .trackRangeBlocks(50).trackedUpdateRate(40)
                        .build()
        );

        FIRE_TORNADO_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "fire_tornado"),
                FabricEntityTypeBuilder.<FireTornadoProjectile>create(SpawnGroup.MISC, FireTornadoProjectile::new)
                        .dimensions(EntityDimensions.changing(17.5f, 70f))
                        .trackRangeBlocks(100).trackedUpdateRate(40)
                        .build()
        );

        JUSTICE_HARPOON_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "justice_harpoon"),
                FabricEntityTypeBuilder.<JusticeHarpoonProjectile>create(SpawnGroup.MISC, JusticeHarpoonProjectile::new)
                        .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                        .trackRangeBlocks(50).trackedUpdateRate(40)
                        .build()
        );

        WORMHOLE_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "wormhole"),
                FabricEntityTypeBuilder.<WormholeEntity>create(SpawnGroup.MISC, WormholeEntity::new)
                        .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                        .trackRangeBlocks(50).trackedUpdateRate(40)
                        .build()
        );

        SWORD_SLASH_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "sword_slash"),
                FabricEntityTypeBuilder.<SwordSlashProjectile>create(SpawnGroup.MISC, SwordSlashProjectile::new)
                        .dimensions(EntityDimensions.fixed(1f, 0.5f))
                        .trackRangeBlocks(50).trackedUpdateRate(40)
                        .build()
        );

        YOYO_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "yoyo"),
                FabricEntityTypeBuilder.<YoyoProjectile>create(SpawnGroup.MISC, YoyoProjectile::new)
                        .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                        .trackRangeBlocks(50).trackedUpdateRate(40)
                        .build()
        );

        RAILKILLER_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "railkiller"),
                FabricEntityTypeBuilder.<RailkillerEntity>create(SpawnGroup.MISC, RailkillerEntity::new)
                        .dimensions(EntityDimensions.fixed(1f, 1f))
                        .trackRangeBlocks(100).trackedUpdateRate(40)
                        .build()
        );

        PLAYER_SOUL_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "player_soul"),
                FabricEntityTypeBuilder.<PlayerSoulEntity>create(SpawnGroup.MISC, PlayerSoulEntity::new)
                        .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                        .trackRangeBlocks(40).trackedUpdateRate(20)
                        .build()
        );

        DETERMINATION_SHOT_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(SoulForge.MOD_ID, "determination_shot"),
                FabricEntityTypeBuilder.<DeterminationShotProjectile>create(SpawnGroup.MISC, DeterminationShotProjectile::new)
                        .dimensions(EntityDimensions.fixed(0.5F, 0.5F))
                        .trackRangeBlocks(50).trackedUpdateRate(40)
                        .build()
        );

        FEAR_BOMB_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE, new Identifier(SoulForge.MOD_ID, "fear_bomb"),
                FabricEntityTypeBuilder.<FearBombEntity>create(SpawnGroup.MISC, FearBombEntity::new)
                        .trackRangeBlocks(100).trackedUpdateRate(40)
                        .dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build()
        );

        SLOWBALL_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE, new Identifier(SoulForge.MOD_ID, "slowball"),
                FabricEntityTypeBuilder.<SlowballProjectile>create(SpawnGroup.MISC, SlowballProjectile::new)
                        .trackRangeBlocks(100).trackedUpdateRate(40)
                        .dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build()
        );

        ICE_SPIKE_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE, new Identifier(SoulForge.MOD_ID, "ice_spike"),
                FabricEntityTypeBuilder.<IceSpikeProjectile>create(SpawnGroup.MISC, IceSpikeProjectile::new)
                        .trackRangeBlocks(100).trackedUpdateRate(40)
                        .dimensions(EntityDimensions.changing(4f, 3f)).build()
        );

        TOTAL_FROSTBITE_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE, new Identifier(SoulForge.MOD_ID, "total_frostbite_entity"),
                FabricEntityTypeBuilder.<TotalFrostbiteEntity>create(SpawnGroup.MISC, TotalFrostbiteEntity::new)
                        .dimensions(EntityDimensions.changing(1.5f, 3f))
                        .trackRangeBlocks(100).trackedUpdateRate(40)
                        .build()
        );

        ANTLER_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE, new Identifier(SoulForge.MOD_ID, "antler_entity"),
                FabricEntityTypeBuilder.<AntlerEntity>create(SpawnGroup.MISC, AntlerEntity::new)
                        .dimensions(EntityDimensions.changing(1f, 1f))
                        .trackRangeBlocks(100).trackedUpdateRate(40)
                        .build()
        );

        SKULL_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE, new Identifier(SoulForge.MOD_ID, "skull"),
                FabricEntityTypeBuilder.<SkullProjectile>create(SpawnGroup.MISC, SkullProjectile::new)
                        .dimensions(EntityDimensions.changing(1f, 1f))
                        .trackRangeBlocks(100).trackedUpdateRate(40)
                        .build()
        );

        FabricDefaultAttributeRegistry.register(TURRET_ENTITY_TYPE, AutoTurretEntity.createMobAttributes());
    }
}

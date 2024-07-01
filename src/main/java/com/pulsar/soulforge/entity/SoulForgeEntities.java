package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.SoulForge;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
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
    public static EntityType<DarkFountainEntity> DARK_FOUNTAIN_ENTITY_TYPE;
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
    public static EntityType<PolarityBall> POLARITY_BALL_ENTITY_TYPE;
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

    public static void register() {
        SNOWGRAVE_PROJECTILE_TYPE = Registry.register(
                Registries.ENTITY_TYPE, Identifier.of(SoulForge.MOD_ID, "snowgrave"),
                EntityType.Builder.<SnowgraveProjectile>create(SnowgraveProjectile::new, SpawnGroup.MISC).build()
        );

        JUSTICE_ARROW_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "justice_arrow"),
                EntityType.Builder.<JusticeArrowProjectile>create(JusticeArrowProjectile::new, SpawnGroup.MISC).build()
        );

        DETERMINATION_ARROW_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "determination_arrow"),
                EntityType.Builder.<DeterminationArrowProjectile>create(DeterminationArrowProjectile::new, SpawnGroup.MISC).build()
        );

        BRAVERY_SPEAR_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "bravery_spear"),
                EntityType.Builder.<BraverySpearProjectile>create(BraverySpearProjectile::new, SpawnGroup.MISC).build()
        );

        HORIZONTAL_BLAST_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "horizontal_blast"),
                EntityType.Builder.<BlastEntity>create(BlastEntity::new, SpawnGroup.MISC).build()
        );

        GUNLANCE_BLAST_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "gunlance_blast"),
                EntityType.Builder.<GunlanceBlastEntity>create(GunlanceBlastEntity::new, SpawnGroup.MISC).build()
        );

        DETERMINATION_SPEAR_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "determination_spear"),
                EntityType.Builder.<DeterminationSpearProjectile>create(DeterminationSpearProjectile::new, SpawnGroup.MISC).build()
        );

        SOJ_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "spear_of_justice"),
                EntityType.Builder.<SOJProjectile>create(SOJProjectile::new, SpawnGroup.MISC).build()
        );

        ENERGY_BALL_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "energy_ball"),
                EntityType.Builder.<EnergyBallProjectile>create(EnergyBallProjectile::new, SpawnGroup.MISC).build()
        );

        JUSTICE_PELLET_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "justice_pellet"),
                EntityType.Builder.<JusticePelletProjectile>create(JusticePelletProjectile::new, SpawnGroup.MISC).build()
        );

        DOME_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "dome"),
                EntityType.Builder.<DomeEntity>create(DomeEntity::new, SpawnGroup.MISC).build()
        );

        DOME_PART_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "dome_part"),
                EntityType.Builder.<DomePart>create(DomePart::new, SpawnGroup.MISC).build()
        );

        TURRET_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "auto_turret"),
                EntityType.Builder.<AutoTurretEntity>create(AutoTurretEntity::new, SpawnGroup.MISC).build()
        );

        SMALL_SLASH_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "small_static_slash"),
                EntityType.Builder.<SmallSlashProjectile>create(SmallSlashProjectile::new, SpawnGroup.MISC).build()
        );

        BIG_SLASH_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "big_static_slash"),
                EntityType.Builder.<BigSlashProjectile>create(BigSlashProjectile::new, SpawnGroup.MISC).build()
        );

        DARK_FOUNTAIN_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "dark_fountain"),
                EntityType.Builder.<DarkFountainEntity>create(DarkFountainEntity::new, SpawnGroup.MISC).build()
        );

        SPECIAL_HELL_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "special_hell"),
                EntityType.Builder.<SpecialHellEntity>create(SpecialHellEntity::new, SpawnGroup.MISC).build()
        );

        ORBITAL_STRIKE_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "orbital_strike"),
                EntityType.Builder.<OrbitalStrikeEntity>create(OrbitalStrikeEntity::new, SpawnGroup.MISC).build()
        );

        FRAGMENTATION_GRENADE_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "fragmentation_grenade"),
                EntityType.Builder.<FragmentationGrenadeProjectile>create(FragmentationGrenadeProjectile::new, SpawnGroup.MISC).build()
        );

        FROZEN_ENERGY_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "frozen_energy"),
                EntityType.Builder.<FrozenEnergyProjectile>create(FrozenEnergyProjectile::new, SpawnGroup.MISC).build()
        );

        HAIL_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "hail"),
                EntityType.Builder.<HailProjectile>create(HailProjectile::new, SpawnGroup.MISC).build()
        );

        PV_HARPOON_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "pv_harpoon"),
                EntityType.Builder.<PVHarpoonProjectile>create(PVHarpoonProjectile::new, SpawnGroup.MISC).build()
        );

        DT_HARPOON_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "dt_harpoon"),
                EntityType.Builder.<DTHarpoonProjectile>create(DTHarpoonProjectile::new, SpawnGroup.MISC).build()
        );

        IMMOBILIZATION_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE, Identifier.of(SoulForge.MOD_ID, "immobilization_entity"),
                EntityType.Builder.<ImmobilizationEntity>create(ImmobilizationEntity::new, SpawnGroup.MISC).build()
        );

        DETERMINATION_PLATFORM_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "determination_platform"),
                EntityType.Builder.<DeterminationPlatformEntity>create(DeterminationPlatformEntity::new, SpawnGroup.MISC).build()
        );

        INTEGRITY_PLATFORM_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "integrity_platform"),
                EntityType.Builder.<IntegrityPlatformEntity>create(IntegrityPlatformEntity::new, SpawnGroup.MISC).build()
        );

        WEATHER_WARNING_LIGHTNING_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "weather_warning_lightning"),
                EntityType.Builder.<WeatherWarningLightningEntity>create(WeatherWarningLightningEntity::new, SpawnGroup.MISC).build()
        );

        LIGHTNING_ROD_LIGHTNING_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "lightning_rod_lightning"),
                EntityType.Builder.<LightningRodLightningEntity>create(LightningRodLightningEntity::new, SpawnGroup.MISC).build()
        );

        DOME_EMITTER_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "dome_emitter"),
                EntityType.Builder.<DomeEmitterEntity>create(DomeEmitterEntity::new, SpawnGroup.MISC).build()
        );

        INCENDIARY_GRENADE_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "incendiary_grenade"),
                EntityType.Builder.<IncendiaryGrenadeEntity>create(IncendiaryGrenadeEntity::new, SpawnGroup.MISC).build()
        );

        LIGHTNING_ROD_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "lightning_rod"),
                EntityType.Builder.<LightningRodProjectile>create(LightningRodProjectile::new, SpawnGroup.MISC).build()
        );

        DETONATOR_MINE_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "detonator_mine"),
                EntityType.Builder.<DetonatorMine>create(DetonatorMine::new, SpawnGroup.MISC).build()
        );

        POLARITY_BALL_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "polarity_ball"),
                EntityType.Builder.<PolarityBall>create(PolarityBall::new, SpawnGroup.MISC).build()
        );

        JUSTICE_ARROW_TRINKET_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "justice_arrow_trinket"),
                EntityType.Builder.<JusticeArrowTrinketProjectile>create(JusticeArrowTrinketProjectile::new, SpawnGroup.MISC).build()
        );

        ANTIHEAL_DART_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "antiheal_dart"),
                EntityType.Builder.<AntihealDartProjectile>create(AntihealDartProjectile::new, SpawnGroup.MISC).build()
        );

        SHIELD_SHARD_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "shield_shard"),
                EntityType.Builder.<ShieldShardEntity>create(ShieldShardEntity::new, SpawnGroup.MISC).build()
        );

        BOUNCING_SHIELD_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "bouncing_shield"),
                EntityType.Builder.<BouncingShieldEntity>create(BouncingShieldEntity::new, SpawnGroup.MISC).build()
        );

        GRAPPLE_HOOK_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "grapple_hook"),
                EntityType.Builder.<GrappleHookProjectile>create(GrappleHookProjectile::new, SpawnGroup.MISC).build()
        );

        FIRE_TORNADO_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "fire_tornado"),
                EntityType.Builder.<FireTornadoProjectile>create(FireTornadoProjectile::new, SpawnGroup.MISC).build()
        );

        JUSTICE_HARPOON_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "justice_harpoon"),
                EntityType.Builder.<JusticeHarpoonProjectile>create(JusticeHarpoonProjectile::new, SpawnGroup.MISC).build()
        );

        WORMHOLE_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "wormhole"),
                EntityType.Builder.<WormholeEntity>create(WormholeEntity::new, SpawnGroup.MISC).build()
        );

        SWORD_SLASH_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "sword_slash"),
                EntityType.Builder.<SwordSlashProjectile>create(SwordSlashProjectile::new, SpawnGroup.MISC).build()
        );

        YOYO_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "yoyo"),
                EntityType.Builder.<YoyoProjectile>create(YoyoProjectile::new, SpawnGroup.MISC).build()
        );

        RAILKILLER_ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(SoulForge.MOD_ID, "railkiller"),
                EntityType.Builder.<RailkillerEntity>create(RailkillerEntity::new, SpawnGroup.MISC).build()
        );

        FabricDefaultAttributeRegistry.register(TURRET_ENTITY_TYPE, AutoTurretEntity.createMobAttributes());
    }
}

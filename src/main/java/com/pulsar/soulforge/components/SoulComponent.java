package com.pulsar.soulforge.components;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.*;
import com.pulsar.soulforge.ability.determination.DeterminationPlatform;
import com.pulsar.soulforge.ability.duals.PerfectedAuraTechnique;
import com.pulsar.soulforge.ability.integrity.Platforms;
import com.pulsar.soulforge.advancement.SoulForgeCriterions;
import com.pulsar.soulforge.armor.PlatformBootsItem;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.data.AbilityLayout;
import com.pulsar.soulforge.data.AbilityList;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.entity.BlastEntity;
import com.pulsar.soulforge.entity.DeterminationPlatformEntity;
import com.pulsar.soulforge.entity.IntegrityPlatformEntity;
import com.pulsar.soulforge.event.EventType;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.item.weapons.MagicSwordItem;
import com.pulsar.soulforge.networking.SoulForgeNetworking;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.tag.SoulForgeTags;
import com.pulsar.soulforge.trait.TraitBase;
import com.pulsar.soulforge.trait.Traits;
import com.pulsar.soulforge.util.ResetData;
import com.pulsar.soulforge.util.SpokenTextRenderer;
import com.pulsar.soulforge.util.Utils;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.*;

public class SoulComponent implements AutoSyncedComponent, CommonTickingComponent {
    private List<TraitBase> traits = List.of(Traits.bravery, Traits.justice);
    private boolean strong = false;
    private boolean pure = false;
    private int lv = 1;
    private int exp = 0;
    private int style = 0;
    private int styleRank = 0;
    private int lastStyleIncrease = 0;
    private float magic = 0;
    private float magicGauge = 0;
    private AbilityList abilities = new AbilityList();
    private final PlayerEntity player;
    private ItemStack weapon = ItemStack.EMPTY;
    private int lastCastTime = 0;
    private List<AbilityBase> discovered = new ArrayList<>();
    private HashMap<String, Integer> monsterSouls = new HashMap<>();
    private HashMap<String, Integer> playerSouls = new HashMap<>();
    private AbilityLayout abilityLayout = new AbilityLayout();
    private int abilityRow = 0;
    private int abilitySlot = 0;
    private boolean magicMode = false;
    private ResetData resetData = new ResetData();
    private Pair<UUID, Integer> wormholeRequest = null;
    private PlayerEntity disguisedAs = null;
    private UUID disguisedAsID = null;
    private final SpokenTextRenderer spokenTextRenderer = new SpokenTextRenderer();

    private boolean initialized = false;

    public SoulComponent(PlayerEntity player) {
        this.player = player;
        lv = 1;
        exp = 0;
        style = 0;
        styleRank = 0;
        magic = 100;
        initialized = false;
    }

    public static void sync(PlayerEntity player) {
        EntityInitializer.SOUL.sync(player);
    }

    public TraitBase getTrait(int num) {
        return traits.get(num);
    }

    public List<TraitBase> getTraits() {
        return traits;
    }

    public boolean hasTrait(TraitBase trait) {
        return traits.stream().map(TraitBase::getName).anyMatch(trait.getName()::equals);
    }

    public int getTraitCount() {
        return traits.size();
    }

    public void setTrait(int num, TraitBase trait) {
        if (traits.size() >= num+1) traits.set(num, trait);
        else if (num <= 1) traits.add(trait);
        if (num == 1 && trait == null) traits = new ArrayList<>(List.of(traits.get(0)));
        abilityLayout = new AbilityLayout();
        for (AbilityBase ability : abilities.getActive()) {
            ability.end((ServerPlayerEntity)player);
        }
        if (pure) resetData.addPure(traits.get(0));
        if (traits.size() >= 2) resetData.addDual(traits.get(0), traits.get(1));
        updateAbilities();
        if (player instanceof ServerPlayerEntity serverPlayer) {
            SoulForgeCriterions.PLAYER_LV.trigger(serverPlayer, getLV());
            SoulForgeCriterions.PLAYER_TRAIT.trigger(serverPlayer, this);
            SoulForgeCriterions.PLAYER_SOUL.trigger(serverPlayer, this);
        }
        sync();
    }

    public void setTraits(List<TraitBase> traits) {
        this.traits = traits;
        abilityLayout = new AbilityLayout();
        for (AbilityBase ability : abilities.getActive()) {
            ability.end((ServerPlayerEntity)player);
        }
        if (pure) resetData.addPure(traits.get(0));
        if (traits.size() >= 2) resetData.addDual(traits.get(0), traits.get(1));
        updateAbilities();
        if (player instanceof ServerPlayerEntity serverPlayer) {
            SoulForgeCriterions.PLAYER_LV.trigger(serverPlayer, getLV());
            SoulForgeCriterions.PLAYER_TRAIT.trigger(serverPlayer, this);
            SoulForgeCriterions.PLAYER_SOUL.trigger(serverPlayer, this);
        }
        sync();
    }

    public void setResetValues(List<TraitBase> traits, boolean strong, boolean pure) {
        this.traits = traits;
        abilityLayout = new AbilityLayout();
        for (AbilityBase ability : abilities.getActive()) {
            ability.end((ServerPlayerEntity)player);
        }
        if (traits.size() >= 2) resetData.addDual(traits.get(0), traits.get(1));
        this.strong = strong;
        this.pure = pure;
        updateAbilities();
        sync();
    }

    public ResetData getResetData() {
        return resetData;
    }

    public void setResetData(ResetData resetData) {
        this.resetData = resetData;
    }

    public void handleEvent(EventType event) {
        if (player instanceof ServerPlayerEntity serverPlayerEntity) {
            switch (event) {
                case FALL_IMMUNITY:
                    if (abilities.isActive("Launch")) {
                        abilities.get("Launch").end(serverPlayerEntity);
                        break;
                    }
                    break;
                case SPAWN_PLATFORM:
                    boolean hasDT = false;
                    boolean hasIG = false;
                    for (AbilityBase ability : abilities.getActive()) {
                        if (ability instanceof DeterminationPlatform) {
                            SoulForgeCriterions.CAST_ABILITY.trigger((ServerPlayerEntity) player, ability);
                            hasDT = true;
                        }
                        if (ability instanceof Platforms) {
                            SoulForgeCriterions.CAST_ABILITY.trigger((ServerPlayerEntity) player, ability);
                            hasIG = true;
                        }
                    }
                    float cost = 1f;
                    if (player.getAttributeInstance(SoulForgeAttributes.MAGIC_COST) != null) {
                        cost *= (float)player.getAttributeValue(SoulForgeAttributes.MAGIC_COST);
                    }
                    if (getMagic() >= cost && (hasDT || hasIG)) {
                        boolean stacked = false;
                        boolean onTriple = false;
                        Box box = new Box(player.getPos().subtract(2, 2, 2), player.getPos().add(2, 2, 2));
                        for (Entity entity : player.getEntityWorld().getOtherEntities(player, box)) {
                            if (entity.getBoundingBox().intersects(player.getBoundingBox().offset(0, -0.5f, 0))) {
                                if (entity instanceof DeterminationPlatformEntity platform) {
                                    if (platform.getStack() < 2) {
                                        platform.setStack(platform.getStack() + 1);
                                        stacked = true;
                                        break;
                                    } else {
                                        onTriple = true;
                                    }
                                }
                                if (entity instanceof IntegrityPlatformEntity platform) {
                                    if (platform.getStack() < 2) {
                                        platform.setStack(platform.getStack() + 1);
                                        stacked = true;
                                        break;
                                    } else {
                                        onTriple = true;
                                    }
                                }
                            }
                        }
                        if (stacked) {
                            setMagic(getMagic() - cost);
                            resetLastCastTime();
                        }
                        if (onTriple) break;
                        if (!stacked && !player.isOnGround()) {
                            if (hasIG) {
                                for (AbilityBase ability : abilities.getActive()) {
                                    if (ability instanceof Platforms) {
                                        ((Platforms) ability).spawn(player);
                                        break;
                                    }
                                }
                            } else {
                                for (AbilityBase ability : abilities.getActive()) {
                                    if (ability instanceof DeterminationPlatform) {
                                        ((DeterminationPlatform) ability).spawn(player);
                                        break;
                                    }
                                }
                            }
                            setMagic(getMagic() - cost);
                            resetLastCastTime();
                        }
                    } else if (!(hasDT || hasIG)) {
                        ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);
                        if (boots.isOf(SoulForgeItems.PLATFORM_BOOTS)) {
                            PlatformBootsItem platformBoots = ((PlatformBootsItem) SoulForgeItems.PLATFORM_BOOTS);
                            if (platformBoots.getCharge(boots) >= 5) {
                                boolean stacked = false;
                                boolean onTriple = false;
                                Box box = new Box(player.getPos().subtract(2, 2, 2), player.getPos().add(2, 2, 2));
                                for (Entity entity : player.getEntityWorld().getOtherEntities(player, box)) {
                                    if (entity.getBoundingBox().intersects(player.getBoundingBox().offset(0, -0.5f, 0))) {
                                        if (entity instanceof IntegrityPlatformEntity platform) {
                                            if (platform.getStack() < 2) {
                                                platform.setStack(platform.getStack() + 1);
                                                stacked = true;
                                                break;
                                            } else {
                                                onTriple = true;
                                            }
                                        }
                                    }
                                }
                                if (stacked) {
                                    platformBoots.decreaseCharge(boots, 5);
                                    resetLastCastTime();
                                }
                                if (onTriple) {
                                    break;
                                }
                                if (!stacked && !player.isOnGround()) {
                                    new Platforms().spawn(player);
                                    platformBoots.decreaseCharge(boots, 5);
                                }
                            }
                        }
                    }
                    break;
            }
        }
    }

    public int getLV() {
        return lv;
    }

    public int getEffectiveLV() {
        float effLv = getLV();
        float multiplier = 1f;
        if (this.player != null) {
            if (player.getAttributeInstance(SoulForgeAttributes.MAGIC_POWER) != null) multiplier = (float)player.getAttributeInstance(SoulForgeAttributes.MAGIC_POWER).getValue();
        }
        if (traits.contains(Traits.determination)) {
            multiplier += 0.1f * getStyleRank();
        }
        if (Utils.isInverted(this)) {
            multiplier += 0.5f;
        }
        return MathHelper.floor(effLv*multiplier);
    }

    public void setLV(int lv) {
        this.lv = Math.min(Math.max(lv, 1), 20);
        if (player instanceof ServerPlayerEntity serverPlayer) {
            SoulForgeCriterions.PLAYER_LV.trigger(serverPlayer, getLV());
            SoulForgeCriterions.PLAYER_TRAIT.trigger(serverPlayer, this);
            SoulForgeCriterions.PLAYER_SOUL.trigger(serverPlayer, this);
        }
        updateAbilities();
        sync();
    }

    public int getEXP() {
        return exp;
    }

    public void setEXP(int exp) {
        this.exp = Math.max(exp, 0);
        int oldAbilityCount = Traits.getAbilities(player, this).size();
        boolean leveledUp = false;
        int expRequirement = getExpRequirement();
        while (this.exp >= expRequirement && expRequirement != -1) {
            this.exp -= expRequirement;
            this.lv += 1;
            leveledUp = true;
            expRequirement = getExpRequirement();
        }
        int newAbilityCount = Traits.getAbilities(player, this).size();
        if (leveledUp && player instanceof ServerPlayerEntity serverPlayer) {
            SoulForgeCriterions.PLAYER_LV.trigger(serverPlayer, getLV());
            SoulForgeCriterions.PLAYER_TRAIT.trigger(serverPlayer, this);
            SoulForgeCriterions.PLAYER_SOUL.trigger(serverPlayer, this);
            player.sendMessage(Text.translatable("soulforge.lv.increase").append(String.valueOf(this.lv)));
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(false); buf.writeBoolean(true);
            buf.writeString("Your LV has increased!");
            if (newAbilityCount <= oldAbilityCount) buf.writeString("");
            else buf.writeString("Unlocked " + (newAbilityCount - oldAbilityCount) + " new abilities.");
            ServerPlayNetworking.send((ServerPlayerEntity)player, SoulForgeNetworking.SHOW_TOAST, buf);
            player.getWorld().playSoundFromEntity(null, player, SoulForgeSounds.UT_LEVEL_UP_EVENT, SoundCategory.PLAYERS, 1f, 1f);
        }
        updateAbilities();
        sync();
    }

    public int getExpRequirement() {
        if (lv >= 20) return -1;
        return MathHelper.floor(10000f*(lv/20f)*(lv/20f));
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
        while (this.style >= getStyleRequirement()) {
            this.style -= getStyleRequirement();
            this.styleRank++;
        }
        this.lastStyleIncrease = player.age;
    }

    public int getStyleRequirement() {
        return switch (this.styleRank) {
            case 0 -> 100;
            case 1 -> 250;
            case 2 -> 350;
            case 3, 4 -> 500;
            default -> 69420;
        };
    }

    public int getStyleRank() {
        return styleRank;
    }

    public void setStyleRank(int styleRank) {
        this.styleRank = MathHelper.clamp(styleRank, 0, 6);
        if (this.styleRank >= 5) {
            player.getWorld().playSound(null, player.getBlockPos(), SoulForgeSounds.UT_CREATE_EVENT, SoundCategory.MASTER, 1f, 1f);
        } else if (this.styleRank > 0) {
            player.getWorld().playSound(null, player.getBlockPos(), SoulForgeSounds.UT_A_GRAB_EVENT, SoundCategory.MASTER, 1f, 1f);
        }
        while (this.style >= getStyleRequirement()) {
            this.style -= getStyleRequirement();
            this.styleRank++;
        }
        if (this.styleRank > 5) {
            player.sendMessage(Text.literal("what sort of frog magic is this"));
        }
    }

    public float getMagic() {
        return magic;
    }

    public float getMagicMax() {
        return 100f;
    }

    public void setMagic(float magic) {
        this.magic = Math.min(Math.max(magic, 0), getMagicMax());
    }

    public float getMagicGauge() {
        return magicGauge;
    }

    public float getMagicGaugeMax() {
        return 30000f;
    }

    public void setMagicGauge(float magicGauge) {
        this.magicGauge = Math.min(Math.max(magicGauge, 0), getMagicGaugeMax());
    }

    public List<AbilityBase> getAbilities() { return List.copyOf(this.abilities.getAll()); }

    public List<AbilityBase> getActiveAbilities() {
        return List.copyOf(this.abilities.getActive());
    }

    public AbilityBase getAbility(String abilityName) {
        if (this.abilities.has(abilityName)) {
            return this.abilities.get(abilityName);
        }
        return null;
    }

    public boolean onCooldown(AbilityBase ability) {
        if (player == null) return false;
        return abilities.has(ability) && abilities.get(ability).getCooldownVal() > 0;
    }

    public boolean onCooldown(String abilityName) {
        return abilities.has(abilityName) && abilities.get(abilityName).getCooldownVal() > 0;
    }

    public float cooldownPercent(AbilityBase ability) {
        try {
            if (abilities.get(ability).getCooldownVal() == 0) return 1f;
            return MathHelper.clamp(1f - ((float)(abilities.get(ability).getCooldownVal()) / (float)(abilities.get(ability).getCooldown())), 0f, 1f);
        } catch (NullPointerException e) {
            return 1f;
        }
    }

    public void setCooldown(AbilityBase ability, int cooldown) {
        if (abilities.get(ability).getCooldown() == 0) return;
        abilities.get(ability).setCooldownVal(cooldown);
    }

    public void setCooldown(String abilityName, int cooldown) {
        if (this.abilities.has(abilityName)) {
            if (abilities.get(abilityName).getCooldown() == 0) return;
            abilities.get(abilityName).setCooldownVal(cooldown);
        }
    }

    public void onDeath() {
        for (AbilityBase ability : abilities.getActive()) {
            ability.setActive(false);
            if (ability instanceof ToggleableAbilityBase toggleable && player instanceof ServerPlayerEntity serverPlayer) {
                toggleable.end(serverPlayer);
            }
        }
        magic = 100f;
        if (Utils.isInverted(this)) magicGauge = 1000f;
    }

    public int lastCastTime() {
        return lastCastTime;
    }

    public void resetLastCastTime() {
        lastCastTime = 0;
    }

    private float manaRegenRate = 0;
    private Vec3d lastPos = Vec3d.ZERO;

    public void magicTick() {
        int manaSicknessAmplifier = 0;
        float manaStartTime = 5f - MathHelper.clamp(getStyleRank(), 0, 5);
        if (player.hasStatusEffect(SoulForgeEffects.MANA_SICKNESS)) manaSicknessAmplifier = Objects.requireNonNull(player.getStatusEffect(SoulForgeEffects.MANA_SICKNESS)).getAmplifier() + 1;
        if (lastCastTime/20f > (manaStartTime*(manaSicknessAmplifier+1))/Math.ceil(lv/5f)) {
            if (getMagic() < 100f) {
                float moveDist = (float) player.getPos().distanceTo(lastPos);
                if ((lastCastTime % (60 - lv * 2) == 0 && moveDist < 0.01f) || (lastCastTime % (120 - lv * 4) == 0 && moveDist >= 0.01f) && manaSicknessAmplifier < 4) {
                    manaRegenRate += 1 / 20f - manaSicknessAmplifier / 80f;
                }
            }
        } else {
            manaRegenRate = 0;
        }
        if (this.abilities.isActive("Perfected Aura Technique")) {
            try {
                if (((PerfectedAuraTechnique)this.abilities.get("Perfected Aura Technique")).fullPower) {
                    manaRegenRate = 2f;
                }
            } catch (Exception ignored) {}
        }
        ValueComponent values = SoulForge.getValues(player);
        float tumorMultiplier = 1f;
        if (player.hasStatusEffect(SoulForgeEffects.MANA_TUMOR)) {
            StatusEffectInstance tumor = player.getStatusEffect(SoulForgeEffects.MANA_TUMOR);
            if (tumor.getAmplifier() == 1) {
                tumorMultiplier = 0.9f;
            }
            if (tumor.getAmplifier() == 2) {
                tumorMultiplier = 0.75f;
            }
            if (values.hasUUID("TumorOwner")) {
                PlayerEntity tumorOwner = player.getWorld().getPlayerByUuid(values.getUUID("TumorOwner"));
                if (tumorOwner != null) {
                    SoulComponent playerSoul = SoulForge.getPlayerSoul(tumorOwner);
                    playerSoul.setMagic(playerSoul.getMagic() + manaRegenRate * (1f - tumorMultiplier));
                }
            }
        }
        float drainingMultiplier = 1f;
        if (values.hasUUID("DrainingField")) {
            PlayerEntity drainedBy = player.getWorld().getPlayerByUuid(values.getUUID("DrainingField"));
            if (drainedBy != null) {
                drainingMultiplier = 2f/3f;
                SoulComponent playerSoul = SoulForge.getPlayerSoul(drainedBy);
                if (playerSoul.getMagic() >= 100f) playerSoul.setMagicGauge(playerSoul.getMagicGauge() + manaRegenRate * (1f - drainingMultiplier));
                else playerSoul.setMagic(playerSoul.getMagic() + manaRegenRate * (1f - drainingMultiplier));
            }
        }
        if (values.hasUUID("ReapingField") && values.hasFloat("ReapingFieldAmount")) {
            PlayerEntity reapedBy = player.getWorld().getPlayerByUuid(values.getUUID("ReapingField"));
            if (reapedBy != null) {
                drainingMultiplier = 1f - values.getFloat("ReapingFieldAmount");
                SoulComponent playerSoul = SoulForge.getPlayerSoul(reapedBy);
                if (playerSoul.getMagic() >= 100f) playerSoul.setMagicGauge(playerSoul.getMagicGauge() + manaRegenRate * (1f - drainingMultiplier));
                else playerSoul.setMagic(playerSoul.getMagic() + manaRegenRate * (1f - drainingMultiplier));
            }
        }
        float magicIncrease = manaRegenRate * tumorMultiplier * drainingMultiplier;
        if (Utils.isInverted(this)) {
            magicIncrease = Math.min(magicIncrease, magicGauge);
            magicGauge -= magicIncrease;
        }
        setMagic(magic + magicIncrease);
        lastCastTime++;
        lastPos = player.getPos();

        if (style > 0 || styleRank > 0) {
            int styleTicks = player.age - lastStyleIncrease;
            if (styleTicks >= 140) {
                if (styleTicks % 10 == 0) {
                    style = MathHelper.clamp(style - getStyleRequirement() / 100, 0, getStyleRequirement());
                    if (style == 0 && styleRank > 0) {
                        styleRank--;
                        style = getStyleRequirement()-1;
                    }
                }
            }
        }
        if (player.hasStatusEffect(SoulForgeEffects.MANA_OVERLOAD)) {
            style = 0;
            styleRank = 0;
        }
    }

    public boolean hasCast(String abilityName) {
        return this.abilities.isActive(abilityName);
    }

    public boolean hasAbility(String abilityName) {
        return this.abilities.has(abilityName);
    }

    public boolean hasWeapon() {
        return !getWeapon().isEmpty();
    }

    public ItemStack getWeapon() {
        if (this.weapon == null) return ItemStack.EMPTY;
        return this.weapon;
    }

    public void setWeapon(ItemStack weapon) {
        setWeapon(weapon, true);
    }

    public void removeWeapon() {
        removeWeapon(true);
    }

    public void setWeapon(ItemStack weapon, boolean sound) {
        if (sound && player != null) {
            player.getWorld().playSoundFromEntity(null, player, SoulForgeSounds.WEAPON_SUMMON_EVENT, SoundCategory.PLAYERS, 1f, 1f);
        }
        this.weapon = weapon;
    }

    public void removeWeapon(boolean sound) {
        if (hasWeapon()) {
            if (getWeapon().isOf(SoulForgeItems.BFRCMG)) {
                setCooldown("BFRCMG", 100);
            }
        }
        if (sound && player != null) player.getWorld().playSoundFromEntity(null, player, SoulForgeSounds.WEAPON_UNSUMMON_EVENT, SoundCategory.PLAYERS, 1f, 1f);
        this.weapon = ItemStack.EMPTY;
    }


    public HashMap<String, Integer> getMonsterSouls() {
        return monsterSouls;
    }
    public HashMap<String, Integer> getPlayerSouls() {
        return playerSouls;
    }

    public void addMonsterSoul(String type, int amount) {
        if (monsterSouls.containsKey(type)) monsterSouls.put(type, monsterSouls.get(type)+amount);
        else monsterSouls.put(type, amount);
        SoulForgeCriterions.MONSTER_SOUL.trigger((ServerPlayerEntity)player, monsterSouls.get(type), type);
    }

    public void addMonsterSoul(Entity entity, int amount) {
        addMonsterSoul(Registries.ENTITY_TYPE.getId(entity.getType()).toString(), amount);
    }

    public void addPlayerSoul(String playerName, int amount) {
        if (playerSouls.containsKey(playerName)) playerSouls.put(playerName, playerSouls.get(playerName)+amount);
        else playerSouls.put(playerName, amount);
    }

    public int getSoulCount(String type) {
        if (!monsterSouls.containsKey(type)) return 0;
        return monsterSouls.get(type);
    }

    public boolean canReset() {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() == SoulForgeItems.DETERMINATION_ARNICITE_HEART) {
                return true;
            }
        }
        return false;
    }

    public AbilityLayout getAbilityLayout() {
        return abilityLayout;
    }

    public AbilityBase getLayoutAbility(int row, int column) {
        return abilityLayout.getSlot(row, column);
    }

    public AbilityLayout.AbilityRow getLayoutRow(int row) {
        return abilityLayout.rows.get(row);
    }

    public void setAbilityLayout(AbilityLayout layout) {
        abilityLayout = layout;
    }

    public void setLayoutAbility(AbilityBase ability, int row, int column) {
        abilityLayout.setSlot(ability, row, column);
    }

    public int getAbilityRow() {
        return abilityRow;
    }

    public void setAbilityRow(int i) {
        abilityRow = MathHelper.clamp(i, 0, 3);
    }

    public int getAbilitySlot() {
        return abilitySlot;
    }

    public void setAbilitySlot(int i) {
        abilitySlot = MathHelper.clamp(i, 0, 9);
    }

    public void toggleMagicMode() {
        magicMode = !magicMode;
    }

    public boolean magicModeActive() {
        return magicMode;
    }

    public void sync() {
        if (!this.player.getWorld().isClient) SoulComponent.sync(this.player);
    }

    public PacketByteBuf toBuffer() {
        PacketByteBuf buf = PacketByteBufs.create();
        try {
            buf.writeVarInt(traits.size());
            buf.writeString(traits.get(0).getName());
            if (traits.size() == 2) buf.writeString(traits.get(1).getName());
            buf.writeBoolean(strong);
            buf.writeBoolean(pure);
            buf.writeVarInt(lv);
            buf.writeVarInt(exp);
            buf.writeVarInt(style);
            buf.writeVarInt(styleRank);
            buf.writeVarInt(lastStyleIncrease);
            buf.writeFloat(magic);
            buf.writeFloat(magicGauge);

            buf.writeVarInt(abilities.getAll().size());
            for (AbilityBase ability : abilities.getAll()) {
                buf.writeString(ability.getName());
                buf.writeNbt(ability.saveNbt(new NbtCompound()));
            }

            buf.writeItemStack(weapon);
            buf.writeVarInt(lastCastTime);

            int discoveredSize = 0;
            for (AbilityBase ability : discovered) {
                if (ability != null) discoveredSize++;
            }
            buf.writeVarInt(discoveredSize);
            for (AbilityBase ability : discovered) {
                if (ability != null) buf.writeString(ability.getID().getPath());
            }

            buf.writeVarInt(monsterSouls.size());
            for (String type : monsterSouls.keySet()) {
                buf.writeString(type);
                buf.writeVarInt(monsterSouls.get(type));
            }

            buf.writeVarInt(playerSouls.size());
            for (String type : playerSouls.keySet()) {
                buf.writeString(type);
                buf.writeVarInt(playerSouls.get(type));
            }

            abilityLayout.toBuf(buf);

            resetData.writeBuf(buf);

            buf.writeBoolean(disguisedAsID != null);
            if (disguisedAsID != null) buf.writeUuid(disguisedAsID);

            spokenTextRenderer.writeBuffer(buf);

            return buf;
        } catch (Exception e) {
            return null;
        }
    }

    public void fromBuffer(PacketByteBuf buf) {
        int traitCount = buf.readVarInt();
        List<TraitBase> traits = new ArrayList<>(List.of(Objects.requireNonNull(Traits.get(buf.readString()))));
        if (traitCount == 2) traits.add(Traits.get(buf.readString()));
        this.traits = traits;
        this.strong = buf.readBoolean();
        this.pure = buf.readBoolean();
        this.lv = buf.readVarInt();
        this.exp = buf.readVarInt();
        this.style = buf.readVarInt();
        this.styleRank = buf.readVarInt();
        this.lastStyleIncrease = buf.readVarInt();
        this.magic = buf.readFloat();
        this.magicGauge = buf.readFloat();

        int abilityCount = buf.readVarInt();
        AbilityList abilityList = new AbilityList();
        for (int i = 0; i < abilityCount; i++) {
            String abilityName = buf.readString();
            try {
                NbtCompound nbt = buf.readNbt();
                AbilityBase ability = Abilities.get(abilityName);
                ability.readNbt(nbt);
                abilityList.add(ability);
            } catch (NullPointerException e) {
                SoulForge.LOGGER.warn("Ability does not exist: {}", abilityName);
            }
        }
        this.abilities = abilityList;

        this.weapon = buf.readItemStack();

        this.lastCastTime = buf.readVarInt();

        int discoveredCount = buf.readVarInt();
        this.discovered = new ArrayList<>();
        for (int i = 0; i < discoveredCount; i++) {
            String id = buf.readString();
            try {
                discovered.add(Abilities.get(new Identifier(SoulForge.MOD_ID, id)));
            } catch (InvalidIdentifierException e) {
                SoulForge.LOGGER.warn("Received invalid ability identifier: " + id + ". Continuing...");
            }
        }

        int soulCount = buf.readVarInt();
        this.monsterSouls = new HashMap<>();
        for (int i = 0; i < soulCount; i++) monsterSouls.put(buf.readString(), buf.readVarInt());

        soulCount = buf.readVarInt();
        this.playerSouls = new HashMap<>();
        for (int i = 0; i < soulCount; i++) playerSouls.put(buf.readString(), buf.readVarInt());

        this.abilityLayout = AbilityLayout.fromBuf(List.copyOf(abilities.getAll()), buf);

        this.resetData = ResetData.fromBuf(buf);

        this.disguisedAsID = null;
        if (buf.readBoolean()) {
            disguisedAsID = buf.readUuid();
            disguisedAs = player.getWorld().getPlayerByUuid(disguisedAsID);
        }

        this.spokenTextRenderer.readBuffer(buf);
    }

    public void castAbility(int index) {
        List<AbilityBase> rowAbilities = getLayoutRow(getAbilityRow()).abilities;
        if (index <= 8 && index >= 0) {
            AbilityBase toCast = rowAbilities.get(index);
            castAbility(toCast);
        } else {
            SoulForge.LOGGER.info("Attempted to cast an ability that the player doesn't have!");
        }
    }

    public boolean hasDiscovered(AbilityBase ability) {
        return discovered.contains(ability);
    }

    public void discover(AbilityBase ability) {
        if (!discovered.contains(ability)) discovered.add(ability);
    }

    public void undiscover(AbilityBase ability) {
        discovered.remove(ability);
    }

    public List<AbilityBase> getDiscovered() {
        return discovered;
    }

    public void clearDiscovered() {
        discovered.clear();
    }

    public boolean isStrong() { return strong; }
    public boolean isPure() { return pure; }
    public void setStrong(boolean strong) {
        this.strong = strong;
    }
    public void setPure(boolean pure) {
        if (this.pure) this.strong = true;
        this.pure = pure;
        if (this.player instanceof ServerPlayerEntity) {
            updateAbilities();
        }
    }

    public void createWormholeRequest(PlayerEntity from) {
        if (from == null) wormholeRequest = null;
        else wormholeRequest = new Pair<>(from.getUuid(), from.getServer().getTicks());
        sync();
    }

    public void removeWormholeRequest() {
        wormholeRequest = null;
        sync();
    }

    @Nullable
    public Pair<UUID, Integer> getWormholeRequest() {
        return wormholeRequest;
    }

    public PlayerEntity getWormholeTarget() {
        if (wormholeRequest != null) {
            return player.getWorld().getPlayerByUuid(wormholeRequest.getLeft());
        }
        return null;
    }

    public int getWormholeTime() {
        if (wormholeRequest != null) {
            return wormholeRequest.getRight();
        }
        return 0;
    }

    public boolean hasWormholeRequest() {
        return wormholeRequest != null;
    }

    public void setDisguise(PlayerEntity target) {
        disguisedAsID = target.getUuid();
        disguisedAs = target;
        sync();
    }

    public void removeDisguise() {
        disguisedAsID = null;
        disguisedAs = null;
        sync();
    }

    public PlayerEntity getDisguise() {
        return disguisedAs;
    }

    public String getSpokenText() {
        return spokenTextRenderer.toRender();
    }

    public void setSpokenText(String text, int speed, int timeToDisappear) {
        spokenTextRenderer.setText(text, speed, timeToDisappear);
        sync();
    }

    public void setSpokenText(String text) {
        spokenTextRenderer.setText(text);
        sync();
    }

    public void castAbility(AbilityBase ability) {
        if (ability == null) return;
        boolean contains = abilities.has(ability.getName());
        if (contains || ability.getType() == AbilityType.PASSIVE) {
            float cost = ability.getCost();
            if (player.getAttributeInstance(SoulForgeAttributes.MAGIC_COST) != null) {
                cost *= (float)player.getAttributeValue(SoulForgeAttributes.MAGIC_COST);
            }
            if (ability instanceof ToggleableAbilityBase toggleable && toggleable.getActive()) cost = 0;
            if (cost <= magic && !onCooldown(ability)) {
                boolean canCast = ability.cast((ServerPlayerEntity)player);
                if (canCast) {
                    SoulForgeCriterions.CAST_ABILITY.trigger((ServerPlayerEntity) player, ability);
                    if (ability instanceof ToggleableAbilityBase toggleable) {
                        if (toggleable.getActive()) {
                            magic -= cost;
                            resetLastCastTime();
                        } else {
                            ability.end((ServerPlayerEntity)player);
                            ability.setActive(false);
                            float cooldown = ability.getCooldown();
                            if (player.getAttributeInstance(SoulForgeAttributes.MAGIC_COST) != null) {
                                cooldown *= (float)player.getAttributeValue(SoulForgeAttributes.MAGIC_COOLDOWN);
                            }
                            setCooldown(ability, (int)cooldown);
                        }
                    } else {
                        magic -= cost;
                        float cooldown = ability.getCooldown();
                        if (player.getAttributeInstance(SoulForgeAttributes.MAGIC_COST) != null) {
                            cooldown *= (float)player.getAttributeValue(SoulForgeAttributes.MAGIC_COOLDOWN);
                        }
                        setCooldown(ability, (int)cooldown);
                        ability.setActive(true);
                        resetLastCastTime();
                    }
                }
                sync();
            }
        } else {
            SoulForge.LOGGER.info("Attempted to cast an ability that the player doesn't have!");
        }
    }

    private void decreaseTimer(ValueComponent values, String value) {
        if (values.hasInt(value)) {
            if (values.getInt(value) > 0) values.setInt(value, values.getInt(value) - 1);
            else values.removeInt(value);
        }
    }

    private static final EntityAttributeModifier strongModifier = new EntityAttributeModifier(UUID.fromString("5390de41-a4a2-450c-be21-efd230c47fc9"), "strong", -0.5f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
    private static final EntityAttributeModifier pureModifier = new EntityAttributeModifier(UUID.fromString("ac02d0d4-839f-4215-b6f0-142c45d8cd47"), "pure", 0.5f, EntityAttributeModifier.Operation.ADDITION);
    private static final EntityAttributeModifier shieldBreakModifier = new EntityAttributeModifier(UUID.fromString("a7fb97e3-f50c-48c0-8e32-3a7c0a4cc0f8"), "perseverance", 1f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
    @Override
    public void tick() {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            if (!initialized) {
                resetTrait();
                updateAbilities();
                initialized = true;
            }

            ValueComponent values = SoulForge.getValues(serverPlayer);

            for (AbilityBase ability : abilities.getActive()) {
                if (ability.tick((ServerPlayerEntity)player)) {
                    ability.end((ServerPlayerEntity)player);
                    if (ability.getType() == AbilityType.TOGGLE || ability.getType() == AbilityType.AURA) {
                        float cooldown = ability.getCooldown();
                        cooldown *= (float)player.getAttributeValue(SoulForgeAttributes.MAGIC_COOLDOWN);
                        setCooldown(ability, (int)cooldown);
                    }
                    ability.setActive(false);
                }
            }
            for (AbilityBase ability : abilities.getOnCooldown()) {
                ability.cooldownTick();
            }

            for (AbilityBase ability : getAbilities()) {
                if (ability.getType() == AbilityType.SIDE_EFFECT && ability instanceof SideEffectAbilityBase sideEffect) {
                    if (Math.random() <= sideEffect.getOccurrenceChance()) {
                        boolean canCast = sideEffect.cast((ServerPlayerEntity)player);
                        if (canCast) {
                            SoulForgeCriterions.CAST_ABILITY.trigger((ServerPlayerEntity) player, ability);
                            ability.setActive(true);
                        }
                        sync();
                    }
                }
            }

            if (isStrong() && !player.getAttributeInstance(SoulForgeAttributes.MAGIC_COST).hasModifier(strongModifier)) {
                player.getAttributeInstance(SoulForgeAttributes.MAGIC_COST).addPersistentModifier(strongModifier);
            }
            if (isPure() && !player.getAttributeInstance(SoulForgeAttributes.MAGIC_POWER).hasModifier(pureModifier)) {
                player.getAttributeInstance(SoulForgeAttributes.MAGIC_POWER).addPersistentModifier(pureModifier);
            }

            if (hasWeapon()) {
                if (getWeapon().getItem() instanceof MagicSwordItem) {
                    MagicSwordItem weapon = (MagicSwordItem) getWeapon().getItem();
                    if (getWeapon().isIn(SoulForgeTags.EFFECTIVE_LV_WEAPON)) {
                        weapon.attackDamage = weapon.baseAttackDamage + getEffectiveLV() * weapon.lvIncrease;
                    } else {
                        weapon.attackDamage = weapon.baseAttackDamage + getLV() * weapon.lvIncrease;
                    }
                }
            }
            if (hasWormholeRequest()) {
                if (player.getServer().getTicks() > getWormholeTime() + 600) {
                    removeWormholeRequest();
                }
            }
            assert values != null;
            decreaseTimer(values, "dtWeaponCooldown");
            decreaseTimer(values, "shieldBashCooldown");
            decreaseTimer(values, "stockpileTimer");
            decreaseTimer(values, "slamJumpTimer");
            if (values.hasInt("stockpileTimer")) {
                if (values.getInt("stockpileTimer") <= 0 && values.hasInt("stockpiles")) {
                    values.setInt("stockpiles", 0);
                }
            }
            if (values.hasInt("stockpiles")) {
                if (values.getInt("stockpiles") == 8) {
                    values.setInt("stockpiles", 0);
                    values.setInt("stockpileTimer", 0);
                    if (abilities.has("Perfected Aura Technique")) {
                        PerfectedAuraTechnique pat = (PerfectedAuraTechnique)abilities.get("Perfected Aura Technique");
                        pat.fullPower = true;
                        pat.timer = 5020;
                        SoulForgeCriterions.CAST_ABILITY.trigger((ServerPlayerEntity) player, pat);
                    }
                }
            }
            if (values.hasTimer("shieldBash")) {
                for (Entity target : player.getEntityWorld().getOtherEntities(player, Box.of(player.getPos().add(0f, 1f, 0f), 1, 2, 1))) {
                    if (target instanceof LivingEntity living) {
                        if (living.damage(SoulForgeDamageTypes.of(player, SoulForgeDamageTypes.SUMMON_WEAPON_DAMAGE_TYPE), 5f)) {
                            setStyle(getStyle() + 5);
                        }
                        living.takeKnockback(1.5f, -player.getVelocity().x, -player.getVelocity().z);
                        if (hasCast("Furioso")) living.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.VULNERABILITY, 100, 1));
                        if (player.getMainHandStack().isOf(SoulForgeItems.DETERMINATION_SHIELD)) {
                            living.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.VULNERABILITY, 100, 0));
                            living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 1));
                            living.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 100, 1));
                            living.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 100, 1));
                        }
                    }
                }
            }
            if (values.hasTimer("clawGouge")) {
                if (values.getTimer("clawGouge") == 15) {
                    TemporaryModifierComponent modifiers = SoulForge.getTemporaryModifiers(player);
                    values.setTimer("forcedRunning", 15);
                    modifiers.addTemporaryModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, new EntityAttributeModifier("claw_gouge", 2, EntityAttributeModifier.Operation.MULTIPLY_TOTAL), 15);
                    Vec3d velAdd = player.getRotationVector().withAxis(Direction.Axis.Y, 0).normalize().multiply(player.getMainHandStack().isOf(SoulForgeItems.DETERMINATION_CLAW) ? 3.5f : 2f);
                    player.addVelocity(velAdd);
                    player.velocityModified = true;
                }
                if (values.getTimer("clawGouge") <= 15) {
                    for (Entity target : player.getEntityWorld().getOtherEntities(player, Box.of(player.getPos().add(0f, 1f, 0f), 1, 2, 1))) {
                        if (target instanceof LivingEntity living) {
                            if (living.damage(player.getDamageSources().playerAttack(player), (float) (player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) * 1.5f))) {
                                setStyle(getStyle() + (int) (player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) * 1.5f));
                            }
                            if (player.getMainHandStack().isOf(SoulForgeItems.DETERMINATION_CLAW)) {
                                living.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.VULNERABILITY, 140, 1));
                            } else {
                                player.setVelocity(Vec3d.ZERO);
                                player.velocityModified = true;
                                if (getMagic() >= 40) {
                                    setMagic(getMagic() - 40f);
                                    Utils.addAntiheal(hasCast("Furioso") ? 1f : 0.8f, getLV() * 40, living);
                                }
                            }
                            if (player.getMainHandStack().isOf(SoulForgeItems.GUNLANCE)) {
                                Vec3d end = player.getEyePos().add(player.getRotationVector().multiply(50f));
                                HitResult hit = player.getWorld().raycast(new RaycastContext(player.getEyePos(), player.getEyePos().add(player.getRotationVector().multiply(50f)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player));
                                if (hit != null) end = hit.getPos().subtract(Utils.getArmPosition(player));
                                BlastEntity blast = new BlastEntity(player.getWorld(), Utils.getArmPosition(player),
                                        player, 0.25f, Vec3d.ZERO, end, getLV() * 1.5f, Color.YELLOW, false, 10);
                                blast.owner = player;
                                ServerWorld serverWorld = (ServerWorld) player.getWorld();
                                serverWorld.spawnEntity(blast);
                                serverWorld.emitGameEvent(GameEvent.ENTITY_PLACE, player.getPos(), GameEvent.Emitter.of(player));
                                serverWorld.playSoundFromEntity(null, player, SoulForgeSounds.UT_BLASTER_EVENT, SoundCategory.PLAYERS, 1f, 1f);
                            }
                            break;
                        }
                    }
                }
            }
            if (values.hasTimer("dtGauntletsRush")) {
                for (Entity target : player.getEntityWorld().getOtherEntities(player, Box.of(player.getPos().add(0f, 1f, 0f), 1, 2, 1))) {
                    if (target instanceof LivingEntity living) {
                        living.getWorld().createExplosion(player, living.getX(), living.getY(), living.getZ(), 1f, World.ExplosionSourceType.NONE);
                        values.removeTimer("dtGauntletsRush");
                        values.removeTimer("forcedRunning");
                        player.setVelocity(0f, 0f, 0f);
                        player.velocityModified = true;
                        break;
                    }
                }
            }
            if (values.hasTimer("yoyoAoETimer")) {
                for (Entity entity : player.getWorld().getOtherEntities(player, Box.of(player.getPos(), 4, 4, 4))) {
                    if (entity instanceof LivingEntity living) {
                        if (living.damage(player.getDamageSources().playerAttack(player), 12f)) {
                            setStyle(getStyle() + 12);
                        }
                    }
                }
                float angle = values.getTimer("yoyoAoETimer");
                Vec3d particlePos = new Vec3d(Math.sin(angle), 1f, Math.cos(angle));
                serverPlayer.getServerWorld().spawnParticles(ParticleTypes.SWEEP_ATTACK, player.getX() + particlePos.x, player.getY() + particlePos.y, player.getZ() + particlePos.z, 2, 0, 0, 0, 0);
                values.removeTimer("yoyoSpin");
            }
            if (values.hasTimer("yoyoSpin")) {
                float angle = (20-values.getTimer("yoyoSpin"))/20f * MathHelper.PI;
                Vec3d start = new Vec3d(values.getFloat("startX"), values.getFloat("startY"), values.getFloat("startZ"));
                Vec3d center = new Vec3d(values.getFloat("centerX"), values.getFloat("centerY"), values.getFloat("centerZ"));
                Vec3d p = start.subtract(center);
                Vec3d axis = new Vec3d(values.getFloat("axisX"), values.getFloat("axisY"), values.getFloat("axisZ")).normalize();
                float a = MathHelper.cos(angle/2f);
                Vec3d f = axis.negate().multiply(MathHelper.sin(angle/2f));
                float b = (float)f.x, c = (float)f.y, d = (float)f.z;
                float aa = a*a, bb = b*b, cc = c*c, dd = d*d;
                float bc = b*c, ad = a*d, ac = a*c, ab = a*b, bd = b*d, cd = c*d;
                Vec3d fx = new Vec3d(aa+bb-cc-dd,2*(bc+ad),2*(bd-ac));
                Vec3d fy = new Vec3d(2*(bc-ad),aa+cc-bb-dd,2*(cd+ab));
                Vec3d fz = new Vec3d(2*(bd+ac),2*(cd-ab),aa+dd-bb-cc);
                Vec3d result = new Vec3d(fx.x*p.x+fx.y*p.y+fx.z*p.z, fy.x*p.x+fy.y*p.y+fy.z*f.z, fz.x*p.x+fz.y*p.y+fz.z*p.z);
                HitResult hit = player.getWorld().raycast(new RaycastContext(center, center.add(result), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player));
                Vec3d target = hit != null ? hit.getPos() : center.add(result);
                player.teleport(target.x, target.y, target.z);
                player.fallDistance = -5;
            }

            /*if (values.hasBool("rampaging")) {
                int rampageStartType = (int) getValue("rampageStart");
                int rampageActiveType = (int) getValue("rampageActive");
                int rampageEndType = (int) getValue("rampageEnd");
                int computedStartDuration = switch (rampageActiveType) {
                    case 4 -> 100;
                    case 5 -> 20;
                    default -> 0;
                };
                int computedActiveDuration = switch (rampageActiveType) {
                    case 0, 2, 3, 4 -> 400;
                    case 1 -> 250;
                    case 5 -> 600;
                    default -> 0;
                };
                int computedEndDuration = switch (rampageEndType) {
                    case 4 -> 20;
                    case 5 -> 40;
                    default -> 5;
                };
                if (getValue("rampageTimer") <= 0f) {
                    switch (rampageStartType) {
                        case 0 -> {
                            for (LivingEntity infront : Utils.getEntitiesInFrontOf(player, 3, 15, 2, 2)) {
                                if (!TeamUtils.canDamageEntity(player.getServer(), player, infront)) {
                                    continue;
                                }
                                if (infront.damage(SoulForgeDamageTypes.of(player, SoulForgeDamageTypes.ABILITY_DAMAGE_TYPE), 10f)) {
                                    infront.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 30, 6));
                                    infront.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 30, 3));
                                }
                            }
                            float cos = MathHelper.cos(player.getYaw() * MathHelper.RADIANS_PER_DEGREE);
                            float sin = MathHelper.sin(player.getYaw() * MathHelper.RADIANS_PER_DEGREE);
                            float cos90 = -MathHelper.cos((player.getYaw() - 90) * MathHelper.RADIANS_PER_DEGREE);
                            float sin90 = -MathHelper.sin((player.getYaw() - 90) * MathHelper.RADIANS_PER_DEGREE);
                            Vec3d f = new Vec3d(-sin, 0, cos);
                            Vec3d s = new Vec3d(sin90, 0, cos90);
                            for (int i = 0; i < 15; i++) {
                                for (int j = 0; j < 6; j++) {
                                    Vec3d pos = player.getPos().add(f.multiply(i)).add(s.multiply(j-3f));
                                    serverPlayer.getServerWorld().spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.STONE.getDefaultState()),
                                            pos.x, pos.y, pos.z, 3, 0f, 0f, 0f, 1);
                                }
                            }
                        }
                        case 1 -> {
                            for (int i = 0; i < 15; i++) {
                                JusticePelletProjectile pellet = new JusticePelletProjectile(player.getWorld(), player);
                                pellet.setPos(new Vec3d(player.getX(), player.getEyeY(), player.getZ()));
                                Vec3d pelletVel = player.getRotationVector().multiply(3.5f)
                                        .add(new Vec3d(Math.random() - 0.5f, Math.random() - 0.5f, Math.random() - 0.5f)).normalize().multiply(4f);
                                pellet.setVelocity(pelletVel);
                                player.getWorld().spawnEntity(pellet);
                            }
                        }
                        case 2 -> {
                            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, computedStartDuration + computedActiveDuration, 1));
                            setValue("shieldBash", 15);
                        }
                        case 3 -> {
                            IceSpikeProjectile projectile = new IceSpikeProjectile(player.getWorld(), player);
                            projectile.setPosition(player.getPos().add(player.getRotationVector().withAxis(Direction.Axis.Y, 0)));
                            projectile.setYaw(player.getYaw());
                            player.getWorld().spawnEntity(projectile);
                            player.getWorld().playSoundFromEntity(null, player, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 1f, 1f);
                        }
                        case 4 -> {
                            values.setTimer("forcedRunning", 100);
                        }
                    }
                    setValue("rampageTimer", getValue("rampageTimer") + 1);
                } else if (getValue("rampageTimer") <= computedStartDuration) {
                    SoulForge.LOGGER.info("starting, rampageTimer: {}, start duration: {}, active duration: {}, end duration: {}", getValue("rampageTimer"), computedStartDuration, computedActiveDuration, computedEndDuration);
                    switch (rampageStartType) {
                        case 4 -> {
                            boolean hit = false;
                            for (Entity target : player.getEntityWorld().getOtherEntities(player, Box.of(player.getPos().add(0f, 1f, 0f), 1, 2, 1))) {
                                if (target instanceof LivingEntity living) {
                                    living.damage(SoulForgeDamageTypes.of(player, SoulForgeDamageTypes.SUMMON_WEAPON_DAMAGE_TYPE), 5f);
                                    living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, getEffectiveLV() / 5));
                                    hit = true;
                                }
                            }
                            if (hit) setValue("rampageTimer", computedStartDuration - 1);
                        }
                        case 5 -> {
                            if (getValue("rampageTimer") == computedStartDuration) {
                                for (LivingEntity target : Utils.getEntitiesInFrontOf(player, 3, 3, 1, 1)) {
                                    if (!TeamUtils.canDamageEntity(player.getServer(), player, target)) continue;
                                    target.damage(SoulForgeDamageTypes.of(player, SoulForgeDamageTypes.SUMMON_WEAPON_DAMAGE_TYPE), 25f);
                                }
                                player.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.VULNERABILITY, computedActiveDuration, 1));
                                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, computedActiveDuration, 2));
                            }
                        }
                    }
                    setValue("rampageTimer", getValue("rampageTimer") + 1);
                } else if (getValue("rampageTimer") <= computedActiveDuration + computedStartDuration && getValue("rampageTimer") > computedStartDuration) {
                    SoulForge.LOGGER.info("active, rampageTimer: {}, start duration: {}, active duration: {}, end duration: {}", getValue("rampageTimer"), computedStartDuration, computedActiveDuration, computedEndDuration);
                    switch (rampageActiveType) {
                        case 0 -> {
                            if (getValue("rampageTimer") == computedStartDuration + 1) {
                                player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 400, 1));
                                player.getAttributeInstance(SoulForgeAttributes.MAGIC_POWER).addTemporaryModifier(new EntityAttributeModifier(
                                        UUID.fromString("ba424f6f-9ae4-4ef5-8d97-3aa09b2fbbb4"), "rampageBravery", 0.25, EntityAttributeModifier.Operation.ADDITION));
                            }
                            if (getValue("rampageTimer") == computedStartDuration + computedActiveDuration) {
                                player.getAttributeInstance(SoulForgeAttributes.MAGIC_POWER).removeModifier(UUID.fromString("ba424f6f-9ae4-4ef5-8d97-3aa09b2fbbb4"));
                            }
                            for (LivingEntity target : player.getWorld().getEntitiesByClass(LivingEntity.class, Box.of(player.getPos(), 14, 14, 14), (entity) -> TeamUtils.canDamageEntity(player.getServer(), player, entity))) {
                                if (target.getFireTicks() < 100) target.setFireTicks(110);
                            }
                        }
                        case 1 -> {
                            if ((getValue("rampageTimer") - computedStartDuration) % 25 == 0) {
                                Vec3d end = player.getEyePos().add(player.getRotationVector().multiply(50f));
                                HitResult hit = player.getWorld().raycast(new RaycastContext(player.getEyePos(), player.getEyePos().add(player.getRotationVector().multiply(50f)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player));
                                if (hit != null) end = hit.getPos().subtract(Utils.getArmPosition(player));
                                BlastEntity blast = new BlastEntity(player.getWorld(), Utils.getArmPosition(player),
                                        player, 0.25f, Vec3d.ZERO, end, getEffectiveLV()/3f, Color.RED, true, Math.min(40, 10*getEffectiveLV()/3), 5);
                                blast.owner = player;
                                ServerWorld serverWorld = serverPlayer.getServerWorld();
                                serverWorld.spawnEntity(blast);
                                serverWorld.emitGameEvent(GameEvent.ENTITY_PLACE, player.getPos(), GameEvent.Emitter.of(player));
                                serverWorld.playSoundFromEntity(null, player, SoulForgeSounds.UT_BLASTER_EVENT, SoundCategory.PLAYERS, 1f, 1f);
                            }
                        }
                        case 2 -> {
                            for (LivingEntity target : player.getWorld().getEntitiesByClass(LivingEntity.class, Box.of(player.getPos(), 14, 14, 14), (entity) -> TeamUtils.canHealEntity(player.getServer(), player, entity))) {
                                target.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 2, 1));
                                target.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 2, 0));
                            }
                        }
                        case 4 -> {
                            if (getValue("rampageTimer") == computedStartDuration + 1) {
                                player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_SPEED).addTemporaryModifier(new EntityAttributeModifier(
                                        UUID.fromString("31c6e2a7-4751-4d8c-9100-0f11c63e24c3"), "rampageIntegrity", 0.5, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
                            }
                            if (getValue("rampageTimer") == computedStartDuration + computedActiveDuration) {
                                player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_SPEED).removeModifier(UUID.fromString("31c6e2a7-4751-4d8c-9100-0f11c63e24c3"));
                            }
                        }
                        case 5 -> {
                            if ((getValue("rampageTimer") - computedStartDuration) % 200 == 0) {
                                TemporaryModifierComponent modifiers = SoulForge.getTemporaryModifiers(player);
                                modifiers.addTemporaryModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(UUID.fromString("6b29ca47-5f00-45ef-aa29-62236c9cf493"),
                                        "rampagePerseverance", 0.02f * getEffectiveLV(), EntityAttributeModifier.Operation.MULTIPLY_TOTAL), 120);
                            }
                        }
                    }
                    setValue("rampageTimer", getValue("rampageTimer") + 1);
                } else if (getValue("rampageTimer") > computedStartDuration + computedActiveDuration && getValue("rampageTimer") < computedStartDuration + computedActiveDuration + computedEndDuration) {
                    SoulForge.LOGGER.info("ending, rampageTimer: {}, start duration: {}, active duration: {}, end duration: {}", getValue("rampageTimer"), computedStartDuration, computedActiveDuration, computedEndDuration);
                    switch (rampageEndType) {
                        case 0 -> {
                            if (getValue("rampageTimer") == computedStartDuration + computedActiveDuration + 1) Shatter.performShatterLiterallyJustThat(serverPlayer);
                        }
                        case 1 -> {
                            if (getValue("rampageTimer") == computedStartDuration + computedActiveDuration + 1) {
                                FragmentationGrenadeProjectile projectile = new FragmentationGrenadeProjectile(player.getWorld(), player.getEyePos(), player);
                                projectile.setPosition(player.getEyePos());
                                projectile.setVelocity(new Vec3d(0f, 2f, 0f));
                                projectile.setOwner(player);
                                serverPlayer.getServerWorld().spawnEntity(projectile);
                                for (int i = 0; i < 6; i++) {
                                    JusticePelletProjectile pellet = new JusticePelletProjectile(player.getWorld(), player);
                                    pellet.setPos(player.getPos().add(new Vec3d(5 * MathHelper.sin((float) (i * Math.PI / 3f)), 1f, 5 * MathHelper.cos((float) (i * Math.PI / 3f)))));
                                    pellet.setVelocity(player.getPos().add(0f, player.getHeight()/2f, 0f).subtract(pellet.getPos()).normalize().multiply(2f));
                                    player.getWorld().spawnEntity(pellet);
                                    pellet.playSound(SoulForgeSounds.UT_A_BULLET_EVENT, 0.5f, 1f);
                                }
                                for (int i = 0; i < 12; i++) {
                                    JusticePelletProjectile pellet = new JusticePelletProjectile(player.getWorld(), player);
                                    pellet.setPos(player.getPos().add(new Vec3d(8 * MathHelper.sin((float) (i * Math.PI / 6f)), 1f, 8 * MathHelper.cos((float) (i * Math.PI / 6f)))));
                                    pellet.setVelocity(player.getPos().add(0f, player.getHeight()/2f, 0f).subtract(pellet.getPos()).normalize().multiply(2f));
                                    player.getWorld().spawnEntity(pellet);
                                    pellet.playSound(SoulForgeSounds.UT_A_BULLET_EVENT, 0.5f, 1f);
                                }
                                for (int i = 0; i < 18; i++) {
                                    JusticePelletProjectile pellet = new JusticePelletProjectile(player.getWorld(), player);
                                    pellet.setPos(player.getPos().add(new Vec3d(13 * MathHelper.sin((float) (i * Math.PI / 9f)), 1f, 13 * MathHelper.cos((float) (i * Math.PI / 9f)))));
                                    pellet.setVelocity(player.getPos().add(0f, player.getHeight()/2f, 0f).subtract(pellet.getPos()).normalize().multiply(2f));
                                    player.getWorld().spawnEntity(pellet);
                                    pellet.playSound(SoulForgeSounds.UT_A_BULLET_EVENT, 0.5f, 1f);
                                }
                            }
                        }
                        case 2 -> {
                            if (getValue("rampageTimer") == computedStartDuration + computedActiveDuration + 1) {
                                for (LivingEntity target : player.getWorld().getEntitiesByClass(LivingEntity.class, Box.of(player.getPos(), 14, 14, 14), (entity) -> TeamUtils.canHealEntity(player.getServer(), player, entity))) {
                                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 1));
                                }
                            }
                        }
                        case 3 -> {
                            if (getValue("rampageTimer") == computedStartDuration + computedActiveDuration + 1) {
                                for (LivingEntity target : player.getWorld().getEntitiesByClass(LivingEntity.class, Box.of(player.getPos(), 14, 14, 14), (entity) -> TeamUtils.canHealEntity(player.getServer(), player, entity))) {
                                    TemporaryModifierComponent modifiers = SoulForge.getTemporaryModifiers(target);
                                    modifiers.addTemporaryModifier(SoulForgeAttributes.MAGIC_POWER, new EntityAttributeModifier(UUID.fromString("c8932fc0-c82a-4552-9417-9fd4bc1b6e8a"),
                                            "rampagePatience", 0.02f * getEffectiveLV(), EntityAttributeModifier.Operation.ADDITION), 200);
                                }
                            }
                        }
                        case 4 -> {
                            if (getValue("rampageTimer") == computedStartDuration + computedActiveDuration + 1) {
                                float horiz = getEffectiveLV() * 0.15f;
                                float vert = getEffectiveLV() * 0.005f;
                                Vec3d direction = new Vec3d(player.getRotationVector().x, 0f, player.getRotationVector().z).normalize().multiply(horiz);
                                PacketByteBuf buf = PacketByteBufs.create();
                                buf.writeBoolean(false).writeBoolean(false).writeBoolean(true);
                                buf.writeVector3f(new Vec3d(direction.x, vert, direction.z).toVector3f());
                                ServerPlayNetworking.send(serverPlayer, SoulForgeNetworking.POSITION_VELOCITY, buf);
                                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 200, 2));
                            }
                        }
                        case 5 -> {
                            if (getValue("rampageTimer") == computedStartDuration + computedActiveDuration + 1) {
                                for (LivingEntity target : Utils.getEntitiesInFrontOf(player, 5f, 8f, 2f, 2f)) {
                                    if (target instanceof PlayerEntity targetPlayer) {
                                        if (!TeamUtils.canDamageEntity(player.getServer(), player, targetPlayer))
                                            continue;
                                    }
                                    target.damage(SoulForgeDamageTypes.of(player, SoulForgeDamageTypes.ABILITY_DAMAGE_TYPE), getEffectiveLV() * 5f);
                                }
                                player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoulForgeSounds.UT_BOMBSPLOSION_EVENT, SoundCategory.MASTER, 1f, 1f);
                                PacketByteBuf buf = PacketByteBufs.create().writeUuid(player.getUuid()).writeString("greater_slash");
                                buf.writeBoolean(false);
                                if (player.getServer() != null)
                                    SoulForgeNetworking.broadcast(null, player.getServer(), SoulForgeNetworking.PERFORM_ANIMATION, buf);
                            }
                        }
                    }
                    setValue("rampageTimer", getValue("rampageTimer") + 1);
                } else {
                    removeValue("rampageTimer");
                    removeTag("rampaging");
                    removeValue("rampageStart");
                    removeValue("rampageActive");
                    removeValue("rampageEnd");
                }
            }*/

            if (this.disguisedAsID != null) this.disguisedAs = player.getWorld().getPlayerByUuid(disguisedAsID);

            if (hasTrait(Traits.perseverance)) {
                if (!player.getAttributeInstance(SoulForgeAttributes.SHIELD_BREAK).hasModifier(shieldBreakModifier)) {
                    player.getAttributeInstance(SoulForgeAttributes.SHIELD_BREAK).addPersistentModifier(shieldBreakModifier);
                }
            } else {
                if (player.getAttributeInstance(SoulForgeAttributes.SHIELD_BREAK).hasModifier(shieldBreakModifier)) {
                    player.getAttributeInstance(SoulForgeAttributes.SHIELD_BREAK).tryRemoveModifier(shieldBreakModifier.getId());
                }
            }

            if (hasTrait(Traits.determination)) {
                Utils.clearModifiersByName(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "limit_break");
                EntityAttributeModifier strengthModifier = new EntityAttributeModifier("limit_break", 0.1 * styleRank, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
                player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).addPersistentModifier(strengthModifier);
            }
            this.spokenTextRenderer.tick();
        }
        if (disguisedAsID != null) {
            if (disguisedAs == null || (disguisedAs.getUuid() != disguisedAsID)) disguisedAs = player.getWorld().getPlayerByUuid(disguisedAsID);
        }
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag) {
        if (player == null) {
            SoulForge.LOGGER.error("Player was null in SoulComponent while attempting to read from NBT!");
            return;
        }

        traits = new ArrayList<>();
        traits.add(Traits.get(tag.getString("trait1")));
        if (tag.contains("trait2")) traits.add(Traits.get(tag.getString("trait2")));

        lv = tag.getInt("lv");
        exp = tag.getInt("exp");
        magic = tag.getFloat("magic");
        magicGauge = tag.contains("magicGauge") ? tag.getFloat("magicGauge") : 0;
        strong = tag.getBoolean("strong");
        pure = tag.getBoolean("pure");
        NbtCompound abilityNbt = tag.getCompound("abilities");
        abilities = new AbilityList();
        for (String abilityName : abilityNbt.getKeys()) {
            try {
                AbilityBase ability = Abilities.get(abilityName);
                ability.readNbt(abilityNbt.getCompound(abilityName));
                ability.setCooldownVal(0);
                abilities.add(ability);
            } catch (NullPointerException e) {
                SoulForge.LOGGER.warn("Ability does not exist: {}", abilityName);
            }
        }
        for (AbilityBase ability : Traits.getAbilities(player, this)) {
            if (!abilities.has(ability)) {
                abilities.add(ability.getInstance());
            }
        }

        discovered = new ArrayList<>();
        List<String> discoveredIds = List.of(tag.getString("discovered").split(","));
        for (String idStr : discoveredIds) {
            if (!idStr.isEmpty()) {
                Identifier id = idStr.contains(":") ? Identifier.tryParse(idStr) : new Identifier(SoulForge.MOD_ID, idStr);
                if (!discovered.contains(Abilities.get(id))) {
                    discovered.add(Abilities.get(id));
                }
            }
        }

        monsterSouls = new HashMap<>();
        NbtCompound souls = (NbtCompound)tag.get("monsterSouls");
        if (souls != null) {
            for (String key : souls.getKeys()) {
                monsterSouls.put(key, souls.getInt(key));
                if (!player.getWorld().isClient) SoulForgeCriterions.MONSTER_SOUL.trigger((ServerPlayerEntity)player, monsterSouls.get(key), key);
            }
        }

        playerSouls = new HashMap<>();
        souls = (NbtCompound)tag.get("playerSouls");
        if (souls != null) {
            for (String key : souls.getKeys()) {
                playerSouls.put(key, souls.getInt(key));
            }
        }

        abilityLayout = AbilityLayout.fromNbt(List.copyOf(abilities.getAll()), tag.getList("abilityLayout", NbtElement.COMPOUND_TYPE));

        resetData = new ResetData(tag.getCompound("resetData"));

        if (tag.contains("wormhole")) {
            NbtCompound wormhole = tag.getCompound("wormhole");
            wormholeRequest = new Pair<>(wormhole.getUuid("target"), wormhole.getInt("time"));
        }

        if (tag.contains("disguisedAs")) {
            disguisedAsID = tag.getUuid("disguisedAs");
            disguisedAs = player.getWorld().getPlayerByUuid(disguisedAsID);
        }

        spokenTextRenderer.readNbt(tag.getCompound("spokenTextRenderer"));

        initialized = true;
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag) {
        tag.putString("trait1", traits.get(0).getName());
        if (traits.size() == 2) tag.putString("trait2", traits.get(1).getName());
        tag.putInt("lv", lv);
        tag.putInt("exp", exp);
        tag.putFloat("magic", magic);
        tag.putFloat("magicGauge", magicGauge);
        tag.putBoolean("strong", strong);
        tag.putBoolean("pure", pure);
        NbtCompound abilityNbt = new NbtCompound();
        for (AbilityBase ability : abilities.getAll()) {
            abilityNbt.put(ability.getName(), ability.saveNbt(new NbtCompound()));
        }
        tag.put("abilities", abilityNbt);
        List<String> discoveredIds = new ArrayList<>();
        for (AbilityBase ability : discovered) {
            if (ability != null) {
                if (!discoveredIds.contains(ability.getID().getPath())) discoveredIds.add(ability.getID().getPath());
            }
        }
        tag.putString("discovered", String.join(",", discoveredIds));
        NbtCompound souls = new NbtCompound();
        for (String key : monsterSouls.keySet()) souls.putInt(key, monsterSouls.get(key));
        tag.put("monsterSouls", souls);
        souls = new NbtCompound();
        for (String key : playerSouls.keySet()) souls.putInt(key, playerSouls.get(key));
        tag.put("playerSouls", souls);
        tag.put("abilityLayout", abilityLayout.toNbt());
        tag.put("resetData", resetData.toNBT());
        if (wormholeRequest != null) {
            NbtCompound wormhole = new NbtCompound();
            wormhole.putUuid("target", wormholeRequest.getLeft());
            wormhole.putInt("time", wormholeRequest.getRight());
            tag.put("wormhole", wormhole);
        }
        if (disguisedAsID != null) tag.putUuid("disguisedAs", disguisedAsID);
        tag.put("spokenTextRenderer", spokenTextRenderer.writeNbt());
    }

    @Override
    public String toString() {
        String str = "Trait: " + (traits.size() == 2 ? traits.get(0).getName() + "-" + traits.get(1).getName() : traits.get(0).getName());
        str += ", LV: " + lv + ", EXP: " + exp;
        return str;
    }

    public void reset() {
        resetTrait();
        lv = 1;
        exp = 0;
        magic = 100f;
        if (Utils.isInverted(this)) magicGauge = 1000f;
        monsterSouls = new HashMap<>();
        playerSouls = new HashMap<>();
        removeWeapon(false);
        abilityLayout = new AbilityLayout();
        for (AbilityBase ability : abilities.getAll()) {
            if (ability.getActive()) {
                ability.end((ServerPlayerEntity)player);
            }
            ability.setActive(false);
        }
        updateAbilities();
        sync();
    }

    public void softReset() {
        lv = 1;
        exp = 0;
        magic = 100f;
        if (Utils.isInverted(this)) magicGauge = 1000f;
        monsterSouls = new HashMap<>();
        playerSouls = new HashMap<>();
        removeWeapon(false);
        abilityLayout = new AbilityLayout();
        for (AbilityBase ability : abilities.getAll()) {
            if (ability.getActive()) {
                ability.end((ServerPlayerEntity)player);
            }
            ability.setActive(false);
        }
        updateAbilities();
        sync();
    }

    private void updateAbilities() {
        if (this.player == null) return;
        if (this.player.getWorld().isClient) return;
        List<String> shouldBeAbilityNames = Traits.getAbilities(player, this).stream().map(AbilityBase::getName).toList();
        for (String abilityName : shouldBeAbilityNames) {
            if (!this.abilities.has(abilityName)) {
                this.abilities.add(Abilities.get(abilityName));
            }
        }
        if (Utils.getHate(player) > 0f) {
            for (AbilityBase ability : Abilities.hateAbilities) {
                if (!this.abilities.has(ability.getName())) {
                    this.abilities.add(ability.getInstance());
                }
            }
        }
        for (AbilityBase ability : this.abilities.getAll()) {
            if (!shouldBeAbilityNames.contains(ability.getName())) {
                this.abilities.remove(ability);
            }
        }
        for (AbilityBase ability : this.abilities.getAll()) {
            discover(ability);
        }
    }

    private void resetTrait() {
        List<TraitBase> oldTraits = getTraits();
        if (oldTraits == null) oldTraits = new ArrayList<>(List.of(Traits.bravery, Traits.integrity, Traits.spite));
        Random rnd = new Random();
        int num = rnd.nextInt(100);
        strong = false;
        pure = false;
        traits = new ArrayList<>();
        if (num <= resetData.resetsSinceDT && !oldTraits.contains(Traits.determination)) {
            traits.add(Traits.determination);
            resetData.resetsSinceDT = -1;
        } else if (num <= resetData.resetsSinceDT + 5 * resetData.resetsSinceDual) {
            traits.add(Traits.randomNormal());
            traits.add(Traits.randomNormal());
            while (traits.get(1) == traits.get(0) && !oldTraits.equals(traits)) {
                traits.set(1, Traits.randomNormal());
            }
            resetData.resetsSinceDual = -1;
            num = rnd.nextInt(10);
            if (num <= resetData.resetsSinceStrong) {
                strong = true;
                resetData.resetsSinceStrong = 0;
            } else {
                resetData.resetsSinceStrong++;
            }
        } else {
            traits.add(Traits.randomNormal());
            while (oldTraits.equals(traits)) {
                traits = new ArrayList<>();
                traits.add(Traits.randomNormal());
            }
            num = rnd.nextInt(50);
            if (num <= resetData.resetsSincePure) {
                strong = true;
                pure = true;
                resetData.resetsSincePure = 0;
                resetData.resetsSinceStrong = 0;
            } else if (num <= resetData.resetsSincePure + resetData.resetsSinceStrong * 5) {
                strong = true;
                resetData.resetsSinceStrong = 0;
                resetData.resetsSincePure++;
            } else {
                resetData.resetsSinceStrong++;
                resetData.resetsSincePure++;
            }
        }
        resetData.resetsSinceDT++;
        resetData.resetsSinceDual++;
        if (player instanceof ServerPlayerEntity serverPlayer) {
            SoulForgeCriterions.PLAYER_LV.trigger(serverPlayer, getLV());
            SoulForgeCriterions.PLAYER_TRAIT.trigger(serverPlayer, this);
            SoulForgeCriterions.PLAYER_SOUL.trigger(serverPlayer, this);
        }
        if (traits.size() == 1) {
            if (traits.get(0) == Traits.bravery) resetData.bravery = true;
            if (traits.get(0) == Traits.justice) resetData.justice = true;
            if (traits.get(0) == Traits.kindness) resetData.kindness = true;
            if (traits.get(0) == Traits.patience) resetData.patience = true;
            if (traits.get(0) == Traits.integrity) resetData.integrity = true;
            if (traits.get(0) == Traits.perseverance) resetData.perseverance = true;
            if (traits.get(0) == Traits.determination) resetData.determination = true;
            if (pure) resetData.addPure(traits.get(0));
        } else if (traits.size() == 2) {
            if (strong) resetData.strongDual = true;
            resetData.addDual(traits.get(0), traits.get(1));
        }
    }
}

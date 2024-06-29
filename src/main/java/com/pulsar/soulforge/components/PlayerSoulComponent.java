package com.pulsar.soulforge.components;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.Abilities;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.ability.bravery.EnergyWave;
import com.pulsar.soulforge.ability.determination.DeterminationPlatform;
import com.pulsar.soulforge.ability.duals.PerfectedAuraTechnique;
import com.pulsar.soulforge.ability.integrity.Platforms;
import com.pulsar.soulforge.ability.pures.Determine;
import com.pulsar.soulforge.advancement.SoulForgeCriterions;
import com.pulsar.soulforge.armor.PlatformBootsItem;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
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
import com.pulsar.soulforge.util.Constants;
import com.pulsar.soulforge.util.ResetData;
import com.pulsar.soulforge.util.SpokenTextRenderer;
import com.pulsar.soulforge.util.Utils;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.List;

public class PlayerSoulComponent implements SoulComponent {
    private List<TraitBase> traits;
    private boolean strong;
    private boolean pure;
    private int lv;
    private int exp;
    private float magic;
    private HashMap<String, AbilityBase> abilities = new HashMap<>();
    private HashMap<String, AbilityBase> activeAbilities = new HashMap<>();
    private HashMap<AbilityBase, Integer> cooldowns = new HashMap<>();
    private List<String> modes;
    private String mode = null;
    private List<String> tags = new ArrayList<>();
    private HashMap<String, Float> values = new HashMap<>();
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
    private final SpokenTextRenderer spokenTextRenderer = new SpokenTextRenderer();
    private ResetData resetData = new ResetData();
    private Pair<UUID, Integer> wormholeRequest = null;
    private PlayerEntity disguisedAs = null;
    private UUID disguisedAsID = null;
    private AbilityBase determinedAbility = null;
    private boolean determining = false;

    public PlayerSoulComponent(PlayerEntity player) {
        this.player = player;
        resetTrait();
        lv = 1;
        exp = 0;
        magic = 100;
        updateAbilities();
        updateModes();
        updateTags();
    }

    public PlayerSoulComponent(PlayerEntity player, List<TraitBase> traits, boolean strong, boolean pure,
                               int lv, int exp, float magic, HashMap<String, AbilityBase> abilities,
                               HashMap<String, AbilityBase> activeAbilities, HashMap<AbilityBase, Integer> cooldowns, String mode,
                               ItemStack weapon, List<String> tags, HashMap<String, Float> values, int lastCastTime,
                               List<AbilityBase> discovered, HashMap<String, Integer> monsterSouls, HashMap<String, Integer> playerSouls,
                               AbilityLayout abilityLayout, int abilityRow, boolean magicMode, ResetData resetData, UUID disguisedAsID) {
        this.player = player;
        this.traits = traits;
        this.strong = strong;
        this.pure = pure;
        this.lv = lv;
        this.exp = exp;
        this.magic = magic;
        this.abilities = abilities;
        this.activeAbilities = activeAbilities;
        this.cooldowns = cooldowns;
        updateModes();
        this.mode = mode;
        this.weapon = weapon;
        this.tags = tags;
        this.values = values;
        this.lastCastTime = lastCastTime;
        this.discovered = discovered;
        this.monsterSouls = monsterSouls;
        this.playerSouls = playerSouls;
        this.abilityLayout = abilityLayout;
        this.abilityRow = abilityRow;
        this.magicMode = magicMode;
        this.resetData = resetData;
        this.disguisedAsID = disguisedAsID;
    }

    @Override
    public TraitBase getTrait(int num) {
        return traits.get(num);
    }

    @Override
    public List<TraitBase> getTraits() {
        return traits;
    }

    @Override
    public int getTraitCount() {
        return traits.size();
    }

    @Override
    public void setTrait(int num, TraitBase trait) {
        if (traits.size() >= num+1) traits.set(num, trait);
        else if (num <= 1) traits.add(trait);
        if (num == 1 && trait == null) traits = new ArrayList<>(List.of(traits.get(0)));
        abilityLayout = new AbilityLayout();
        for (AbilityBase ability : activeAbilities.values()) {
            ability.end((ServerPlayerEntity)player);
        }
        if (pure) resetData.addPure(traits.get(0));
        if (traits.size() >= 2) resetData.addDual(traits.get(0), traits.get(1));
        updateAbilities();
        updateModes();
        updateTags();
        SoulForgeCriterions.PLAYER_TRAIT.trigger((ServerPlayerEntity) player, this);
        sync();
    }

    @Override
    public void setTraits(List<TraitBase> traits) {
        this.traits = traits;
        abilityLayout = new AbilityLayout();
        for (AbilityBase ability : activeAbilities.values()) {
            ability.end((ServerPlayerEntity)player);
        }
        if (pure) resetData.addPure(traits.get(0));
        if (traits.size() >= 2) resetData.addDual(traits.get(0), traits.get(1));
        updateAbilities();
        updateModes();
        updateTags();
        SoulForgeCriterions.PLAYER_TRAIT.trigger((ServerPlayerEntity) player, this);
        sync();
    }

    @Override
    public void setResetValues(List<TraitBase> traits, boolean strong, boolean pure) {
        this.traits = traits;
        abilityLayout = new AbilityLayout();
        for (AbilityBase ability : activeAbilities.values()) {
            ability.end((ServerPlayerEntity)player);
        }
        if (traits.size() >= 2) resetData.addDual(traits.get(0), traits.get(1));
        this.strong = strong;
        this.pure = pure;
        updateAbilities();
        updateModes();
        updateTags();
        SoulForgeCriterions.PLAYER_TRAIT.trigger((ServerPlayerEntity) player, this);
        sync();
    }

    @Override
    public ResetData getResetData() {
        return resetData;
    }

    @Override
    public void setResetData(ResetData resetData) {
        this.resetData = resetData;
    }

    @Override
    public List<String> getTags() {
        return tags;
    }

    @Override
    public void addTag(String tag) {
        if (!tags.contains(tag)) tags.add(tag);
    }

    @Override
    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }

    @Override
    public void removeTag(String tag) {
        tags.remove(tag);
    }

    @Override
    public HashMap<String, Float> getValues() {
        return values;
    }

    @Override
    public float getValue(String value) {
        return values.get(value);
    }

    @Override
    public void setValue(String key, float value) {
        values.put(key, value);
    }

    @Override
    public boolean hasValue(String value) {
        return values.containsKey(value);
    }

    @Override
    public void removeValue(String value) {
        values.remove(value);
    }

    private void updateTags() {
        tags = new ArrayList<>();
    }

    @Override
    public void handleEvent(EventType event) {
        if (player instanceof ServerPlayerEntity serverPlayerEntity) {
            switch (event) {
                case FALL_IMMUNITY:
                    if (activeAbilities.containsKey("Launch")) {
                        activeAbilities.get("Launch").end(serverPlayerEntity);
                        activeAbilities.remove("Launch");
                        break;
                    }
                    break;
                case SPAWN_PLATFORM:
                    boolean hasDT = false;
                    boolean hasIG = false;
                    for (AbilityBase ability : activeAbilities.values()) {
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
                                for (AbilityBase ability : activeAbilities.values()) {
                                    if (ability instanceof Platforms) {
                                        ((Platforms) ability).spawn(player);
                                        break;
                                    }
                                }
                            } else {
                                for (AbilityBase ability : activeAbilities.values()) {
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

    @Override
    public int getLV() {
        if (traits.contains(Traits.spite)) return 100;
        return lv;
    }

    @Override
    public int getEffectiveLV() {
        float effLv = getLV();
        float multiplier = 1f;
        if (player.getAttributeInstance(SoulForgeAttributes.MAGIC_POWER) != null) multiplier = (float)player.getAttributeInstance(SoulForgeAttributes.MAGIC_POWER).getValue();
        if (pure) multiplier += 0.5f;
        if (traits.contains(Traits.determination)) {
            multiplier += MathHelper.clamp(0.5f*((player.getMaxHealth()-player.getHealth())/player.getMaxHealth()), 0f, 0.5f);
        }
        return MathHelper.floor(effLv*multiplier);
    }

    @Override
    public void setLV(int lv) {
        this.lv = Math.min(Math.max(lv, 1), 20);
        SoulForgeCriterions.PLAYER_LV.trigger((ServerPlayerEntity)player, this.lv);
        updateAbilities();
        updateModes();
        updateTags();
        sync();
    }

    @Override
    public int getEXP() {
        return exp;
    }

    @Override
    public void setEXP(int exp) {
        this.exp = Math.max(exp, 0);
        boolean leveledUp = false;
        int expRequirement = getExpRequirement();
        while (this.exp >= expRequirement && expRequirement != -1) {
            this.exp -= expRequirement;
            this.lv += 1;
            leveledUp = true;
            expRequirement = getExpRequirement();
        }
        if (leveledUp && player instanceof ServerPlayerEntity) {
            SoulForgeCriterions.PLAYER_LV.trigger((ServerPlayerEntity)player, this.lv);
            player.sendMessage(Text.of("Your LOVE increased to " + this.lv));
            player.getWorld().playSoundFromEntity(null, player, SoulForgeSounds.UT_LEVEL_UP_EVENT, SoundCategory.PLAYERS, 1f, 1f);
        }
        updateAbilities();
        updateModes();
        updateTags();
        sync();
    }

    @Override
    public int getExpRequirement() {
        if (lv >= 20) return -1;
        return MathHelper.floor(10000f*(lv/20f)*(lv/20f));
    }

    @Override
    public float getMagic() {
        return magic;
    }

    @Override
    public void setMagic(float magic) {
        this.magic = Math.min(Math.max(magic, 0), 100);
    }

    @Override
    public List<AbilityBase> getModeAbilities() {
        return Traits.getModeAbilities(mode, this);
    }

    @Override
    public List<AbilityBase> getAbilities() { return List.copyOf(this.abilities.values()); }

    @Override
    public List<AbilityBase> getActiveAbilities() {
        return List.copyOf(this.activeAbilities.values());
    }

    @Override
    public AbilityBase getAbility(String abilityName) {
        if (this.abilities.containsKey(abilityName)) {
            return this.abilities.get(abilityName);
        }
        return null;
    }

    @Override
    public boolean onCooldown(AbilityBase ability) {
        return cooldowns.containsKey(ability);
    }

    @Override
    public boolean onCooldown(String abilityName) {
        for (AbilityBase ability : cooldowns.keySet()) {
            if (Objects.equals(ability.getName(), abilityName)) return true;
        }
        return false;
    }

    @Override
    public float cooldownPercent(AbilityBase ability) {
        if (cooldowns.containsKey(ability)) {
            float cooldownVal = ability.getCooldown();
            if (ability instanceof Determine && hasValue("determineCooldown")) cooldownVal = getValue("determineCooldown");
            if (pure) cooldownVal /= 2;
            if (hasCast("Valiant Heart")) cooldownVal /= 1.33f;
            if (ability instanceof EnergyWave && hasValue("energyWaveCooldown")) cooldownVal = getValue("energyWaveCooldown");
            return 1f - MathHelper.clamp(cooldowns.get(ability) / cooldownVal, 0f, 1f);
        }
        return 1f;
    }

    @Override
    public void setCooldown(AbilityBase ability, int cooldown) {
        cooldowns.put(ability, cooldown);
    }

    @Override
    public void setCooldown(String abilityName, int cooldown) {
        if (this.abilities.containsKey(abilityName)) {
            cooldowns.put(this.abilities.get(abilityName), cooldown);
        }
    }

    @Override
    public void onDeath() {
        cooldowns = new HashMap<>();
        if (player instanceof ServerPlayerEntity serverPlayer) {
            for (AbilityBase ability : activeAbilities.values()) {
                ability.end(serverPlayer);
            }
        }
        activeAbilities = new HashMap<>();
        updateTags();
        setMagic(100f);
    }

    @Override
    public int lastCastTime() {
        return lastCastTime;
    }

    @Override
    public void resetLastCastTime() {
        lastCastTime = 0;
    }

    private float manaRegenRate = 0;
    private Vec3d lastPos = Vec3d.ZERO;

    @Override
    public void magicTick() {
        int manaOverloadAmplifier = 0;
        if (player.hasStatusEffect(SoulForgeEffects.MANA_OVERLOAD)) manaOverloadAmplifier = Objects.requireNonNull(player.getStatusEffect(SoulForgeEffects.MANA_OVERLOAD)).getAmplifier();
        if (lastCastTime/20f > (5f*(manaOverloadAmplifier+1))/Math.ceil(lv/5f)) {
            float moveDist = (float) player.getPos().distanceTo(lastPos);
            if ((lastCastTime%(60-lv*2) == 0 && moveDist < 0.01f) || (lastCastTime%(120-lv*4) == 0 && moveDist >= 0.01f) && manaOverloadAmplifier < 4) {
                manaRegenRate += 1/20f - manaOverloadAmplifier/80f;
            }
        } else {
            manaRegenRate = 0;
        }
        if (this.activeAbilities.containsKey("Perfected Aura Technique")) {
            if (((PerfectedAuraTechnique)this.activeAbilities.get("Perfected Aura Technique")).fullPower) {
                manaRegenRate = 2f;
            }
        }
        setMagic(magic + manaRegenRate);
        lastCastTime++;
        lastPos = player.getPos();
    }

    @Override
    public boolean hasCast(String abilityName) {
        return this.activeAbilities.containsKey(abilityName);
    }

    @Override
    public boolean hasAbility(String abilityName) {
        return this.abilities.containsKey(abilityName);
    }

    @Override
    public boolean hasWeapon() {
        return !getWeapon().isEmpty();
    }

    @Override
    public ItemStack getWeapon() {
        if (this.weapon == null) return ItemStack.EMPTY;
        return this.weapon;
    }

    @Override
    public void setWeapon(ItemStack weapon) {
        setWeapon(weapon, true);
    }

    @Override
    public void removeWeapon() {
        removeWeapon(true);
    }

    @Override
    public void setWeapon(ItemStack weapon, boolean sound) {
        if (sound) {
            player.getWorld().playSoundFromEntity(null, player, SoulForgeSounds.WEAPON_SUMMON_EVENT, SoundCategory.PLAYERS, 1f, 1f);
        }
        this.weapon = weapon;
    }

    @Override
    public void removeWeapon(boolean sound) {
        if (hasWeapon()) {
            if (getWeapon().isOf(SoulForgeItems.BFRCMG)) {
                setCooldown("BFRCMG", 100);
            }
        }
        if (sound) player.getWorld().playSoundFromEntity(null, player, SoulForgeSounds.WEAPON_UNSUMMON_EVENT, SoundCategory.PLAYERS, 1f, 1f);
        this.weapon = ItemStack.EMPTY;
    }


    @Override
    public HashMap<String, Integer> getMonsterSouls() {
        return monsterSouls;
    }
    @Override
    public HashMap<String, Integer> getPlayerSouls() {
        return playerSouls;
    }

    @Override
    public void addMonsterSoul(String type, int amount) {
        if (monsterSouls.containsKey(type)) monsterSouls.put(type, monsterSouls.get(type)+amount);
        else monsterSouls.put(type, amount);
        SoulForgeCriterions.MONSTER_SOUL.trigger((ServerPlayerEntity)player, monsterSouls.get(type), type);
    }

    @Override
    public void addMonsterSoul(Entity entity, int amount) {
        addMonsterSoul(entity.getType().getUntranslatedName(), amount);
    }

    @Override
    public void addPlayerSoul(String playerName, int amount) {
        if (playerSouls.containsKey(playerName)) playerSouls.put(playerName, playerSouls.get(playerName)+amount);
        else playerSouls.put(playerName, amount);
    }

    @Override
    public int getSoulCount(String type) {
        if (!monsterSouls.containsKey(type)) return 0;
        return monsterSouls.get(type);
    }

    @Override
    public boolean canReset() {
        int hostileCount = 0;
        for (Map.Entry<String, Integer> entry : monsterSouls.entrySet()) {
            if (Constants.hostiles.contains(entry.getKey())) {
                if (entry.getValue() > 0) hostileCount++;
            }
        }
        if (hostileCount >= 7) {
            for (int i = 0; i < player.getInventory().size(); i++) {
                ItemStack stack = player.getInventory().getStack(i);
                if (stack.getItem() == SoulForgeItems.DETERMINATION_ARNICITE_HEART) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public AbilityLayout getAbilityLayout() {
        return abilityLayout;
    }

    @Override
    public AbilityBase getLayoutAbility(int row, int column) {
        return abilityLayout.getSlot(row, column);
    }

    @Override
    public AbilityLayout.AbilityRow getLayoutRow(int row) {
        return abilityLayout.rows.get(row);
    }

    @Override
    public void setAbilityLayout(AbilityLayout layout) {
        abilityLayout = layout;
    }

    @Override
    public void setLayoutAbility(AbilityBase ability, int row, int column) {
        abilityLayout.setSlot(ability, row, column);
    }

    @Override
    public int getAbilityRow() {
        return abilityRow;
    }

    @Override
    public void setAbilityRow(int i) {
        abilityRow = MathHelper.clamp(i, 0, 3);
    }

    @Override
    public int getAbilitySlot() {
        return abilitySlot;
    }

    @Override
    public void setAbilitySlot(int i) {
        abilitySlot = MathHelper.clamp(i, 0, 9);
    }

    @Override
    public void toggleMagicMode() {
        magicMode = !magicMode;
    }

    @Override
    public boolean magicModeActive() {
        return magicMode;
    }

    @Override
    public void sync() {
        SoulComponent.sync(this.player);
    }

    @Override
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
            buf.writeFloat(magic);

            buf.writeVarInt(activeAbilities.size());
            for (AbilityBase active : activeAbilities.values()) {
                buf.writeString(active.getName());
                buf.writeNbt(active.saveNbt(new NbtCompound()));
            }

            buf.writeVarInt(cooldowns.size());
            for (Map.Entry<AbilityBase, Integer> cooldown : cooldowns.entrySet()) {
                buf.writeString(cooldown.getKey().getName());
                buf.writeVarInt(cooldown.getValue());
            }

            buf.writeString(mode);

            buf.writeItemStack(weapon);

            buf.writeString(String.join(",", tags));
            buf.writeVarInt(values.size());
            for (Map.Entry<String, Float> value : values.entrySet()) {
                buf.writeString(value.getKey());
                buf.writeFloat(value.getValue());
            }
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
            buf.writeVarInt(abilityRow);
            buf.writeBoolean(magicMode);

            resetData.writeBuf(buf);

            buf.writeBoolean(disguisedAsID != null);
            if (disguisedAsID != null) buf.writeUuid(disguisedAsID);

            return buf;
        } catch (Exception e) {
            return null;
        }
    }

    public static SoulComponent fromBuffer(PlayerEntity player, PacketByteBuf buf) {
        int traitCount = buf.readVarInt();
        List<TraitBase> traits = new ArrayList<>(List.of(Objects.requireNonNull(Traits.get(buf.readString()))));
        if (traitCount == 2) traits.add(Traits.get(buf.readString()));
        boolean strong = buf.readBoolean();
        boolean pure = buf.readBoolean();
        int lv = buf.readVarInt();
        int exp = buf.readVarInt();
        float magic = buf.readFloat();
        HashMap<String, AbilityBase> abilities = new HashMap<>();
        for (AbilityBase ability : Traits.getAbilities(traits, lv, pure)) {
            abilities.put(ability.getName(), ability);
        }

        int activeCount = buf.readVarInt();
        HashMap<String, NbtCompound> actives = new HashMap<>();
        for (int i = 0; i < activeCount; i++) {
            actives.put(buf.readString(), buf.readNbt());
        }
        int cooldownCount = buf.readVarInt();
        HashMap<String, Integer> cooldownNames = new HashMap<>();
        for (int i = 0; i < cooldownCount; i++) cooldownNames.put(buf.readString(), buf.readVarInt());

        HashMap<String, AbilityBase> activeAbilities = new HashMap<>();
        HashMap<AbilityBase, Integer> cooldowns = new HashMap<>();
        for (String abilityName : actives.keySet()) {
            if (abilities.containsKey(abilityName)) {
                abilities.get(abilityName).readNbt(actives.get(abilityName));
                activeAbilities.put(abilityName, abilities.get(abilityName));
            }
        }
        for (String cooldown : cooldownNames.keySet()) {
            if (abilities.containsKey(cooldown)) {
                cooldowns.put(abilities.get(cooldown), cooldownNames.get(cooldown));
            }
        }

        String mode = buf.readString();

        ItemStack weapon = buf.readItemStack();

        List<String> tags = new ArrayList<>(Arrays.asList(buf.readString().split(",")));

        int valueCount = buf.readVarInt();
        HashMap<String, Float> values = new HashMap<>();
        for (int i = 0; i < valueCount; i++) values.put(buf.readString(), buf.readFloat());

        int lastCastTime = buf.readVarInt();

        int discoveredCount = buf.readVarInt();
        List<AbilityBase> discovered = new ArrayList<>();
        for (int i = 0; i < discoveredCount; i++) {
            String id = buf.readString();
            try {
                discovered.add(Abilities.get(new Identifier(SoulForge.MOD_ID, id)));
            } catch (InvalidIdentifierException e) {
                SoulForge.LOGGER.warn("Received invalid ability identifier: " + id + ". Continuing...");
            }
        }

        int soulCount = buf.readVarInt();
        HashMap<String, Integer> monsterSouls = new HashMap<>();
        for (int i = 0; i < soulCount; i++) monsterSouls.put(buf.readString(), buf.readVarInt());

        soulCount = buf.readVarInt();
        HashMap<String, Integer> playerSouls = new HashMap<>();
        for (int i = 0; i < soulCount; i++) playerSouls.put(buf.readString(), buf.readVarInt());

        AbilityLayout layout = AbilityLayout.fromBuf(List.copyOf(abilities.values()), buf);
        int row = buf.readVarInt();
        boolean magicMode = buf.readBoolean();

        ResetData resetData = ResetData.fromBuf(buf);

        UUID disguisedAsID = null;
        if (buf.readBoolean()) {
            disguisedAsID = buf.readUuid();
        }

        return new PlayerSoulComponent(player, traits, strong, pure, lv, exp, magic, abilities, activeAbilities, cooldowns, mode, weapon, tags, values, lastCastTime, discovered, monsterSouls, playerSouls, layout, row, magicMode, resetData, disguisedAsID);
    }

    @Override
    public void castAbility(int index) {
        List<AbilityBase> rowAbilities = getLayoutRow(getAbilityRow()).abilities;
        if (index <= 8 && index >= 0) {
            AbilityBase toCast = rowAbilities.get(index);
            castAbility(toCast);
        } else {
            SoulForge.LOGGER.info("Attempted to cast an ability that the player doesn't have!");
        }
    }

    @Override
    public boolean hasDiscovered(AbilityBase ability) {
        return discovered.contains(ability);
    }

    @Override
    public void discover(AbilityBase ability) {
        if (!discovered.contains(ability)) discovered.add(ability);
    }

    @Override
    public void undiscover(AbilityBase ability) {
        discovered.remove(ability);
    }

    @Override
    public List<AbilityBase> getDiscovered() {
        return discovered;
    }

    @Override
    public void clearDiscovered() {
        discovered.clear();
    }

    @Override
    public boolean isStrong() { return strong; }
    @Override
    public boolean isPure() { return pure; }
    @Override
    public void setStrong(boolean strong) {
        this.strong = strong;
    }
    @Override
    public void setPure(boolean pure) {
        if (this.pure) this.strong = true;
        this.pure = pure;
        updateAbilities();
    }

    @Override
    public SpokenTextRenderer getSpokenTextRenderer() {
        return spokenTextRenderer;
    }

    @Override
    public void setSpokenText(String text, int speed, int timeToDisappear) {
        spokenTextRenderer.setText(text, speed, timeToDisappear);
        if (!player.getWorld().isClient) ServerPlayNetworking.send((ServerPlayerEntity)player, SoulForgeNetworking.SET_SPOKEN_TEXT, PacketByteBufs.create().writeString(text).writeVarInt(speed).writeVarInt(timeToDisappear));
    }

    @Override
    public void setSpokenText(String text) {
        spokenTextRenderer.setText(text);
        if (!player.getWorld().isClient) ServerPlayNetworking.send((ServerPlayerEntity)player, SoulForgeNetworking.SET_SPOKEN_TEXT, PacketByteBufs.create().writeString(text).writeVarInt(4).writeVarInt(40));
    }

    @Override
    public String getSpokenText() {
        return spokenTextRenderer.toRender();
    }

    @Override
    public void createWormholeRequest(PlayerEntity from) {
        if (from == null) wormholeRequest = null;
        else wormholeRequest = new Pair<>(from.getUuid(), from.getServer().getTicks());
        sync();
    }

    @Override
    public void removeWormholeRequest() {
        wormholeRequest = null;
        sync();
    }

    @Override
    @Nullable
    public Pair<UUID, Integer> getWormholeRequest() {
        return wormholeRequest;
    }

    @Override
    public PlayerEntity getWormholeTarget() {
        if (wormholeRequest != null) {
            return player.getWorld().getPlayerByUuid(wormholeRequest.getLeft());
        }
        return null;
    }

    @Override
    public int getWormholeTime() {
        if (wormholeRequest != null) {
            return wormholeRequest.getRight();
        }
        return 0;
    }

    @Override
    public boolean hasWormholeRequest() {
        return wormholeRequest != null;
    }

    @Override
    public void setDisguise(PlayerEntity target) {
        disguisedAs = target;
        disguisedAsID = target.getUuid();
    }

    @Override
    public void removeDisguise() {
        disguisedAs = null;
        disguisedAsID = null;
    }

    @Override
    public PlayerEntity getDisguise() {
        return disguisedAs;
    }

    @Override
    public void castAbility(AbilityBase ability) {
        if (ability == null) return;
        boolean isDetermined = false;
        Determine determine = null;
        boolean contains = abilities.containsKey(ability.getName());
        if (ability instanceof Determine && !determining) {
            if (!player.getWorld().isClient) {
                if (((Determine) ability).selected != null) {
                    determine = (Determine) ability;
                    if (determinedAbility != null && determinedAbility.getType() == determine.selected.getType()) {
                        ability = determinedAbility;
                    } else {
                        ability = determine.selected.getInstance();
                    }
                    isDetermined = true;
                    SoulForge.LOGGER.info("determining: " + determine.selected);
                }
            }
        }
        for (AbilityBase rowAbility : getLayoutRow(getAbilityRow()).abilities) {
            if (rowAbility == null) continue;
            if (rowAbility == ability) {
                contains = true;
                break;
            }
        }
        if ((contains || isDetermined || ability.getType() == AbilityType.PASSIVE) && ability.getType() != AbilityType.PASSIVE_NOCAST) {
            float cost = ability.getCost();
            if (player.getAttributeInstance(SoulForgeAttributes.MAGIC_COST) != null) {
                cost *= (float)player.getAttributeInstance(SoulForgeAttributes.MAGIC_COST).getValue();
            }
            if (strong && !getTraits().contains(Traits.determination)) cost /= 2f;
            if (hasCast("Valiant Heart")) cost /= 1.33f;
            if (ability instanceof ToggleableAbilityBase toggleable && toggleable.getActive()) cost = 0;
            if (cost <= magic && ((determine != null && !cooldowns.containsKey(determine)) || !cooldowns.containsKey(ability))) {
                boolean canCast = ability.cast((ServerPlayerEntity)player);
                if (canCast && !(ability instanceof Determine)) {
                    SoulForgeCriterions.CAST_ABILITY.trigger((ServerPlayerEntity) player, ability);
                    if (ability instanceof ToggleableAbilityBase toggleable) {
                        if (toggleable.getActive()) {
                            magic -= cost;
                            if (isDetermined) determinedAbility = ability;
                            else activeAbilities.put(ability.getName(), ability);
                            resetLastCastTime();
                        } else {
                            float cooldown = ability.getCooldown();
                            if (player.getAttributeInstance(SoulForgeAttributes.MAGIC_COOLDOWN) != null) cooldown = (int)(cooldown * player.getAttributeInstance(SoulForgeAttributes.MAGIC_COOLDOWN).getValue());
                            if (pure) cooldown /= 2;
                            if (hasCast("Valiant Heart")) cooldown /= 1.33f;
                            if (determine != null) {
                                cooldowns.put(determine, (int)cooldown);
                                setValue("determineCooldown", cooldown);
                                determining = true;
                            }
                            else cooldowns.put(ability, (int)cooldown);
                        }
                    } else {
                        magic -= cost;
                        float cooldown = ability.getCooldown();
                        if (player.getAttributeInstance(SoulForgeAttributes.MAGIC_COOLDOWN) != null) cooldown = (int)(cooldown * player.getAttributeInstance(SoulForgeAttributes.MAGIC_COOLDOWN).getValue());
                        if (pure) cooldown /= 2;
                        if (hasCast("Valiant Heart")) cooldown /= 1.33f;
                        if (determine != null) {
                            cooldowns.put(determine, (int)cooldown);
                            setValue("determineCooldown", cooldown);
                            determinedAbility = ability;
                            determining = true;
                        } else {
                            cooldowns.put(ability, (int) cooldown);
                            activeAbilities.put(ability.getName(), ability);
                        }
                        resetLastCastTime();
                    }
                    sync();
                }
            }
        } else {
            SoulForge.LOGGER.info("Attempted to cast an ability that the player doesn't have!");
        }
    }

    private void decreaseTimer(String value) {
        if (hasValue(value)) {
            if (getValue(value) > 0) setValue(value, (int) getValue(value) - 1);
        }
    }

    @Override
    public void tick() {
        if (player instanceof ServerPlayerEntity) {
            HashMap<AbilityBase, Integer> newCooldowns = new HashMap<>();
            for (AbilityBase ability : cooldowns.keySet()) {
                if (cooldowns.get(ability) > 1) {
                    newCooldowns.put(ability, cooldowns.get(ability) - 1);
                }
            }
            cooldowns = newCooldowns;
            for (AbilityBase ability : List.copyOf(activeAbilities.values())) {
                if (ability instanceof Determine) continue;
                if (ability.tick((ServerPlayerEntity)player)) {
                    ability.end((ServerPlayerEntity)player);
                    if (ability.getType() == AbilityType.TOGGLE) {
                        float cooldown = ability.getCooldown();
                        if (pure) cooldown /= 2;
                        if (hasCast("Valiant Heart")) cooldown /= 1.33f;
                        cooldowns.put(ability, (int)cooldown);
                    }
                    activeAbilities.remove(ability.getName());
                }
            }
            if (determinedAbility != null && player instanceof ServerPlayerEntity serverPlayer) {
                if (abilities.containsKey("Determine")) {
                    if (determinedAbility.tick(serverPlayer)) {
                        determinedAbility.end(serverPlayer);
                        SoulForge.LOGGER.info("ending: " + ((Determine)abilities.get("Determine")).selected);
                        abilities.get("Determine").end(serverPlayer);
                        //((Determine) ability).selected = null;
                        if (determinedAbility.getType() == AbilityType.TOGGLE) {
                            float cooldown = determinedAbility.getCooldown();
                            if (pure) cooldown /= 2;
                            if (hasCast("Valiant Heart")) cooldown /= 1.33f;
                            cooldowns.put(abilities.get("Determine"), (int)cooldown);
                        }
                        determinedAbility = null;
                        determining = false;
                    }
                }
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
            decreaseTimer("parry");
            decreaseTimer("parryCooldown");
            decreaseTimer("overchargeCooldown");
            decreaseTimer("dtWeaponCooldown");
            decreaseTimer("shieldBashCooldown");
            decreaseTimer("stockpileTimer");
            decreaseTimer("slamJumpTimer");
            if (hasValue("stockpileTimer")) {
                if (getValue("stockpileTimer") <= 0 && hasValue("stockpiles")) {
                    setValue("stockpiles", 0);
                }
            }
            if (hasValue("stockpiles")) {
                if (getValue("stockpiles") == 8) {
                    setValue("stockpiles", 0);
                    setValue("stockpileTimer", 0);
                    if (abilities.containsKey("Perfected Aura Technique")) {
                        PerfectedAuraTechnique pat = (PerfectedAuraTechnique)abilities.get("Perfected Aura Technique");
                        pat.fullPower = true;
                        pat.timer = 5020;
                        SoulForgeCriterions.CAST_ABILITY.trigger((ServerPlayerEntity) player, pat);
                    }
                }
            }
            if (hasValue("shieldBash")) {
                if (getValue("shieldBash") > 0) {
                    player.setInvulnerable(true);
                    setValue("shieldBash", (int)getValue("shieldBash") - 1);
                    if (getValue("shieldBash") <= 0) {
                        player.setInvulnerable(false);
                        setValue("shieldBashCooldown", 60);
                    }
                    for (Entity target : player.getEntityWorld().getOtherEntities(player, Box.of(player.getPos().add(0f, 1f, 0f), 1, 2, 1))) {
                        if (target instanceof LivingEntity living) {
                            living.damage(player.getDamageSources().playerAttack(player), 2f);
                            living.takeKnockback(1.5f, -player.getVelocity().x, -player.getVelocity().z);
                        }
                    }
                }
            }
            if (hasValue("clawGouge")) {
                if (getValue("clawGouge") > 0) {
                    setValue("clawGouge", (int)getValue("clawGouge") - 1);
                    if (getValue("clawGouge") == 15) {
                        removeTag("immobile");
                        Vec3d velAdd = player.getRotationVector().withAxis(Direction.Axis.Y, 0).normalize().multiply(2f);
                        player.addVelocity(velAdd);
                        player.velocityModified = true;
                    }
                    if (getValue("clawGouge") <= 15) {
                        for (Entity target : player.getEntityWorld().getOtherEntities(player, Box.of(player.getPos().add(0f, 1f, 0f), 1, 2, 1))) {
                            if (target instanceof LivingEntity living) {
                                living.damage(player.getDamageSources().playerAttack(player), (float)(player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE)*1.5f));
                                player.setVelocity(Vec3d.ZERO);
                                player.velocityModified = true;
                                if (living instanceof PlayerEntity) {
                                    if (getMagic() >= 40) {
                                        setMagic(getMagic() - 40f);
                                        SoulComponent targetSoul = SoulForge.getPlayerSoul((PlayerEntity)living);
                                        Utils.addAntiheal(0.8f, getLV()*40f, targetSoul);
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
            }
            if (hasValue("dtGauntletsRush")) {
                if (getValue("dtGauntletsRush") > 0) {
                    setValue("dtGauntletsRush", (int)getValue("dtGauntletsRush") - 1);
                    for (Entity target : player.getEntityWorld().getOtherEntities(player, Box.of(player.getPos().add(0f, 1f, 0f), 1, 2, 1))) {
                        if (target instanceof LivingEntity living) {
                            living.getWorld().createExplosion(player, living.getX(), living.getY(), living.getZ(), 1.5f, World.ExplosionSourceType.NONE);
                            setValue("dtGauntletsRush", 0);
                            player.setVelocity(0f, 0f, 0f);
                            player.velocityModified = true;
                            break;
                        }
                    }
                }
            }
            if (hasValue("yoyoAoETimer")) {
                if (getValue("yoyoAoETimer") > 0) {
                    for (Entity entity : player.getWorld().getOtherEntities(player, Box.of(player.getPos(), 4, 4, 4))) {
                        if (entity instanceof LivingEntity living) {
                            living.damage(player.getDamageSources().playerAttack(player), 12f);
                        }
                    }
                    if (player instanceof ServerPlayerEntity serverPlayer) {
                        float angle = getValue("yoyoAoETimer");
                        Vec3d particlePos = new Vec3d(Math.sin(angle), 1f, Math.cos(angle));
                        serverPlayer.getServerWorld().spawnParticles(ParticleTypes.SWEEP_ATTACK, player.getX() + particlePos.x, player.getY() + particlePos.y, player.getZ() + particlePos.z, 2, 0, 0, 0, 0);
                    }
                    setValue("yoyoAoETimer", getValue("yoyoAoETimer") - 1);
                    setValue("yoyoSpin", 0);
                }
            }
            if (hasValue("yoyoSpin")) {
                if (getValue("yoyoSpin") > 0) {
                    float angle = (20-getValue("yoyoSpin"))/20f * MathHelper.PI;
                    Vec3d start = new Vec3d(getValue("startX"), getValue("startY"), getValue("startZ"));
                    Vec3d center = new Vec3d(getValue("centerX"), getValue("centerY"), getValue("centerZ"));
                    Vec3d p = start.subtract(center);
                    Vec3d axis = new Vec3d(getValue("axisX"), getValue("axisY"), getValue("axisZ")).normalize();
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
                    setValue("yoyoSpin", getValue("yoyoSpin") - 1);
                }
            }

            if (this.disguisedAsID != null) this.disguisedAs = player.getWorld().getPlayerByUuid(disguisedAsID);

            //behold, my antiheal conglomeration
            if (hasValue("antiheal")) {
                if (hasValue("antihealDuration")) {
                    setValue("antihealDuration", getValue("antihealDuration") - 1);
                    if (getValue("antihealDuration") <= 0) setValue("antiheal", 0);
                } else setValue("antiheal", 0);
            } else if (hasValue("antihealDuration")) setValue("antihealDuration", 0);

            if (getTraits().contains(Traits.perseverance)) {
                if (player.getMainHandStack().isOf(SoulForgeItems.PERSEVERANCE_CLAW)) setValue("shieldBreak", 3f);
                else setValue("shieldBreak", 2f);
            } else {
                setValue("shieldBreak", 1f);
            }

            if (getTraits().contains(Traits.determination)) {
                Utils.clearModifiersByName(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "limit_break");
                EntityAttributeModifier strengthModifier = new EntityAttributeModifier("limit_break", 0.5*((player.getMaxHealth()-player.getHealth())/player.getMaxHealth()), EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
                player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).addPersistentModifier(strengthModifier);
            }
        }
        spokenTextRenderer.tick();
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag) {
        if (player == null) {
            SoulForge.LOGGER.error("Player was null in PlayerSoulComponent while attempting to read from NBT!");
            return;
        }

        traits = new ArrayList<>();
        traits.add(Traits.get(tag.getString("trait1")));
        if (tag.contains("trait2")) traits.add(Traits.get(tag.getString("trait2")));

        lv = tag.getInt("lv");
        exp = tag.getInt("exp");
        magic = tag.getFloat("magic");
        strong = tag.getBoolean("strong");
        pure = tag.getBoolean("pure");
        abilities = new HashMap<>();
        for (AbilityBase ability : Traits.getAbilities(traits, lv, pure)) {
            abilities.put(ability.getName(), ability);
        }

        discovered = new ArrayList<>();
        List<String> discoveredIds = List.of(tag.getString("discovered").split(","));
        for (String id : discoveredIds) {
            if (!id.equals("") && !discovered.contains(Abilities.get(new Identifier(SoulForge.MOD_ID, id)))) discovered.add(Abilities.get(new Identifier(SoulForge.MOD_ID, id)));
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

        NbtList abilityNbt = tag.getList("abilityNbt", NbtElement.COMPOUND_TYPE);
        if (abilityNbt != null) {
            for (int i = 0; i < abilityNbt.size(); i++) {
                NbtCompound nbt = abilityNbt.getCompound(i);
                if (abilities.containsKey(nbt.getString("name"))) {
                    abilities.get(nbt.getString("name")).readNbt(nbt);
                    activeAbilities.put(nbt.getString("name"), abilities.get(nbt.getString("name")));
                }
            }
        }

        abilityLayout = AbilityLayout.fromNbt(List.copyOf(abilities.values()), tag.getList("abilityLayout", NbtElement.COMPOUND_TYPE));
        abilityRow = tag.getInt("abilityRow");
        magicMode = tag.getBoolean("magicMode");

        resetData = new ResetData(tag.getCompound("resetData"));

        if (tag.contains("wormhole")) {
            NbtCompound wormhole = tag.getCompound("wormhole");
            wormholeRequest = new Pair<>(wormhole.getUuid("target"), wormhole.getInt("time"));
        }

        if (tag.contains("disguisedAs")) disguisedAsID = tag.getUuid("disguisedAs");

        updateModes();
        updateTags();
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag) {
        tag.putString("trait1", traits.get(0).getName());
        if (traits.size() == 2) tag.putString("trait2", traits.get(1).getName());
        tag.putInt("lv", lv);
        tag.putInt("exp", exp);
        tag.putFloat("magic", magic);
        tag.putBoolean("strong", strong);
        tag.putBoolean("pure", pure);
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
        NbtList abilityNbt = new NbtList();
        for (AbilityBase ability : activeAbilities.values()) {
            NbtCompound nbt = new NbtCompound();
            nbt = ability.saveNbt(nbt);
            abilityNbt.add(nbt);
        }
        tag.put("abilityNbt", abilityNbt);
        tag.put("abilityLayout", abilityLayout.toNbt());
        tag.putInt("abilityRow", abilityRow);
        tag.putBoolean("magicMode", magicMode);
        tag.put("resetData", resetData.toNBT());
        if (wormholeRequest != null) {
            NbtCompound wormhole = new NbtCompound();
            wormhole.putUuid("target", wormholeRequest.getLeft());
            wormhole.putInt("time", wormholeRequest.getRight());
            tag.put("wormhole", wormhole);
        }
        if (disguisedAsID != null) tag.putUuid("disguisedAs", disguisedAsID);
    }

    @Override
    public String toString() {
        String str = "Trait: " + (traits.size() == 2 ? traits.get(0).getName() + "-" + traits.get(1).getName() : traits.get(0).getName());
        str += ", LV: " + lv + ", EXP: " + exp;
        return str;
    }

    @Override
    public void reset() {
        resetTrait();
        lv = 1;
        exp = 0;
        magic = 100f;
        monsterSouls = new HashMap<>();
        playerSouls = new HashMap<>();
        removeWeapon(false);
        abilityLayout = new AbilityLayout();
        for (AbilityBase ability : activeAbilities.values()) {
            ability.end((ServerPlayerEntity)player);
        }
        activeAbilities = new HashMap<>();
        updateAbilities();
        updateModes();
        updateTags();
        sync();
    }

    @Override
    public void softReset() {
        lv = 1;
        exp = 0;
        magic = 100f;
        monsterSouls = new HashMap<>();
        playerSouls = new HashMap<>();
        removeWeapon(false);
        abilityLayout = new AbilityLayout();
        for (AbilityBase ability : activeAbilities.values()) {
            ability.end((ServerPlayerEntity)player);
        }
        activeAbilities = new HashMap<>();
        updateAbilities();
        updateModes();
        updateTags();
        sync();
    }

    private void updateAbilities() {
        abilities = new HashMap<>();
        for (AbilityBase ability : Traits.getAbilities(traits, lv, pure)) {
            abilities.put(ability.getName(), ability);
            discover(ability);
        }
    }

    private void updateModes() {
        modes = new ArrayList<>();
        modes.add(traits.get(0).getName());
        if (traits.size() == 2) modes.add(traits.get(1).getName());
        boolean hasPassives = false;
        for (AbilityBase ability : abilities.values()) {
            if (ability.getType() == AbilityType.PASSIVE || ability.getType() == AbilityType.PASSIVE_NOCAST) {
                hasPassives = true;
            }
        }
        if (!Constants.getDualTraitAbilities(traits).isEmpty()) modes.add("Duals");
        if (hasPassives) modes.add("Passives");
        if (mode == null || !modes.contains(mode)) mode = modes.get(0);
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
        if (player instanceof ServerPlayerEntity) {
            SoulForgeCriterions.PLAYER_TRAIT.trigger((ServerPlayerEntity) player, this);
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

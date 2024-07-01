package com.pulsar.soulforge.components;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.Abilities;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.ability.determination.DeterminationPlatform;
import com.pulsar.soulforge.ability.duals.PerfectedAuraTechnique;
import com.pulsar.soulforge.ability.integrity.Platforms;
import com.pulsar.soulforge.ability.justice.Launch;
import com.pulsar.soulforge.advancement.SoulForgeCriterions;
import com.pulsar.soulforge.armor.PlatformBootsItem;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.client.networking.PlayerSoulPacket;
import com.pulsar.soulforge.client.networking.SetSpokenTextPacket;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.entity.BlastEntity;
import com.pulsar.soulforge.entity.DeterminationPlatformEntity;
import com.pulsar.soulforge.entity.IntegrityPlatformEntity;
import com.pulsar.soulforge.event.EventType;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.item.weapons.MagicSwordItem;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.tag.SoulForgeTags;
import com.pulsar.soulforge.trait.TraitBase;
import com.pulsar.soulforge.trait.Traits;
import com.pulsar.soulforge.util.Constants;
import com.pulsar.soulforge.util.ResetData;
import com.pulsar.soulforge.util.SpokenTextRenderer;
import com.pulsar.soulforge.util.Utils;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
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

public class PlayerSoulComponent implements SoulComponent {
    private List<TraitBase> traits;
    private boolean strong;
    private boolean pure;
    private int lv;
    private int exp;
    private float magic;
    private AbilityList abilities = new AbilityList();
    private List<String> tags = new ArrayList<>();
    private final HashMap<String, Float> values = new HashMap<>();
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

    public PlayerSoulComponent(PlayerEntity player) {
        this.player = player;
        resetTrait();
        lv = 1;
        exp = 0;
        magic = 100;
        updateAbilities();
        updateTags();
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
        if (num == 1 && trait == null) traits = new ArrayList<>(List.of(traits.getFirst()));
        abilityLayout = new AbilityLayout();
        for (AbilityBase ability : abilities.getAllActive()) {
            ability.end((ServerPlayerEntity)player);
        }
        if (pure) resetData.addPure(traits.get(0));
        if (traits.size() >= 2) resetData.addDual(traits.get(0), traits.get(1));
        updateAbilities();
        updateTags();
        SoulForgeCriterions.PLAYER_TRAIT.trigger((ServerPlayerEntity) player, this);
        sync();
    }

    @Override
    public void setTraits(List<TraitBase> traits) {
        this.traits = traits;
        abilityLayout = new AbilityLayout();
        for (AbilityBase ability : abilities.getAllActive()) {
            ability.end((ServerPlayerEntity)player);
        }
        if (pure) resetData.addPure(traits.get(0));
        if (traits.size() >= 2) resetData.addDual(traits.get(0), traits.get(1));
        updateAbilities();
        updateTags();
        SoulForgeCriterions.PLAYER_TRAIT.trigger((ServerPlayerEntity) player, this);
        sync();
    }

    @Override
    public void setResetValues(List<TraitBase> traits, boolean strong, boolean pure) {
        this.traits = traits;
        abilityLayout = new AbilityLayout();
        for (AbilityBase ability : abilities.getAllActive()) {
            ability.end((ServerPlayerEntity)player);
        }
        if (traits.size() >= 2) resetData.addDual(traits.get(0), traits.get(1));
        this.strong = strong;
        this.pure = pure;
        updateAbilities();
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
                    if (abilities.contains(new Launch()) && abilities.get(new Launch()).isActive()) {
                        abilities.get(new Launch()).end(serverPlayerEntity);
                        break;
                    }
                    break;
                case SPAWN_PLATFORM:
                    boolean hasDT = false;
                    boolean hasIG = false;
                    for (AbilityBase ability : abilities.getAllActive()) {
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
                                if (abilities.contains(new Platforms()) && abilities.get(new Platforms()).isActive()) {
                                    abilities.get(new Platforms()).spawn(player);
                                }
                            } else {
                                if (abilities.contains(new DeterminationPlatform()) && abilities.get(new DeterminationPlatform()).isActive()) {
                                    abilities.get(new DeterminationPlatform()).spawn(player);
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
        if (player.getAttributeInstance(SoulForgeAttributes.MAGIC_POWER) != null) multiplier = (float) Objects.requireNonNull(player.getAttributeInstance(SoulForgeAttributes.MAGIC_POWER)).getValue();
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
    public List<AbilityBase> getAbilities() { return List.copyOf(this.abilities.getAll()); }

    @Override
    public List<AbilityBase> getActiveAbilities() {
        return List.copyOf(this.abilities.getAllActive());
    }

    @Override
    public AbilityBase getAbility(String abilityName) {
        return this.abilities.get(abilityName);
    }

    @Override
    public <T extends AbilityBase> T getAbility(T ability) {
        return this.abilities.get(ability);
    }

    @Override
    public boolean onCooldown(AbilityBase ability) {
        return this.abilities.get(ability).isOnCooldown(Objects.requireNonNull(player.getServer()).getTicks());
    }

    @Override
    public boolean onCooldown(String abilityName) {
        return this.abilities.get(abilityName).isOnCooldown(Objects.requireNonNull(player.getServer()).getTicks());
    }

    @Override
    public float cooldownPercent(AbilityBase ability) {
        /*float cooldownVal = ability.getCooldown();
        if (pure) cooldownVal /= 2;
        if (hasCast("Valiant Heart")) cooldownVal /= 1.33f;
        if (ability instanceof EnergyWave && hasValue("energyWaveCooldown")) cooldownVal = getValue("energyWaveCooldown");*/
        return this.abilities.get(ability).getCooldownPercent(Objects.requireNonNull(player.getServer()).getTicks());
    }

    @Override
    public void setCooldown(AbilityBase ability, int lastCast) {
        this.abilities.get(ability).setLastCast(lastCast);
    }

    @Override
    public void setCooldown(String abilityName, int lastCast) {
        this.abilities.get(abilityName).setLastCast(lastCast);
    }

    @Override
    public void onDeath() {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            for (AbilityBase ability : abilities.getAllActive()) {
                ability.end(serverPlayer);
            }
        }
        for (AbilityBase ability : abilities.getAll()) {
            ability.setActive(false);
            ability.setLastCast(0);
        }
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
        if (this.abilities.contains(new PerfectedAuraTechnique())) {
            if (this.abilities.get(new PerfectedAuraTechnique()).fullPower) {
                manaRegenRate = 2f;
            }
        }
        setMagic(magic + manaRegenRate);
        lastCastTime++;
        lastPos = player.getPos();
    }

    @Override
    public boolean hasCast(String abilityName) {
        return this.abilities.get(abilityName).isActive();
    }

    @Override
    public boolean hasAbility(String abilityName) {
        return this.abilities.contains(abilityName);
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
    public PlayerSoulPacket toPacket() {
        return new PlayerSoulPacket(this.exp, this.magic, this.abilityLayout.toNbt(), this.abilityRow, this.magicMode);
    }

    public void fromPacket(PlayerSoulPacket packet) {
        this.exp = packet.exp();
        this.magic = packet.magic();

        this.abilityLayout = AbilityLayout.fromNbt(List.copyOf(abilities.getAll()), packet.abilityLayout());
        this.abilityRow = packet.abilityRow();
        this.magicMode = packet.magicMode();
    }

    @Override
    public void castAbility(int index) {
        List<AbilityBase> rowAbilities = getLayoutRow(getAbilityRow()).abilities;
        if (index <= 8 && index >= 0) {
            AbilityBase toCast = rowAbilities.get(index);
            castAbility(toCast);
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
        if (!player.getWorld().isClient) ServerPlayNetworking.send((ServerPlayerEntity)player, new SetSpokenTextPacket(text, speed, timeToDisappear));
    }

    @Override
    public void setSpokenText(String text) {
        spokenTextRenderer.setText(text);
        if (!player.getWorld().isClient) ServerPlayNetworking.send((ServerPlayerEntity)player, new SetSpokenTextPacket(text, 4, 40));
    }

    @Override
    public String getSpokenText() {
        return spokenTextRenderer.toRender();
    }

    @Override
    public void createWormholeRequest(PlayerEntity from) {
        if (from == null) wormholeRequest = null;
        else wormholeRequest = new Pair<>(from.getUuid(), Objects.requireNonNull(from.getServer()).getTicks());
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
        boolean contains = abilities.contains(ability.getName());
        for (AbilityBase rowAbility : getLayoutRow(getAbilityRow()).abilities) {
            if (rowAbility == null) continue;
            if (rowAbility == ability) {
                contains = true;
                break;
            }
        }
        if ((contains || ability.getType() == AbilityType.PASSIVE) && ability.getType() != AbilityType.PASSIVE_NOCAST) {
            float cost = ability.getCost();
            if (player.getAttributeInstance(SoulForgeAttributes.MAGIC_COST) != null) {
                cost *= (float) Objects.requireNonNull(player.getAttributeInstance(SoulForgeAttributes.MAGIC_COST)).getValue();
            }
            if (strong && !getTraits().contains(Traits.determination)) cost /= 2f;
            if (hasCast("Valiant Heart")) cost /= 1.33f;
            if (ability instanceof ToggleableAbilityBase toggleable && toggleable.isActive()) cost = 0;
            if (cost <= magic && !onCooldown(ability)) {
                boolean canCast = ability.cast((ServerPlayerEntity)player);
                if (canCast) {
                    SoulForgeCriterions.CAST_ABILITY.trigger((ServerPlayerEntity) player, ability);
                    if (ability instanceof ToggleableAbilityBase toggleable) {
                        if (toggleable.isActive()) {
                            magic -= cost;
                            abilities.get(ability).setActive(true);
                            resetLastCastTime();
                        } else {
                            /*float cooldown = ability.getCooldown();
                            if (player.getAttributeInstance(SoulForgeAttributes.MAGIC_COOLDOWN) != null) cooldown = (int)(cooldown * Objects.requireNonNull(player.getAttributeInstance(SoulForgeAttributes.MAGIC_COOLDOWN)).getValue());
                            if (pure) cooldown /= 2;
                            if (hasCast("Valiant Heart")) cooldown /= 1.33f;*/
                            abilities.get(ability).setLastCast(Objects.requireNonNull(player.getServer()).getTicks());
                        }
                    } else {
                        magic -= cost;
                        /*float cooldown = ability.getCooldown();
                        if (player.getAttributeInstance(SoulForgeAttributes.MAGIC_COOLDOWN) != null) cooldown = (int)(cooldown * player.getAttributeInstance(SoulForgeAttributes.MAGIC_COOLDOWN).getValue());
                        if (pure) cooldown /= 2;
                        if (hasCast("Valiant Heart")) cooldown /= 1.33f;*/
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
            for (AbilityBase ability : abilities.getAllActive()) {
                if (ability.tick((ServerPlayerEntity)player)) {
                    ability.end((ServerPlayerEntity)player);
                    if (ability.getType() == AbilityType.TOGGLE) {
                        /*float cooldown = ability.getCooldown();
                        if (pure) cooldown /= 2;
                        if (hasCast("Valiant Heart")) cooldown /= 1.33f;*/
                        ability.setLastCast(Objects.requireNonNull(player.getServer()).getTicks());
                    }
                }
            }
            if (hasWeapon()) {
                if (getWeapon().getItem() instanceof MagicSwordItem magicWeapon) {
                    if (getWeapon().isIn(SoulForgeTags.EFFECTIVE_LV_WEAPON)) {
                        magicWeapon.attackDamage = magicWeapon.baseAttackDamage + getEffectiveLV() * magicWeapon.lvIncrease;
                    } else {
                        magicWeapon.attackDamage = magicWeapon.baseAttackDamage + getLV() * magicWeapon.lvIncrease;
                    }
                }
            }
            if (hasWormholeRequest()) {
                if (Objects.requireNonNull(player.getServer()).getTicks() > getWormholeTime() + 600) {
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
                    if (abilities.contains(new PerfectedAuraTechnique())) {
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
                        player.addVelocityInternal(velAdd);
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
                    player.setPos(target.x, target.y, target.z);
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
                Utils.removeModifier(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "limit_break");
                EntityAttributeModifier strengthModifier = new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, "limit_break"), 0.5*((player.getMaxHealth()-player.getHealth())/player.getMaxHealth()), EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)).addPersistentModifier(strengthModifier);
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
        abilities = new AbilityList();
        for (AbilityBase ability : Traits.getAbilities(traits, lv, pure)) {
            abilities.add(ability);
        }

        discovered = new ArrayList<>();
        List<String> discoveredIds = List.of(tag.getString("discovered").split(","));
        for (String id : discoveredIds) {
            if (!id.isEmpty() && !discovered.contains(Abilities.get(Identifier.of(SoulForge.MOD_ID, id)))) discovered.add(Abilities.get(Identifier.of(SoulForge.MOD_ID, id)));
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
                if (abilities.contains(nbt.getString("name"))) {
                    abilities.get(nbt.getString("name")).readNbt(nbt);
                }
            }
        }

        abilityLayout = AbilityLayout.fromNbt(List.copyOf(abilities.getAll()), tag.getCompound("abilityLayout"));
        abilityRow = tag.getInt("abilityRow");
        magicMode = tag.getBoolean("magicMode");

        resetData = new ResetData(tag.getCompound("resetData"));

        if (tag.contains("wormhole")) {
            NbtCompound wormhole = tag.getCompound("wormhole");
            wormholeRequest = new Pair<>(wormhole.getUuid("target"), wormhole.getInt("time"));
        }

        if (tag.contains("disguisedAs")) disguisedAsID = tag.getUuid("disguisedAs");

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
        StringBuilder discoveredString = new StringBuilder();
        for (AbilityBase ability : discovered) {
            discoveredString.append(ability.getID().toString()).append(",");
        }
        tag.putString("discovered", discoveredString.substring(0, discoveredString.length()-1));
        List<String> discoveredIds = List.of(tag.getString("discovered").split(","));
        for (String id : discoveredIds) {
            if (!id.isEmpty() && !discovered.contains(Abilities.get(Identifier.of(SoulForge.MOD_ID, id)))) discovered.add(Abilities.get(Identifier.of(SoulForge.MOD_ID, id)));
        }
        NbtCompound souls = new NbtCompound();
        for (String key : monsterSouls.keySet()) souls.putInt(key, monsterSouls.get(key));
        tag.put("monsterSouls", souls);
        souls = new NbtCompound();
        for (String key : playerSouls.keySet()) souls.putInt(key, playerSouls.get(key));
        tag.put("playerSouls", souls);
        NbtCompound abilityNbt = new NbtCompound();
        for (AbilityBase ability : abilities.getAll()) {
            NbtCompound nbt = new NbtCompound();
            nbt = ability.saveNbt(nbt);
            abilityNbt.put(ability.getID().toString(), nbt);
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
        for (AbilityBase ability : abilities.getAllActive()) {
            ability.end((ServerPlayerEntity)player);
        }
        for (AbilityBase ability : abilities.getAll()) {
            ability.setActive(false);
        }
        updateAbilities();
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
        for (AbilityBase ability : abilities.getAllActive()) {
            ability.end((ServerPlayerEntity)player);
        }
        for (AbilityBase ability : abilities.getAll()) {
            ability.setActive(false);
        }
        updateAbilities();
        updateTags();
        sync();
    }

    private void updateAbilities() {
        abilities = new AbilityList();
        for (AbilityBase ability : Traits.getAbilities(traits, lv, pure)) {
            ability.setDiscovered(true);
            abilities.add(ability);
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
        if (player instanceof ServerPlayerEntity) {
            SoulForgeCriterions.PLAYER_TRAIT.trigger((ServerPlayerEntity) player, this);
        }
        if (traits.size() == 1) {
            if (traits.getFirst() == Traits.bravery) resetData.bravery = true;
            if (traits.getFirst() == Traits.justice) resetData.justice = true;
            if (traits.getFirst() == Traits.kindness) resetData.kindness = true;
            if (traits.getFirst() == Traits.patience) resetData.patience = true;
            if (traits.getFirst() == Traits.integrity) resetData.integrity = true;
            if (traits.getFirst() == Traits.perseverance) resetData.perseverance = true;
            if (traits.getFirst() == Traits.determination) resetData.determination = true;
            if (pure) resetData.addPure(traits.getFirst());
        } else if (traits.size() == 2) {
            if (strong) resetData.strongDual = true;
            resetData.addDual(traits.get(0), traits.get(1));
        }
    }
}

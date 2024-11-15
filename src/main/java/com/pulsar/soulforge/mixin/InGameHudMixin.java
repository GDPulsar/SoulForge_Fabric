package com.pulsar.soulforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.Abilities;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.config.ConfigHelper;
import com.pulsar.soulforge.data.AbilityLayout;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static net.minecraft.client.gui.widget.ClickableWidget.WIDGETS_TEXTURE;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow private int scaledHeight;

    @Shadow private int scaledWidth;

    @Shadow @Final private static Identifier ICONS;

    @Shadow @Final private MinecraftClient client;

    @Shadow public abstract TextRenderer getTextRenderer();

    @Shadow private int heldItemTooltipFade;

    @Shadow public float vignetteDarkness;

    @Shadow protected abstract void renderHotbarItem(DrawContext context, int x, int y, float f, PlayerEntity player, ItemStack stack, int seed);

    @Shadow protected abstract PlayerEntity getCameraPlayer();

    @Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 0))
    private void soulforge$renderSecondHotbar(float tickDelta, DrawContext context, CallbackInfo ci) {
        if (ConfigHelper.getSplitHotbars()) {
            context.drawTexture(WIDGETS_TEXTURE, this.scaledWidth / 2 - 91, this.scaledHeight - 44, 0, 0, 182, 22);
            PlayerEntity player = !(MinecraftClient.getInstance().getCameraEntity() instanceof PlayerEntity) ? null : (PlayerEntity)MinecraftClient.getInstance().getCameraEntity();
            for (int i = 0; i < 9; i++) {
                int x = this.scaledWidth / 2 - 90 + i * 20 + 2;
                this.renderAbilityHotbarIcon(context, x - 1, this.scaledHeight - 42, player, i);
            }
        }
    }

    @Redirect(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 1))
    private void soulforge$renderSelectedAndWeapon(DrawContext context, Identifier texture, int x, int y, int u, int v, int width, int height) {
        PlayerEntity player = !(MinecraftClient.getInstance().getCameraEntity() instanceof PlayerEntity) ? null : (PlayerEntity)MinecraftClient.getInstance().getCameraEntity();
        if (player != null) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            if (playerSoul.magicModeActive()) {
                y = ConfigHelper.getSplitHotbars() ? y - 22 : y;
                context.drawTexture(texture, this.scaledWidth / 2 - 92 + playerSoul.getAbilitySlot() * 20, y, u, v, width, height);
            } else {
                if (player.getInventory().selectedSlot != 9) context.drawTexture(texture, x, y, u, v, width, height);
            }
        }
    }

    @Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 1, shift = At.Shift.AFTER))
    private void soulforge$renderWeaponSlot(float tickDelta, DrawContext context, CallbackInfo ci) {
        PlayerEntity player = this.getCameraPlayer();
        if (player != null) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            if (playerSoul.hasWeapon()) {
                ItemStack weapon = playerSoul.getWeapon();
                if (!weapon.isEmpty() && (ConfigHelper.getSplitHotbars() || !playerSoul.magicModeActive())) {
                    int rx = this.scaledWidth / 2 + 109;
                    context.drawTexture(WIDGETS_TEXTURE, rx, this.scaledHeight - 23, 58, 22, 24, 24);
                    if (player.getInventory().selectedSlot == 9 && !playerSoul.magicModeActive())
                        context.drawTexture(WIDGETS_TEXTURE, rx+1, this.scaledHeight - 23, 0, 22, 24, 22);
                    int m = context.getScaledWindowHeight() - 19;
                    this.renderHotbarItem(context, rx + 5, m, tickDelta, player, weapon, 0);
                }
            }
        }
    }

    @Redirect(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbarItem(Lnet/minecraft/client/gui/DrawContext;IIFLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;I)V", ordinal = 0))
    private void soulforge$replaceItemsWithAbilities(InGameHud instance, DrawContext context, int x, int y, float f, PlayerEntity player, ItemStack stack, int seed) {
        if (!ConfigHelper.getSplitHotbars()) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            if (playerSoul.magicModeActive()) {
                int i = (x - 2 - this.scaledWidth / 2 + 90) / 20;
                this.renderAbilityHotbarIcon(context, x - 1, y - 1, player, i);
                return;
            }
        }
        instance.renderHotbarItem(context, x, y, f, player, stack, seed);
    }

    @Unique
    private void renderAbilityHotbarIcon(DrawContext context, int n, int o, PlayerEntity playerEntity, int i) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(playerEntity);
        AbilityLayout.AbilityRow row = playerSoul.getLayoutRow(playerSoul.getAbilityRow());
        AbilityBase ability = row.abilities.get(i);
        if (ability != null) {
            int height = MathHelper.floor(18f*playerSoul.cooldownPercent(ability));
            int textureHeight = MathHelper.floor(height*(66f/18f));
            String id = ability.getID().getNamespace();
            String path = ability.getID().getPath();
            Identifier textureLocation;
            Identifier grayscaleLocation;
            if (ability instanceof ToggleableAbilityBase toggleable) {
                String activeStr = toggleable.getActive() ? "on" : "off";
                textureLocation = new Identifier(id, "textures/ui/ability_icon/" + path + "_" + activeStr + ".png");
                grayscaleLocation = new Identifier(id, "textures/ui/ability_icon/grayscale/" + path + "_off.png");
            } else {
                textureLocation = new Identifier(id, "textures/ui/ability_icon/" + path + ".png");
                grayscaleLocation = new Identifier(id, "textures/ui/ability_icon/grayscale/" + path + ".png");
            }
            context.drawTexture(textureLocation, n, o+(18-height), 18, height, 0, 66f-textureHeight, 66, textureHeight, 66, 66);
            context.drawTexture(grayscaleLocation, n, o, 18, 18-height, 0, 0, 66, 66-textureHeight, 66, 66);
        }
    }

    @Inject(method = "render", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/BossBarHud;render(Lnet/minecraft/client/gui/DrawContext;)V", shift = At.Shift.AFTER))
    private void preRenderHotbar(DrawContext context, float tickDelta, CallbackInfo ci) {
        if (ConfigHelper.getSplitHotbars()) scaledHeight -= 22;
    }

    @Inject(method = "render", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHeldItemTooltip(Lnet/minecraft/client/gui/DrawContext;)V", shift = At.Shift.AFTER))
    private void postRenderHotbarSurvival(DrawContext context, float tickDelta, CallbackInfo ci) {
        if (ConfigHelper.getSplitHotbars()) scaledHeight += 22;
    }

    @Inject(method = "render", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/SpectatorHud;render(Lnet/minecraft/client/gui/DrawContext;)V", shift = At.Shift.BEFORE))
    private void postRenderHotbarSpectator(DrawContext context, float tickDelta, CallbackInfo ci) {
        if (ConfigHelper.getSplitHotbars()) scaledHeight += 22;
    }

    @Inject(method = "renderHeldItemTooltip", at = @At("TAIL"))
    private void onRenderHeldItemTooltip(DrawContext context, CallbackInfo ci) {
        this.renderSelectedAbilityTooltip(context);
    }

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        if (this.client.player != null) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(client.player);
            if (playerSoul.magicModeActive()) {
                AbilityBase selectedAbility = playerSoul.getLayoutAbility(playerSoul.getAbilityRow(), playerSoul.getAbilitySlot());
                if (selectedAbility == null) {
                    this.selectedAbilityTooltipFade = 0;
                } else if (Objects.equals(selectedAbility.getName(), this.currentAbility)) {
                    if (this.selectedAbilityTooltipFade > 0) {
                        --this.selectedAbilityTooltipFade;
                    }
                } else {
                    this.selectedAbilityTooltipFade = (int) (40.0 * this.client.options.getNotificationDisplayTime().getValue());
                }

                this.currentAbility = selectedAbility != null ? selectedAbility.getName() : "";
                this.heldItemTooltipFade = 0;
            } else {
                this.selectedAbilityTooltipFade = 0;
                this.currentAbility = "";
            }
        }
    }

    @Unique
    private int selectedAbilityTooltipFade = 0;
    @Unique
    private String currentAbility = "";

    @Unique
    public void renderSelectedAbilityTooltip(DrawContext context) {
        if (this.selectedAbilityTooltipFade > 0 && !Objects.equals(this.currentAbility, "")) {
            AbilityBase currentAbility = Abilities.get(this.currentAbility);
            if (currentAbility == null) return;

            MutableText mutableText = Text.empty().append(currentAbility.getName());

            int i = this.getTextRenderer().getWidth(mutableText);
            int j = (this.scaledWidth - i) / 2;
            int k = this.scaledHeight - 59;
            if (!this.client.interactionManager.hasStatusBars()) {
                k += 14;
            }

            int l = (int)((float)this.selectedAbilityTooltipFade * 256.0F / 10.0F);
            if (l > 255) {
                l = 255;
            }

            if (l > 0) {
                int var10001 = j - 2;
                int var10002 = k - 2;
                int var10003 = j + i + 2;
                Objects.requireNonNull(this.getTextRenderer());
                context.fill(var10001, var10002, var10003, k + 9 + 2, this.client.options.getTextBackgroundColor(0));
                context.drawTextWithShadow(this.getTextRenderer(), mutableText, j, k, 16777215 + (l << 24));
            }
        }
    }

    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;isFancyGraphicsOrBetter()Z"))
    private boolean soulforge$doProceedVignette(boolean original) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(this.client.player);
        if (playerSoul.hasCast("Proceed")) {
            this.vignetteDarkness = 1f;
            return true;
        }
        return original;
    }
}

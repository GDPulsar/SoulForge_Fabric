package com.pulsar.soulforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.systems.RenderSystem;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.SoulForgeClient;
import com.pulsar.soulforge.ability.Abilities;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.data.AbilityLayout;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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

    @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    private void renderHotbarExtras(float tickDelta, DrawContext context, CallbackInfo ci) {
        if (SoulForgeClient.appleSkin && !SoulForgeClient.appleSkinApplied) {
            SoulForgeClient.appleSkinLoad();
        }
        PlayerEntity playerEntity = !(MinecraftClient.getInstance().getCameraEntity() instanceof PlayerEntity) ? null : (PlayerEntity)MinecraftClient.getInstance().getCameraEntity();
        if (playerEntity != null) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(playerEntity);
            int scaledHeight = context.getScaledWindowHeight();

            ItemStack itemStack = playerEntity.getOffHandStack();
            Arm arm = playerEntity.getMainArm().getOpposite();
            int i = this.scaledWidth / 2;
            context.getMatrices().push();
            context.getMatrices().translate(0.0F, 0.0F, -90.0F);
            context.drawTexture(WIDGETS_TEXTURE, i - 91, this.scaledHeight - 22, 0, 0, 182, 22);
            context.drawTexture(WIDGETS_TEXTURE, i - 91, this.scaledHeight - 44, 0, 0, 182, 22);
            if (playerEntity.getInventory().selectedSlot < 9 && !playerSoul.magicModeActive())
                context.drawTexture(WIDGETS_TEXTURE, i - 91 - 1 + playerEntity.getInventory().selectedSlot * 20, this.scaledHeight - 22 - 1, 0, 22, 24, 24);
            if (!itemStack.isEmpty()) {
                context.drawTexture(WIDGETS_TEXTURE, i - 91 - 29, this.scaledHeight - 23, 24, 22, 29, 24);
            }
            if (playerSoul.magicModeActive()) {
                context.drawTexture(WIDGETS_TEXTURE, i - 91 - 1 + playerSoul.getAbilitySlot() * 20, this.scaledHeight - 44 - 1, 0, 22, 24, 24);
            }
            if (!itemStack.isEmpty()) {
                context.drawTexture(WIDGETS_TEXTURE, i - 91 - 29, this.scaledHeight - 23, 24, 22, 29, 24);
            }
            context.getMatrices().pop();

            int l = 1;

            int m;
            int n;
            int o;
            for (m = 0; m < 9; ++m) {
                n = i - 90 + m * 20 + 2;
                o = scaledHeight - 16 - 3;
                this.renderHotbarItem(context, n, o, tickDelta, playerEntity, playerEntity.getInventory().main.get(m), l++);
                this.renderAbilityHotbarIcon(context, n - 1, o - 22 - 1, playerEntity, m);
            }

            if (!itemStack.isEmpty()) {
                m = scaledHeight - 16 - 3;
                this.renderHotbarItem(context, i - 91 - 26, m, tickDelta, playerEntity, itemStack, l++);
            }

            if (playerSoul.hasWeapon()) {
                itemStack = playerSoul.getWeapon();
                if (!itemStack.isEmpty()) {
                    int rx = i + 109;
                    context.drawTexture(WIDGETS_TEXTURE, rx, this.scaledHeight - 23, 58, 22, 24, 24);
                    if (playerEntity.getInventory().selectedSlot == 9 && !playerSoul.magicModeActive())
                        context.drawTexture(WIDGETS_TEXTURE, rx+1, this.scaledHeight - 23, 0, 22, 24, 22);
                    m = context.getScaledWindowHeight() - 19;
                    this.renderHotbarItem(context, rx+5, m, tickDelta, playerEntity, itemStack, l);
                }
            }

            RenderSystem.enableBlend();
            if (MinecraftClient.getInstance().options.getAttackIndicator().getValue() == AttackIndicator.HOTBAR) {
                assert MinecraftClient.getInstance().player != null;
                float f = MinecraftClient.getInstance().player.getAttackCooldownProgress(0.0F);
                if (f < 1.0F) {
                    n = scaledHeight - 20;
                    o = i + 91 + 6;
                    if (arm == Arm.RIGHT) {
                        o = i - 91 - 22;
                    }

                    int p = (int) (f * 19.0F);
                    context.drawTexture(ICONS, o, n, 0, 94, 18, 18);
                    context.drawTexture(ICONS, o, n + 18 - p, 18, 112 - p, 18, p);
                }
            }

            RenderSystem.disableBlend();
            ci.cancel();
        }
    }

    @Unique
    private void renderAbilityHotbarIcon(DrawContext context, int n, int o, PlayerEntity playerEntity, int i) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(playerEntity);
        AbilityLayout.AbilityRow row = playerSoul.getLayoutRow(playerSoul.getAbilityRow());
        AbilityBase ability = row.abilities.get(i);
        if (ability != null) {
            int height = MathHelper.floor(18f*playerSoul.cooldownPercent(ability));
            int textureHeight = MathHelper.floor(height*(66f/18f));
            Identifier textureLocation = new Identifier(SoulForge.MOD_ID, "textures/ui/ability_icon/" + ability.getID().getPath() + ".png");
            Identifier grayscaleLocation = new Identifier(SoulForge.MOD_ID, "textures/ui/ability_icon/grayscale/" + ability.getID().getPath() + ".png");
            context.drawTexture(textureLocation, n, o+(18-height), 18, height, 0, 66f-textureHeight, 66, textureHeight, 66, 66);
            context.drawTexture(grayscaleLocation, n, o, 18, 18-height, 0, 0, 66, 66-textureHeight, 66, 66);
        }
    }

    @Unique
    private void renderHotbarItem(DrawContext context, int x, int y, float tickDelta, PlayerEntity player, ItemStack stack, int i) {
        if (!stack.isEmpty()) {
            float f = (float)stack.getBobbingAnimationTime() - tickDelta;
            if (f > 0.0F) {
                float g = 1.0F + f / 5.0F;
                context.getMatrices().push();
                context.getMatrices().translate((float)(x + 8), (float)(y + 12), 0.0F);
                context.getMatrices().scale(1.0F / g, (g + 1.0F) / 2.0F, 1.0F);
                context.getMatrices().translate((float)(-(x + 8)), (float)(-(y + 12)), 0.0F);
            }

            context.drawItem(player, stack, x, y, i);
            if (f > 0.0F) {
                context.getMatrices().pop();
            }

            context.drawItemInSlot(MinecraftClient.getInstance().textRenderer, stack, x, y);
        }
    }

    @Inject(method = "render", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/BossBarHud;render(Lnet/minecraft/client/gui/DrawContext;)V", shift = At.Shift.AFTER))
    private void preRenderHotbar(DrawContext context, float tickDelta, CallbackInfo ci) {
        scaledHeight -= 22;
    }

    @Inject(method = "render", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHeldItemTooltip(Lnet/minecraft/client/gui/DrawContext;)V", shift = At.Shift.AFTER))
    private void postRenderHotbarSurvival(DrawContext context, float tickDelta, CallbackInfo ci) {
        scaledHeight += 22;
    }

    @Inject(method = "render", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/SpectatorHud;render(Lnet/minecraft/client/gui/DrawContext;)V", shift = At.Shift.BEFORE))
    private void postRenderHotbarSpectator(DrawContext context, float tickDelta, CallbackInfo ci) {
        scaledHeight += 22;
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

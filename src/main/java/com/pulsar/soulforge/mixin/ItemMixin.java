package com.pulsar.soulforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.item.devices.machines.SiphonImbuer;
import com.pulsar.soulforge.siphon.Siphon;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "appendTooltip", at=@At("HEAD"))
    protected void modifyTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type, CallbackInfo ci) {
        if (stack.get(SoulForgeItems.SIPHON_COMPONENT) != null) {
            Siphon.Type siphonType = stack.get(SoulForgeItems.SIPHON_COMPONENT);
            if (siphonType != null) {
                switch (siphonType) {
                    case BRAVERY -> tooltip.add(Text.translatable("siphon.soulforge.bravery").formatted(Formatting.GOLD));
                    case JUSTICE -> tooltip.add(Text.translatable("siphon.soulforge.justice").formatted(Formatting.YELLOW));
                    case KINDNESS -> tooltip.add(Text.translatable("siphon.soulforge.kindness").formatted(Formatting.GREEN));
                    case PATIENCE -> tooltip.add(Text.translatable("siphon.soulforge.patience").formatted(Formatting.BLUE));
                    case INTEGRITY -> tooltip.add(Text.translatable("siphon.soulforge.integrity").formatted(Formatting.DARK_BLUE));
                    case PERSEVERANCE -> tooltip.add(Text.translatable("siphon.soulforge.perseverance").formatted(Formatting.LIGHT_PURPLE));
                    case DETERMINATION -> tooltip.add(Text.translatable("siphon.soulforge.determination").formatted(Formatting.RED));
                    case SPITE -> tooltip.add(Text.literal("Siphon: Phoenix").formatted(Formatting.DARK_RED));
                }
            }
        }
        if (stack.get(SoulForgeItems.IMBUED_COMPONENT)) {
            tooltip.add(Text.translatable("item.soulforge.siphon_imbuer.imbued"));
        }
    }

    @ModifyReturnValue(method = "use", at=@At("RETURN"))
    private TypedActionResult<ItemStack> onUse(TypedActionResult<ItemStack> original, @Local World world, @Local PlayerEntity user, @Local Hand hand) {
        if (!world.isClient) {
            ItemStack stack = user.getStackInHand(hand);
            if (stack.get(SoulForgeItems.SIPHON_COMPONENT) != null) {
                Siphon.Type siphonType = stack.get(SoulForgeItems.SIPHON_COMPONENT);
                if ((siphonType == Siphon.Type.JUSTICE || siphonType == Siphon.Type.SPITE) && stack.getItem() instanceof MiningToolItem) {
                    SoulComponent playerSoul = SoulForge.getPlayerSoul(user);
                    if (playerSoul.getMagic() >= 10f) {
                        BlockHitResult hit = world.raycast(new RaycastContext(user.getEyePos(), user.getEyePos().add(user.getRotationVector().multiply(50)), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, user));
                        if (hit != null) {
                            BlockState block = world.getBlockState(hit.getBlockPos());
                            if (user.canHarvest(block) && user.canModifyBlocks() && stack.getItem().isCorrectForDrops(stack, block)) {
                                LootContextParameterSet.Builder builder = (new LootContextParameterSet.Builder((ServerWorld) world)).add(LootContextParameters.ORIGIN, Vec3d.ofCenter(hit.getBlockPos())).add(LootContextParameters.TOOL, stack).addOptional(LootContextParameters.BLOCK_ENTITY, world.getBlockEntity(hit.getBlockPos()));
                                for (ItemStack itemStack : block.getDroppedStacks(builder)) {
                                    ItemEntity item = new ItemEntity(world, hit.getBlockPos().getX(), hit.getBlockPos().getY(), hit.getBlockPos().getZ(), itemStack);
                                    item.setToDefaultPickupDelay();
                                    world.spawnEntity(item);
                                }
                            }
                            world.setBlockState(hit.getBlockPos(), Blocks.AIR.getDefaultState());
                            playerSoul.setMagic(playerSoul.getMagic() - 10f);
                            playerSoul.resetLastCastTime();
                            return TypedActionResult.success(stack);
                        }
                    }
                }
            }
        }
        return original;
    }

    @Unique
    private int regenTimer = 0;
    @Inject(method = "inventoryTick", at=@At("HEAD"))
    private void onInventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (!world.isClient) {
            if (stack.get(SoulForgeItems.SIPHON_COMPONENT) != null) {
                Siphon.Type siphonType = stack.get(SoulForgeItems.SIPHON_COMPONENT);
                if (siphonType == Siphon.Type.KINDNESS || siphonType == Siphon.Type.SPITE) {
                    regenTimer++;
                    if (regenTimer >= 60) {
                        stack.setDamage(Math.max(stack.getDamage() - 1, 0));
                        regenTimer = 0;
                    }
                }
            }
            if (entity instanceof PlayerEntity player) {
                if (stack.getItem() instanceof AxeItem) {
                    if (selected && player.age % 5 == 0) {
                        if (Utils.isImbued(stack, player)) {
                            if (stack.get(SoulForgeItems.MOUSE_DOWN_COMPONENT)) {
                                ItemStack imbuerStack = Utils.getImbuer(stack, player);
                                EntityHitResult hit = Utils.getFocussedEntity(player, (float)player.getEntityInteractionRange());
                                if (hit != null && hit.getEntity() instanceof LivingEntity target) {
                                    if (target.isAttackable()) {
                                        if (!target.handleAttack(player)) {
                                            target.damage(player.getDamageSources().playerAttack(player), (float) (player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) / 2f));
                                            target.timeUntilRegen = 14;
                                        }
                                    }
                                }
                                ((SiphonImbuer) imbuerStack.getItem()).decreaseCharge(imbuerStack, 5);
                            }
                        }
                    }
                }
            }
        }
    }
}

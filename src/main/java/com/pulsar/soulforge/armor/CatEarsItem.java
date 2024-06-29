package com.pulsar.soulforge.armor;

import com.pulsar.soulforge.client.armor.CatEarsRenderer;
import com.pulsar.soulforge.client.armor.PlatformBootsRenderer;
import com.pulsar.soulforge.item.devices.ArmorDeviceBase;
import com.pulsar.soulforge.trait.Traits;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class CatEarsItem extends ArmorItem implements GeoItem {
    public static class CatEarsMaterial implements ArmorMaterial {
        @Override
        public int getDurability(ArmorItem.Type type) { return 0; }

        @Override
        public int getProtection(ArmorItem.Type type) { return 0; }

        @Override
        public int getEnchantability() { return 0; }

        @Override
        public SoundEvent getEquipSound() { return SoundEvents.ITEM_ARMOR_EQUIP_GENERIC; }

        @Override
        public Ingredient getRepairIngredient() { return null; }

        @Override
        public String getName() { return "cat_ears"; }

        @Override
        public float getToughness() { return 0; }

        @Override
        public float getKnockbackResistance() { return 0; }
    }

    public static ArmorMaterial MATERIAL = new CatEarsMaterial();

    public CatEarsItem() {
        super(MATERIAL, Type.HELMET, new FabricItemSettings().maxCount(1));
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public BipedEntityModel<LivingEntity> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, BipedEntityModel<LivingEntity> original) {
                if(this.renderer == null)
                    this.renderer = new CatEarsRenderer();

                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);

                return this.renderer;
            }
        });
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return this.renderProvider;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 20, state -> {
            state.getController().setAnimation(RawAnimation.begin().then("cat_ears.idle", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}

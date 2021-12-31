package com.brachy84.mechtech.common.items;

import com.brachy84.mechtech.api.armor.IModule;
import com.brachy84.mechtech.api.armor.ModularArmor;
import com.brachy84.mechtech.common.MTConfig;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IElectricItem;
import gregtech.api.items.armor.ArmorMetaItem;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.items.metaitem.stats.IItemBehaviour;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.brachy84.mechtech.common.items.MTMetaItems.*;

public class MTArmorItem extends ArmorMetaItem<ArmorMetaItem<?>.ArmorMetaValueItem> {

    @Override
    public void registerSubItems() {
        MODULAR_HELMET = addItem(0, "modular_helmet").setArmorLogic(new ModularArmor(EntityEquipmentSlot.HEAD, MTConfig.modularArmor.helmetSlots));
        MODULAR_CHESTPLATE = addItem(1, "modular_chestplate").setArmorLogic(new ModularArmor(EntityEquipmentSlot.CHEST, MTConfig.modularArmor.chestPlateSlots));
        MODULAR_LEGGINGS = addItem(2, "modular_leggings").setArmorLogic(new ModularArmor(EntityEquipmentSlot.LEGS, MTConfig.modularArmor.leggingsSlots));
        MODULAR_BOOTS = addItem(3, "modular_boots").setArmorLogic(new ModularArmor(EntityEquipmentSlot.FEET, MTConfig.modularArmor.bootsSlot));
    }

    @Override
    public void addInformation(@Nonnull ItemStack itemStack, @Nullable World worldIn, @Nonnull List<String> lines, @Nonnull ITooltipFlag tooltipFlag) {
        ArmorMetaItem<?>.ArmorMetaValueItem item = this.getItem(itemStack);
        if (item != null) {
            if (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54)) {
                lines.add(I18n.format("metaitem.modular_armor.tooltip.sneak"));
            } else {
                lines.add(I18n.format("metaitem.modular_armor.tooltip.unsneak"));
            }
            Collection<IModule> modules = ModularArmor.getModulesOf(itemStack);
            ModularArmor modularArmor = ModularArmor.get(itemStack);
            String unlocalizedTooltip = "metaitem." + item.unlocalizedName + ".tooltip";
            if (I18n.hasKey(unlocalizedTooltip)) {
                lines.addAll(Arrays.asList(I18n.format(unlocalizedTooltip, new Object[0]).split("/n")));
            }

            if (modules.size() > 0) {
                lines.add(I18n.format("metaitem.modular_armor.installed_modules", modules.size(), modularArmor.getModuleSlots()));
                for (IModule module : modules) {
                    lines.add(" - " + module.getLocalizedName());
                }
                for (IModule module : modules) {
                    module.addTooltip(itemStack, worldIn, lines, tooltipFlag);
                }
            } else {
                lines.add(I18n.format("metaitem.modular_armor.no_modules"));
            }

            IElectricItem electricItem = itemStack.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, (EnumFacing) null);
            if (electricItem != null) {
                if (electricItem.getMaxCharge() == 0) {
                    lines.add(I18n.format("metaitem.modular_armor.no_battery"));
                } else {
                    lines.add(I18n.format("metaitem.generic.electric_item.tooltip", electricItem.getCharge(), electricItem.getMaxCharge(), "Unspecified"));
                }
            }

            for (IItemBehaviour behaviour : this.getBehaviours(itemStack)) {
                behaviour.addInformation(itemStack, lines);
            }
        }
    }

    @Override
    public boolean showDurabilityBar(@Nonnull ItemStack stack) {
        MetaItem<?>.MetaValueItem metaValueItem = getItem(stack);
        if (metaValueItem != null && metaValueItem.getDurabilityManager() != null) {
            return metaValueItem.getDurabilityManager().showsDurabilityBar(stack);
        }
        IElectricItem electricItem = stack.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
        return ModularArmor.getBatteries(stack).size() > 0 && electricItem != null && (stack.getMaxStackSize() == 1 || electricItem.getCharge() > 0L);
    }
}
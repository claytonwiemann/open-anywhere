package net.claywiemann.openanywhere.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class ShulkerInventory {

    public static SimpleContainer load(ItemStack shulker) {
        NonNullList<ItemStack> items =
                NonNullList.withSize(27, ItemStack.EMPTY);

        // SNBT basically the shulkers nbt data
        CompoundTag tag = shulker.getTagElement("BlockEntityTag");

        if(tag != null) {
            ContainerHelper.loadAllItems(tag, items);
        }

        SimpleContainer container = new SimpleContainer(27);

        for(int i = 0; i < 27; i++) {
            container.setItem(i, items.get(i));
        }

        return container;
    }

    public static void save(ItemStack shulker, SimpleContainer container) {
        NonNullList<ItemStack> items =
                NonNullList.withSize(27, ItemStack.EMPTY);

        for(int i = 0; i < 27; i++) {
            items.set(i, container.getItem(i).copy());
        }

        CompoundTag tag = shulker.getOrCreateTagElement("BlockEntityTag");
        ContainerHelper.saveAllItems(tag, items, true);
    }
}
package net.claywiemann.openanywhere.client;

import net.claywiemann.openanywhere.OpenAnywhere;
import net.claywiemann.openanywhere.network.ModNetworking;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = OpenAnywhere.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public class KeyInputHandler {

    @SubscribeEvent
    public static void onKeyPressed(ScreenEvent.KeyPressed.Pre event) {

        if(!(event.getScreen() instanceof AbstractContainerScreen<?> screen)){
            return;
        }

        if(KeyBindings.OPEN_CONTAINER.matches(
                event.getKeyCode(),
                event.getScanCode()
        )) {

            Slot slot = screen.getSlotUnderMouse();

            if(slot != null && slot.hasItem()) {
                ItemStack stack = slot.getItem();

                if(
                        stack.is(Items.CRAFTING_TABLE)
                        || stack.is(Items.ENDER_CHEST)
                        || stack.is(Items.LOOM)
                        || stack.is(Items.GRINDSTONE)
                        || stack.is(Items.STONECUTTER)
                        || stack.is(Items.ENCHANTING_TABLE)
                        || stack.is(Items.CARTOGRAPHY_TABLE)
                        || stack.is(Items.ANVIL) || stack.is(Items.CHIPPED_ANVIL) || stack.is(Items.DAMAGED_ANVIL)
                        || (stack.getItem() instanceof BlockItem blockItem
                        && blockItem.getBlock() instanceof ShulkerBoxBlock)
                        //Different colors of shulker box
                ){

                    MousePositionHandler.savePosition();
                    ModNetworking.sendOpenRequest(slot.index);
                }
            }
        }
    }
}
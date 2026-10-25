package net.claywiemann.openanywhere.network;

import net.claywiemann.openanywhere.inventory.ShulkerInventory;
import net.claywiemann.openanywhere.menu.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenContainerPacket {

    private final int slotId;

    public OpenContainerPacket(int slotId) {
        this.slotId = slotId;
    }

    // For the encode/decode its serialized. So read from the buffer
    public static void encode(OpenContainerPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.slotId);
    }

    public static OpenContainerPacket decode(FriendlyByteBuf buffer) {
        return new OpenContainerPacket(buffer.readInt());
    }

    // Executed when server receives packet
    public static void handle(
            OpenContainerPacket packet,
            Supplier<NetworkEvent.Context> contextSupplier
    ) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();

            if(player == null) {
                return;
            }

            // Make sure slot is valid for container type
            if(packet.slotId < 0 || packet.slotId >= player.containerMenu.slots.size()) {
                return;
            }

            Slot slot = player.containerMenu.getSlot(packet.slotId);
            ItemStack stack = slot.getItem();

            if(stack.is(Items.ENDER_CHEST)) {
                openEnderChest(player);
            }
            else if(stack.is(Items.CRAFTING_TABLE)) {
                openCraftingTable(player);
            }
            else if(stack.is(Items.STONECUTTER)) {
                openStonecutter(player);
            }
            else if(stack.is(Items.GRINDSTONE)) {
                openGrindstone(player);
            }
            else if(stack.is(Items.LOOM)) {
                openLoom(player);
            }
            else if(stack.is(Items.ANVIL) || stack.is(Items.CHIPPED_ANVIL) || stack.is(Items.DAMAGED_ANVIL)) {
                openAnvil(player);
            }
            else if(stack.is(Items.CARTOGRAPHY_TABLE)) {
                openCartographyTable(player);
            }
            else if(stack.is(Items.ENCHANTING_TABLE)) {
                openEnchantingTable(player);
            }
            else if(stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock){
                openShulkerBox(player, slot);
            }
        });

        context.setPacketHandled(true);
    }


    private static void openShulkerBox(ServerPlayer player, Slot slot) {
        ItemStack stack = slot.getItem();

        if(!(stack.getItem() instanceof BlockItem blockItem) || !(blockItem.getBlock() instanceof ShulkerBoxBlock)) {
            return;
        }

        SimpleContainer container = ShulkerInventory.load(stack);
        ContainerSource source;
        if(slot.container instanceof PlayerEnderChestContainer) {
            source = ContainerSource.ENDER_CHEST;
        } else if (slot.container instanceof Inventory) {
            source = ContainerSource.PLAYER_INVENTORY;
        } else {
            source = ContainerSource.CHEST;
        }

        player.openMenu(new SimpleMenuProvider(
                (containerId, inventory, p) ->
                        new PortableShulkerBoxMenu(
                                containerId,
                                inventory,
                                player,
                                container,
                                slot.container,
                                slot.getContainerSlot(),
                                source
                        ),
                stack.getHoverName()
        ));


    }

    public static void openEnderChest(ServerPlayer player) {
        player.openMenu(new SimpleMenuProvider(
                (containerId, inventory, p) ->
                        ChestMenu.threeRows(
                                containerId,
                                inventory,
                                player.getEnderChestInventory()
                        ),
                Component.translatable("container.enderchest")
        ));
    }

    private static void openCraftingTable(ServerPlayer player) {
        player.openMenu(new SimpleMenuProvider(
                (containerId, inventory, p) ->
                        new PortableCraftingMenu(containerId, inventory),
                Component.translatable("container.crafting")
        ));
    }

    private static void openGrindstone(ServerPlayer player) {
        player.openMenu(new SimpleMenuProvider(
                (containerId, inventory, p) ->
                        new PortableGrindstoneMenu(containerId, inventory),
                Component.translatable("container.grindstone_title")
        ));
    }

    private static void openEnchantingTable(ServerPlayer player) {
        player.openMenu(new SimpleMenuProvider(
                (containerId, inventory, p) ->
                        new PortableEnchantmentTableMenu(containerId, inventory),
                Component.translatable("container.enchant")
        ));
    }

    private static void openAnvil(ServerPlayer player) {
        player.openMenu(new SimpleMenuProvider(
                (containerId, inventory, p) ->
                        new PortableAnvilMenu(containerId, inventory),
                Component.translatable("block.minecraft.anvil")
        ));
    }

    private static void openCartographyTable(ServerPlayer player) {
        player.openMenu(new SimpleMenuProvider(
                (containerId, inventory, p) ->
                        new PortableCartographyTableMenu(containerId, inventory),
                Component.translatable("container.cartography_table")
        ));
    }

    private static void openLoom(ServerPlayer player) {
        player.openMenu(new SimpleMenuProvider(
                (containerId, inventory, p) ->
                        new PortableLoomMenu(containerId, inventory),
                Component.translatable("container.loom")
        ));
    }

    private static void openStonecutter(ServerPlayer player) {
        player.openMenu(new SimpleMenuProvider(
                (containerId, inventory, p) ->
                        new PortableStonecutterMenu(containerId, inventory),
                Component.translatable("container.stonecutter")
        ));
    }
}


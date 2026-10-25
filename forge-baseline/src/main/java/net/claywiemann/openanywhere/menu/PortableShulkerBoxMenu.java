package net.claywiemann.openanywhere.menu;

import net.claywiemann.openanywhere.inventory.ShulkerInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PortableShulkerBoxMenu extends ShulkerBoxMenu {

    private final ServerPlayer player;
    private final SimpleContainer shulkerInventory;
    private final Container sourceContainer;
    private final int sourceIndex;
    private final ItemStack shulkerStack;
    private final int lockedMenuSlot;
    private final ContainerSource containerSource;

    // Client-side constructor
    public PortableShulkerBoxMenu(int containerId, Inventory inventory, ServerPlayer player, ContainerSource containerSource) {
        super(containerId, inventory);
        this.player = player;
        this.containerSource = containerSource;

        this.shulkerInventory = null;
        this.sourceContainer = null;
        this.sourceIndex = -1;
        this.shulkerStack = ItemStack.EMPTY;
        this.lockedMenuSlot = -1;
    }

    // Server-side constructor
    public PortableShulkerBoxMenu(
            int containerId,
            Inventory inventory,
            ServerPlayer player,
            SimpleContainer container,
            Container sourceContainer,
            int sourceIndex,
            ContainerSource containerSource
    ) {
        super(containerId, inventory, container);
        this.player = player;

        this.shulkerInventory = container;
        this.sourceContainer = sourceContainer;
        this.sourceIndex = sourceIndex;
        this.shulkerStack = sourceContainer.getItem(sourceIndex);
        this.containerSource = containerSource;

        // 0–26: Shulker inventory
        // 27–53: Player main inventory
        // 54–62: Player hotbar
        // Locking the shulker box source slot
        if(sourceContainer == inventory) {
            this.lockedMenuSlot = sourceIndex < 9
                    ? 54 + sourceIndex
                    : 27 + sourceIndex - 9;
        } else {
            this.lockedMenuSlot = -1;
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        if(sourceContainer == null) {
            return true;
        }

        return sourceIndex >= 0
                && sourceIndex < sourceContainer.getContainerSize()
                && sourceContainer.getItem(sourceIndex) == shulkerStack;
    }

    // All inventory mouse, transfer, editions, removals, etc. Since we need to save the contents after
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void clicked(
            int slotId,
            int button,
            @NotNull ClickType clickType,
            @NotNull Player player
    ) {
        if(lockedMenuSlot >= 0) {

            if(slotId == lockedMenuSlot) {
                return;
            }

            if(clickType == ClickType.SWAP
                    && sourceIndex < 9
                    && button == sourceIndex) {
                return;
            }
        }

        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slotId) {
        if(slotId == lockedMenuSlot) {
            return ItemStack.EMPTY;
        }

        return super.quickMoveStack(player, slotId);
    }

    @Override
    public boolean canDragTo(Slot slot) {
        if(slot.index == lockedMenuSlot) {
            return false;
        }

        return super.canDragTo(slot);
    }

    @Override
    public boolean canTakeItemForPickAll(@NotNull ItemStack stack, Slot slot) {
        if(slot.index == lockedMenuSlot) {
            return false;
        }

        return super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public void removed(Player player) {
        if(!player.level().isClientSide) {
            if(shulkerInventory != null
                    && sourceContainer != null
                    && sourceIndex >= 0
                    && sourceIndex < sourceContainer.getContainerSize()
                    && sourceContainer.getItem(sourceIndex) == shulkerStack) {

                ShulkerInventory.save(shulkerStack, shulkerInventory);

                // Let Minecraft know the data changed
                sourceContainer.setChanged();
            }

            switch (containerSource) {
                case ENDER_CHEST ->
                        EnderChestReopenHandler.schedule((ServerPlayer)player);

                case CHEST -> {
                        ChestReopenHandler.schedule((ServerPlayer)player, sourceContainer);
                }

                case PLAYER_INVENTORY -> {

                }
            }
        }

        super.removed(player);
    }
}
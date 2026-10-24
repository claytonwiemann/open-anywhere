package net.claywiemann.openanywhere.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.LoomMenu;
import org.jetbrains.annotations.NotNull;

public class PortableLoomMenu extends LoomMenu {

    public PortableLoomMenu(int containerId, Inventory inventory) {
        super(
                containerId,
                inventory,
                ContainerLevelAccess.create(
                        inventory.player.level(),
                        inventory.player.blockPosition()
                )
        );
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }
}

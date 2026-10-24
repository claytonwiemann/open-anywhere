package net.claywiemann.openanywhere.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import org.jetbrains.annotations.NotNull;

public class PortableCartographyTableMenu extends CartographyTableMenu {


    public PortableCartographyTableMenu(int containerId, Inventory inventory) {
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

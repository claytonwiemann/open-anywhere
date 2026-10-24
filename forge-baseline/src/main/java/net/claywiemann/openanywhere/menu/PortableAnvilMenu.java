package net.claywiemann.openanywhere.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import org.jetbrains.annotations.NotNull;

public class PortableAnvilMenu extends AnvilMenu {

    public PortableAnvilMenu(int containerId, Inventory inventory){
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

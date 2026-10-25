package net.claywiemann.openanywhere.menu;

import net.claywiemann.openanywhere.client.MousePositionHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChestReopenHandler {

    private static final Map<UUID, Container> pending = new HashMap<>();

    public static void schedule(ServerPlayer player, Container container) {
        pending.put(player.getUUID(), container);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {

        if(event.phase != TickEvent.Phase.END || pending.isEmpty()) {
            return;
        }

        Map<UUID, Container> scheduled = new HashMap<>(pending);
        pending.clear();

        for (Map.Entry<UUID, Container> entry : scheduled.entrySet()) {
            UUID uuid = entry.getKey();
            Container container = entry.getValue();

            ServerPlayer player = event.getServer()
                    .getPlayerList()
                    .getPlayer(uuid);

            if (player == null || player.hasDisconnected()) {
                continue;
            }

            if (player.containerMenu != player.inventoryMenu) {
                continue;
            }

            if (!container.stillValid(player)) {
                continue;
            }

            int rows = container.getContainerSize() / 9;

            if (rows != 3 && rows != 6) {
                continue;
            }

            player.openMenu(new SimpleMenuProvider(
                    (id, inventory, p) ->
                            new ChestMenu(
                                    rows == 6
                                            ? MenuType.GENERIC_9x6
                                            : MenuType.GENERIC_9x3,
                                    id,
                                    inventory,
                                    container,
                                    rows
                            ),
                    Component.translatable("container.chest")
            ));
        }
    }
}

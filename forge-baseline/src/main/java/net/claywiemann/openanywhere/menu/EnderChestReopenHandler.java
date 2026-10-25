package net.claywiemann.openanywhere.menu;

import net.claywiemann.openanywhere.client.MousePositionHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnderChestReopenHandler {

    // using UUID because if the player disconnects before the next tick, would be bad
    private static final Set<UUID> pending = new HashSet<>();

    public static void schedule(ServerPlayer player) {
        pending.add(player.getUUID());
    }

    // When scheduled this will check each tick
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {

        // Wait for the end of the tick
        if(event.phase != TickEvent.Phase.END || pending.isEmpty()) {
            return;
        }

        // This might look dumb, but this is just to make sure the request is only processed once
        // Like if it tries to do multiple things on the same tick kinda deal
        Set<UUID> scheduled = new HashSet<>(pending);
        pending.clear();

        for (UUID uuid : scheduled) {
            ServerPlayer player = event.getServer()
                    .getPlayerList()
                    .getPlayer(uuid);

            if(player == null || player.hasDisconnected()) {
                continue;
            }

            if(player.containerMenu != player.inventoryMenu) {
                continue;
            }

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
    }
}
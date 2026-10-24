package net.claywiemann.openanywhere.network;

import net.claywiemann.openanywhere.OpenAnywhere;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetworking {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(OpenAnywhere.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    public static void register() {
        int packetId = 0;

        // To register more messages, pretty much copy paste this. If needed for the future
        CHANNEL.registerMessage(
                packetId++,
                OpenContainerPacket.class,
                OpenContainerPacket::encode,
                OpenContainerPacket::decode,
                OpenContainerPacket::handle
        );
    }

    public static void sendOpenRequest(int slotId) {
        CHANNEL.sendToServer(new OpenContainerPacket(slotId));
    }
}
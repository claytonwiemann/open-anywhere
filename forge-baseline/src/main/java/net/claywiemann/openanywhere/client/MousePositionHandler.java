package net.claywiemann.openanywhere.client;

import net.claywiemann.openanywhere.OpenAnywhere;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(
        modid = OpenAnywhere.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public class MousePositionHandler {

    private static double savedX;
    private static double savedY;
    private static boolean shouldRestore = false;

    public static void savePosition() {
        Minecraft minecraft = Minecraft.getInstance();

        savedX = minecraft.mouseHandler.xpos();
        savedY = minecraft.mouseHandler.ypos();

        shouldRestore = true;
    }

    @SubscribeEvent
    public static void onScreenOpening(ScreenEvent.Opening event) {
        if(!shouldRestore) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        minecraft.execute(() -> {
            GLFW.glfwSetCursorPos(
                    minecraft.getWindow().getWindow(),
                    savedX,
                    savedY
            );

            shouldRestore = false;
        });
    }
}
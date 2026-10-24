package sleys.efedp.forge.bootstrap;

import net.minecraftforge.common.MinecraftForge;
import sleys.efedp.forge.system.animations.AnimationRegistryOperations;
import sleys.sl.library.annotations.ErrorHandled;

public class BootstrapServer {

    @ErrorHandled
    protected static void Initialize() {
        BootstrapServer.registerServerEvents();
    }

    private static void registerServerEvents() {
        MinecraftForge.EVENT_BUS.addListener(AnimationRegistryOperations::onModifierAnimationsServer);
    }
}

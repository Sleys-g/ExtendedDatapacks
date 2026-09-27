package sleys.efedp.forge.bootstrap;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import sleys.efedp.forge.system.animations.AnimationRegistryOperations;
import sleys.sl.library.annotations.ErrorHandled;

public class BootstrapServer {

    @ErrorHandled
    protected static IEventBus Initialize(IEventBus modBus) {
        BootstrapServer.registerServerEvents();
        return modBus;
    }

    private static void registerServerEvents() {
        MinecraftForge.EVENT_BUS.addListener(AnimationRegistryOperations::onModifierAnimationsServer);
    }
}

package sleys.efedp.neoforge.bootstrap;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import sleys.efedp.neoforge.system.animations.AnimationRegistryOperations;
import sleys.sl.library.annotations.ErrorHandled;

public class BootstrapServer {

    @ErrorHandled
    protected static IEventBus Initialize(IEventBus modBus) {
        BootstrapServer.registerServerEvents();
        return modBus;
    }

    private static void registerServerEvents() {
        NeoForge.EVENT_BUS.addListener(AnimationRegistryOperations::onModifierAnimationsServer);
    }
}

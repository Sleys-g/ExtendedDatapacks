package sleys.efedp.neoforge.bootstrap;

import net.neoforged.neoforge.common.NeoForge;
import sleys.efedp.neoforge.system.animations.AnimationRegistryOperations;
import sleys.sl.library.annotations.ErrorHandled;

public class BootstrapServer {

    @ErrorHandled
    protected static void Initialize() {
        BootstrapServer.registerServerEvents();
    }

    private static void registerServerEvents() {
        NeoForge.EVENT_BUS.addListener(AnimationRegistryOperations::onModifierAnimationsServer);
    }
}

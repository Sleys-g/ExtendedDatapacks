package sleys.efedp.forge.bootstrap;

import sleys.efedp.forge.api.registry.RegistryThirdPartiesTrackers;

public class BootstrapThirdPartiesBuilds {

    protected static void Initialize() {
        startThirdPartyBuilds();
    }

    private static void startThirdPartyBuilds() {
        RegistryThirdPartiesTrackers.EXECUTION_ANIMATIONS_REGISTRY.startToTracking();
    }
}

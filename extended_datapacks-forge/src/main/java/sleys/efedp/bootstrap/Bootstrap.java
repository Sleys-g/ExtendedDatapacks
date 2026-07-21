package sleys.efedp.bootstrap;

import net.minecraftforge.eventbus.api.IEventBus;
import sleys.efedp.main.ExtendedDatapacks;
import sleys.sl.library.runtime.policy.PolicyRuntimeTasks;
import sleys.sl.library.runtime.policy.error.ErrorPolicy;
import sleys.sl.library.runtime.policy.side.LogicalPolicy;

public class Bootstrap {

    public static void start(IEventBus modBus) {
        ExtendedDatapacks.LOGGER.info("[Extended Datapacks - Bootstrap] Initializing...");
        BootstrapBuilds.Initialize();
        BootstrapCommon.Initialize(modBus);
        BootstrapThirdParties.Initialize(modBus);
        PolicyRuntimeTasks.runOnLogicalSide(
                LogicalPolicy.LOGICAL_CLIENT,
                ErrorPolicy.DEPURATE_ERROR,
                () -> BootstrapClient.Initialize(modBus)
        );
    }
}

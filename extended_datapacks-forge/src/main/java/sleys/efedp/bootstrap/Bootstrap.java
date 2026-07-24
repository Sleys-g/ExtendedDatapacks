package sleys.efedp.bootstrap;

import net.minecraftforge.eventbus.api.IEventBus;
import sleys.efedp.ExtendedDatapacks;
import sleys.sl.library.execution.policy.ErrorPolicy;
import sleys.sl.library.execution.policy.LogicalPolicy;
import sleys.sl.library.execution.policy.LogicalTasks;

public class Bootstrap {

    public static void start(IEventBus modBus) {
        ExtendedDatapacks.LOGGER.info("[Extended Datapacks - Bootstrap] Initializing...");
        BootstrapBuilds.Initialize();
        BootstrapCommon.Initialize(modBus);
        BootstrapThirdParties.Initialize(modBus);
        LogicalTasks.operate(
                LogicalPolicy.LOGICAL_CLIENT,
                ErrorPolicy.DEPURATE,
                "Bootstrap - Client",
                modBus, BootstrapClient::Initialize
        );
    }
}

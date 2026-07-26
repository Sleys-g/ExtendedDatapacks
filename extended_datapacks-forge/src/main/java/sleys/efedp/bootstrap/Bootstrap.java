package sleys.efedp.bootstrap;

import net.minecraftforge.eventbus.api.IEventBus;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.animations.json.properties.functional.playback.PlaySpeedModifierLambdaList;
import sleys.efedp.system.animations.json.properties.functional.time.AnimationsEventsList;
import sleys.sl.library.execution.policy.ErrorPolicy;
import sleys.sl.library.execution.policy.LogicalPolicy;
import sleys.sl.library.execution.policy.LogicalTasks;

public class Bootstrap {
    private static SystemState STATE = SystemState.OPEN;

    private enum SystemState {
        OPEN,
        CLOSED
    }

    public static boolean isClosedRegistry() {
        return !STATE.equals(SystemState.OPEN);
    }

    public static void start(IEventBus modBus) {
        if (STATE.equals(SystemState.CLOSED)) return;
        ExtendedDatapacks.LOGGER.info("[Extended Datapacks - Bootstrap] Initializing...");
        AnimationEventTypeRegistry.register(ExtendedDatapacks.MODID, AnimationsEventsList.class);
        PlaySpeedModifierTypeRegistry.register(ExtendedDatapacks.MODID, PlaySpeedModifierLambdaList.class);

        BootstrapBuilds.Initialize();
        BootstrapCommon.Initialize(modBus);
        BootstrapThirdParties.Initialize(modBus);
        LogicalTasks.operate(
                LogicalPolicy.LOGICAL_CLIENT,
                ErrorPolicy.DEPURATE,
                "Bootstrap - Client",
                modBus, BootstrapClient::Initialize
        );

        STATE = SystemState.CLOSED;
    }
}

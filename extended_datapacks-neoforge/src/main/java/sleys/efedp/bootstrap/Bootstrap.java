package sleys.efedp.bootstrap;

import net.neoforged.bus.api.IEventBus;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.animations.json.properties.functional.playback.PlaySpeedModifierLambdaList;
import sleys.efedp.system.animations.json.properties.functional.playback.PlaySpeedModifierTypeRegistry;
import sleys.efedp.system.animations.json.properties.functional.time.list.*;
import sleys.efedp.system.animations.json.properties.functional.time.registry.AnimationEventTypeRegistry;
import sleys.sl.library.contract.ExpectedContracts;
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
        Bootstrap.changes();
        if (STATE.equals(SystemState.CLOSED)) return;
        ExtendedDatapacks.LOGGER.info("[Extended Datapacks - Bootstrap] Initializing...");
        Bootstrap.registryAnimationsEvents();

        PlaySpeedModifierTypeRegistry.register(ExtendedDatapacks.MODID, PlaySpeedModifierLambdaList.class);

        BootstrapBuilds.Initialize();
        BootstrapCommon.Initialize(modBus);
        LogicalTasks.operate(
                LogicalPolicy.LOGICAL_CLIENT,
                ErrorPolicy.DEPURATE,
                "Bootstrap - Client",
                modBus, BootstrapClient::Initialize
        );

        STATE = SystemState.CLOSED;
    }

    private static void registryAnimationsEvents() {
        AnimationEventTypeRegistry.register(ExtendedDatapacks.MODID, DataAnimationsEvents.class);
        AnimationEventTypeRegistry.register(ExtendedDatapacks.MODID, EntityAnimationsEvents.class);
        AnimationEventTypeRegistry.register(ExtendedDatapacks.MODID, GameplayAnimationsEvents.class);
        AnimationEventTypeRegistry.register(ExtendedDatapacks.MODID, ParticleAnimationsEvents.class);
        AnimationEventTypeRegistry.register(ExtendedDatapacks.MODID, SummonAnimationsEvents.class);
        AnimationEventTypeRegistry.register(ExtendedDatapacks.MODID, VisualAnimationsEvents.class);
    }

    private static void changes() {
        ExpectedContracts.require(
                ExtendedDatapacks.MODID, ExtendedDatapacks.VERSION,
                """
                        Changes have been introduced to the Innate Skills system. All dependents using this area are
                        requested to update their implementations accordingly. If Innate Skills are not used, this
                        message can be safely ignored.
                        
                        The registration and behavior of Innate Skills have been substantially revised to simplify
                        their implementation and remove unnecessary references. This change makes the previous
                        Phase-based system incompatible with current and newer versions.
                        
                        You can review the changes here: https://github.com/Sleys-g/ExtendedDatapacks/wiki/Recent-Changes
                        """
        );
    }
}

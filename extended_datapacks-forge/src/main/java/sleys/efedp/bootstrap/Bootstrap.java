package sleys.efedp.bootstrap;

import net.minecraftforge.eventbus.api.IEventBus;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.animations.json.properties.functional.playback.list.PlaySpeedModifierLambda;
import sleys.efedp.system.animations.json.properties.functional.playback.registry.PlaySpeedModifierTypeRegistry;
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
        Bootstrap.registrySpeedModifiers();

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

    private static void registryAnimationsEvents() {
        AnimationEventTypeRegistry.registerAsMain(DataAnimationsEvents.class);
        AnimationEventTypeRegistry.registerAsMain(EntityAnimationsEvents.class);
        AnimationEventTypeRegistry.registerAsMain(GameplayAnimationsEvents.class);
        AnimationEventTypeRegistry.registerAsMain(ParticleAnimationsEvents.class);
        AnimationEventTypeRegistry.registerAsMain(SummonAnimationsEvents.class);
        AnimationEventTypeRegistry.registerAsMain(VisualAnimationsEvents.class);
    }

    private static void registrySpeedModifiers() {
        PlaySpeedModifierTypeRegistry.registerAsMain(PlaySpeedModifierLambda.class);
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

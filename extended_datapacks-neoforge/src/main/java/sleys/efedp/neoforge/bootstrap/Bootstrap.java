package sleys.efedp.neoforge.bootstrap;

import net.neoforged.bus.api.IEventBus;
import sleys.efedp.neoforge.system.animations.json.properties.armature.EpicFightArmatureTypes;
import sleys.efedp.neoforge.system.animations.json.properties.time.types.*;
import sleys.efedp.neoforge.ExtendedDatapacks;
import sleys.efedp.neoforge.api.registry.ExtendedDatapacksRegistry;
import sleys.efedp.neoforge.system.animations.json.animations.types.AttackAnimationAccessorType;
import sleys.efedp.neoforge.system.animations.json.animations.types.ActionAnimationAccessorType;
import sleys.efedp.neoforge.system.animations.json.animations.types.StaticAnimationAccessorType;
import sleys.efedp.neoforge.system.animations.json.groups.types.EpicFightAnimationGroups;
import sleys.efedp.neoforge.system.animations.json.properties.playback.types.AnimationPlaySpeedModifiers;
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

        Bootstrap.registryAnimationsArmature();
        Bootstrap.registryAnimationsGroups();
        Bootstrap.registryAnimationsAccessors();
        Bootstrap.registryAnimationsEvents();
        Bootstrap.registrySpeedModifiers();

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

    private static void registryAnimationsArmature() {
        ExtendedDatapacksRegistry.ANIMATION_ARMATURE.register(
                ExtendedDatapacks.MODID,
                EpicFightArmatureTypes.class
        );
    }

    private static void registryAnimationsGroups() {
        ExtendedDatapacksRegistry.ANIMATION_GROUP.register(
                ExtendedDatapacks.MODID,
                EpicFightAnimationGroups.class
        );
    }

    private static void registryAnimationsAccessors() {
        ExtendedDatapacksRegistry.ANIMATION_ACCESSOR.register(
                ExtendedDatapacks.MODID,
                AttackAnimationAccessorType.class,
                ActionAnimationAccessorType.class,
                StaticAnimationAccessorType.class
        );
    }

    private static void registryAnimationsEvents() {
        ExtendedDatapacksRegistry.ANIMATIONS_EVENTS.register(
                ExtendedDatapacks.MODID,
                DataAnimationsEvents.class,
                EntityAnimationsEvents.class,
                GameplayAnimationsEvents.class,
                ParticleAnimationsEvents.class,
                SummonAnimationsEvents.class,
                VisualAnimationsEvents.class,
                GameAnimationsEvents.class,
                TaskableGameplayAnimationsEvents.class
        );
    }

    private static void registrySpeedModifiers() {
        ExtendedDatapacksRegistry.ANIMATION_PLAYBACK.register(
                ExtendedDatapacks.MODID, AnimationPlaySpeedModifiers.class
        );
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

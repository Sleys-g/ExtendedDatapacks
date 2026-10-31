package sleys.efedp.forge.bootstrap;

import net.minecraftforge.eventbus.api.IEventBus;
import sleys.efedp.forge.ExtendedDatapacks;
import sleys.efedp.forge.api.registry.ExtendedDatapacksRegistry;
import sleys.efedp.forge.core.control.types.*;
import sleys.efedp.forge.core.events.types.*;
import sleys.efedp.forge.system.animations.animations.types.ActionAnimationAccessorType;
import sleys.efedp.forge.system.animations.animations.types.AttackAnimationAccessorType;
import sleys.efedp.forge.system.animations.animations.types.StaticAnimationAccessorType;
import sleys.efedp.forge.system.animations.groups.types.EpicFightAnimationGroups;
import sleys.efedp.forge.system.animations.properties.armature.EpicFightArmatureTypes;
import sleys.efedp.forge.system.animations.properties.playback.types.AnimationPlaySpeedModifiers;
import sleys.sl.library.contract.ExpectedContracts;
import sleys.sl.library.execution.policy.LogicalPolicy;

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
        Bootstrap.registryControlableEvents();

        BootstrapBuilds.Initialize();
        BootstrapCommon.Initialize(modBus);
        BootstrapThirdParties.Initialize(modBus);

        if (LogicalPolicy.LOGICAL_CLIENT.isValidSide()) BootstrapClient.Initialize(modBus);
        if (LogicalPolicy.DEDICATED_SERVER.isValidSide()) BootstrapServer.Initialize();
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
        ExtendedDatapacksRegistry.EVENTS.register(
                ExtendedDatapacks.MODID,
                DataEvents.class,
                EntityEvents.class,
                GameplayEvents.class,
                ParticleEvents.class,
                SummonEvents.class,
                VisualEvents.class,
                GameEvents.class,
                TaskableGameplayEvents.class
        );
    }

    private static void registryControlableEvents() {
        ExtendedDatapacksRegistry.CONTROLABLE_EVENTS.register(
                ExtendedDatapacks.MODID,
                CombatControlableEvents.class,
                IAControlableEvents.class,
                LootControlableEvents.class,
                MovementControlableEvents.class,
                StatusControlableEvents.class,
                TickControlableEvents.class
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
                        For internal virtualization (InJar) modifications, the folder name changes from 'virtualizate' to 'virtualization'.
                        Within 'innate_skill_builder', for all declared skills, the 'modid' data field is renamed to 'mod_id'.
                        
                        Although some backward compatibility has been added temporarily, it will be removed in version 3.0.
                        
                        Please consider migrating!
                        
                        You can review the changes here: https://github.com/Sleys-g/ExtendedDatapacks/wiki/Recent-Changes
                        """
        );
    }
}

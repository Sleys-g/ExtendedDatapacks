package sleys.efedp.neoforge.bootstrap;

import net.neoforged.bus.api.IEventBus;
import sleys.efedp.neoforge.core.control.types.*;
import sleys.efedp.neoforge.system.animations.properties.armature.EpicFightArmatureTypes;
import sleys.efedp.neoforge.ExtendedDatapacks;
import sleys.efedp.neoforge.api.registry.ExtendedDatapacksRegistry;
import sleys.efedp.neoforge.system.animations.animations.types.AttackAnimationAccessorType;
import sleys.efedp.neoforge.system.animations.animations.types.ActionAnimationAccessorType;
import sleys.efedp.neoforge.system.animations.animations.types.StaticAnimationAccessorType;
import sleys.efedp.neoforge.system.animations.groups.types.EpicFightAnimationGroups;
import sleys.efedp.neoforge.system.animations.properties.playback.types.AnimationPlaySpeedModifiers;
import sleys.efedp.neoforge.core.events.types.*;
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
                        
                        If you do not apply these renames, your virtualized animations within InJar will fail, as will your skills defined in 'innate_skill_builder'.
                        If you do not use any of these features, please ignore this message.
                        
                        You can review the changes here: https://github.com/Sleys-g/ExtendedDatapacks/wiki/Recent-Changes
                        """
        );
    }
}

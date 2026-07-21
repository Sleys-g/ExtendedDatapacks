package sleys.efedp.bootstrap;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.animations.AnimationRegistryOperations;
import sleys.efedp.system.innates.ExtendedDatapacksRegistryConditionalInnateSkills;
import sleys.efedp.system.innates.ExtendedDatapacksRegistryHoldableInnateSkills;
import sleys.efedp.system.innates.ExtendedDatapacksRegistrySimpleInnateSkills;
import sleys.efedp.system.innates.ExtendedDatapacksRegistryStacksConditionalInnateSkills;
import sleys.efedp.system.skills.ExtendedDatapacksRegistryGuards;
import sleys.efedp.system.skills.ExtendedDatapacksRegistryPassives;
import sleys.efedp.system.weapons.ExtendedDatapacksRegistryWeaponCategories;
import sleys.efedp.system.weapons.json.WeaponItemsProperties;
import sleys.sl.epicfight.mutator.SkillBuilderHook;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

public class BootstrapCommon {

    protected static void Initialize(IEventBus modBus) {
        ExtendedDatapacks.LOGGER.info("[Extended Datapacks - Bootstrap] Initializing Common Systems...");
        RegisterHookers();
        RegistryEnumerations();
        InitializeRegistries(modBus);
        NeoForge.EVENT_BUS.addListener(BootstrapCommon::onReloadEvent);
    }

    private static void onReloadEvent(AddReloadListenerEvent event) {
        event.addListener(new WeaponItemsProperties());
    }

    private static void RegisterHookers() {
        SkillBuilderHook.registerAssignor(ExtendedDatapacksRegistryGuards::addAnyParameterToGuards);
        SkillBuilderHook.registerAssignor(ExtendedDatapacksRegistryPassives::addAnyParameterToSkill);
    }

    private static void RegistryEnumerations() {
        WeaponCategory.ENUM_MANAGER.registerEnumCls(
                ExtendedDatapacks.MODID,
                ExtendedDatapacksRegistryWeaponCategories.class
        );
    }

    private static void InitializeRegistries(IEventBus modBus) {
        ExtendedDatapacksRegistrySimpleInnateSkills.initialize(modBus);
        modBus.register(ExtendedDatapacksRegistrySimpleInnateSkills.class);

        ExtendedDatapacksRegistryConditionalInnateSkills.initialize(modBus);
        modBus.register(ExtendedDatapacksRegistryConditionalInnateSkills.class);

        ExtendedDatapacksRegistryHoldableInnateSkills.initialize(modBus);
        modBus.register(ExtendedDatapacksRegistryHoldableInnateSkills.class);

        ExtendedDatapacksRegistryStacksConditionalInnateSkills.initialize(modBus);
        modBus.register(ExtendedDatapacksRegistryStacksConditionalInnateSkills.class);

        modBus.register(AnimationRegistryOperations.class);
    }
}

package sleys.efedp.bootstrap;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.animations.AnimationRegistryOperations;
import sleys.efedp.system.innates.ConditionalInnateSkillsRegistry;
import sleys.efedp.system.innates.ConditionalStackInnateSkillsRegistry;
import sleys.efedp.system.innates.HoldableInnateSkillsRegistry;
import sleys.efedp.system.innates.SimpleInnateSkillsRegistry;
import sleys.efedp.system.skills.ModifyGuardsApplier;
import sleys.efedp.system.skills.ModifyPassivesApplier;
import sleys.efedp.system.weapons.WeaponCategoriesRegistry;
import sleys.efedp.system.weapons.json.WeaponItemsPropertiesBuilder;
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
        event.addListener(new WeaponItemsPropertiesBuilder());
    }

    private static void RegisterHookers() {
        SkillBuilderHook.registerAssignor(ModifyGuardsApplier::addAnyParameterToGuards);
        SkillBuilderHook.registerAssignor(ModifyPassivesApplier::addAnyParameterToSkill);
    }

    private static void RegistryEnumerations() {
        WeaponCategory.ENUM_MANAGER.registerEnumCls(
                ExtendedDatapacks.MODID,
                WeaponCategoriesRegistry.class
        );
    }

    private static void InitializeRegistries(IEventBus modBus) {
        SimpleInnateSkillsRegistry.initialize(modBus);
        modBus.register(SimpleInnateSkillsRegistry.class);

        ConditionalInnateSkillsRegistry.initialize(modBus);
        modBus.register(ConditionalInnateSkillsRegistry.class);

        HoldableInnateSkillsRegistry.initialize(modBus);
        modBus.register(HoldableInnateSkillsRegistry.class);

        ConditionalStackInnateSkillsRegistry.initialize(modBus);
        modBus.register(ConditionalStackInnateSkillsRegistry.class);

        modBus.register(AnimationRegistryOperations.class);
    }
}

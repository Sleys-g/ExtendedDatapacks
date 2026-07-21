package sleys.efedp.bootstrap;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import sleys.efedp.main.ExtendedDatapacks;
import sleys.efedp.system.animations.AnimationRegistryOperations;
import sleys.efedp.system.innates.ExtendedDatapacksRegistryConditionalInnateSkills;
import sleys.efedp.system.innates.ExtendedDatapacksRegistryHoldableInnateSkills;
import sleys.efedp.system.innates.ExtendedDatapacksRegistrySimpleInnateSkills;
import sleys.efedp.system.innates.ExtendedDatapacksRegistryStacksConditionalInnateSkills;
import sleys.efedp.system.skills.ExtendedDatapacksRegistryGuards;
import sleys.efedp.system.skills.ExtendedDatapacksRegistryPassives;
import sleys.efedp.system.weapons.ExtendedDatapacksRegistryWeaponCategories;
import sleys.efedp.system.weapons.json.WeaponItemsProperties;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

public class BootstrapCommon {

    protected static void Initialize(IEventBus modBus) {
        ExtendedDatapacks.LOGGER.info("[Extended Datapacks - Bootstrap] Initializing Common Systems...");
        RegistryEnumerations();
        InitializeRegistries(modBus);
        MinecraftForge.EVENT_BUS.addListener(BootstrapCommon::onReloadEvent);
    }

    private static void onReloadEvent(AddReloadListenerEvent event) {
        event.addListener(new WeaponItemsProperties());
    }

    private static void RegistryEnumerations() {
        WeaponCategory.ENUM_MANAGER.registerEnumCls(
                ExtendedDatapacks.MODID,
                ExtendedDatapacksRegistryWeaponCategories.class
        );
    }

    private static void InitializeRegistries(IEventBus modBus) {
        modBus.register(ExtendedDatapacksRegistrySimpleInnateSkills.class);
        modBus.register(ExtendedDatapacksRegistryConditionalInnateSkills.class);
        modBus.register(ExtendedDatapacksRegistryHoldableInnateSkills.class);
        modBus.register(ExtendedDatapacksRegistryStacksConditionalInnateSkills.class);
        modBus.register(ExtendedDatapacksRegistryPassives.class);
        modBus.register(ExtendedDatapacksRegistryGuards.class);

        modBus.register(AnimationRegistryOperations.class); /// BETA
    }
}

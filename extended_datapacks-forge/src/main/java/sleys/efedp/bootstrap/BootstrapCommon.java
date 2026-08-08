package sleys.efedp.bootstrap;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.animations.AnimationRegistryOperations;
import sleys.efedp.system.innates.*;
import sleys.efedp.system.skills.ModifyGuardsApplier;
import sleys.efedp.system.skills.ModifyPassivesApplier;
import sleys.efedp.system.weapons.WeaponCategoriesRegistry;
import sleys.efedp.system.weapons.json.WeaponItemsPropertiesBuilder;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

public class BootstrapCommon {

    protected static void Initialize(IEventBus modBus) {
        ExtendedDatapacks.LOGGER.info("[Extended Datapacks - Bootstrap] Initializing Common Systems...");
        RegistryEnumerations();
        InitializeRegistries(modBus);
        MinecraftForge.EVENT_BUS.addListener(BootstrapCommon::onReloadEvent);
    }

    private static void onReloadEvent(AddReloadListenerEvent event) {
        event.addListener(new WeaponItemsPropertiesBuilder());
    }

    private static void RegistryEnumerations() {
        WeaponCategory.ENUM_MANAGER.registerEnumCls(
                ExtendedDatapacks.MODID,
                WeaponCategoriesRegistry.class
        );
    }

    private static void InitializeRegistries(IEventBus modBus) {
        modBus.register(SimpleInnateSkillsRegistry.class);
        modBus.register(HoldableInnateSkillsRegistry.class);
        modBus.register(ConditionalInnateSkillsRegistry.class);
        modBus.register(ConditionalStackInnateSkillsRegistry.class);

        modBus.register(ModifyPassivesApplier.class);
        modBus.register(ModifyGuardsApplier.class);

        modBus.register(AnimationRegistryOperations.class); /// BETA
    }
}

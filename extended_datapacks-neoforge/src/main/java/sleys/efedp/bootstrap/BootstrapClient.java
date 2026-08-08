package sleys.efedp.bootstrap;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.common.NeoForge;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.skills.RegistryCategoryIcon;
import sleys.efedp.system.visuals.OverlayPacketCoroutine;
import sleys.efedp.system.visuals.ShaderPacketCoroutine;
import sleys.efedp.system.visuals.json.OverlayAssetPacksBuilder;
import sleys.efedp.system.visuals.json.ShaderAssetsPacksBuilder;
import sleys.efedp.system.weapons.WeaponsModelsRegistry;
import sleys.efedp.system.weapons.WeaponItemsPropertiesCoroutine;
import sleys.efedp.system.weapons.WeaponsPassiveParticlesCoroutine;
import sleys.efedp.system.weapons.json.WeaponAdvancedSwingTrailBuilder;
import sleys.efedp.system.weapons.json.WeaponPerStyleModelBakerBuilder;
import sleys.efedp.system.weapons.json.WeaponsPassiveParticleBuilder;
import sleys.sl.epicfight.mutator.WeaponCategoryIconHook;
import sleys.sl.library.annotations.ErrorHandled;
import sleys.sl.library.execution.task.Coroutine;

public class BootstrapClient {

    @ErrorHandled
    protected static IEventBus Initialize(IEventBus modBus) {
        ExtendedDatapacks.LOGGER.info("[Extended Datapacks - Bootstrap Client] Initializing Client Systems...");
        registerClientEvents();
        registerClientHookers();
        registerClientBus(modBus);
        NeoForge.EVENT_BUS.addListener(BootstrapClient::registerClientCoroutines);
        return modBus;
    }

    private static void registerClientEvents() {
        NeoForge.EVENT_BUS.register(OverlayAssetPacksBuilder.class);
        NeoForge.EVENT_BUS.register(ShaderAssetsPacksBuilder.class);
    }

    private static void registerClientHookers() {
        WeaponCategoryIconHook.register(RegistryCategoryIcon::AddAnyIconToCategory);
    }

    private static void registerClientBus(IEventBus modBus) {
        modBus.register(WeaponAdvancedSwingTrailBuilder.class);
        modBus.register(WeaponsModelsRegistry.class);
        modBus.register(WeaponPerStyleModelBakerBuilder.class);
        modBus.addListener(BootstrapClient::registerReloadListeners);
    }

    private static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(WeaponsPassiveParticleBuilder.INSTANCE);
    }

    private static void registerClientCoroutines(ClientPlayerNetworkEvent.LoggingIn event) {
        Coroutine.CLIENT.start(new ShaderPacketCoroutine());
        Coroutine.CLIENT.start(new OverlayPacketCoroutine());
        Coroutine.CLIENT.start(new WeaponItemsPropertiesCoroutine());
        Coroutine.CLIENT.start(new WeaponsPassiveParticlesCoroutine());
    }
}

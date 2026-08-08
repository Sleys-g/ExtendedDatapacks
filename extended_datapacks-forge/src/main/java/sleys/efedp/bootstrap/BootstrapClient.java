package sleys.efedp.bootstrap;

import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
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
import sleys.sl.library.annotations.ErrorHandled;
import sleys.sl.library.execution.task.Coroutine;

public class BootstrapClient {

    @ErrorHandled
    protected static IEventBus Initialize(IEventBus modBus) {
        ExtendedDatapacks.LOGGER.info("[Extended Datapacks - Bootstrap Client] Initializing Client Systems...");
        registerClientEvents();
        registerClientBus(modBus);
        MinecraftForge.EVENT_BUS.addListener(BootstrapClient::registerClientCoroutines);

        return modBus;
    }

    private static void registerClientEvents() {
        MinecraftForge.EVENT_BUS.register(OverlayAssetPacksBuilder.class);
        MinecraftForge.EVENT_BUS.register(ShaderAssetsPacksBuilder.class);
    }

    private static void registerClientBus(IEventBus modBus) {
        modBus.register(WeaponAdvancedSwingTrailBuilder.class);
        modBus.register(WeaponsModelsRegistry.class);
        modBus.register(WeaponPerStyleModelBakerBuilder.class);
        modBus.register(RegistryCategoryIcon.class);
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

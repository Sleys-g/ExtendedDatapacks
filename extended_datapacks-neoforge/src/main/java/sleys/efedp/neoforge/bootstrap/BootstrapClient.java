package sleys.efedp.neoforge.bootstrap;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.common.NeoForge;
import sleys.efedp.neoforge.ExtendedDatapacks;
import sleys.efedp.neoforge.system.animations.AnimationRegistryOperations;
import sleys.efedp.neoforge.system.skills.icon.RegistryCategoryIcon;
import sleys.efedp.neoforge.system.visuals.overlays.OverlayPacketCoroutine;
import sleys.efedp.neoforge.system.visuals.shaders.ShaderPacketCoroutine;
import sleys.efedp.neoforge.system.visuals.overlays.builder.OverlayAssetPacksBuilder;
import sleys.efedp.neoforge.system.visuals.shaders.builder.ShaderAssetsPacksBuilder;
import sleys.efedp.neoforge.system.visuals.models.WeaponsModelsRegistry;
import sleys.efedp.neoforge.system.items.WeaponItemsPropertiesCoroutine;
import sleys.efedp.neoforge.system.visuals.particles.WeaponsPassiveParticlesCoroutine;
import sleys.efedp.neoforge.system.visuals.trails.builder.WeaponAdvancedSwingTrailBuilder;
import sleys.efedp.neoforge.system.visuals.models.builder.WeaponPerStyleModelBakerBuilder;
import sleys.efedp.neoforge.system.visuals.particles.builder.WeaponsPassiveParticleBuilder;
import sleys.sl.epicfight.mutator.WeaponCategoryIconHook;
import sleys.sl.library.annotations.ErrorHandled;
import sleys.sl.library.execution.task.Coroutine;

public class BootstrapClient {

    @ErrorHandled
    protected static void Initialize(IEventBus modBus) {
        ExtendedDatapacks.LOGGER.info("[Extended Datapacks - Bootstrap Client] Initializing Client Systems...");
        registerClientEvents();
        registerClientHookers();
        registerClientBus(modBus);
    }

    private static void registerClientEvents() {
        NeoForge.EVENT_BUS.register(OverlayAssetPacksBuilder.class);
        NeoForge.EVENT_BUS.register(ShaderAssetsPacksBuilder.class);
        NeoForge.EVENT_BUS.addListener(BootstrapClient::registerClientCoroutines);
    }

    private static void registerClientHookers() {
        WeaponCategoryIconHook.register(RegistryCategoryIcon::AddAnyIconToCategory);
    }

    private static void registerClientBus(IEventBus modBus) {
        modBus.register(WeaponAdvancedSwingTrailBuilder.class);
        modBus.register(WeaponsModelsRegistry.class);
        modBus.register(WeaponPerStyleModelBakerBuilder.class);
        modBus.addListener(BootstrapClient::registerReloadListeners);
        modBus.addListener(AnimationRegistryOperations::onModifierAnimationsClient); /// Animations modification
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

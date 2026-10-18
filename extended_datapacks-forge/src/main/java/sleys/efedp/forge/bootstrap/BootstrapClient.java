package sleys.efedp.forge.bootstrap;

import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import sleys.efedp.forge.ExtendedDatapacks;
import sleys.efedp.forge.system.animations.AnimationRegistryOperations;
import sleys.efedp.forge.system.skills.icon.RegistryCategoryIcon;
import sleys.efedp.forge.system.visuals.overlays.OverlayPacketCoroutine;
import sleys.efedp.forge.system.visuals.shaders.ShaderPacketCoroutine;
import sleys.efedp.forge.system.visuals.overlays.builder.OverlayAssetPacksBuilder;
import sleys.efedp.forge.system.visuals.shaders.builder.ShaderAssetsPacksBuilder;
import sleys.efedp.forge.system.visuals.models.WeaponsModelsRegistry;
import sleys.efedp.forge.system.items.WeaponItemsPropertiesCoroutine;
import sleys.efedp.forge.system.visuals.particles.WeaponsPassiveParticlesCoroutine;
import sleys.efedp.forge.system.visuals.trails.builder.WeaponAdvancedSwingTrailBuilder;
import sleys.efedp.forge.system.visuals.models.builder.WeaponPerStyleModelBakerBuilder;
import sleys.efedp.forge.system.visuals.particles.builder.WeaponsPassiveParticleBuilder;
import sleys.sl.library.annotations.ErrorHandled;
import sleys.sl.library.execution.task.Coroutine;

public class BootstrapClient {

    @ErrorHandled
    protected static IEventBus Initialize(IEventBus modBus) {
        ExtendedDatapacks.LOGGER.info("[Extended Datapacks - Bootstrap Client] Initializing Client Systems...");
        registerClientEvents();
        registerClientBus(modBus);
        return modBus;
    }

    private static void registerClientEvents() {
        MinecraftForge.EVENT_BUS.register(OverlayAssetPacksBuilder.class);
        MinecraftForge.EVENT_BUS.register(ShaderAssetsPacksBuilder.class);
        MinecraftForge.EVENT_BUS.addListener(BootstrapClient::registerClientCoroutines);
    }

    private static void registerClientBus(IEventBus modBus) {
        modBus.register(WeaponAdvancedSwingTrailBuilder.class);
        modBus.register(WeaponsModelsRegistry.class);
        modBus.register(WeaponPerStyleModelBakerBuilder.class);
        modBus.register(RegistryCategoryIcon.class);
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

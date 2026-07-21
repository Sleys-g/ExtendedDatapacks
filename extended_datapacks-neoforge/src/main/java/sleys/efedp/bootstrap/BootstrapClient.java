package sleys.efedp.bootstrap;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.common.NeoForge;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.skills.ExtendedDatapacksRegistryCategoryIcon;
import sleys.efedp.system.visuals.OverlayPacketCoroutineRunner;
import sleys.efedp.system.visuals.ShaderPacketCoroutineRunner;
import sleys.efedp.system.visuals.json.OverlayAssetPacksSystem;
import sleys.efedp.system.visuals.json.ShaderAssetsPacksSystem;
import sleys.efedp.system.weapons.ExtendedDatapacksRegistryWeaponsModels;
import sleys.efedp.system.weapons.WeaponItemsPropertiesCoroutineRunner;
import sleys.efedp.system.weapons.WeaponsPassiveParticlesCoroutineRunner;
import sleys.efedp.system.weapons.json.WeaponAdvancedSwingTrail;
import sleys.efedp.system.weapons.json.WeaponPerStyleModelBaker;
import sleys.efedp.system.weapons.json.WeaponsPassiveParticle;
import sleys.sl.epicfight.mutator.WeaponCategoryIconHook;
import sleys.sl.library.execution.task.Coroutine;

@OnlyIn(Dist.CLIENT)
public class BootstrapClient {

    protected static IEventBus Initialize(IEventBus modBus) {
        ExtendedDatapacks.LOGGER.info("[Extended Datapacks - Bootstrap Client] Initializing Client Systems...");
        registerClientEvents();
        registerClientHookers();
        registerClientBus(modBus);
        NeoForge.EVENT_BUS.addListener(BootstrapClient::registerClientCoroutines);
        return modBus;
    }

    private static void registerClientEvents() {
        NeoForge.EVENT_BUS.register(OverlayAssetPacksSystem.class);
        NeoForge.EVENT_BUS.register(ShaderAssetsPacksSystem.class);
    }

    private static void registerClientHookers() {
        WeaponCategoryIconHook.register(ExtendedDatapacksRegistryCategoryIcon::AddAnyIconToCategory);
    }

    private static void registerClientBus(IEventBus modBus) {
        modBus.register(WeaponAdvancedSwingTrail.class);
        modBus.register(ExtendedDatapacksRegistryWeaponsModels.class);
        modBus.register(WeaponPerStyleModelBaker.class);
        modBus.addListener(BootstrapClient::registerReloadListeners);
    }

    private static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(WeaponsPassiveParticle.INSTANCE);
    }

    private static void registerClientCoroutines(ClientPlayerNetworkEvent.LoggingIn event) {
        Coroutine.CLIENT.start(new ShaderPacketCoroutineRunner());
        Coroutine.CLIENT.start(new OverlayPacketCoroutineRunner());
        Coroutine.CLIENT.start(new WeaponItemsPropertiesCoroutineRunner());
        Coroutine.CLIENT.start(new WeaponsPassiveParticlesCoroutineRunner());
    }
}

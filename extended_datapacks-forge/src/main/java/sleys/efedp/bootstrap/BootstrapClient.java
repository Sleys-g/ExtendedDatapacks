package sleys.efedp.bootstrap;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import sleys.efedp.main.ExtendedDatapacks;
import sleys.efedp.system.skills.ExtendedDatapacksRegistryCategoryIcon;
import sleys.efedp.system.visuals.OverlayPacketCoroutineRunner;
import sleys.efedp.system.visuals.ShaderPacketCoroutineRunner;
import sleys.efedp.system.visuals.json.OverlayAssetPacksSystem;
import sleys.efedp.system.visuals.json.ShaderAssetsPacksSystem;
import sleys.efedp.system.weapons.ExtendedDatapacksRegistryWeaponsModels;
import sleys.efedp.system.weapons.WeaponItemsPropertiesCoroutineRunner;
import sleys.efedp.system.weapons.json.WeaponAdvancedSwingTrail;
import sleys.efedp.system.weapons.json.WeaponPerStyleModelBaker;
import sleys.sl.library.runtime.task.Coroutine;

@OnlyIn(Dist.CLIENT)
public class BootstrapClient {

    protected static void Initialize(IEventBus modBus) {
        ExtendedDatapacks.LOGGER.info("[Extended Datapacks - Bootstrap Client] Initializing Client Systems...");
        registerClientEvents();
        registerClientBus(modBus);
        MinecraftForge.EVENT_BUS.addListener(BootstrapClient::registerClientCoroutines);
    }

    private static void registerClientEvents() {
        MinecraftForge.EVENT_BUS.register(OverlayAssetPacksSystem.class);
        MinecraftForge.EVENT_BUS.register(ShaderAssetsPacksSystem.class);
    }

    private static void registerClientBus(IEventBus modBus) {
        modBus.register(WeaponAdvancedSwingTrail.class);
        modBus.register(ExtendedDatapacksRegistryWeaponsModels.class);
        modBus.register(WeaponPerStyleModelBaker.class);
        modBus.register(ExtendedDatapacksRegistryCategoryIcon.class);
    }

    public static void registerClientCoroutines(ClientPlayerNetworkEvent.LoggingIn event) {
        Coroutine.CLIENT.start(new ShaderPacketCoroutineRunner());
        Coroutine.CLIENT.start(new OverlayPacketCoroutineRunner());
        Coroutine.CLIENT.start(new WeaponItemsPropertiesCoroutineRunner());
    }
}

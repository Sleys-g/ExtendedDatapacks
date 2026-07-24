package sleys.efedp.bootstrap;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.thirdparty.combatevolution.ExtendedDatapacksRegistryExecution;
import sleys.efedp.system.thirdparty.wom.json.WoMSkillAccessorBuilder;
import sleys.sl.library.SLLCore;

public class BootstrapThirdParties {
    public static boolean COMBAT_EVOLUTION = false;
    public static boolean WEAPONS_OF_MIRACLE = false;

    protected static void Initialize(IEventBus modBus) {
        startThirdParties(modBus);
    }

    private static void startThirdParties(IEventBus modBus) {
        COMBAT_EVOLUTION = SLLCore.getIfExist("combat_evolution");
        if (COMBAT_EVOLUTION) {
            ExtendedDatapacks.LOGGER.info("[Extended Datapacks - Bootstrap] Initializing registry for Combat Evolution");
            startCombatEvolution(modBus);
        }

        WEAPONS_OF_MIRACLE = SLLCore.getIfExist("wom");
        if (WEAPONS_OF_MIRACLE) {
            ExtendedDatapacks.LOGGER.info("[Extended Datapacks - Bootstrap] Initializing capabilities for Weapons Of Miracles");
            MinecraftForge.EVENT_BUS.addListener(BootstrapThirdParties::startWeaponsOfMiracle);
        }
    }

    private static void startCombatEvolution(IEventBus modBus) {
        modBus.register(ExtendedDatapacksRegistryExecution.class);
    }

    private static void startWeaponsOfMiracle(AddReloadListenerEvent event) {
        event.addListener(new WoMSkillAccessorBuilder());
    }
}

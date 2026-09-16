package sleys.efedp.forge.bootstrap;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import sleys.efedp.forge.ExtendedDatapacks;
import sleys.efedp.forge.system.thirdparty.combatevolution.RegistryExecutionAnimations;
import sleys.efedp.forge.system.thirdparty.wom.json.WoMSkillAccessorBuilder;
import sleys.sl.library.SLLCore;

public class BootstrapThirdParties {
    public static boolean COMBAT_EVOLUTION = false;
    public static boolean WEAPONS_OF_MIRACLE = false;

    protected static void Initialize(IEventBus modBus) {
        ExtendedDatapacks.LOGGER.info("[Extended Datapacks - Bootstrap] Initializing Third Parties Integration...");
        startToSearching();
        BootstrapThirdPartiesBuilds.Initialize();
        startThirdParties(modBus);
    }

    private static void startToSearching() {
        COMBAT_EVOLUTION = SLLCore.getIfExist("combat_evolution");
        WEAPONS_OF_MIRACLE = SLLCore.getIfExist("wom");
    }

    private static void startThirdParties(IEventBus modBus) {
        if (COMBAT_EVOLUTION) {
            ExtendedDatapacks.LOGGER.info("[Extended Datapacks - Bootstrap] Initializing registry for Combat Evolution");
            startCombatEvolution(modBus);
        }

        if (WEAPONS_OF_MIRACLE) {
            ExtendedDatapacks.LOGGER.info("[Extended Datapacks - Bootstrap] Initializing capabilities for Weapons Of Miracles");
            MinecraftForge.EVENT_BUS.addListener(BootstrapThirdParties::startWeaponsOfMiracle);
        }
    }

    private static void startCombatEvolution(IEventBus modBus) {
        modBus.register(RegistryExecutionAnimations.class);
    }

    private static void startWeaponsOfMiracle(AddReloadListenerEvent event) {
        event.addListener(new WoMSkillAccessorBuilder());
    }
}

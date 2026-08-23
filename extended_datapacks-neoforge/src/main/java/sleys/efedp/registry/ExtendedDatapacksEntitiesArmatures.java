package sleys.efedp.registry;

import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import yesman.epicfight.gameasset.Armatures;

public class ExtendedDatapacksEntitiesArmatures {

    public static void registerEntitiesArmatures(FMLCommonSetupEvent event) {
        Armatures.registerEntityTypeArmature(
                ExtendedDatapacksEntities.OWNABLE_WITHER_GHOST.get(), Armatures.WITHER
        );
    }
}

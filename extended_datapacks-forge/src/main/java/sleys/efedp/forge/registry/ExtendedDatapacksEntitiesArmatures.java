package sleys.efedp.forge.registry;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import yesman.epicfight.gameasset.Armatures;

public class ExtendedDatapacksEntitiesArmatures {

    public static void registerEntitiesArmatures(FMLCommonSetupEvent event) {
        Armatures.registerEntityTypeArmature(
                ExtendedDatapacksEntities.OWNABLE_WITHER_GHOST.get(), Armatures.WITHER
        );
    }
}

package sleys.efedp.neoforge.registry;

import sleys.efedp.neoforge.world.entitypatchs.OwnableWitherGhostPatch;
import yesman.epicfight.api.event.types.registry.EntityPatchRegistryEvent;

public class ExtendedDatapacksPatchesEntities {

    public static void registerPatchesEntities(EntityPatchRegistryEvent patchesEntities) {
        patchesEntities.registerEntityPatch(ExtendedDatapacksEntities.OWNABLE_WITHER_GHOST.get(), OwnableWitherGhostPatch::new);
    }
}

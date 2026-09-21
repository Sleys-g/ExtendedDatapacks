package sleys.efedp.neoforge.registry;

import sleys.efedp.neoforge.world.entitypatchs.OwnableAnimatedPlayerPatch;
import sleys.efedp.neoforge.world.entitypatchs.OwnableClonePlayerPatch;
import sleys.efedp.neoforge.world.entitypatchs.OwnableWitherGhostPatch;
import yesman.epicfight.api.event.types.registry.EntityPatchRegistryEvent;

public class ExtendedDatapacksPatchesEntities {

    public static void registerPatchesEntities(EntityPatchRegistryEvent patchesEntities) {
        patchesEntities.registerEntityPatch(ExtendedDatapacksEntities.OWNABLE_WITHER_GHOST.get(), OwnableWitherGhostPatch::new);
        patchesEntities.registerEntityPatch(ExtendedDatapacksEntities.OWNABLE_CLONE_PLAYER.get(), OwnableClonePlayerPatch::new);
        patchesEntities.registerEntityPatch(ExtendedDatapacksEntities.OWNABLE_ANIMATED_PLAYER.get(), OwnableAnimatedPlayerPatch::new);
    }
}

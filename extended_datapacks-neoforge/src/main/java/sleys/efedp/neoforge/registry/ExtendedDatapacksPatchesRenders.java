package sleys.efedp.neoforge.registry;

import sleys.efedp.neoforge.client.render.entitypatchs.OwnableWitherGhostPatchRender;
import yesman.epicfight.api.client.event.types.registry.RegisterPatchedRenderersEvent;

public class ExtendedDatapacksPatchesRenders {

    public static void registerPatchesRenders(RegisterPatchedRenderersEvent.AddEntity patchedRenderersEvent) {
        patchedRenderersEvent.addPatchedEntityRenderer(
                ExtendedDatapacksEntities.OWNABLE_WITHER_GHOST.get(),
                entityType -> new OwnableWitherGhostPatchRender()
        );
    }
}

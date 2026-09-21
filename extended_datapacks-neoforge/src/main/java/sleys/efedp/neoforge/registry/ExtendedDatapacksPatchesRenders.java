package sleys.efedp.neoforge.registry;

import sleys.efedp.neoforge.client.render.entitypatchs.OwnableWitherGhostPatchRender;
import yesman.epicfight.api.client.event.types.registry.RegisterPatchedRenderersEvent;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.client.renderer.patched.entity.PHumanoidRenderer;

public class ExtendedDatapacksPatchesRenders {

    public static void registerPatchesRenders(RegisterPatchedRenderersEvent.AddEntity patchedRenderersEvent) {
        var context = patchedRenderersEvent.getContext();
        patchedRenderersEvent.addPatchedEntityRenderer(
                ExtendedDatapacksEntities.OWNABLE_WITHER_GHOST.get(),
                entityType -> new OwnableWitherGhostPatchRender()
        );
        patchedRenderersEvent.addPatchedEntityRenderer(
                ExtendedDatapacksEntities.OWNABLE_CLONE_PLAYER.get(),
                entityType -> new PHumanoidRenderer<>(Meshes.BIPED, context, entityType)
                        .initLayerLast(context, entityType)
        );
        patchedRenderersEvent.addPatchedEntityRenderer(
                ExtendedDatapacksEntities.OWNABLE_ANIMATED_PLAYER.get(),
                entityType -> new PHumanoidRenderer<>(Meshes.BIPED, context, entityType)
                        .initLayerLast(context, entityType)
        );
    }
}

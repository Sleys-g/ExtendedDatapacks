package sleys.efedp.forge.registry;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import sleys.efedp.forge.client.render.entitypatchs.OwnableWitherGhostPatchRender;
import yesman.epicfight.api.client.forgeevent.PatchedRenderersEvent;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.client.renderer.patched.entity.PHumanoidRenderer;

public class ExtendedDatapacksPatchesRenders {

    @SubscribeEvent
    public static void registerPatchesRenders(PatchedRenderersEvent.Add patchedRenderersEvent) {
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

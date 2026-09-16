package sleys.efedp.forge.registry;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import sleys.efedp.forge.client.render.entitypatchs.OwnableWitherGhostPatchRender;
import yesman.epicfight.api.client.forgeevent.PatchedRenderersEvent;

public class ExtendedDatapacksPatchesRenders {

    @SubscribeEvent
    public static void registerPatchesRenders(PatchedRenderersEvent.Add patchedRenderersEvent) {
        patchedRenderersEvent.addPatchedEntityRenderer(
                ExtendedDatapacksEntities.OWNABLE_WITHER_GHOST.get(),
                entityType -> new OwnableWitherGhostPatchRender()
        );
    }
}

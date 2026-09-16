package sleys.efedp.neoforge.registry;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import sleys.efedp.neoforge.client.render.entities.OwnableWitherGhostRender;

public final class ExtendedDatapacksRenders {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ExtendedDatapacksEntities.OWNABLE_WITHER_GHOST.get(), OwnableWitherGhostRender::new);
    }
}

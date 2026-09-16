package sleys.efedp.forge.registry;

import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sleys.efedp.forge.client.render.entities.OwnableWitherGhostRender;

public final class ExtendedDatapacksRenders {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ExtendedDatapacksEntities.OWNABLE_WITHER_GHOST.get(), OwnableWitherGhostRender::new);
    }
}

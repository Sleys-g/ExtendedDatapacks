package sleys.efedp.neoforge.registry;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import sleys.efedp.neoforge.client.render.entities.OwnableAnimatedPlayerRenderer;
import sleys.efedp.neoforge.client.render.entities.OwnableClonePlayerRenderer;
import sleys.efedp.neoforge.client.render.entities.OwnableWitherGhostRender;

public final class ExtendedDatapacksRenders {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ExtendedDatapacksEntities.OWNABLE_WITHER_GHOST.get(), OwnableWitherGhostRender::new);
        event.registerEntityRenderer(ExtendedDatapacksEntities.OWNABLE_CLONE_PLAYER.get(), OwnableClonePlayerRenderer::new);
        event.registerEntityRenderer(ExtendedDatapacksEntities.OWNABLE_ANIMATED_PLAYER.get(), OwnableAnimatedPlayerRenderer::new);
    }
}

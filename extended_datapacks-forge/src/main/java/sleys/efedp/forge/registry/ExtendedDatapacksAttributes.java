package sleys.efedp.forge.registry;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sleys.efedp.forge.world.entities.OwnableWitherGhost;
import sleys.efedp.forge.world.entitypatchs.OwnableWitherGhostPatch;

public class ExtendedDatapacksAttributes {

    @SubscribeEvent
    public static void entityAttributeCreationEvent(EntityAttributeCreationEvent event) {
        event.put(ExtendedDatapacksEntities.OWNABLE_WITHER_GHOST.get(), OwnableWitherGhost.createAttributes().build());
    }

    @SubscribeEvent
    public static void entityAttributeModificationEvent(EntityAttributeModificationEvent event) {
        OwnableWitherGhostPatch.initAttributes(event);
    }
}

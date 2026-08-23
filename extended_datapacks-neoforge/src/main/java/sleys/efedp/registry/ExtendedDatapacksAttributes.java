package sleys.efedp.registry;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import sleys.efedp.world.entities.OwnableWitherGhost;
import sleys.efedp.world.entitypatchs.OwnableWitherGhostPatch;

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

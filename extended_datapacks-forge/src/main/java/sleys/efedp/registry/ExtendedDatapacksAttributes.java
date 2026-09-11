package sleys.efedp.registry;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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

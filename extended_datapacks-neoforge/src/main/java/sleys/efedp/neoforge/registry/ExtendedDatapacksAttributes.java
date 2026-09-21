package sleys.efedp.neoforge.registry;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import sleys.efedp.neoforge.world.entities.OwnableAnimatedPlayer;
import sleys.efedp.neoforge.world.entities.OwnableClonePlayer;
import sleys.efedp.neoforge.world.entities.OwnableWitherGhost;
import sleys.efedp.neoforge.world.entitypatchs.OwnableAnimatedPlayerPatch;
import sleys.efedp.neoforge.world.entitypatchs.OwnableClonePlayerPatch;
import sleys.efedp.neoforge.world.entitypatchs.OwnableWitherGhostPatch;

public class ExtendedDatapacksAttributes {

    @SubscribeEvent
    public static void entityAttributeCreationEvent(EntityAttributeCreationEvent event) {
        event.put(ExtendedDatapacksEntities.OWNABLE_WITHER_GHOST.get(), OwnableWitherGhost.createAttributes().build());
        event.put(ExtendedDatapacksEntities.OWNABLE_CLONE_PLAYER.get(), OwnableClonePlayer.createAttributes().build());
        event.put(ExtendedDatapacksEntities.OWNABLE_ANIMATED_PLAYER.get(), OwnableAnimatedPlayer.createAttributes().build());
    }

    @SubscribeEvent
    public static void entityAttributeModificationEvent(EntityAttributeModificationEvent event) {
        OwnableWitherGhostPatch.initAttributes(event);
        OwnableClonePlayerPatch.initAttributes(event);
        OwnableAnimatedPlayerPatch.initAttributes(event);
    }
}

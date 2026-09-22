package sleys.efedp.forge.registry;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sleys.efedp.forge.world.entities.OwnableAnimatedPlayer;
import sleys.efedp.forge.world.entities.OwnableClonePlayer;
import sleys.efedp.forge.world.entities.OwnableWitherGhost;
import sleys.efedp.forge.world.entitypatchs.OwnableAnimatedPlayerPatch;
import sleys.efedp.forge.world.entitypatchs.OwnableClonePlayerPatch;
import sleys.efedp.forge.world.entitypatchs.OwnableWitherGhostPatch;

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

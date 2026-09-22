package sleys.efedp.forge.registry;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import sleys.efedp.forge.world.entitypatchs.OwnableAnimatedPlayerPatch;
import sleys.efedp.forge.world.entitypatchs.OwnableClonePlayerPatch;
import sleys.efedp.forge.world.entitypatchs.OwnableWitherGhostPatch;
import yesman.epicfight.api.forgeevent.EntityPatchRegistryEvent;

public class ExtendedDatapacksPatchesEntities {

    @SubscribeEvent
    public static void registerPatchesEntities(EntityPatchRegistryEvent patchesEntities) {
        var entry = patchesEntities.getTypeEntry();
        entry.put(ExtendedDatapacksEntities.OWNABLE_WITHER_GHOST.get(), (entityIn) -> OwnableWitherGhostPatch::new);
        entry.put(ExtendedDatapacksEntities.OWNABLE_CLONE_PLAYER.get(), (entityIn) -> OwnableClonePlayerPatch::new);
        entry.put(ExtendedDatapacksEntities.OWNABLE_ANIMATED_PLAYER.get(), (entityIn) -> OwnableAnimatedPlayerPatch::new);
    }
}

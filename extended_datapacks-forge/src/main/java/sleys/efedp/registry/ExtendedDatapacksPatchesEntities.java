package sleys.efedp.registry;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import sleys.efedp.world.entitypatchs.OwnableWitherGhostPatch;
import yesman.epicfight.api.forgeevent.EntityPatchRegistryEvent;

public class ExtendedDatapacksPatchesEntities {

    @SubscribeEvent
    public static void registerPatchesEntities(EntityPatchRegistryEvent patchesEntities) {
        var entry = patchesEntities.getTypeEntry();
        entry.put(ExtendedDatapacksEntities.OWNABLE_WITHER_GHOST.get(), (entityIn) -> OwnableWitherGhostPatch::new);
    }
}

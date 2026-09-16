package sleys.efedp.forge.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import sleys.efedp.forge.ExtendedDatapacks;
import sleys.efedp.forge.world.entities.OwnableWitherGhost;

public final class ExtendedDatapacksEntities {

    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(
            Registries.ENTITY_TYPE, ExtendedDatapacks.MODID
    );

    public static final RegistryObject<EntityType<OwnableWitherGhost>> OWNABLE_WITHER_GHOST =
            REGISTRY.register("ownable_wither_ghost",
                    () -> EntityType.Builder.<OwnableWitherGhost>of(OwnableWitherGhost::new, MobCategory.MISC)
                            .fireImmune()
                            .sized(0.9F, 3.5F)
                            .clientTrackingRange(10)
                            .build("ownable_wither_ghost")
            );
}

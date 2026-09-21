package sleys.efedp.neoforge.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import sleys.efedp.neoforge.ExtendedDatapacks;
import sleys.efedp.neoforge.world.entities.OwnableAnimatedPlayer;
import sleys.efedp.neoforge.world.entities.OwnableClonePlayer;
import sleys.efedp.neoforge.world.entities.OwnableWitherGhost;

public final class ExtendedDatapacksEntities {

    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(
            Registries.ENTITY_TYPE, ExtendedDatapacks.MODID
    );

    public static final DeferredHolder<EntityType<?>, EntityType<OwnableWitherGhost>> OWNABLE_WITHER_GHOST =
            REGISTRY.register("ownable_wither_ghost",
                    () -> EntityType.Builder.<OwnableWitherGhost>of(OwnableWitherGhost::new, MobCategory.MISC)
                            .fireImmune()
                            .sized(0.9F, 3.5F)
                            .clientTrackingRange(10)
                            .build("ownable_wither_ghost")
            );

    public static final DeferredHolder<EntityType<?>, EntityType<OwnableClonePlayer>> OWNABLE_CLONE_PLAYER =
            REGISTRY.register("ownable_clone_player",
                    () -> EntityType.Builder.<OwnableClonePlayer>of(OwnableClonePlayer::new, MobCategory.MISC)
                            .sized(0.6F, 1.8F)
                            .clientTrackingRange(10)
                            .build("ownable_clone_player")
            );

    public static final DeferredHolder<EntityType<?>, EntityType<OwnableAnimatedPlayer>> OWNABLE_ANIMATED_PLAYER =
            REGISTRY.register("ownable_animated_player",
                    () -> EntityType.Builder.<OwnableAnimatedPlayer>of(OwnableAnimatedPlayer::new, MobCategory.MISC)
                            .sized(0.6F, 1.8F)
                            .clientTrackingRange(10)
                            .build("ownable_animated_player")
            );
}

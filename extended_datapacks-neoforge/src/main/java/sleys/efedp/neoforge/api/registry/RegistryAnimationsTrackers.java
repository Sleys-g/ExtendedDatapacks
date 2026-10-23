package sleys.efedp.neoforge.api.registry;

import net.neoforged.fml.loading.FMLPaths;
import sleys.efedp.neoforge.system.animations.definitions.AnimationAccessorDefinition;
import sleys.efedp.neoforge.system.animations.definitions.AnimationConfigDefinition;
import sleys.efedp.neoforge.system.animations.definitions.AnimationVirtualizationDefinition;
import sleys.sl.library.util.io.BuildPathOrFile;

import java.nio.file.Path;

public final class RegistryAnimationsTrackers {

    public static final GenericRegistryBuilder<AnimationAccessorDefinition<?>> ANIMATION_REGISTRY = new GenericRegistryBuilder<>(
            AnimationAccessorDefinition.CODEC,
            GenericRegistryBuilder.BuilderContext.WITH_MOD_ID,
            "animations/registry",
            "Animations Registry",
            getConfigAnimationTracker("registry")
    );

    public static final GenericRegistryBuilder<AnimationVirtualizationDefinition<?>> ANIMATION_VIRTUALIZATION = new GenericRegistryBuilder<>(
            AnimationVirtualizationDefinition.CODEC,
            GenericRegistryBuilder.BuilderContext.WITHOUT_MOD_ID,
            "animations/virtualization",
            "Animations Virtualization",
            getConfigAnimationTracker("virtualization")
    );

    public static final GenericRegistryBuilder<AnimationConfigDefinition<?>> ANIMATION_CONFIGURATION = new GenericRegistryBuilder<>(
            AnimationConfigDefinition.CODEC,
            GenericRegistryBuilder.BuilderContext.WITHOUT_MOD_ID,
            "animations/config",
            "Animations Config",
            getConfigAnimationTracker("config")
    );

    private static Path getConfigAnimationTracker(String subfolder) {
        Path dir = FMLPaths.CONFIGDIR.get()
                .resolve("epicfight_edp")
                .resolve("animations")
                .resolve(subfolder);
        BuildPathOrFile.buildPathFolder(dir);
        return dir;
    }
}

package sleys.efedp.neoforge.api.registry;

import net.neoforged.fml.loading.FMLPaths;
import sleys.efedp.neoforge.ExtendedDatapacks;
import sleys.efedp.neoforge.system.animations.definitions.AnimationAccessorDefinition;
import sleys.efedp.neoforge.system.animations.definitions.AnimationConfigDefinition;
import sleys.efedp.neoforge.system.animations.definitions.AnimationVirtualizationDefinition;
import sleys.sl.library.annotations.LegacyFunction;
import sleys.sl.library.util.data.codec.CodecRegistryBuilder;
import sleys.sl.library.util.io.BuildPathOrFile;

import java.nio.file.Path;

public final class RegistryAnimationsTrackers {

    public static final CodecRegistryBuilder<AnimationAccessorDefinition<?>> ANIMATION_REGISTRY =
            new CodecRegistryBuilder<>(
                    ExtendedDatapacks.LOGGER,
                    AnimationAccessorDefinition.CODEC,
                    CodecRegistryBuilder.BuilderContext.WITH_MOD_ID,
                    "animations/registry",
                    "Animations Registry",
                    getConfigAnimationTracker("registry")
            );

    public static final CodecRegistryBuilder<AnimationVirtualizationDefinition<?>> ANIMATION_VIRTUALIZATION =
            new CodecRegistryBuilder<>(
                    ExtendedDatapacks.LOGGER,
                    AnimationVirtualizationDefinition.CODEC,
                    CodecRegistryBuilder.BuilderContext.WITHOUT_MOD_ID,
                    "animations/virtualization",
                    "Animations Virtualization",
                    getConfigAnimationTracker("virtualization")
            );

    @LegacyFunction(since = "2.5.6") @Deprecated(since = "3.0", forRemoval = true)
    public static final CodecRegistryBuilder<AnimationVirtualizationDefinition<?>> ANIMATION_VIRTUALIZATION_LEGACY =
            new CodecRegistryBuilder<>(
                    ExtendedDatapacks.LOGGER,
                    AnimationVirtualizationDefinition.CODEC,
                    CodecRegistryBuilder.BuilderContext.WITHOUT_MOD_ID,
                    "animations/virtualizate",
                    "Animations Virtualization Legacy",
                    getConfigAnimationTracker("virtualization_legacy")
            );

    public static final CodecRegistryBuilder<AnimationConfigDefinition<?>> ANIMATION_CONFIGURATION =
            new CodecRegistryBuilder<>(
                    ExtendedDatapacks.LOGGER,
                    AnimationConfigDefinition.CODEC,
                    CodecRegistryBuilder.BuilderContext.WITHOUT_MOD_ID,
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

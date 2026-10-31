package sleys.efedp.neoforge.api.registry;

import net.neoforged.fml.loading.FMLPaths;
import sleys.efedp.neoforge.ExtendedDatapacks;
import sleys.efedp.neoforge.system.category.definitions.WeaponCategoryRegistryDefinitions;
import sleys.sl.library.util.data.codec.CodecRegistryBuilder;
import sleys.sl.library.util.io.BuildPathOrFile;

import java.nio.file.Path;

public final class RegistryWeaponTrackers {

    public static final CodecRegistryBuilder<WeaponCategoryRegistryDefinitions> WEAPON_CATEGORY_REGISTRY =
            new CodecRegistryBuilder<>(
                    ExtendedDatapacks.LOGGER,
                    WeaponCategoryRegistryDefinitions.CODEC,
                    CodecRegistryBuilder.BuilderContext.WITHOUT_MOD_ID,
                    "weapon_builder/category",
                    "Registry Weapon Category",
                    getWeaponBuilderTracker()
            );

    private static Path getWeaponBuilderTracker() {
        Path dir = FMLPaths.CONFIGDIR.get()
                .resolve("epicfight_edp")
                .resolve("weapon_builder")
                .resolve("category");
        BuildPathOrFile.buildPathFolder(dir);
        return dir;
    }
}

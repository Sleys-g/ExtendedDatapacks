package sleys.efedp.neoforge.api.registry;

import net.neoforged.fml.loading.FMLPaths;
import sleys.efedp.neoforge.system.category.definitions.WeaponCategoryRegistryDefinitions;
import sleys.sl.library.util.io.BuildPathOrFile;

import java.nio.file.Path;

public final class RegistryWeaponTrackers {

    public static final GenericRegistryBuilder<WeaponCategoryRegistryDefinitions> WEAPON_CATEGORY_REGISTRY = new GenericRegistryBuilder<>(
            WeaponCategoryRegistryDefinitions.CODEC,
            GenericRegistryBuilder.BuilderContext.WITHOUT_MOD_ID,
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

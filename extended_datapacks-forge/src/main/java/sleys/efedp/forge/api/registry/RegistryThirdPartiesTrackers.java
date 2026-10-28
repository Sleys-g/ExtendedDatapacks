package sleys.efedp.forge.api.registry;

import net.minecraftforge.fml.loading.FMLPaths;
import sleys.efedp.forge.system.thirdparty.combatevolution.definitions.ExecutionAnimationsDefinitions;
import sleys.sl.library.util.io.BuildPathOrFile;

import java.nio.file.Path;

public final class RegistryThirdPartiesTrackers {

    public static final GenericRegistryBuilder<ExecutionAnimationsDefinitions> EXECUTION_ANIMATIONS_REGISTRY = new GenericRegistryBuilder<>(
            ExecutionAnimationsDefinitions.CODEC,
            GenericRegistryBuilder.BuilderContext.WITHOUT_MOD_ID,
            "third_party/combat_evolution/execution",
            "Execution Animations Registry",
            getWeaponBuilderTracker("combat_evolution", "execution")
    );

    @SuppressWarnings("SameParameterValue")
    private static Path getWeaponBuilderTracker(String category, String subfolder) {
        Path dir = FMLPaths.CONFIGDIR.get()
                .resolve("epicfight_edp")
                .resolve("third_party")
                .resolve(category)
                .resolve(subfolder);
        BuildPathOrFile.buildPathFolder(dir);
        return dir;
    }
}

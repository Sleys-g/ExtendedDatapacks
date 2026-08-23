package sleys.efedp.bootstrap;

import net.minecraftforge.fml.loading.FMLPaths;
import sleys.efedp.system.thirdparty.combatevolution.json.ExecutionAnimationBuilder;
import sleys.sl.library.util.io.BuildPathOrFile;

import java.nio.file.Path;
import java.util.function.Consumer;

public class BootstrapThirdPartiesBuilds {
    protected static void Initialize() {
        startThirdPartyBuilds();
    }

    @SuppressWarnings("all")
    private static void buildThirdPartyConfigTracker(String category, String subfolder, Consumer<Path> tracker) {
        Path dir = FMLPaths.CONFIGDIR.get()
                .resolve("epicfight_edp")
                .resolve("third_party")
                .resolve(category)
                .resolve(subfolder);
        BuildPathOrFile.buildPathFolder(dir);
        tracker.accept(dir);
    }

    private static void startThirdPartyBuilds() {
        buildThirdPartyConfigTracker("combat_evolution", "execution", ExecutionAnimationBuilder::startToTracking);
    }
}

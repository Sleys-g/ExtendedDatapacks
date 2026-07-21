package sleys.efedp.system.animations.json.definitions;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.animations.json.definitions.config.AnimationConfigDefinition;
import sleys.sl.datadriven.api.SLDataDrivenAPI;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class AnimationsConfigBuilder {
    private static final String SL_FOLDER_KEY = "animations/config";
    private static final Map<String, List<AnimationConfigDefinition<?>>> ANIMATION_CONFIG_DATA = new HashMap<>();

    public static void startToTracking(Path configDir) {
        ANIMATION_CONFIG_DATA.clear();
        startToTrackingFromConfig(configDir);
        startToTrackingFromAPI();
    }

    private static void startToTrackingFromConfig(Path configDir) {
        if (!Files.exists(configDir)) {
            fileError("/Config Folder");
            return;
        }

        ExecutionTasks.operateAndGetResult(
                ExecutionPolicy.RESIST,
                configDir, AnimationsConfigBuilder::startToWalking
        ).ifFailure(e -> ExtendedDatapacks.LOGGER.warn(
                "[Animations Config] Error reading Animation Registry Config", e
        ));
    }

    private static Path startToWalking(Path configDir) throws IOException {
        Stream<Path> paths = Files.list(configDir);
        paths.filter(p -> p.toString().endsWith(".json"))
                .forEach(root ->
                        ExecutionTasks.runAndGetResult(
                                ExecutionPolicy.RESIST,
                                () -> startToLoad(root, "config")
                        ).ifFailure(e -> ExtendedDatapacks.LOGGER.warn(
                                "[Animations Config] Error reading: {}", root, e
                        ))
                )
        ;

        return configDir;
    }

    private static void startToTrackingFromAPI() {
        var advancedAnimationsBuilders = SLDataDrivenAPI.collectResources(SL_FOLDER_KEY);
        if (advancedAnimationsBuilders.isEmpty()) {
            fileError("In-Jar Folder");
            return;
        }

        for (var entry : advancedAnimationsBuilders.entrySet()) {

            String modId = entry.getKey();
            for (Path file : entry.getValue()) {
                if (!file.toString().endsWith(".json")) continue;

                ExtendedDatapacks.LOGGER.info(
                        "[Animations Config] Parameterization file detected In-Jar, operating for {} -> {}",
                        modId,
                        file.getFileName()
                );

                ExecutionTasks.runAndGetResult(
                        ExecutionPolicy.RESIST,
                        () -> startToLoad(file, modId)
                ).ifFailure(e -> ExtendedDatapacks.LOGGER.warn(
                        "[Animations Config] Error reading: {}", file, e
                ));
            }
        }
    }

    private static void fileError(String side) {
        ExtendedDatapacks.LOGGER.info(
                "[Animations Config] There are no parameter files for Animations Config on the side of {}",
                side
        );
    }

    private static void startToLoad(Path file, String modId) throws IOException {
        Reader reader = Files.newBufferedReader(file);
        JsonElement json = JsonParser.parseReader(reader);
        AnimationConfigDefinition.CODEC
                .parse(JsonOps.INSTANCE, json)
                .resultOrPartial(err ->
                        ExtendedDatapacks.LOGGER.error(
                                "[Animations Config] Failed to parse {} -> {}: {}",
                                modId, file.getFileName(), err
                        )
                ).ifPresent(def ->
                        ANIMATION_CONFIG_DATA
                        .computeIfAbsent(modId, k -> new ArrayList<>()).add(def)
                );
    }

    public static Map<String, List<AnimationConfigDefinition<?>>> getAnimationConfigData() {
        return ANIMATION_CONFIG_DATA;
    }
}

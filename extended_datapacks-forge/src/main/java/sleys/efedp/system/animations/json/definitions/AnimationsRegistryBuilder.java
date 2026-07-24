package sleys.efedp.system.animations.json.definitions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.animations.json.definitions.registry.AnimationRegistryDefinition;
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

public class AnimationsRegistryBuilder {
    private static final String SL_FOLDER_KEY = "animations/registry";
    private static final Map<String, List<AnimationRegistryDefinition<?>>> ANIMATION_DEFINITIONS_DATA = new HashMap<>();

    public static void startToTracking(Path configDir) {
        ANIMATION_DEFINITIONS_DATA.clear();
        startToTrackingFromConfig(configDir);
        startToTrackingFromAPI();
    }

    private static void startToTrackingFromConfig(Path configDir) {
        if (!Files.exists(configDir)) {
            fileError("/Config Folder");
            return;
        }

        ExecutionTasks.operateAndGetResult(
                ExecutionPolicy.RESIST, configDir,
                AnimationsRegistryBuilder::startToWalking
        ).ifFailure(e -> ExtendedDatapacks.LOGGER.warn("[Animations Registry] Error reading Animation Registry Config", e));
    }

    private static Path startToWalking(Path configDir) throws IOException {
        Stream<Path> paths = Files.list(configDir);
        paths.filter(p -> p.toString().endsWith(".json"))
                .forEach(root ->
                        ExecutionTasks.runAndGetResult(
                                ExecutionPolicy.RESIST,
                                () -> startToLoad(root, "config")
                        ).ifFailure(e -> ExtendedDatapacks.LOGGER.warn(
                                "[Animations Registry] Error reading: {}", root, e
                        ))
                )
        ;

        return configDir;
    }

    private static void startToTrackingFromAPI() {
        var advancedAnimationsBuilders = SLDataDrivenAPI.collectResources(SL_FOLDER_KEY);
        if (!advancedAnimationsBuilders.isEmpty()) {
            for (var entry : advancedAnimationsBuilders.entrySet()) {

                String modId = entry.getKey();
                for (Path file : entry.getValue()) {
                    if (!file.toString().endsWith(".json")) continue;

                    ExtendedDatapacks.LOGGER.info(
                            "[Animations Registry] Parameterization file detected In-Jar, operating for {} -> {}",
                            modId,
                            file.getFileName()
                    );

                    ExecutionTasks.runAndGetResult(
                            ExecutionPolicy.RESIST,
                            () -> startToLoad(file, modId)
                    ).ifFailure(e -> ExtendedDatapacks.LOGGER.warn(
                            "[Animations Registry] Error reading: {}", file, e
                    ));
                }
            }
        } else {
            fileError("In-Jar Folder");
        }
    }

    private static void fileError(String side) {
        ExtendedDatapacks.LOGGER.info(
                "[Animations Registry] There are no parameter files for Animations Registry on the side of {}",
                side
        );
    }

    private static void startToLoad(Path file, String modId) throws IOException {
        Reader reader = Files.newBufferedReader(file);
        JsonElement json = JsonParser.parseReader(reader);

        if (modId.equals("config") && json.isJsonObject()) {
            JsonObject object = json.getAsJsonObject();
            if (object.has("mod_id")) {
                var modKey = object.get("mod_id").getAsString();
                AnimationsRegistryBuilder.startToRegisterEntry(
                        file, modKey, json
                );

                ExtendedDatapacks.LOGGER.info(
                        "[Animations Registry] Loading from configuration folder... Registering under the namespaces: {}", modKey
                );

            }

            return;
        }

        AnimationsRegistryBuilder.startToRegisterEntry(file, modId, json);
    }

    private static void startToRegisterEntry(Path file, String modId, JsonElement json) {
        AnimationRegistryDefinition.CODEC
                .parse(JsonOps.INSTANCE, json)
                .resultOrPartial(err ->
                        ExtendedDatapacks.LOGGER.error(
                                "[Animations Registry] Failed to parse {} -> {}: {}",
                                modId, file.getFileName(), err
                        )
                ).ifPresent(def ->
                        ANIMATION_DEFINITIONS_DATA
                                .computeIfAbsent(modId, k -> new ArrayList<>()).add(def)
                );
    }

    public static Map<String, List<AnimationRegistryDefinition<?>>> getAnimationDefinitionsData() {
        return ANIMATION_DEFINITIONS_DATA;
    }
}

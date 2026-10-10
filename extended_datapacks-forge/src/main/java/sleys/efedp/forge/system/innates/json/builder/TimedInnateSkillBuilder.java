package sleys.efedp.forge.system.innates.json.builder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import sleys.efedp.forge.ExtendedDatapacks;
import sleys.efedp.forge.system.innates.json.definitions.TimedInnateSkillDefinition;
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

public class TimedInnateSkillBuilder {
    private static final String SL_FOLDER_KEY = "innate_skill_builder/timed_innate_skill";
    private static final Map<String, List<TimedInnateSkillDefinition>> TIMED_INNATE_SKILL_BUILD_DATA = new HashMap<>();

    public static void startToTracking(Path configDir) {
        TIMED_INNATE_SKILL_BUILD_DATA.clear();
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
                configDir, TimedInnateSkillBuilder::startToWalking
        ).ifFailure(e -> ExtendedDatapacks.LOGGER.warn(
                "[Timed Innate Skills] Error reading Timed Innate Skill Builder config", e
        ));
    }

    @SuppressWarnings("resource")
    private static Path startToWalking(Path configDir) throws IOException {
        Stream<Path> paths = Files.list(configDir);
        paths.filter(p -> p.toString().endsWith(".json"))
                .forEach(root ->
                        ExecutionTasks.runAndGetResult(
                                ExecutionPolicy.RESIST,
                                () -> startToLoad(root, "config")
                        ).ifFailure(e -> ExtendedDatapacks.LOGGER.warn(
                                "[Timed Innate Skills] Error reading: {}", root, e
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
                        "[Timed Innate Skills] Parameterization file detected In-Jar, operating for {} -> {}",
                        modId,
                        file.getFileName()
                );

                ExecutionTasks.runAndGetResult(
                        ExecutionPolicy.RESIST,
                        () -> startToLoad(file, modId)
                ).ifFailure(e -> ExtendedDatapacks.LOGGER.warn(
                        "[Timed Innate Skills] Error reading: {}", file, e
                ));
            }
        }
    }

    private static void fileError(String side) {
        ExtendedDatapacks.LOGGER.info(
                "[Timed Innate Skills] There are no parameter files for Timed Innate Skills on the side of {}",
                side
        );
    }

    private static void startToLoad(Path file, String modId) throws IOException {
        Reader reader = Files.newBufferedReader(file);
        JsonElement json = JsonParser.parseReader(reader);

        if (modId.equals("config") && json.isJsonObject()) {

            JsonObject object = json.getAsJsonObject();
            if (object.has("modid")) {
                var newModId = object.get("modid").getAsString();
                ExtendedDatapacks.LOGGER.info(
                        "[Timed Innate Skills] Loading from configuration folder... Registering under the namespaces: {}", newModId
                );
                TimedInnateSkillBuilder.startToRegisterEntry(
                        file, newModId, json
                );
            }

            return;
        }

        TimedInnateSkillBuilder.startToRegisterEntry(file, modId, json);
    }

    private static void startToRegisterEntry(Path file, String modId, JsonElement json) {
        TimedInnateSkillDefinition.CODEC
                .codec()
                .parse(JsonOps.INSTANCE, json)
                .resultOrPartial(err ->
                        ExtendedDatapacks.LOGGER.error(
                                "[Timed Innate Skills] Failed to parse {} -> {}: {}",
                                modId, file.getFileName(), err
                        )
                ).ifPresent(def ->
                        TIMED_INNATE_SKILL_BUILD_DATA
                                .computeIfAbsent(modId, k -> new ArrayList<>()).add(def)
                );
    }

    public static Map<String, List<TimedInnateSkillDefinition>> getTimedInnateSkillBuildData() {
        return TIMED_INNATE_SKILL_BUILD_DATA;
    }
}

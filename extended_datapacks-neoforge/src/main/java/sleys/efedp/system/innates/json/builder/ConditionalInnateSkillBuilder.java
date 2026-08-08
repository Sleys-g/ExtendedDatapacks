package sleys.efedp.system.innates.json.builder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.innates.json.definitions.ConditionalInnateSkillDefinition;
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

public class ConditionalInnateSkillBuilder {
    private static final String SL_FOLDER_KEY = "innate_skill_builder/conditional_innate_skill";
    private static final Map<String, List<ConditionalInnateSkillDefinition>> CONDITIONAL_INNATE_SKILL_BUILD_DATA = new HashMap<>();

    public static void startToTracking(Path configDir) {
        CONDITIONAL_INNATE_SKILL_BUILD_DATA.clear();
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
                configDir, ConditionalInnateSkillBuilder::startToWalking
        ).ifFailure(e -> ExtendedDatapacks.LOGGER.warn(
                "[Conditional Innate Skills] Error reading Conditional Innate Skill Builder config", e
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
                                "[Conditional Innate Skills] Error reading: {}", root, e
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
                        "[Conditional Innate Skills] Parameterization file detected In-Jar, operating for {} -> {}",
                        modId,
                        file.getFileName()
                );

                ExecutionTasks.runAndGetResult(
                        ExecutionPolicy.RESIST,
                        () -> startToLoad(file, modId)
                ).ifFailure(e -> ExtendedDatapacks.LOGGER.warn(
                        "[Conditional Innate Skills] Error reading: {}", file, e
                ));
            }
        }
    }

    private static void fileError(String side) {
        ExtendedDatapacks.LOGGER.info(
                "[Conditional Innate Skills] There are no parameter files for Conditional Innate Skills on the side of {}",
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
                        "[Conditional Innate Skills] Loading from configuration folder... Registering under the namespaces: {}", newModId
                );
                ConditionalInnateSkillBuilder.startToRegisterEntry(
                        file, newModId, json
                );
            }

            return;
        }

        ConditionalInnateSkillBuilder.startToRegisterEntry(file, modId, json);
    }

    private static void startToRegisterEntry(Path file, String modId, JsonElement json) {
        ConditionalInnateSkillDefinition.CODEC
                .codec()
                .parse(JsonOps.INSTANCE, json)
                .resultOrPartial(err ->
                        ExtendedDatapacks.LOGGER.error(
                                "[Animations Registry] Failed to parse {} -> {}: {}",
                                modId, file.getFileName(), err
                        )
                ).ifPresent(def ->
                        CONDITIONAL_INNATE_SKILL_BUILD_DATA
                                .computeIfAbsent(modId, k -> new ArrayList<>()).add(def)
                );
    }

    public static Map<String, List<ConditionalInnateSkillDefinition>> getConditionalInnateSkillBuildData() {
        return CONDITIONAL_INNATE_SKILL_BUILD_DATA;
    }
}

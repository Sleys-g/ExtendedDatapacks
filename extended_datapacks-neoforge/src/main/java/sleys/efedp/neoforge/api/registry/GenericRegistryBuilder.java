package sleys.efedp.neoforge.api.registry;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import sleys.efedp.neoforge.ExtendedDatapacks;

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

public class GenericRegistryBuilder<T> {

    private static final String CONFIG_SOURCE = "config";
    private static final String JSON_EXTENSION = ".json";

    private final Map<String, List<T>> definitions = new HashMap<>();
    private final Codec<T> codec;

    private final String resourceFolder;
    private final String logTag;
    private final Path configFolder;

    public GenericRegistryBuilder(Codec<T> codec, String resourceFolder,
                                  String logTag, Path configFolder) {
        this.codec = codec;
        this.resourceFolder = resourceFolder;
        this.logTag = logTag;
        this.configFolder = configFolder;
    }

    public void startToTracking() {
        definitions.clear();
        trackFromConfig(configFolder);
        trackFromResources();
    }

    private void trackFromConfig(Path configDir) {
        if (!Files.exists(configDir)) {
            reportMissingSource("/Config Folder");
            return;
        }

        ExecutionTasks.operateAndGetResult(ExecutionPolicy.RESIST, configDir, this::walkConfigFolder)
                .ifFailure(e -> ExtendedDatapacks.LOGGER.warn("[{}] Error walking config folder", logTag, e));
    }

    private Path walkConfigFolder(Path configDir) throws IOException {
        try (Stream<Path> paths = Files.walk(configDir)) {
            paths.filter(Files::isRegularFile)
                    .filter(this::isJson)
                    .sorted()
                    .forEach(file -> loadSafely(file, CONFIG_SOURCE));
        }
        return configDir;
    }

    private void trackFromResources() {
        var resourcesByMod = SLDataDrivenAPI.collectResources(resourceFolder);
        if (resourcesByMod.isEmpty()) {
            reportMissingSource("In-Jar Folder");
            return;
        }

        for (var entry : resourcesByMod.entrySet()) {
            String modId = entry.getKey();
            for (Path file : entry.getValue()) {
                if (!isJson(file)) continue;

                ExtendedDatapacks.LOGGER.info(
                        "[{}] Data file detected In-Jar, loading for {} -> {}", logTag, modId, file.getFileName()
                );

                loadSafely(file, modId);
            }
        }
    }

    private void loadSafely(Path file, String source) {
        ExecutionTasks.runAndGetResult(
                ExecutionPolicy.RESIST,
                () -> loadFile(file, source)
        ).ifFailure(e -> ExtendedDatapacks.LOGGER.warn(
                "[{}] Error reading: {}", logTag, file, e
        ));
    }

    private void loadFile(Path file, String source) throws IOException {
        JsonElement json;
        try (Reader reader = Files.newBufferedReader(file)) {
            json = JsonParser.parseReader(reader);
        }

        if (CONFIG_SOURCE.equals(source)) {
            if (!json.isJsonObject() || !json.getAsJsonObject().has("mod_id")) {
                ExtendedDatapacks.LOGGER.warn(
                        "[{}] Skipping {}: config files require a \"mod_id\" field", logTag, file
                );
                return;
            }

            String modKey = json.getAsJsonObject().get("mod_id").getAsString();
            ExtendedDatapacks.LOGGER.info(
                    "[{}] Loading from config folder... Registering under namespace: {}", logTag, modKey
            );
            registerEntry(file, modKey, json);
            return;
        }

        ExtendedDatapacks.LOGGER.info("[{}] Loading from In-Jar folder... Registering under namespace: {}", logTag, source);
        registerEntry(file, source, json);
    }

    private void registerEntry(Path file, String modId, JsonElement json) {
        codec.parse(JsonOps.INSTANCE, json).resultOrPartial(err -> ExtendedDatapacks.LOGGER.error(
                "[{}] Failed to parse {} -> {}: {}", logTag, modId, file.getFileName(), err)
        ).ifPresent(def -> {
            definitions.computeIfAbsent(modId, k -> new ArrayList<>()).add(def);
            ExtendedDatapacks.LOGGER.info(
                    "[{}] Successful file parse for {} under the namespace: {}", logTag, file.getFileName(), modId
            );
        });
    }

    private boolean isJson(Path path) {
        var result = path.toString().endsWith(JSON_EXTENSION);
        if (!result) ExtendedDatapacks.LOGGER.warn("[{}] The file {} was detected, but it does not have a valid extension!", logTag, path);
        return result;
    }

    private void reportMissingSource(String source) {
        ExtendedDatapacks.LOGGER.info("[{}] No data files found for this registry in: {}", logTag, source);
    }

    public Map<String, List<T>> getDefinitionData() {
        return definitions;
    }
}
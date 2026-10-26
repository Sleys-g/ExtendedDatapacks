package sleys.efedp.neoforge.api.registry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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

    private static final List<String> MOD_ID_FIELDS = List.of("mod_id", "modid", "modId", "modID");
    private static final String CONFIG_SOURCE = "config";
    private static final String JSON_EXTENSION = ".json";

    private final Map<String, List<T>> definitions = new HashMap<>();
    private final Codec<T> codec;
    private final BuilderContext context;

    private final String resourceFolder;
    private final String logTag;
    private final Path configFolder;

    public enum BuilderContext {
        WITH_MOD_ID,
        WITHOUT_MOD_ID;
    }

    public GenericRegistryBuilder(Codec<T> codec,
                                  BuilderContext context,
                                  String resourceFolder,
                                  String logTag,
                                  Path configFolder) {
        this.codec = codec;
        this.context = context;
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
                        "[{}] Data file detected In-Jar, loading for {} -> {}", logTag, modId, formatPath(file)
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
                "[{}] Error reading: {}", logTag, formatPath(file), e
        ));
    }

    private void loadFile(Path file, String source) throws IOException {
        JsonElement json;
        try (Reader reader = Files.newBufferedReader(file)) {
            json = JsonParser.parseReader(reader);
        }

        if (context == BuilderContext.WITH_MOD_ID) {
            if (CONFIG_SOURCE.equals(source) && json.isJsonObject()) {
                ExtendedDatapacks.LOGGER.warn(
                        "[{}] Attempting to load the file {}: from the configuration folder", logTag, formatPath(file)
                );

                String modId = this.resolveModId(file, json);
                if (modId != null) {
                    ExtendedDatapacks.LOGGER.info(
                            "[{}] Loading from config folder... Registering under namespace: {}", logTag, modId
                    );

                    registerEntry(file, modId, json);
                } else {
                    ExtendedDatapacks.LOGGER.warn(
                            "[{}] Skipping {}: it was not possible to reach the modid in any way.", logTag, formatPath(file)
                    );
                }

                return;
            }

            ExtendedDatapacks.LOGGER.info("[{}] Loading from In-Jar folder... Registering under namespace: {}", logTag, source);
            registerEntry(file, source, json);
        }

        if (context == BuilderContext.WITHOUT_MOD_ID) {
            if (CONFIG_SOURCE.equals(source)) {
                ExtendedDatapacks.LOGGER.info("[{}] Loading from config folder... Without namespace as file: {}", logTag, formatPath(file));
                registerEntry(file, source, json);
                return;
            }

            ExtendedDatapacks.LOGGER.info("[{}] Loading from In-Jar folder... Without namespace as file: {}", logTag, formatPath(file));
            registerEntry(file, source, json);
        }
    }

    private String resolveModId(Path file, JsonElement json) {
        if (!json.isJsonObject()) return null;

        JsonObject object = json.getAsJsonObject();
        for (String field : MOD_ID_FIELDS) {
            JsonElement element = object.get(field);
            if (element == null || !element.isJsonPrimitive()) continue;

            String value = element.getAsString();
            if (value.isBlank()) continue;

            if (!field.equals("mod_id")) {
                ExtendedDatapacks.LOGGER.warn(
                        "[{}] {} uses the legacy \"{}\" field, please migrate to \"mod_id\"", logTag, formatPath(file), field
                );
            }
            return value;
        }
        return null;
    }

    private void registerEntry(Path file, String modId, JsonElement json) {
        codec.parse(JsonOps.INSTANCE, json).resultOrPartial(err -> ExtendedDatapacks.LOGGER.error(
                "[{}] Failed to parse {} -> {}: {}", logTag, modId, formatPath(file), err)
        ).ifPresent(def -> {
            definitions.computeIfAbsent(modId, k -> new ArrayList<>()).add(def);
            ExtendedDatapacks.LOGGER.info(
                    "[{}] Successful file parse for {} under the namespace: {}", logTag, formatPath(file), modId
            );
        });
    }

    private boolean isJson(Path path) {
        var result = path.toString().endsWith(JSON_EXTENSION);
        if (!result) ExtendedDatapacks.LOGGER.warn("[{}] The file {} was detected, but it does not have a valid extension!", logTag, formatPath(path));
        return result;
    }

    private void reportMissingSource(String source) {
        ExtendedDatapacks.LOGGER.info("[{}] No data files found for this registry in: {}", logTag, source);
    }

    private String formatPath(Path file) {
        return ExecutionTasks.getAndFallback(ExecutionPolicy.RESIST, () -> this.getResolvedPath(file), file.toString());
    }

    private String getResolvedPath(Path file) {
        Path root = Path.of("").toAbsolutePath();
        Path absoluteFile = file.toAbsolutePath();
        if (absoluteFile.startsWith(root)) {
            return root.relativize(absoluteFile).toString();
        }
        return file.toString();
    }

    public Map<String, List<T>> getDefinitionData() {
        return definitions;
    }
}
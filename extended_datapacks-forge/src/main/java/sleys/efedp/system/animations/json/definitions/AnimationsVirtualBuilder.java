package sleys.efedp.system.animations.json.definitions;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import sleys.efedp.main.ExtendedDatapacks;
import sleys.efedp.system.animations.json.definitions.virtualization.AnimationVirtualDefinition;
import sleys.sl.datadriven.api.SLDataDrivenAPI;
import sleys.sl.library.runtime.policy.error.ErrorPolicy;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class AnimationsVirtualBuilder {
    private static final String SL_FOLDER_KEY = "animations/virtualizate";
    private static final Map<String, List<AnimationVirtualDefinition<?>>> ANIMATION_VIRTUALIZATION_DATA = new HashMap<>();

    public static void startToTracking(Path configDir) {
        ANIMATION_VIRTUALIZATION_DATA.clear();
        startToTrackingFromConfig(configDir);
        startToTrackingFromAPI();
    }

    private static void startToTrackingFromConfig(Path configDir) {
        if (Files.exists(configDir)) {
            ErrorPolicy.DEPURATE_ERROR.executeTaskWithMsg(
                    () -> startToWalking(configDir),
                    "[Animations Virtualization] Error reading Animation Virtualizate config"
            );
        } else {
            fileError("/Config Folder");
        }
    }

    private static void startToWalking(Path configDir) throws IOException {
        Stream<Path> paths = Files.list(configDir);
        paths.filter(p -> p.toString().endsWith(".json"))
                .forEach(root ->
                        ErrorPolicy.DEPURATE_ERROR.executeTaskWithMsg(
                                () -> startToLoad(root, "config"),
                                "[Animations Virtualization] Error reading: " + root
                        )
                )
        ;
    }

    private static void startToTrackingFromAPI() {
        var advancedAnimationsBuilders = SLDataDrivenAPI.collectResources(SL_FOLDER_KEY);
        if (!advancedAnimationsBuilders.isEmpty()) {
            for (var entry : advancedAnimationsBuilders.entrySet()) {

                String modId = entry.getKey();
                for (Path file : entry.getValue()) {
                    if (!file.toString().endsWith(".json")) continue;

                    ExtendedDatapacks.LOGGER.info(
                            "[Animations Virtualization] Parameterization file detected In-Jar, operating for {} -> {}",
                            modId,
                            file.getFileName()
                    );

                    ErrorPolicy.DEPURATE_ERROR.executeTaskWithMsg(
                            () -> startToLoad(file, modId),
                            "[Animations Virtualization] Error reading: " + file
                    );
                }
            }
        } else {
            fileError("In-Jar Folder");
        }
    }

    private static void fileError(String side) {
        ExtendedDatapacks.LOGGER.info(
                "[Animations Virtualization] There are no parameter files for Animations Virtualization on the side of {}",
                side
        );
    }

    private static void startToLoad(Path file, String modId) throws IOException {
        Reader reader = Files.newBufferedReader(file);
        JsonElement json = JsonParser.parseReader(reader);
        AnimationVirtualDefinition.CODEC
                .parse(JsonOps.INSTANCE, json)
                .resultOrPartial(err ->
                        ExtendedDatapacks.LOGGER.error(
                                "[Animations Virtualization] Failed to parse {} -> {}: {}",
                                modId, file.getFileName(), err
                        )
                ).ifPresent(def ->
                        ANIMATION_VIRTUALIZATION_DATA
                        .computeIfAbsent(modId, k -> new ArrayList<>()).add(def)
                );
    }

    public static Map<String, List<AnimationVirtualDefinition<?>>> getAnimationVirtualizationData() {
        return ANIMATION_VIRTUALIZATION_DATA;
    }
}

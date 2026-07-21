package sleys.efedp.system.innates.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import sleys.efedp.main.ExtendedDatapacks;
import sleys.sl.datadriven.api.SLDataDrivenAPI;
import sleys.sl.library.runtime.policy.error.ErrorPolicy;
import sleys.sl.library.util.file.GsonUtilities;
import sleys.sl.library.util.file.JsonComponentArgs;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class StacksConditionalInnateSkillBuilder {
    private static final Map<String, List<RawConditionalInnateSkillBuilderData>> STACKS_CONDITIONAL_INNATE_SKILL_BUILD_DATA = new ConcurrentHashMap<>();
    private static final String SL_FOLDER_KEY = "innate_skill_builder/stacks_conditional_innate_skill";

    public static void startToTracking(Path configDir) {
        STACKS_CONDITIONAL_INNATE_SKILL_BUILD_DATA.clear();
        startToTrackingFromConfig(configDir);
        startToTrackingFromAPI();
    }

    private static void startToTrackingFromConfig(Path configDir) {
        if (Files.exists(configDir)) {
            ErrorPolicy.DEPURATE_ERROR.executeTaskWithMsg(
                    () -> startToWalking(configDir),
                    "[Stacks Conditional Innate Skills] Error reading Conditional Innate config"
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
                                "[Stacks Conditional Innate Skills] Error reading: " + root
                        )
                )
        ;
    }

    private static void startToTrackingFromAPI() {
        var simpleInnateSkillsBuilders = SLDataDrivenAPI.collectResources(SL_FOLDER_KEY);
        if (!simpleInnateSkillsBuilders.isEmpty()) {
            for (var entry : simpleInnateSkillsBuilders.entrySet()) {

                String modId = entry.getKey();
                for (Path file : entry.getValue()) {

                    if (!file.toString().endsWith(".json")) continue;
                    ExtendedDatapacks.LOGGER.info(
                            "[Stacks Conditional Innate Skills] Parameterization file detected In-Jar, operating for {} -> {}",
                            modId,
                            file.getFileName()
                    );

                    ErrorPolicy.DEPURATE_ERROR.executeTaskWithMsg(
                            () -> startToLoad(file, modId),
                            "[Stacks Conditional Innate Skills] Error reading: " + file
                    );
                }
            }
        } else {
            fileError("In-Jar Folder");
        }
    }

    private static void fileError(String side) {
        ExtendedDatapacks.LOGGER.info(
                "[Stacks Conditional Innate Skills] There are no parameter files for Conditional Innate Skills on the side of {}",
                side
        );
    }

    private static void startToLoad(Path file, String modId) throws IOException {
        Reader reader = Files.newBufferedReader(file);
        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

        ExtendedDatapacks.LOGGER.info(
                "[Stacks Conditional Innate Skills] Reading file: {}",
                file.getFileName()
        );

        startToProcessConditionalInnateSkills(root, file.getFileName().toString(), modId);
    }

    private static void startToProcessConditionalInnateSkills(JsonObject root, String fileName, String modId) {
        if (modId.equals("config")) {
            if (!root.has("modid")) {
                ExtendedDatapacks.LOGGER.warn("[Stacks Conditional Innate Skills] missing 'modid' in: {}", fileName);
                return;
            }
            modId = root.get("modid").getAsString();
        } else {
            if (root.has("modid")) {
                ExtendedDatapacks.LOGGER.warn("[Stacks Conditional Innate Skills] 'modid' ignored in {}, since file is loaded from jar.", fileName);
            }
        }

        if (!root.has("name")) {
            ExtendedDatapacks.LOGGER.warn("[Stacks Conditional Innate Skills] Skill without 'name' in {}", fileName);
            return;
        }

        String name = root.get("name").getAsString();
        if (!root.has("conditions")) {
            ExtendedDatapacks.LOGGER.warn("[Stacks Conditional Innate Skills] Missing 'conditions' object in {}", fileName);
            return;
        }

        JsonObject conditionsObj = root.getAsJsonObject("conditions");
        Map<ConditionsType, ConditionalAnimationData> animations = new EnumMap<>(ConditionsType.class);
        for (Map.Entry<String, JsonElement> entry : conditionsObj.entrySet()) {
            String conditionKey = entry.getKey();
            ConditionsType type = ConditionsType.fromString(conditionKey);

            JsonObject conditionBody = entry.getValue().getAsJsonObject();

            if (!conditionBody.has("animation")) {
                ExtendedDatapacks.LOGGER.warn("[Stacks Conditional Innate Skills] Condition '{}' missing 'animation' in {}", conditionKey, fileName);
                continue;
            }
            if (!conditionBody.has("properties_type")) {
                ExtendedDatapacks.LOGGER.warn("[Stacks Conditional Innate Skills] Condition '{}' missing 'properties_type' in {}", conditionKey, fileName);
                continue;
            }
            if (!conditionBody.has("properties")) {
                ExtendedDatapacks.LOGGER.warn("[Stacks Conditional Innate Skills] Condition '{}' missing 'properties' in {}", conditionKey, fileName);
                continue;
            }

            String animation = conditionBody.get("animation").getAsString();
            int stacks = GsonUtilities.getAsInteger(conditionBody, "stacks", 1);
            List<AnimationsProperties> phases = parsePhases(
                    conditionBody.get("properties_type").getAsString(),
                    conditionBody.getAsJsonObject("properties"),
                    fileName
            );

            animations.put(type, new ConditionalAnimationData(animation, stacks, phases));
        }

        boolean hasTooltip = root.has("tooltip");
        if (!hasTooltip) {
            ExtendedDatapacks.LOGGER.info("[Holdable Innate Skills] The control field for 'tooltip' does not exist, is invalid, or is empty; data behavior is delegated to the standard function in {}...", fileName);
        }

        List<JsonComponentArgs> tooltipData = new ArrayList<>();
        if (hasTooltip) {
            var tooltipArray = root.getAsJsonArray("tooltip");
            for (var tooltip : tooltipArray) {
                tooltipData.add(JsonComponentArgs.parseComponentArgs(tooltip.getAsJsonObject()));
            }
        }

        RawConditionalInnateSkillBuilderData data = new RawConditionalInnateSkillBuilderData(modId, name, tooltipData, animations);
        STACKS_CONDITIONAL_INNATE_SKILL_BUILD_DATA.computeIfAbsent(modId, k -> new ArrayList<>()).add(data);
    }

    private static List<AnimationsProperties> parsePhases(
            String type,
            JsonObject properties,
            String fileName
    ) {

        List<AnimationsProperties> phases = new ArrayList<>();

        switch (type) {

            case "mono_phase" ->
                    phases.add(
                            AnimationsProperties.parseProperties(properties)
                    );

            case "multi_phase" ->
                    properties.entrySet()
                            .stream()
                            .sorted(
                                    Comparator.comparingInt(
                                            e -> Integer.parseInt(e.getKey())
                                    )
                            )
                            .forEach(entry -> {

                                JsonObject phaseObj =
                                        entry.getValue().getAsJsonObject();

                                phases.add(
                                        AnimationsProperties.parseProperties(phaseObj)
                                );
                            });

            default -> throw new IllegalArgumentException(
                    "Unknown properties_type '" + type +
                            "' in " + fileName
            );
        }

        return phases;
    }

    public static Map<String, List<RawConditionalInnateSkillBuilderData>> getData() {
        return STACKS_CONDITIONAL_INNATE_SKILL_BUILD_DATA;
    }

    public record RawConditionalInnateSkillBuilderData(
            String modId,
            String name,
            @Nullable List<JsonComponentArgs> tooltip,
            Map<ConditionsType, ConditionalAnimationData> animations
    ) {
        public Component getFormattedAdditional(String key, ItemStack stack) {
            if (key == null || key.isEmpty()) return Component.empty();

            MutableComponent base;
            if (tooltip == null || tooltip.isEmpty()) {
                base = Component.translatable(key);
            } else {
                Object[] resolved = tooltip.stream()
                        .map(jsonDynamicArgs -> jsonDynamicArgs.resolve(stack))
                        .toArray();
                base = Component.translatable(key, resolved);
            }

            return base.withStyle(ChatFormatting.DARK_GRAY);
        }
    }

    public record ConditionalAnimationData(
            String animation,
            int stacks,
            List<AnimationsProperties> phases
    ) {
    }
}
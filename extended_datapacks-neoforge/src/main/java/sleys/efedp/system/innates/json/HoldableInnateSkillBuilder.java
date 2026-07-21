package sleys.efedp.system.innates.json;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import sleys.efedp.ExtendedDatapacks;
import sleys.sl.datadriven.api.SLDataDrivenAPI;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
import sleys.sl.library.util.io.JsonComponentArgs;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class HoldableInnateSkillBuilder {
    private static final Map<String, List<RawSimpleInnateSkillBuilderData>> HOLDABLE_INNATE_SKILL_BUILD_DATA = new ConcurrentHashMap<>();
    private static final String SL_FOLDER_KEY = "innate_skill_builder/holdable_innate_skill";

    public static void startToTracking(Path configDir) {
        HOLDABLE_INNATE_SKILL_BUILD_DATA.clear();
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
                configDir, HoldableInnateSkillBuilder::startToWalking
        ).ifFailure(e ->
                ExtendedDatapacks.LOGGER.error("[Holdable Innate Skills] Error reading Holdable Innate config", e)
        );
    }

    private static Path startToWalking(Path configDir) throws IOException, UncheckedIOException {
        Stream<Path> paths = Files.list(configDir);
        paths.filter(p -> p.toString().endsWith(".json"))
                .forEach(root ->
                        ExecutionTasks.runAndGetResult(
                                ExecutionPolicy.RESIST,
                                () -> startToLoad(root, "config")
                        ).ifFailure(e ->
                                ExtendedDatapacks.LOGGER.error("[Holdable Innate Skills] Error reading: {}", root, e)
                        )
                )
        ;
        return configDir;
    }

    private static void startToTrackingFromAPI() {
        var simpleInnateSkillsBuilders = SLDataDrivenAPI.collectResources(SL_FOLDER_KEY);
        if (simpleInnateSkillsBuilders.isEmpty()) {
            fileError("In-Jar Folder");
            return;
        }

        for (var entry : simpleInnateSkillsBuilders.entrySet()) {

            String modId = entry.getKey();
            for (Path file : entry.getValue()) {
                if (!file.toString().endsWith(".json")) continue;

                ExtendedDatapacks.LOGGER.info(
                        "[Holdable Innate Skills] Parameterization file detected In-Jar, operating for {} -> {}",
                        modId,
                        file.getFileName()
                );

                ExecutionTasks.runAndGetResult(
                        ExecutionPolicy.RESIST,
                        () -> startToLoad(file, modId)
                ).ifFailure(e -> ExtendedDatapacks.LOGGER.error(
                        "[Holdable Innate Skills] Error reading: {}", file, e)
                );
            }
        }
    }

    private static void fileError(String side) {
        ExtendedDatapacks.LOGGER.info(
                "[Holdable Innate Skills] There are no parameter files for Holdable Innate on the side of {}",
                side
        );
    }

    private static void startToLoad(Path file, String modId) throws IOException {
        Reader reader = Files.newBufferedReader(file);
        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

        ExtendedDatapacks.LOGGER.info(
                "[Holdable Innate Skills] Reading file: {}",
                file.getFileName()
        );

        startToProcessHoldableInnateSkills(root, file.getFileName().toString(), modId);
    }

    private static void startToProcessHoldableInnateSkills(JsonObject root, String fileName, String modId) {
        if (modId.equals("config")) {
            if (!root.has("modid")) {
                ExtendedDatapacks.LOGGER.warn(
                        "[Holdable Innate Skills] missing 'modid' in: {}",
                        fileName
                );
                return;
            }
            modId = root.get("modid").getAsString();
        } else {
            if (root.has("modid")) {
                ExtendedDatapacks.LOGGER.warn(
                        "[Holdable Innate Skills] 'modid' ignored in {}, since file is loaded from jar.",
                        fileName
                );
            }
        }

        if (!root.has("name")) {
            ExtendedDatapacks.LOGGER.warn("[Holdable Innate Skills] Skill without 'name' in {}", fileName);
            return;
        }

        if (!root.has("animation")) {
            ExtendedDatapacks.LOGGER.warn("[Holdable Innate Skills] Skill without valid 'animation' in {}", fileName);
            return;
        }

        if (!root.has("chargeAnimation")) {
            ExtendedDatapacks.LOGGER.warn("[Holdable Innate Skills] Skill without valid 'chargeAnimation' in {}", fileName);
            return;
        }

        if (!root.has("maxAllowedCharging")) {
            ExtendedDatapacks.LOGGER.warn("[Holdable Innate Skills] Skill without valid 'maxAllowedCharging' in {}", fileName);
            return;
        }

        if (!root.has("maxChargingTicks")) {
            ExtendedDatapacks.LOGGER.warn("[Holdable Innate Skills] Skill without valid 'maxChargingTicks' in {}", fileName);
            return;
        }

        if (!root.has("minChargingTicks")) {
            ExtendedDatapacks.LOGGER.warn("[Holdable Innate Skills] Skill without valid 'minChargingTicks' in {}", fileName);
            return;
        }

        if (!root.has("properties_type")) {
            ExtendedDatapacks.LOGGER.warn("[Holdable Innate Skills] Missing 'properties_type' in {}", fileName);
            return;
        }

        if (!root.has("properties")) {
            ExtendedDatapacks.LOGGER.warn("[Holdable Innate Skills] Missing 'properties' in {}", fileName);
            return;
        }

        if (!root.has("reduceSpeed")) {
            ExtendedDatapacks.LOGGER.warn("[Holdable Innate Skills] Skill without valid 'reduceSpeed' in {}", fileName);
            return;
        }


        boolean reduceSpeed = root.get("reduceSpeed").getAsBoolean();
        String name = root.get("name").getAsString();

        String chargeAnimation = root.get("chargeAnimation").getAsString();
        int maxAllowedCharging = root.get("maxAllowedCharging").getAsInt();
        int maxChargingTicks = root.get("maxChargingTicks").getAsInt();
        int minChargingTicks = root.get("minChargingTicks").getAsInt();

        String animation = root.get("animation").getAsString();
        String type = root.get("properties_type").getAsString();

        JsonObject properties = root.getAsJsonObject("properties");

        List<InnateAnimationsProperties> phases = new ArrayList<>();

        switch (type) {
            case "mono_phase" -> phases.add(InnateAnimationsProperties.parseProperties(properties));
            case "multi_phase" -> properties.entrySet().stream()
                    .sorted(Comparator.comparingInt(e -> Integer.parseInt(e.getKey())))
                    .forEach(entry -> {
                        JsonObject phaseObj = entry.getValue().getAsJsonObject();
                        phases.add(InnateAnimationsProperties.parseProperties(phaseObj));
                    });

            default -> {
                ExtendedDatapacks.LOGGER.warn("[Holdable Innate Skills] Unknown properties_type '{}' in {}", type, fileName);
                return;
            }
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

        RawSimpleInnateSkillBuilderData data =
                new RawSimpleInnateSkillBuilderData(
                        modId, name,
                        maxAllowedCharging,
                        maxChargingTicks,
                        minChargingTicks,
                        chargeAnimation, animation,
                        reduceSpeed, tooltipData, phases
                );

        HOLDABLE_INNATE_SKILL_BUILD_DATA
                .computeIfAbsent(modId, k -> new ArrayList<>())
                .add(data);
    }

    public static Map<String, List<RawSimpleInnateSkillBuilderData>> getData() {
        return HOLDABLE_INNATE_SKILL_BUILD_DATA;
    }

    public record RawSimpleInnateSkillBuilderData(
            String modId,
            String name,
            int maxAllowedCharging,
            int maxChargingTicks,
            int minChargingTicks,
            String chargeAnimation,
            String animation,
            boolean reduceSpeed,
            @Nullable List<JsonComponentArgs> tooltip,
            List<InnateAnimationsProperties> phases
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
}
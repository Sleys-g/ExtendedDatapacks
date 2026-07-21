package sleys.efedp.system.skills.json;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import sleys.efedp.ExtendedDatapacks;
import sleys.sl.datadriven.api.SLDataDrivenAPI;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class PassiveSkillBuilderModifier {
    private static final Map<String, List<RawSkillBuilderData>> SKILL_BUILD_DATA = new ConcurrentHashMap<>();
    private static final String SL_FOLDER_KEY = "skill_builder/passive_skills";

    public static void startToTracking(Path configDir) {
        SKILL_BUILD_DATA.clear();
        startToTrackingFromConfig(configDir);
        startToTrackingFromAPI();
    }

    private static void startToTrackingFromConfig(Path configDir) {
        if (!Files.exists(configDir)) {
            fileError(" /Config Folder");
            return;
        }

        ExecutionTasks.operateAndGetResult(
                ExecutionPolicy.RESIST, configDir,
                PassiveSkillBuilderModifier::startToWalking
        ).ifFailure(e -> ExtendedDatapacks.LOGGER.warn(
                "[Add Skill to Build] Error reading SkillBuilder config", e
        ));
    }

    private static Path startToWalking(Path configDir) throws IOException, UncheckedIOException {
        Stream<Path> paths = Files.list(configDir);
        paths.filter(p -> p.toString().endsWith(".json"))
                .forEach(path ->
                        ExecutionTasks.operateAndGetResult(
                                ExecutionPolicy.RESIST, configDir,
                                PassiveSkillBuilderModifier::startToLoad
                        ).ifFailure(e -> ExtendedDatapacks.LOGGER.warn(
                                "[Add Skill to Build] Error reading: {}", path, e
                        ))
                )
        ;

        return configDir;
    }

    private static void startToTrackingFromAPI() {
        var passiveSkillBuilders = SLDataDrivenAPI.collectResources(SL_FOLDER_KEY);
        if (passiveSkillBuilders.isEmpty()) {
            fileError(" In-Jar Folder");
            return;
        }

        for (var entry : passiveSkillBuilders.entrySet()) {
            String modId = entry.getKey();
            for (Path file : entry.getValue()) {
                if (!file.toString().endsWith(".json")) continue;

                ExtendedDatapacks.LOGGER.info("[Add Skill to Build] Parameterization file detected In-Jar, operating for {} -> {}",
                        modId,
                        file.getFileName()
                );

                ExecutionTasks.operateAndGetResult(
                        ExecutionPolicy.RESIST, file,
                        PassiveSkillBuilderModifier::startToLoad
                ).ifFailure(e -> ExtendedDatapacks.LOGGER.warn(
                        "[Add Skill to Build] Error reading: {}", file, e
                ));
            }
        }
    }

    private static void fileError(String side) {
        ExtendedDatapacks.LOGGER.info("[Add Skill to Build] There are no parameter files files for SkillBuilder on the side of {}",
                side
        );
    }

    private static Path startToLoad(Path file) throws IOException {
        Reader reader = Files.newBufferedReader(file);
        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
        ExtendedDatapacks.LOGGER.info("[Add Skill to Build] Reading file: {}", file.getFileName().toString());
        startToProcessSkillEntry(root, file.getFileName().toString());
        return file;
    }

    private static void startToProcessSkillEntry(JsonObject root, String fileName) {
        if (!root.has("skill_builder") || !root.get("skill_builder").isJsonArray()) {
            ExtendedDatapacks.LOGGER.warn("[Add Skill to Build] File without 'skill_builder': {}", fileName);
            return;
        }

        JsonArray array = root.getAsJsonArray("skill_builder");
        for (JsonElement element : array) {

            if (!element.isJsonObject()) continue;

            RawSkillBuilderData data = parseRawBuilder(element.getAsJsonObject(), fileName);
            if (data != null && data.skillId() != null) {
                SKILL_BUILD_DATA
                        .computeIfAbsent(data.skillId(), k -> new ArrayList<>())
                        .add(data);
            }

        }
    }

    private static RawSkillBuilderData parseRawBuilder(JsonObject json, String fileName) {
        if (!json.has("skill_id") || !json.has("category")) {
            ExtendedDatapacks.LOGGER.warn("[Add Skill to Build] Missing 'skill_id' or 'category' in: {}", fileName);
            return null;
        }

        String skillId = json.get("skill_id").getAsString();
        if (skillId == null || skillId.isEmpty()) {
            ExtendedDatapacks.LOGGER.warn("[Add Skill to Build] invalid 'skill_id' in: {}", fileName);
            return null;
        }

        String category = json.get("category").getAsString();
        if (category == null || category.isEmpty()) {
            ExtendedDatapacks.LOGGER.warn("[Add Skill to Build] invalid 'category' in: {}", fileName);
            return null;
        }

        return new RawSkillBuilderData(skillId, category);
    }

    @Nullable
    public static List<RawSkillBuilderData> get(String skillId) {
        return SKILL_BUILD_DATA.get(skillId);
    }

    public record RawSkillBuilderData(String skillId, String categoryId) {

        public ResourceLocation getParseSkill() {
            return ResourceLocation.tryParse(skillId);
        }

        public WeaponCategory getParseWeaponCategory() {
            return  WeaponCategory.ENUM_MANAGER.getOrThrow(categoryId);
        }

        public boolean isSameSkill(ResourceLocation skillRegistry, ResourceLocation skillParsed) {
            return skillRegistry.equals(skillParsed);
        }
    }
}

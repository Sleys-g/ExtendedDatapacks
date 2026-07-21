package sleys.efedp.system.skills.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.capability.ExtendedDatapacksUtilities;
import sleys.sl.datadriven.api.SLDataDrivenAPI;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

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

public class SkillIconBuilderModifier {
    private static final List<String> ICON_BUILD_DATA = new ArrayList<>();
    private static final Map<String, SkillIconBuilderModifier.RawIconSkillBuilderData> SKILL_ICON_BUILD_DATA = new ConcurrentHashMap<>();
    private static final String SL_FOLDER_KEY = "skill_builder/category_icon";

    public static void startToTracking(Path configDir) {
        ICON_BUILD_DATA.clear();
        startToTrackingFromConfig(configDir);
        startToTrackingFromAPI();
    }

    private static void startToTrackingFromConfig(Path configDir) {
        if (!Files.exists(configDir)) {
            fileError(" /Config Folder");
            return;
        }

        ExecutionTasks.operateAndGetResult(
                ExecutionPolicy.RESIST,
                configDir, SkillIconBuilderModifier::startToWalking
        ).ifFailure(e -> ExtendedDatapacks.LOGGER.warn(
                "[Add Icon to Build] Error reading IconSkillBuilder config", e
        ));
    }

    private static Path startToWalking(Path configDir) throws IOException, UncheckedIOException {
        Stream<Path> paths = Files.list(configDir);
        paths.filter(p -> p.toString().endsWith(".json"))
                .forEach(path ->
                        ExecutionTasks.operateAndGetResult(
                                ExecutionPolicy.RESIST,
                                path, SkillIconBuilderModifier::startToLoad
                        ).ifFailure(e -> ExtendedDatapacks.LOGGER.warn(
                                "[Add Icon to Build] Error reading: {}", path, e
                        ))
                )
        ;

        return configDir;
    }

    private static void startToTrackingFromAPI() {
        var iconSkillBuilders = SLDataDrivenAPI.collectResources(SL_FOLDER_KEY);
        if (!iconSkillBuilders.isEmpty()) {
            for (var entry : iconSkillBuilders.entrySet()) {
                String modId = entry.getKey();
                for (Path file : entry.getValue()) {

                    if (!file.toString().endsWith(".json")) continue;
                    ExtendedDatapacks.LOGGER.info("[Add Icon to Build] Parameterization file detected In-Jar, operating for {} -> {}",
                            modId,
                            file.getFileName()
                    );

                    ExecutionTasks.operateAndGetResult(
                            ExecutionPolicy.RESIST,
                            file, SkillIconBuilderModifier::startToLoad
                    ).ifFailure(e -> ExtendedDatapacks.LOGGER.warn(
                            "[Add Icon to Build] Error reading: {}", file, e
                    ));
                }
            }
        } else {
            fileError(" In-Jar Folder");
        }
    }

    private static void fileError(String side) {
        ExtendedDatapacks.LOGGER.info("[Add Icon to Build] There are no parameter files files for Icon Category on the side of {}",
                side
        );
    }

    private static Path startToLoad(Path file) throws IOException {
        Reader reader = Files.newBufferedReader(file);
        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
        ExtendedDatapacks.LOGGER.info("[Add Icon to Build] Reading file: {}", file.getFileName().toString());
        startToProcessIconEntry(root, file.getFileName().toString());
        return file;
    }

    private static void startToProcessIconEntry(JsonObject root, String fileName) {
        if (!root.has("icon_skill_builder") || !root.get("icon_skill_builder").isJsonArray()) {
            ExtendedDatapacks.LOGGER.warn("[Add Icon to Build] File without 'icon_skill_builder': {}", fileName);
            return;
        }

        JsonArray array = root.getAsJsonArray("icon_skill_builder");
        for (JsonElement element : array) {

            if (!element.isJsonObject()) continue;

            SkillIconBuilderModifier.RawIconSkillBuilderData data = parseBuilder(element.getAsJsonObject(), fileName);
            if (data != null && data.itemID() != null) {
                ICON_BUILD_DATA.add(data.itemID());
                SKILL_ICON_BUILD_DATA.put(data.itemID(), data);
            }
        }
    }

    private static SkillIconBuilderModifier.RawIconSkillBuilderData parseBuilder(JsonObject json, String fileName) {
        if (!json.has("item_id") || !json.has("category")) {
            ExtendedDatapacks.LOGGER.warn("[Add Icon to Build] missing 'item_id' or 'category' in {}", fileName);
            return null;
        }

        String itemId = json.get("item_id").getAsString();
        if (itemId == null || itemId.isEmpty()) {
            ExtendedDatapacks.LOGGER.warn("[Add Icon to Build] invalid 'item_id' in: {}", fileName);
            return null;
        }

        String category = json.get("category").getAsString();
        if (category == null || category.isEmpty()) {
            ExtendedDatapacks.LOGGER.warn("[Add Icon to Build] invalid 'category' in: {}", fileName);
            return null;
        }

        return new SkillIconBuilderModifier.RawIconSkillBuilderData(itemId, category);
    }

    public static List<String> getItemList() {
        return ICON_BUILD_DATA;
    }

    public static RawIconSkillBuilderData getIconData(String itemID) {
        return SKILL_ICON_BUILD_DATA.get(itemID);
    }

    public record RawIconSkillBuilderData(String itemID, String categoryId) {

        public WeaponCategory getParseWeaponCategory(String categoryId) {
            return  WeaponCategory.ENUM_MANAGER.getOrThrow(categoryId);
        }

        public Item getParseItem(String itemID) {
            ResourceLocation itemKey = ResourceLocation.tryParse(itemID);
            if (itemKey == null) return null;
            return ExtendedDatapacksUtilities.getSafeItem(itemKey.getNamespace(), itemKey.getPath());
        }
    }
}

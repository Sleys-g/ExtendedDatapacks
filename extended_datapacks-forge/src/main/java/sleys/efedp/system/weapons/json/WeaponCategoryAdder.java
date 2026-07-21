package sleys.efedp.system.weapons.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import sleys.efedp.main.ExtendedDatapacks;
import sleys.sl.datadriven.api.SLDataDrivenAPI;
import sleys.sl.library.runtime.policy.error.ErrorPolicy;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class WeaponCategoryAdder {
    private static final List<CategoryData> WEAPON_CATEGORY_DATA = new ArrayList<>();

    public static void startToTracking(Path configDir) {
        WEAPON_CATEGORY_DATA.clear();
        if (Files.exists(configDir)) {
            ErrorPolicy.DEPURATE_ERROR.executeTaskWithMsg(
                    () -> startToLoadFromConfig(configDir), "[Add Category to Build] Error reading Category config"
            );
        } else {
            fileError("/Config Folder");
        }

        var weaponCategoryBuilders = SLDataDrivenAPI.collectResources("weapon_builder/category");
        if (!weaponCategoryBuilders.isEmpty()) {
            for (var entry : weaponCategoryBuilders.entrySet()) {
                String modId = entry.getKey();
                for (Path file : entry.getValue()) {
                    if (!file.toString().endsWith(".json")) continue;
                    ExtendedDatapacks.LOGGER.info(
                            "[Add Category to Build] Parameterization file detected In-Jar, operating for {} -> {}",
                            modId,
                            file.getFileName()
                    );

                    ErrorPolicy.DEPURATE_ERROR.executeTaskWithMsg(
                            () -> startToLoad(file, modId),
                            "[Add Category to Build] Error reading: " + file
                    );
                }
            }
        } else {
            fileError("In-Jar Folder");
        }
    }

    private static void fileError(String side) {
        ExtendedDatapacks.LOGGER.info(
                "[Add Category to Build] There are no parameter files for Weapon Category on {}",
                side
        );
    }

    @SuppressWarnings("all")
    private static void startToLoadFromConfig(Path configDir) throws IOException {
        Files.list(configDir).filter(p -> p.toString().endsWith(".json"))
                .forEach(p -> ErrorPolicy.DEPURATE_ERROR.executeTask(
                        () -> startToLoad(p, "config")
                ))
        ;
    }

    private static void startToLoad(Path file, String modId) throws IOException {
        Reader reader = Files.newBufferedReader(file);
        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
        ExtendedDatapacks.LOGGER.info(
                "[Add Category to Build] Reading In-Jar File: {}", file.getFileName()
        );

        startToProcessWeaponCategoryEntry(root, file.getFileName().toString(), modId);
    }

    private static void startToProcessWeaponCategoryEntry(JsonObject root, String fileName, String modId) {
        if (!root.has("weapon_category_builder") || !root.get("weapon_category_builder").isJsonArray()) {
            ExtendedDatapacks.LOGGER.warn(
                    "[Add Category to Build] File without 'weapon_category_builder': {}",
                    fileName
            );
            return;
        }

        JsonArray array = root.getAsJsonArray("weapon_category_builder");
        for (JsonElement element : array) {

            if (!element.isJsonObject()) continue;
            List<CategoryData> dataList = parseWeaponCategoryConfig(element.getAsJsonObject(), fileName, modId);
            WEAPON_CATEGORY_DATA.addAll(dataList);
        }
    }

    private static List<CategoryData> parseWeaponCategoryConfig(JsonObject json, String fileName, String modId) {
        List<CategoryData> result = new ArrayList<>();
        if (!json.has("category")) {
            ExtendedDatapacks.LOGGER.warn(
                    "[Add Category to Build] missing 'category' in: {}",
                    fileName
            );
            return result;
        }

        if (modId.equals("config")) {
            if ( !json.has("modid")) {
                ExtendedDatapacks.LOGGER.warn(
                        "[Add Category to Build] missing 'modID' in: {}, If the system does not originate from within a mod, assigning a signing modId is mandatory.",
                        fileName
                );
                return result;
            } else {
                modId = json.get("modid").getAsString();
            }
        } else {
            if (json.has("modid")) {
                ExtendedDatapacks.LOGGER.warn(
                        "[Add Category to Build] A 'modid' was found in: {}, however, the file exists from an In-Jar, therefore the assignment is ignored and the namespace in which it is registered is considered correct.",
                        fileName
                );
            }
        }

        JsonElement element = json.get("category");
        if (element.isJsonPrimitive()) {

            String category = element.getAsString();

            if (!category.isEmpty()) {
                result.add(new CategoryData(modId, category));
            }

        }

        else if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (JsonElement e : array) {
                if (!e.isJsonPrimitive()) continue;
                String category = e.getAsString();
                if (!category.isEmpty()) {
                    result.add(new CategoryData(modId, category));
                }
            }
        }
        else {
            ExtendedDatapacks.LOGGER.warn(
                    "[Add Category to Build] invalid 'category' format in {}",
                    fileName
            );
        }

        return result;
    }

    public static List<CategoryData> getCategoryData() {
        return WEAPON_CATEGORY_DATA;
    }

    public record CategoryData(String modId, String categoryId) {
        public WeaponCategory getParsedWeaponCategory() {
            return WeaponCategory.ENUM_MANAGER.getOrThrow(categoryId);
        }
    }
}
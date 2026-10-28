package sleys.efedp.neoforge.system.category;

import sleys.efedp.neoforge.ExtendedDatapacks;
import sleys.efedp.neoforge.api.registry.RegistryWeaponTrackers;
import sleys.efedp.neoforge.system.category.definitions.WeaponCategoryRegistryDefinitions;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public final class WeaponCategoriesRegistry implements WeaponCategory {

    private static boolean INITIALIZED = false;
    public static final WeaponCategoriesRegistry INSTANCE = new WeaponCategoriesRegistry();

    private WeaponCategoriesRegistry() {}

    @Override
    public int universalOrdinal() {
        return 0;
    }

    public static WeaponCategoriesRegistry[] values() {
        initialize();
        return new WeaponCategoriesRegistry[]{ INSTANCE };
    }

    private static synchronized void initialize() {
        if (INITIALIZED) return;

        ExtendedDatapacks.LOGGER.info("[Weapon Category Registry] Starting initialization of JSON weapon categories");

        Map<String, List<WeaponCategoryRegistryDefinitions>> rawDefinitions = RegistryWeaponTrackers
                .WEAPON_CATEGORY_REGISTRY
                .getDefinitionData();

        List<WeaponCategoryRegistryDefinitions.CategoryData> allCategories = new ArrayList<>();

        for (Map.Entry<String, List<WeaponCategoryRegistryDefinitions>> entry : rawDefinitions.entrySet()) {
            String modId = entry.getKey();
            for (WeaponCategoryRegistryDefinitions def : entry.getValue()) {
                allCategories.addAll(def.toCategoryData(modId));
            }
        }

        if (allCategories.isEmpty()) {
            ExtendedDatapacks.LOGGER.info("[Weapon Category Registry] No JSON categories found to register");
            INITIALIZED = true;
            return;
        }

        Map<String, List<String>> categoriesByModId = allCategories.stream()
                .collect(Collectors.groupingBy(
                        WeaponCategoryRegistryDefinitions.CategoryData::modId,
                        Collectors.mapping(WeaponCategoryRegistryDefinitions.CategoryData::categoryId, Collectors.toList())
                ));

        int totalRegistered = 0;
        int totalSkipped = 0;

        for (Map.Entry<String, List<String>> entry : categoriesByModId.entrySet()) {
            String modId = entry.getKey();
            List<String> categories = entry.getValue();

            for (String categoryId : categories) {
                categoryId = categoryId.toUpperCase(Locale.ROOT);
                try { totalSkipped = ifAlreadyRegistered(categoryId, totalSkipped); }
                catch (Exception ignored) { totalRegistered = ifNeedRegister(modId, categoryId, totalRegistered); }
            }
        }

        ExtendedDatapacks.LOGGER.info(
                "[Weapon Category Registry] Initialization complete - Registered: {}, Skipped (already exist): {}",
                totalRegistered, totalSkipped
        );

        INITIALIZED = true;
    }

    private static int ifAlreadyRegistered(String categoryId, int totalSkipped) {
        WeaponCategory.ENUM_MANAGER.getOrThrow(categoryId);
        ExtendedDatapacks.LOGGER.warn("[Weapon Category Registry] Category '{}' already exists, skipping", categoryId);
        return totalSkipped + 1;
    }

    private static int ifNeedRegister(String modId, String categoryId, int totalRegistered) {
        new JsonWeaponCategory(modId, categoryId);
        ExtendedDatapacks.LOGGER.info("[Weapon Category Registry] Registered '{}' for mod '{}'", categoryId, modId);
        return totalRegistered + 1;
    }

    public static class JsonWeaponCategory implements WeaponCategory {

        private final int id;
        private final String name;
        private final String modId;

        public JsonWeaponCategory(String modId, String name) {
            this.name = name;
            this.modId = modId;
            this.id = WeaponCategory.ENUM_MANAGER.assign(this);

            ExtendedDatapacks.LOGGER.info(
                    "[Weapon Category Registry] Created Json -> WeaponCategory: [ModId = {}, Name = {}, ID = {}]",
                    modId, name, this.id
            );
        }

        @Override
        public int universalOrdinal() {
            return this.id;
        }

        public String getName() {
            return name;
        }

        public String getModId() {
            return modId;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
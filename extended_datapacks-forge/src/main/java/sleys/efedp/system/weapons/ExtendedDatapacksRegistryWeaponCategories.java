package sleys.efedp.system.weapons;

import sleys.efedp.main.ExtendedDatapacks;
import sleys.efedp.system.weapons.json.WeaponCategoryAdder;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class ExtendedDatapacksRegistryWeaponCategories implements WeaponCategory {


    private static boolean INITIALIZED = false;
    public static final ExtendedDatapacksRegistryWeaponCategories INSTANCE = new ExtendedDatapacksRegistryWeaponCategories();

    private ExtendedDatapacksRegistryWeaponCategories() {}

    @Override
    public int universalOrdinal() {
        return 0;
    }

    public static ExtendedDatapacksRegistryWeaponCategories[] values() {
        initialize();
        return new ExtendedDatapacksRegistryWeaponCategories[]{ INSTANCE };
    }

    private static synchronized void initialize() {
        if (INITIALIZED) return;

        ExtendedDatapacks.LOGGER.info("[Weapon Category Registry] Starting initialization of JSON weapon categories");
        List<WeaponCategoryAdder.CategoryData> allCategories = WeaponCategoryAdder.getCategoryData();

        if (allCategories.isEmpty()) {
            ExtendedDatapacks.LOGGER.info("[Weapon Category Registry] No JSON categories found to register");
            INITIALIZED = true;
            return;
        }

        Map<String, List<String>> categoriesByModId = allCategories.stream()
                .collect(Collectors.groupingBy(
                        WeaponCategoryAdder.CategoryData::modId,
                        Collectors.mapping(WeaponCategoryAdder.CategoryData::categoryId, Collectors.toList())
                ));

        int totalRegistered = 0;
        int totalSkipped = 0;

        for (Map.Entry<String, List<String>> entry : categoriesByModId.entrySet()) {
            String modId = entry.getKey();
            List<String> categories = entry.getValue();

            for (String categoryId : categories) {
                categoryId = categoryId.toUpperCase(Locale.ROOT);
                try {
                    WeaponCategory.ENUM_MANAGER.getOrThrow(categoryId);
                    ExtendedDatapacks.LOGGER.warn("[Weapon Category Registry] Category '{}' already exists, skipping", categoryId);
                    totalSkipped++;
                } catch (Exception e) {
                    new JsonWeaponCategory(modId, categoryId);
                    ExtendedDatapacks.LOGGER.info("[Weapon Category Registry] Registered '{}' for mod '{}'", categoryId, modId);
                    totalRegistered++;
                }
            }
        }

        ExtendedDatapacks.LOGGER.info(
                "[Weapon Category Registry] Initialization complete - Registered: {}, Skipped (already exist): {}",
                totalRegistered, totalSkipped
        );

        INITIALIZED = true;
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
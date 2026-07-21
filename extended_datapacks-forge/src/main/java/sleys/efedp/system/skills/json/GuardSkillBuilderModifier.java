package sleys.efedp.system.skills.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import sleys.efedp.main.ExtendedDatapacks;
import sleys.sl.datadriven.api.SLDataDrivenAPI;
import sleys.sl.library.runtime.policy.error.ErrorPolicy;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class GuardSkillBuilderModifier {
    private static final Map<String, List<RawGuardBuilderData>> SKILL_GUARD_BUILD_DATA = new ConcurrentHashMap<>();
    private static final String SL_FOLDER_KEY = "skill_builder/guard_skills";

    public static void startToTracking(Path configDir) {
        SKILL_GUARD_BUILD_DATA.clear();
        startToTrackingFromConfig(configDir);
        startToTrackingFromAPI();
    }

    private static void startToTrackingFromConfig(Path configDir) {
        if (Files.exists(configDir)) {
            ErrorPolicy.DEPURATE_ERROR.executeTaskWithMsg(
                    () -> GuardSkillBuilderModifier.startToWalking(configDir),
                    "[Add Guard to Build] Error reading SkillGuardBuilder config"
            );
        } else {
            fileError(" /Config Folder");
        }
    }

    private static void startToWalking(Path configDir) throws IOException, UncheckedIOException {
        Stream<Path> paths = Files.list(configDir);
        paths.filter(p -> p.toString().endsWith(".json"))
                .forEach(path ->
                        ErrorPolicy.DEPURATE_ERROR.executeTaskWithMsg(
                                () -> startToLoad(path),
                                "[Add Guard to Build] Error reading: " + path
                        )
                )
        ;
    }

    private static void startToTrackingFromAPI() {
        var guardSkillBuilders = SLDataDrivenAPI.collectResources(SL_FOLDER_KEY);
        if (!guardSkillBuilders.isEmpty()) {
            for (var entry : guardSkillBuilders.entrySet()) {
                String modId = entry.getKey();
                for (Path file : entry.getValue()) {
                    if (!file.toString().endsWith(".json")) continue;
                    ExtendedDatapacks.LOGGER.info("[Add Guard to Build] Parameterization file detected In-Jar, operating for {} -> {}",
                            modId,
                            file.getFileName()
                    );

                    ErrorPolicy.DEPURATE_ERROR.executeTaskWithMsg(
                            () -> startToLoad(file),
                            "[Add Guard to Build] Error reading: " + file
                    );
                }
            }
        } else {
            fileError(" In-Jar Folder");
        }
    }

    private static void fileError(String side) {
        ExtendedDatapacks.LOGGER.info("[Add Guard to Build] There are no parameter files for SkillGuardBuilder on the side of {}",
                side
        );
    }

    private static void startToLoad(Path file) throws IOException {
        Reader reader = Files.newBufferedReader(file);
        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
        ExtendedDatapacks.LOGGER.info("[Add Guard to Build] Reading file: {}", file.getFileName().toString());
        startToProcessGuardEntry(root, file.getFileName().toString());
    }

    private static void startToProcessGuardEntry(JsonObject root, String fileName) {
        if (!root.has("skill_guard_builder") || !root.get("skill_guard_builder").isJsonArray()) {
            ExtendedDatapacks.LOGGER.warn("[Add Guard to Build] File without 'skill_guard_builder': {}", fileName);
            return;
        }

        JsonArray array = root.getAsJsonArray("skill_guard_builder");
        for (JsonElement element : array) {
            if (!element.isJsonObject()) continue;

            RawGuardBuilderData data = startToParse(element.getAsJsonObject(), fileName);
            if (data != null && data.skillId() != null) {
                SKILL_GUARD_BUILD_DATA
                        .computeIfAbsent(data.skillId(), k -> new ArrayList<>())
                        .add(data);
            }
        }
    }

    private static RawGuardBuilderData startToParse(JsonObject json, String fileName) {
        if (!json.has("skill_guard_id") || !json.has("category")) {
            ExtendedDatapacks.LOGGER.warn("[Add Guard to Build] Missing 'skill_guard_id' or category in: {}", fileName);
            return null;
        }

        String skillId = json.get("skill_guard_id").getAsString();
        if (skillId == null || skillId.isEmpty()) {
            ExtendedDatapacks.LOGGER.warn("[Add Guard to Build] invalid 'skill_guard' in: {}", fileName);
            return null;
        }

        String category = json.get("category").getAsString();
        if (category == null || category.isEmpty()) {
            ExtendedDatapacks.LOGGER.warn("[Add Guard to Build] invalid 'category' in: {}", fileName);
            return null;
        }

        if (!json.has("guard_motion") || !json.has("guard_break_motion")) {
            ExtendedDatapacks.LOGGER.warn("[Add Guard to Build] 'guard_motion' or 'guard_break_motion' missing in: {}", fileName);
            return null;
        }

        GuardMotionData guardMotionData = parseMotionData(json.get("guard_motion"), fileName, "guard_motion");
        if (guardMotionData == null) {
            ExtendedDatapacks.LOGGER.warn("[Add Guard to Build] Error parsing 'guard_motion' in: {}", fileName);
            return null;
        }

        GuardMotionData guardBreakMotionData = parseMotionData(json.get("guard_break_motion"), fileName, "guard_break_motion");
        if (guardBreakMotionData == null) {
            ExtendedDatapacks.LOGGER.warn("[Add Guard to Build] Error parsing 'guard_break_motion' in: {}", fileName);
            return null;
        }

        GuardMotionData guardAdvancedMotionData = null;
        if (json.has("guard_advanced_motion")) {
            guardAdvancedMotionData = parseMotionData(json.get("guard_advanced_motion"), fileName, "guard_advanced_motion");
        }

        return new RawGuardBuilderData(
                skillId,
                category,
                guardMotionData,
                guardBreakMotionData,
                guardAdvancedMotionData
        );
    }

    private static GuardMotionData parseMotionData(JsonElement element, String fileName, String fieldName) {
        if (element.isJsonPrimitive()) {
            String animString = element.getAsString();
            if (animString == null || animString.isEmpty()) {
                ExtendedDatapacks.LOGGER.warn("[Add Guard to Build] Empty animation in: {} of {}", fieldName, fileName);
                return null;
            }

            Map<String, Object> returnMap = new HashMap<>();
            returnMap.put("return", animString);

            return new GuardMotionData("single", returnMap);
        }

        if (!element.isJsonObject()) {
            ExtendedDatapacks.LOGGER.warn("[Add Guard to Build] Invalid format in: {} of {}", fieldName, fileName);
            return null;
        }

        JsonObject motionObj = element.getAsJsonObject();

        if (!motionObj.has("type")) {
            ExtendedDatapacks.LOGGER.warn("[Add Guard to Build] 'type' is missing from: {} of {}", fieldName, fileName);
            return null;
        }

        String type = motionObj.get("type").getAsString();
        if (!motionObj.has("per_style")) {
            ExtendedDatapacks.LOGGER.warn("[Add Guard to Build] 'per_style' is missing from: {} of {}", fieldName, fileName);
            return null;
        }

        if (!motionObj.get("per_style").isJsonObject()) {
            ExtendedDatapacks.LOGGER.warn("[Add Guard to Build] 'per_style' must be an object in: {} of {}", fieldName, fileName);
            return null;
        }

        JsonObject perStyleObj = motionObj.getAsJsonObject("per_style");
        Map<String, Object> styleMap = new HashMap<>();


        for (Map.Entry<String, JsonElement> entry : perStyleObj.entrySet()) {
            String styleName = entry.getKey();
            JsonElement value = entry.getValue();

            if ("single".equals(type)) {
                if (!value.isJsonPrimitive()) {
                    ExtendedDatapacks.LOGGER.warn("[Add Guard to Build] Value must be string for type 'single' in: {} -> {} of {}",
                            fieldName, styleName, fileName
                    );
                    continue;
                }
                styleMap.put(styleName, value.getAsString());

            } else if ("multi".equals(type)) {
                if (!value.isJsonArray()) {
                    ExtendedDatapacks.LOGGER.warn("[Add Guard to Build] Value must be array for type 'multi' in: {} -> {} of {}",
                            fieldName, styleName, fileName
                    );
                    continue;
                }

                JsonArray array = value.getAsJsonArray();
                List<String> stringList = new ArrayList<>();

                for (JsonElement elem : array) {
                    if (elem.isJsonPrimitive()) {
                        stringList.add(elem.getAsString());
                    }
                }

                styleMap.put(styleName, stringList);

            } else {
                ExtendedDatapacks.LOGGER.warn("[Add Guard to Build] Type '{}' not supported in: {} of {}", type, fieldName, fileName);
                return null;
            }
        }

        if (styleMap.isEmpty()) {
            ExtendedDatapacks.LOGGER.warn("[Add Guard to Build] empty 'per_style' in: {} of {}", fieldName, fileName);
            return null;
        }

        return new GuardMotionData(type, styleMap);
    }

    public record GuardMotionData(String type, Map<String, Object> styleData) {
        public GuardMotionData(String type, Map<String, Object> styleData) {
            this.type = type;
            this.styleData = Collections.unmodifiableMap(styleData);
        }

        public Set<String> getDeclaredStyles() {
            return styleData.keySet();
        }

        public boolean isPerStyle() {
            return styleData.size() > 1 || !styleData.containsKey("return");
        }

        public boolean hasStyle(String style) {
            return styleData.containsKey(style);
        }

        public boolean isMulti() {
            return "multi".equals(type);
        }

        @Nullable
        public String getAnimationForStyle(String style) {
            Object value = styleData.get(style);
            if (value instanceof String) {
                return (String) value;
            }
            return null;
        }

        @Nullable
        public List<String> getAnimationsForStyle(String style) {
            Object value = styleData.get(style);
            if (value instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> list = (List<String>) value;
                return list;
            }
            return null;
        }

        @Nullable
        public String getReturnAnimation() {
            return getAnimationForStyle("return");
        }
    }

    @Nullable
    public static List<RawGuardBuilderData> get(String skillId) {
        return SKILL_GUARD_BUILD_DATA.get(skillId);
    }

    public record RawGuardBuilderData(
            String skillId,
            String categoryId,
            GuardMotionData guardMotion,
            GuardMotionData guardBreakMotion,
            @Nullable GuardMotionData guardAdvancedMotion
    ) {
        public boolean hasAdvancedMotion() {
            return guardAdvancedMotion != null;
        }

        public String getGuardMotionForStyle(String style) {
            return guardMotion.getAnimationForStyle(style);
        }

        public String getGuardBreakMotionForStyle(String style) {
            return guardBreakMotion.getAnimationForStyle(style);
        }

        @Nullable
        public String getAdvancedMotionForStyle(String style) {
            if (guardAdvancedMotion == null) return null;
            return guardAdvancedMotion.getAnimationForStyle(style);
        }

        @Nullable
        public List<String> getAdvancedMotionsForStyle(String style) {
            if (guardAdvancedMotion == null) return null;
            return guardAdvancedMotion.getAnimationsForStyle(style);
        }

        @Nullable
        public AnimationManager.AnimationAccessor<? extends StaticAnimation> getParseMotions(String animation) {
            var registryAnimationID = ResourceLocation.tryParse(animation);
            if (registryAnimationID == null) return null;
            return AnimationManager.byKey(registryAnimationID);
        }

        public ResourceLocation getParseSkill() {
            return ResourceLocation.tryParse(skillId);
        }

        public WeaponCategory getParseWeaponCategory() {
            return  WeaponCategory.ENUM_MANAGER.getOrThrow(categoryId);
        }

        public Style getParseStyle(String style) {
            return  Style.ENUM_MANAGER.getOrThrow(style);
        }

        public boolean isSameSkill(ResourceLocation skillRegistry, ResourceLocation skillParsed) {
            return skillRegistry.equals(skillParsed);
        }
    }
}
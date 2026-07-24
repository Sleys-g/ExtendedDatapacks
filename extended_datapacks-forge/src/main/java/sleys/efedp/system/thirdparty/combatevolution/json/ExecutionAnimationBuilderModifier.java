package sleys.efedp.system.thirdparty.combatevolution.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import net.shelmarow.combat_evolution.execution.ExecutionTypeManager;
import net.shelmarow.combat_evolution.gameassets.animation.ExecutionAttackAnimation;
import net.shelmarow.combat_evolution.gameassets.animation.ExecutionHitAnimation;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.bootstrap.BootstrapThirdParties;
import sleys.sl.datadriven.api.SLDataDrivenAPI;
import sleys.sl.epicfight.capability.StyleInvalid;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class ExecutionAnimationBuilderModifier {
    private static final List<RawExecutionBuilderData> EXECUTION_BUILDER_DATA = new ArrayList<>();
    private static final String SL_FOLDER_KEY = "third_party/combat_evolution/execution";

    public static void startToTracking(Path configDir) {
        if (!BootstrapThirdParties.COMBAT_EVOLUTION) return;
        EXECUTION_BUILDER_DATA.clear();

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
                configDir, ExecutionAnimationBuilderModifier::startToWaling
        ).ifFailure(e ->
                ExtendedDatapacks.LOGGER.error("[Add Execution to Build] Error reading Category config", e)
        );
    }

    private static Path startToWaling(Path configDir) throws IOException {
        Stream<Path> paths = Files.list(configDir);
        paths.filter(p -> p.toString().endsWith(".json"))
                .forEach(path ->
                        ExecutionTasks.operateAndGetResult(
                                ExecutionPolicy.RESIST,
                                path, ExecutionAnimationBuilderModifier::startToLoad
                        ).ifFailure(e ->
                                ExtendedDatapacks.LOGGER.warn("[Add Execution to Build] Error reading: {}", path, e)
                        )
                );

        return configDir;
    }

    private static void startToTrackingFromAPI() {
        var executionAnimationBuilders = SLDataDrivenAPI.collectResources(SL_FOLDER_KEY);

        if (executionAnimationBuilders.isEmpty()) {
            fileError("In-Jar Folder");
            return;
        }

        for (var entry : executionAnimationBuilders.entrySet()) {

            String modId = entry.getKey();
            for (Path file : entry.getValue()) {
                if (!file.toString().endsWith(".json")) continue;
                ExtendedDatapacks.LOGGER.info(
                        "[Add Execution to Build] Parameterization file detected In-Jar, operating for {} -> {}",
                        modId,
                        file.getFileName()
                );

                ExecutionTasks.operateAndGetResult(
                        ExecutionPolicy.RESIST,
                        file, ExecutionAnimationBuilderModifier::startToLoad
                ).ifFailure(e ->
                        ExtendedDatapacks.LOGGER.warn( "[Add Execution to Build] Error reading: {}", file, e)
                );
            }
        }

    }

    private static void fileError(String side) {
        ExtendedDatapacks.LOGGER.info(
                "[Add Execution to Build] There are no parameter files for Execution Animation on {}",
                side
        );
    }


    private static Path startToLoad(Path file) throws IOException {
        Reader reader = Files.newBufferedReader(file);
        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
        ExtendedDatapacks.LOGGER.info(
                "[Add Execution to Build] Reading file: {}",
                file.getFileName()
        );

        startToProcessExecutions(root, file.getFileName().toString());
        return file;
    }

    private static void startToProcessExecutions(JsonObject root, String fileName) {
        if (!root.has("execution_animation_builder") || !root.get("execution_animation_builder").isJsonArray()) {
            ExtendedDatapacks.LOGGER.warn(
                    "[Add Execution to Build] File without 'execution_animation_builder': {}",
                    fileName
            );
            return;
        }

        JsonArray array = root.getAsJsonArray("execution_animation_builder");

        for (JsonElement element : array) {

            if (!element.isJsonObject()) continue;
            List<RawExecutionBuilderData> dataList = startToParse(element.getAsJsonObject(), fileName);
            EXECUTION_BUILDER_DATA.addAll(dataList);
        }
    }

    private static List<RawExecutionBuilderData> startToParse(JsonObject json, String fileName) {

        List<RawExecutionBuilderData> result = new ArrayList<>();

        if (!json.has("category")) {
            ExtendedDatapacks.LOGGER.warn(
                    "[Add Category to Build] missing 'category' in: {}",
                    fileName
            );
            return result;
        }

        if (!json.has("item")) {
            ExtendedDatapacks.LOGGER.warn(
                    "[Add Category to Build] missing 'item' in: {}",
                    fileName
            );
            return result;
        }

        if (!json.has("style")) {
            ExtendedDatapacks.LOGGER.warn(
                    "[Add Category to Build] missing 'style' in: {}",
                    fileName
            );
            return result;
        }

        if (!json.has("executionAnimation")) {
            ExtendedDatapacks.LOGGER.warn(
                    "[Add Category to Build] missing 'executionAnimation' in: {}",
                    fileName
            );
            return result;
        }

        if (!json.has("executedAnimation")) {
            ExtendedDatapacks.LOGGER.warn(
                    "[Add Category to Build] missing 'executedAnimation' in: {}",
                    fileName
            );
            return result;
        }

        if (!json.has("xOffset")) {
            ExtendedDatapacks.LOGGER.warn(
                    "[Add Category to Build] missing 'xOffset' in: {}",
                    fileName
            );
            return result;
        }
        if (!json.has("yOffset")) {
            ExtendedDatapacks.LOGGER.warn(
                    "[Add Category to Build] missing 'yOffset' in: {}",
                    fileName
            );
            return result;
        }
        if (!json.has("zOffset")) {
            ExtendedDatapacks.LOGGER.warn(
                    "[Add Category to Build] missing 'zOffset' in: {}",
                    fileName
            );
            return result;
        }

        if (!json.has("rotationOffset")) {
            ExtendedDatapacks.LOGGER.warn(
                    "[Add Category to Build] missing 'rotationOffset' in: {}",
                    fileName
            );
            return result;
        }

        if (!json.has("totalTick")) {
            ExtendedDatapacks.LOGGER.warn(
                    "[Add Category to Build] missing 'totalTick' in: {}",
                    fileName
            );
            return result;
        }


        JsonElement category = json.get("category");
        JsonElement item = json.get("item");
        JsonElement style = json.get("style");
        JsonElement executionAnimation = json.get("executionAnimation");
        JsonElement executedAnimation = json.get("executedAnimation");

        JsonElement xOffset = json.get("xOffset");
        JsonElement yOffset = json.get("yOffset");
        JsonElement zOffset = json.get("zOffset");
        JsonElement rotationOffset = json.get("rotationOffset");
        JsonElement totalTick = json.get("totalTick");
        result.add(new RawExecutionBuilderData(
                category.getAsString(), item.getAsString(), style.getAsString(),
                executionAnimation.getAsString(), executedAnimation.getAsString(),
                xOffset.getAsFloat(), yOffset.getAsFloat(), zOffset.getAsFloat(),
                rotationOffset.getAsFloat(), totalTick.getAsInt()
        ));

        return result;
    }

    public static List<RawExecutionBuilderData> getExecutionData() {
        return EXECUTION_BUILDER_DATA;
    }

    public record RawExecutionBuilderData(
            String category,
            String item,
            String style,
            String executionAnimation,
            String executedAnimation,
            float xOffset,
            float yOffset,
            float zOffset,
            float rotationOffset,
            int totalTick

    ) {
        public boolean isNullItem() {
            return item.toLowerCase(Locale.ROOT).equals("null");
        }

        @Nullable
        public ResourceLocation getParseItemRegistry() {
            if (isNullItem()) return null;
            return ResourceLocation.tryParse(item);
        }

        @Nullable
        public Item getParseItem() {
            var parseRegistry = getParseItemRegistry();
            if (parseRegistry == null) return null;
            return ForgeRegistries.ITEMS.getValue(parseRegistry);
        }

        public Style getParseStyle() {
            if (style.equals("null")) return StyleInvalid.INVALID;
            return  Style.ENUM_MANAGER.getOrThrow(style);
        }

        public WeaponCategory getParsedWeaponCategory() {
            return ExecutionTasks.getAndFallback(
                    ExecutionPolicy.RESIST,
                    () -> WeaponCategory.ENUM_MANAGER.getOrThrow(category),
                    null
            );
        }

        public ExecutionTypeManager.Type getParsedExecutionManager(
                AnimationManager.AnimationAccessor<StaticAnimation> executionAnimationKey,
                AnimationManager.AnimationAccessor<StaticAnimation> executionHitAnimationKey) {

            return new ExecutionTypeManager.Type(
                    getExecutionAnimationAccessor(executionAnimationKey),
                    getExecutionHitAnimationAccessor(executionHitAnimationKey),
                    new Vec3(xOffset, yOffset, zOffset), rotationOffset, totalTick
            );
        }
    }

    @SuppressWarnings({"unchecked", "RedundantCast"})
    private static AnimationManager.AnimationAccessor<? extends ExecutionAttackAnimation> getExecutionAnimationAccessor(
            AnimationManager.AnimationAccessor<StaticAnimation> animationKey) {

        return (AnimationManager.AnimationAccessor<? extends ExecutionAttackAnimation>) (AnimationManager.AnimationAccessor<?>) animationKey;
    }

    @SuppressWarnings({"unchecked", "RedundantCast"})
    private static AnimationManager.AnimationAccessor<? extends ExecutionHitAnimation> getExecutionHitAnimationAccessor(
            AnimationManager.AnimationAccessor<StaticAnimation> animationKey) {

        return (AnimationManager.AnimationAccessor<? extends ExecutionHitAnimation>) (AnimationManager.AnimationAccessor<?>) animationKey;
    }
}

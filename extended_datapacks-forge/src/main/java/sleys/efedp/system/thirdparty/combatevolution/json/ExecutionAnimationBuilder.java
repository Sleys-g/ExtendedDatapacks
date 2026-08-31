package sleys.efedp.system.thirdparty.combatevolution.json;

import com.google.gson.*;
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
import sleys.sl.library.util.io.GsonUtilities;
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

public class ExecutionAnimationBuilder {
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
                configDir, ExecutionAnimationBuilder::startToWaling
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
                                path, ExecutionAnimationBuilder::startToLoad
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
                        file, ExecutionAnimationBuilder::startToLoad
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

        EXECUTION_BUILDER_DATA.add(startToParse(root));
        return file;
    }


    private static RawExecutionBuilderData startToParse(JsonObject json) {
        String category = GsonUtilities.getAsString(json, "category", null);
        String item = GsonUtilities.getAsString(json, "item", "null");
        String style = GsonUtilities.getAsString(json, "style", "null");
        String executionAnimation = GsonUtilities.getAsString(json, "executionAnimation", null);
        String executedAnimation = GsonUtilities.getAsString(json, "executedAnimation", null);

        float xOffset = GsonUtilities.getAsFloat(json, "xOffset", 0F);
        float yOffset = GsonUtilities.getAsFloat(json, "yOffset", 0F);
        float zOffset = GsonUtilities.getAsFloat(json, "zOffset", 0F);
        float rotationOffset = GsonUtilities.getAsFloat(json, "rotationOffset", 0F);
        int totalTick = GsonUtilities.getAsInteger(json, "totalTick", 0);

        return new RawExecutionBuilderData(
                category, item, style,
                executionAnimation, executedAnimation,
                xOffset, yOffset, zOffset,
                rotationOffset, totalTick
        );
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
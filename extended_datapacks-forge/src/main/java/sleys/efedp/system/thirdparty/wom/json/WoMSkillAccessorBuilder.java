package sleys.efedp.system.thirdparty.wom.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import sleys.efedp.ExtendedDatapacks;
import sleys.sl.epicfight.util.helper.patch.PatchPlayerHelper;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WoMSkillAccessorBuilder extends SimplePreparableReloadListener<Map<ResourceLocation, JsonElement>> {
    private static Map<ResourceLocation, SkillAccessorBuilder> SKILL_ACCESSOR_BUILDER_DATA = new HashMap<>();

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    private static final String DIRECTORY_PATH = "wom_configs";


    @Override
    protected @NotNull Map<ResourceLocation, JsonElement> prepare(@NotNull ResourceManager manager,
                                                                  @NotNull ProfilerFiller profiler) {
        ExtendedDatapacks.LOGGER.info("[WoM Skill Configs] Preparing WoM Configs JSONs...");
        Map<ResourceLocation, JsonElement> map = new HashMap<>();
        SimpleJsonResourceReloadListener.scanDirectory(manager, DIRECTORY_PATH, GSON, map);

        if (map.isEmpty()) {
            ExtendedDatapacks.LOGGER.info("[WoM Skill Configs] No wom skill config files found...");
        } else {
            ExtendedDatapacks.LOGGER.info("[WoM Skill Configs] A total of {} skill config were registered!", map.size());
        }
        return map;
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> object,
                         @NotNull ResourceManager manager, @NotNull ProfilerFiller profiler) {

        Map<ResourceLocation, SkillAccessorBuilder> newEntries = new HashMap<>();
        object.forEach((location, element) ->
                        ExecutionTasks.runAndGetResult(
                                ExecutionPolicy.RESIST,
                                () -> this.startToRegistry(newEntries, location, element)
                        ).ifFailure(e ->
                                ExtendedDatapacks.LOGGER.warn("[Weapons Item Properties] Error when trying to load the weapon properties: {}; due to a: ", location, e)
                        )
                );

        SKILL_ACCESSOR_BUILDER_DATA = newEntries;
    }

    public static Optional<SkillAccessorBuilder> getEntry(ResourceLocation id) {
        return Optional.ofNullable(SKILL_ACCESSOR_BUILDER_DATA.get(id));
    }

    @SuppressWarnings("deprecation")
    public static Optional<SkillAccessorBuilder> getSafeEntry(InteractionHand hand, Player player) {
        if (!PatchPlayerHelper.isValidPlayer(player)) return Optional.empty();

        var stack = player.getItemInHand(hand);
        if (stack.isEmpty()) return Optional.empty();

        var stackRL = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return sleys.efedp.system.thirdparty.wom.json.WoMSkillAccessorBuilder.getEntry(stackRL);
    }

    public static Optional<DemonMarkPassiveHelper> getSafeEntryAsDemonMark(InteractionHand hand, Player player) {
        var propertiesData = getSafeEntry(hand, player);
        return propertiesData.map(SkillAccessorBuilder::demonMark);
    }

    public static Optional<SolarPassiveHelper> getSafeEntryAsSolarPassive(InteractionHand hand, Player player) {
        var propertiesData = getSafeEntry(hand, player);
        return propertiesData.map(SkillAccessorBuilder::solarPassive);
    }

    private void startToRegistry(Map<ResourceLocation, SkillAccessorBuilder> newEntries,
                                 ResourceLocation location, JsonElement element) {

        SkillAccessorBuilder entry = GSON.fromJson(element, SkillAccessorBuilder.class);
        newEntries.put(location, entry);
        ExtendedDatapacks.LOGGER.info("[Weapons Item Properties] WoM Skill config {} added!", location);
    }

    public record SkillAccessorBuilder(@SerializedName("demon_mark") DemonMarkPassiveHelper demonMark,
                                       @SerializedName("solar_passive") SolarPassiveHelper solarPassive) {}

    public record DemonMarkPassiveHelper(@SerializedName("allow_antitheus_particles") boolean allowAntitheusParticles,
                                         @SerializedName("allow_basicAntitheus_particles") boolean allowBasicAntitheusParticles) {}

    public record SolarPassiveHelper(@SerializedName("allow_solar_particles") boolean allowSolarParticles) {}
}

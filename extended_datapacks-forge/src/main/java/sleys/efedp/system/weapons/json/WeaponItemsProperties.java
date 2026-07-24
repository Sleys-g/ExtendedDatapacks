package sleys.efedp.system.weapons.json;

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
import java.util.concurrent.ConcurrentHashMap;

public class WeaponItemsProperties extends SimplePreparableReloadListener<Map<ResourceLocation, JsonElement>> {
    public static Map<ResourceLocation, WeaponItemPropertyData> WEAPON_ITEM_PROPERTIES_DATA = new ConcurrentHashMap<>();

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    private static final String DIRECTORY_PATH = "weapons_item_properties";

    @Override
    protected @NotNull Map<ResourceLocation, JsonElement> prepare(@NotNull ResourceManager manager,
                                                                  @NotNull ProfilerFiller profiler) {

        ExtendedDatapacks.LOGGER.info("[Weapons Item Properties] Preparing Weapons Properties JSONs...");
        Map<ResourceLocation, JsonElement> map = new HashMap<>();
        SimpleJsonResourceReloadListener.scanDirectory(manager, DIRECTORY_PATH, GSON, map);

        if (map.isEmpty()) {
            ExtendedDatapacks.LOGGER.info("[Weapons Item Properties] No weapon properties files found...");
        } else {
            ExtendedDatapacks.LOGGER.info("[Weapons Item Properties] A total of {} properties were registered!", map.size());
        }
        return map;
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> object,
                         @NotNull ResourceManager manager, @NotNull ProfilerFiller profiler) {

        Map<ResourceLocation, WeaponItemPropertyData> newEntries = new HashMap<>();
        object.forEach((location, element) ->
                ExecutionTasks.runAndGetResult(
                        ExecutionPolicy.RESIST,
                        () -> this.startToRegistry(newEntries, location, element)
                ).ifFailure(e ->
                        ExtendedDatapacks.LOGGER.error(
                                "[Weapons Item Properties] Error when trying to load the weapon properties: {}; due to a: ",
                                location, e
                        )
                )
        );

        WEAPON_ITEM_PROPERTIES_DATA = newEntries;
    }

    private void startToRegistry(Map<ResourceLocation, WeaponItemPropertyData> newEntries,
                                 ResourceLocation location, JsonElement element) {


        WeaponItemPropertyData entry = GSON.fromJson(element, WeaponItemPropertyData.class);
        newEntries.put(location, entry);
        ExtendedDatapacks.LOGGER.info("[Weapons Item Properties] Weapon Property {} added!", location);
    }

    public static Optional<WeaponItemPropertyData> getEntry(ResourceLocation id) {
        return Optional.ofNullable(WEAPON_ITEM_PROPERTIES_DATA.get(id));
    }

    @SuppressWarnings("deprecation")
    public static Optional<WeaponItemPropertyData> getSafeEntry(InteractionHand hand, Player player) {
        if (!PatchPlayerHelper.isValidPlayer(player)) return Optional.empty();
        if (hand == null) return Optional.empty();

        var stack = player.getItemInHand(hand);
        if (stack.isEmpty()) return Optional.empty();

        var stackRL = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return WeaponItemsProperties.getEntry(stackRL);
    }

    public static Optional<CombatPropertyParams> getSafeEntryAsCombat(InteractionHand hand, Player player) {
        var propertiesData = getSafeEntry(hand, player);
        return propertiesData.map(WeaponItemPropertyData::combat);
    }

    public static Optional<InteractionPropertyParams> getSafeEntryAsInteraction(InteractionHand hand, Player player) {
        var propertiesData = getSafeEntry(hand, player);
        return propertiesData.map(WeaponItemPropertyData::interaction);
    }

    public record WeaponItemPropertyData(CombatPropertyParams combat, InteractionPropertyParams interaction) {}

    public record CombatPropertyParams(@SerializedName("disable_hurt_enemy") Boolean disableHurtEnemy) {
        public Boolean isDisableHurtEnemy() {
            return disableHurtEnemy != null;
        }
    }

    public record InteractionPropertyParams(@SerializedName("force_use_method") Boolean forceUseMethod,
                                            @SerializedName("disable_vanilla_use_animation") Boolean disableUseAnimation,
                                            @SerializedName("use_animation") String useAnimation) {

        public ResourceLocation parsedUseAnimation() {
            return ResourceLocation.tryParse(useAnimation);
        }

        public boolean isForcedUseMethod() {
            return forceUseMethod != null;
        }

        public boolean isDisableUseAnimation() {
            return disableUseAnimation != null;
        }
    }
}
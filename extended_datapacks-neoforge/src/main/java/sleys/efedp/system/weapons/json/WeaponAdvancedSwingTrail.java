package sleys.efedp.system.weapons.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.commons.io.IOUtils;
import sleys.efedp.ExtendedDatapacks;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
import sleys.sl.library.util.data.color.RGB;
import sleys.sl.library.util.io.GsonUtilities;
import yesman.epicfight.world.capabilities.item.Style;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("deprecated")
public class WeaponAdvancedSwingTrail {
    private static final Map<ResourceLocation, AdvancedSwingTrails> SWING_TRAIL_DATA = new ConcurrentHashMap<>();
    private static final String DIRECTORY_PATH = "advanced_swing_trails";
    private static boolean LOADED = false;

    @SubscribeEvent
    public static void OnFinalProcessClient(FMLClientSetupEvent event) {
        if (LOADED) return;
        LOADED = true;
        initialize();
    }

     public static void reinitializeAdvancedSwingTrail() {
        initialize();
    }

    private static void initialize() {
        SWING_TRAIL_DATA.clear();
        ExecutionTasks.runAndGetResult(
                ExecutionPolicy.RESIST,
                WeaponAdvancedSwingTrail::startToTracking
        ).ifFailure(e -> ExtendedDatapacks.LOGGER.warn(
                "[Advanced Swing Trail] Error loading parameterization configuration", e
        ));
    }

    private static void startToTracking() {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        Map<ResourceLocation, Resource> resources = resourceManager.listResources(
                DIRECTORY_PATH,
                path -> path.getPath().endsWith(".json")
        );

        if (resources.isEmpty()) {
            ExtendedDatapacks.LOGGER.info("[Advanced Swing Trail] No advanced parameterization files found for Swing Trails");
            return;
        }

        ExtendedDatapacks.LOGGER.info("[Advanced Swing Trail] Loading {} parameterization files for Swing Trails:", resources.size());
        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            ExecutionTasks.runAndGetResult(
                    ExecutionPolicy.RESIST,
                    () ->  startToLoad(entry.getValue(), entry.getKey())
            ).ifFailure(e -> ExtendedDatapacks.LOGGER.warn(
                    "[Advanced Swing Trail] Error loading file: {}", entry.getKey(), e
            ));
        }

        ExtendedDatapacks.LOGGER.info("[Advanced Swing Trail] Swing Trails configuration LOADED successfully. {} records",
                SWING_TRAIL_DATA.size()
        );
    }

    private static void startToLoad(Resource resource, ResourceLocation resourceLocation) throws IOException {
        InputStream stream = resource.open();
        String json = IOUtils.toString(stream, StandardCharsets.UTF_8);
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        if (root.has("trails") && root.get("trails").isJsonArray()) {
            JsonArray trailsArray = root.getAsJsonArray("trails");
            for (JsonElement element : trailsArray) {
                startToProcessAdvancedSwingTrails(element.getAsJsonObject(), resourceLocation);
            }
        } else {
            ExtendedDatapacks.LOGGER.warn(
                    "[Advanced Swing Trail] Missing 'trails' array in {}",
                    resourceLocation
            );
        }
    }

    private static void startToProcessAdvancedSwingTrails(JsonObject trailObj, ResourceLocation source) {
        if (!trailObj.has("item")) {
            ExtendedDatapacks.LOGGER.warn("[Advanced Swing Trail] Entry without 'item' field in: {}", source);
            return;
        }

        ResourceLocation itemId = ResourceLocation.tryParse(trailObj.get("item").getAsString());
        if (itemId == null) {
            ExtendedDatapacks.LOGGER.warn("[Advanced Swing Trail] Invalid item ID: {}", trailObj.get("item").getAsString());
            return;
        }


        AdvancedSwingTrails trail = ExecutionTasks.getAndFallback(
                ExecutionPolicy.RESIST,
                () -> parseTrailConfig(trailObj),
                null
        );

        if (trail != null) {
            SWING_TRAIL_DATA.put(itemId, trail);
        } else {
            ExtendedDatapacks.LOGGER.error("[Advanced Swing Trail] Error parsing trail configuration");
        }
    }

    private static AdvancedSwingTrails parseTrailConfig(JsonObject json) {
        Style defaultStyle = json.has("default")
                ? Style.ENUM_MANAGER.getOrThrow(json.get("default").getAsString())
                : null;

        Map<Style, SwingTrail> styleTrailMap = new HashMap<>();

        if (json.has("style_config") && json.get("style_config").isJsonArray()) {
            JsonArray configs = json.getAsJsonArray("style_config");

            for (JsonElement e : configs) {
                JsonObject obj = e.getAsJsonObject();

                if (!obj.has("style") || !obj.has("trail_data")) {
                    continue;
                }

                ExecutionTasks.runAndGetResult(
                        ExecutionPolicy.RESIST,
                        () -> registryParsedAdvancedTrails(styleTrailMap, obj)
                ).ifFailure(exception -> ExtendedDatapacks.LOGGER.warn(
                        "[Advanced Swing Trail] Invalid Style configuration entry: {}", obj, exception
                ));
            }
        }

        return new AdvancedSwingTrails(
                defaultStyle,
                styleTrailMap
        );
    }

    private static void registryParsedAdvancedTrails(Map<Style, SwingTrail> styleTrailMap, JsonObject obj) {
        Style style = Style.ENUM_MANAGER.getOrThrow(obj.get("style").getAsString());
        SwingTrail trail = parseSwingTrail(obj.getAsJsonObject("trail_data"));
        styleTrailMap.put(style, trail);
    }

    private static SwingTrail parseSwingTrail(JsonObject json) {
        RGB color = GsonUtilities.getAsRGB(json, "color", RGB.DEFAULT);
        Vec3 beginPos = GsonUtilities.getAsVec3(json, "begin_pos", Vec3.ZERO);
        Vec3 endPos = GsonUtilities.getAsVec3(json, "end_pos", Vec3.ZERO);

        int interpolations = GsonUtilities.getAsInteger(json, "interpolations", 20);
        int lifetime = GsonUtilities.getAsInteger(json, "lifetime", 10);

        ResourceLocation texturePath = GsonUtilities.getAsResourceLocation(json, "texture_path", null);
        ResourceLocation particleId = GsonUtilities.getAsResourceLocation(json, "particle_type", null);

        SimpleParticleType particle =
                particleId != null
                        ? (SimpleParticleType) BuiltInRegistries.PARTICLE_TYPE.get(particleId)
                        : null;

        return new SwingTrail(
                color,
                beginPos,
                endPos,
                interpolations,
                lifetime,
                texturePath,
                particle
        );
    }

    public static AdvancedSwingTrails getAdvancedSwingTrails(ItemStack item) {
        if (!LOADED || item.isEmpty()) {
            return AdvancedSwingTrails.DEFAULT;
        }

        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item.getItem());

        if (SWING_TRAIL_DATA.containsKey(id)) {
            return SWING_TRAIL_DATA.get(id);
        }

        return AdvancedSwingTrails.DEFAULT;
    }

    public static List<ResourceLocation> getRegisteredItems() {
        List<ResourceLocation> resourceLocationList = new ArrayList<>();
        for (var itemKey : SWING_TRAIL_DATA.entrySet()) {
            resourceLocationList.add(itemKey.getKey());
        }

        return resourceLocationList;
    }

    public record AdvancedSwingTrails(Style defaultStyle, Map<Style, SwingTrail> styleTrails) {
        public static final AdvancedSwingTrails DEFAULT =
                new AdvancedSwingTrails(
                        null,
                        Map.of()
                );

        public SwingTrail getTrailForStyle(Style style) {
            SwingTrail trail = styleTrails.get(style);

            if (trail != null) {
                return trail;
            }

            return defaultStyle != null
                    ? styleTrails.get(defaultStyle)
                    : null;
        }
    }

    public record SwingTrail(
            RGB color, Vec3 start, Vec3 end,
            int interpolateCount, int trailLifetime,
            ResourceLocation texturePath, SimpleParticleType particle
    ) {}
}
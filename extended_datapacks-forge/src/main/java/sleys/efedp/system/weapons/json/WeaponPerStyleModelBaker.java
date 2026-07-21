package sleys.efedp.system.weapons.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.commons.io.IOUtils;
import sleys.efedp.main.ExtendedDatapacks;
import sleys.sl.library.runtime.policy.error.ErrorPolicy;
import yesman.epicfight.world.capabilities.item.Style;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class WeaponPerStyleModelBaker {
    private static final Map<ResourceLocation, WeaponModelPerStyle> MODELS_ITEMS_DATA = new ConcurrentHashMap<>();
    private static final String DIRECTORY_PATH = "weapons_model_properties";
    private static final WeaponModelPerStyle DEFAULT = WeaponModelPerStyle.EMPTY;
    
    @SubscribeEvent
    public static void OnFinalProcessClient(FMLClientSetupEvent event) {
        initialize();
    }

    public static void reinitialize() {
        initialize();
    }

    private static void initialize() {
        MODELS_ITEMS_DATA.clear();
        ErrorPolicy.DEPURATE_ERROR.executeTaskWithMsg(
                WeaponPerStyleModelBaker::startToTracking,
                "[Weapon Model Properties] Error loading parameterization configuration..."
        );
    }

    private static void startToTracking() {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        Map<ResourceLocation, Resource> resources = resourceManager.listResources(
                WeaponPerStyleModelBaker.DIRECTORY_PATH,
                path -> path.getPath().endsWith(".json")
        );

        if (resources.isEmpty()) {
            ExtendedDatapacks.LOGGER.info("[Weapon Model Properties] No advanced parameterization files found for Weapon Models");
            return;
        }

        ExtendedDatapacks.LOGGER.info("[Weapon Model Properties] Loading {} parameterization files for Weapon Models", resources.size());
        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            ErrorPolicy.DEPURATE_ERROR.executeTaskWithMsg(
                    () -> startToLoad(entry.getValue(), entry.getKey()),
                    "[Weapon Model Properties] Error loading file: " + entry.getKey()
            );
        }

        ExtendedDatapacks.LOGGER.info("[Weapon Model Properties] Weapon Models configuration loaded successfully: {} records", MODELS_ITEMS_DATA.size());
    }

    private static void startToLoad(Resource resource, ResourceLocation resourceLocation) throws IOException {
        InputStream stream = resource.open();
        String json = IOUtils.toString(stream, StandardCharsets.UTF_8);
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();

        if (root.has("weapons_models") && root.get("weapons_models").isJsonArray()) {
            JsonArray modelsArray = root.getAsJsonArray("weapons_models");
            for (JsonElement element : modelsArray) {
                startToProcessModelEntry(element.getAsJsonObject(), resourceLocation);
            }
        } else {
            ExtendedDatapacks.LOGGER.warn(
                    "[Weapon Model Properties] Missing 'weapons_models' array in {}",
                    resourceLocation
            );
        }
    }

    private static void startToProcessModelEntry(JsonObject trailObj, ResourceLocation source) {
        if (!trailObj.has("item")) {
            ExtendedDatapacks.LOGGER.warn("[Weapon Model Properties] Entry without 'item' field in: {}", source);
            return;
        }

        ResourceLocation itemId = ResourceLocation.tryParse(trailObj.get("item").getAsString());
        if (itemId == null) {
            ExtendedDatapacks.LOGGER.warn("[Weapon Model Properties] Invalid item ID: {}", trailObj.get("item").getAsString());
            return;
        }

        WeaponModelPerStyle models = ErrorPolicy.DEPURATE_ERROR.executeSupplier(
                () -> parseModelsConfig(trailObj),
                null
        );
        
        if (models != null) {
            MODELS_ITEMS_DATA.put(itemId, models);
        } else {
            ExtendedDatapacks.LOGGER.error("[Weapon Model Properties] Error process configuration");
        }
    }

    private static WeaponModelPerStyle parseModelsConfig(JsonObject json) {
        Map<Style, ResourceLocation> styleModels = new HashMap<>();

        if (json.has("style_config") && json.get("style_config").isJsonArray()) {
            JsonArray configs = json.getAsJsonArray("style_config");
            for (JsonElement e : configs) {
                JsonObject obj = e.getAsJsonObject();

                if (!obj.has("style") || !obj.has("model")) continue;
                ErrorPolicy.DEPURATE_ERROR.executeTaskWithMsg(
                        () -> registryParsedConfig(styleModels, obj),
                        "[Weapon Model Properties] Invalid style config"
                );
            }
        }

        return new WeaponModelPerStyle(styleModels);
    }

    private static void registryParsedConfig(Map<Style, ResourceLocation> styleModels, JsonObject obj) {
        Style style = Style.ENUM_MANAGER.getOrThrow(obj.get("style").getAsString());
        ResourceLocation model = ResourceLocation.tryParse(obj.get("model").getAsString());

        if (model == null) {
            ExtendedDatapacks.LOGGER.warn(
                    "[Weapon Model Properties] Invalid model path: {}",
                    obj.get("model")
            );
            return;
        }

        styleModels.put(style, model);
    }

    @SuppressWarnings("deprecation")
    public static WeaponModelPerStyle getModelStyleItems(ItemStack item) {
        if (item.isEmpty()) return DEFAULT;

        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item.getItem());
        return MODELS_ITEMS_DATA.getOrDefault(id, DEFAULT);
    }

    public static Set<ResourceLocation> getAllModels() {
        return MODELS_ITEMS_DATA
                .values()
                .stream()
                .flatMap(config -> config.styleModels().values().stream())
                .collect(Collectors.toSet());
    }

    public record WeaponModelPerStyle(Map<Style, ResourceLocation> styleModels) {
        public static final WeaponModelPerStyle EMPTY = new WeaponModelPerStyle(Map.of());

        public ResourceLocation getModel(Style style) {
            return styleModels.get(style);
        }

        public Map<Style, ResourceLocation> getStyleModels() {
            return styleModels;
        }

        public boolean hasModels() {
            return !styleModels.isEmpty();
        }
    }
}

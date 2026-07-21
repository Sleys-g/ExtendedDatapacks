package sleys.efedp.system.visuals.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import org.apache.commons.io.IOUtils;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.capability.ExtendedDatapacksUtilities;
import sleys.sl.library.exceptions.OutrangePacketException;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
import sleys.sl.library.util.io.GsonUtilities;
import sleys.sl.shaders.data.IShaderParameters;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ShaderAssetsPacksSystem {
    private static final Map<String, List<ShaderPacket>> SHADER_PACKETS = new ConcurrentHashMap<>();
    private static final String DIRECTORY_PATH = "shader_packet";
    private static boolean LOADED = false;

    @SubscribeEvent
    public static void onClientLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        if (!LOADED) {
            LOADED = true;
            initialize();
        }
    }

    public static void reinitializeShaderAssetsPack() {
        initialize();
    }

    private static void initialize() {
        SHADER_PACKETS.clear();
        ExecutionTasks.runAndGetResult(
                ExecutionPolicy.RESIST,
                ShaderAssetsPacksSystem::startToTracking
        ).ifFailure(e -> ExtendedDatapacks.LOGGER.warn(
                "[Shader Packets] Error loading Shader Packets", e)
        );
    }

    private static void startToTracking() {
        var resourceManager = Minecraft.getInstance().getResourceManager();
        var resources = resourceManager.listResources(DIRECTORY_PATH, path -> path.getPath().endsWith(".json"));
        for (var entry : resources.entrySet()) {
            var resource = entry.getValue();
            ExecutionTasks.operateAndGetResult(
                    ExecutionPolicy.RESIST,
                    resource, ShaderAssetsPacksSystem::startToLoad
            ).ifFailure(e -> ExtendedDatapacks.LOGGER.warn(
                    "[Shader Packets] Error processing Shader Packet: {}", entry.getValue(), e
            ));
        }

        ExtendedDatapacks.LOGGER.info("[Shader Packets] Shader Packets LOADED: {}", SHADER_PACKETS.size());
    }

    private static Resource startToLoad(Resource resource) throws IOException {
        var stream = resource.open();
        String json = IOUtils.toString(stream, StandardCharsets.UTF_8);
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        if (!root.has("shader_packet")) return null;

        JsonArray array = root.getAsJsonArray("shader_packet");
        for (JsonElement element : array) {
            ShaderPacket packet = ShaderPacket.tryToBuildThis(element.getAsJsonObject());
            SHADER_PACKETS.computeIfAbsent(packet.category(), k -> new ArrayList<>()).add(packet);
        }

        return resource;
    }

    public static List<ShaderPacket> getForCategory(String category) {
        return SHADER_PACKETS.get(category);
    }

    public record ShaderPacket(String category, String triggerItem, ActivationType activationType, ShaderEffect effect) {

        public static ShaderPacket tryToBuildThis(JsonObject obj) {
            String category = GsonUtilities.getAsString(obj, "category", null);
            String triggerItem = GsonUtilities.getAsString(obj, "trigger_item", "all");

            var activationTypeAsString = GsonUtilities.getAsString(obj, "activation_type", "").toUpperCase();
            ActivationType activationType = ActivationType.valueOf(activationTypeAsString);

            ShaderEffect effect = ShaderEffect.tryToBuildThis(obj.getAsJsonObject("effect"));
            return new ShaderPacket(category, triggerItem, activationType, effect);
        }

        public WeaponCategory getParseWeaponCategory() {
            return WeaponCategory.ENUM_MANAGER.getOrThrow(category);
        }

        public boolean isForAllItem() {
            return triggerItem.toLowerCase(Locale.ROOT).equals("all");
        }

        @Nullable
        public ResourceLocation getParseItemRegistry() {
            if (isForAllItem()) return null;
            return ResourceLocation.tryParse(triggerItem);
        }

        @Nullable
        public Item getParseItem() {
            ResourceLocation itemKey = getParseItemRegistry();
            if (itemKey == null) return null;
            return ExtendedDatapacksUtilities.getSafeItem(itemKey.getNamespace(), itemKey.getPath());
        }
    }

    public record ShaderEffect(String skill, String style, String animation, Float elapse,
                               IShaderParameters shader) {

        public static ShaderEffect tryToBuildThis(JsonObject obj) {
            var skill = GsonUtilities.getAsString(obj, "skill", null);
            var style = GsonUtilities.getAsString(obj, "style", null);
            var animation = GsonUtilities.getAsString(obj, "animation", null);
            var elapse = GsonUtilities.getAsFloat(obj, "elapse", -1F);

            IShaderParameters shader = null;
            for (var entry : obj.entrySet()) {
                String elementKey = entry.getKey();

                if (elementKey.equals("skill") || elementKey.equals("style") ||
                   elementKey.equals("animation") || elementKey.equals("elapse")) continue;

                var jsonElement = entry.getValue();
                if (!jsonElement.isJsonObject()) continue;

                shader = ShaderPacketParsingSystem.tryToGetSealedShaderPacket(jsonElement.getAsJsonObject(), elementKey);
                break;
            }

            if (shader == null) throw new OutrangePacketException("[Shader Packets] No shader declaration found inside effect!");
            return new ShaderEffect(skill, style, animation, elapse, shader);
        }

        @Nullable
        public Style getParseStyle() {
            return Style.ENUM_MANAGER.getOrThrow(style);
        }
    }
}
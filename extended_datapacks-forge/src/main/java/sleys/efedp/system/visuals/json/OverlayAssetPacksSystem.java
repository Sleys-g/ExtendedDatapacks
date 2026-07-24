package sleys.efedp.system.visuals.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.io.IOUtils;
import sleys.efedp.capability.ExtendedDatapacksUtilities;
import sleys.efedp.ExtendedDatapacks;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
import sleys.sl.library.util.io.GsonUtilities;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class OverlayAssetPacksSystem {
    private static final Map<String, List<OverlayPacket>> OVERLAY_PACKETS = new ConcurrentHashMap<>();
    private static final String DIRECTORY_PATH = "overlay_packet";
    private static boolean LOADED = false;

    @SubscribeEvent
    public static void onClientLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        if (LOADED) return;
        LOADED = true;
        initialize();
    }

    public static void reinitializeOverlayAssetPack() {
        initialize();
    }

    private static void initialize() {
        OVERLAY_PACKETS.clear();
        ExecutionTasks.runAndGetResult(
                ExecutionPolicy.RESIST,
                OverlayAssetPacksSystem::startToTracking
        ).ifFailure(e -> ExtendedDatapacks.LOGGER.warn(
                "[Overlays Packets] Error loading overlay configs", e
        ));
    }

    private static void startToTracking() {
        var resourceManager = Minecraft.getInstance().getResourceManager();
        var resources = resourceManager.listResources(DIRECTORY_PATH, path -> path.getPath().endsWith(".json"));
        for (var entry : resources.entrySet()) {
            var resource = entry.getValue();
            ExecutionTasks.operateAndGetResult(
                    ExecutionPolicy.RESIST, resource,
                    OverlayAssetPacksSystem::startToLoad
            ).ifFailure(e -> ExtendedDatapacks.LOGGER.warn(
                    "[Overlays Packets] Error processing: {}", entry.getKey(), e
            ));
        }
        ExtendedDatapacks.LOGGER.info("[Overlays Packets] Loaded {} overlay packets", OVERLAY_PACKETS.size());
    }

    private static Resource startToLoad(Resource resource) throws IOException {
        var stream = resource.open();
        String json = IOUtils.toString(stream, StandardCharsets.UTF_8);
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        if (!root.has("overlay_packet") || !root.get("overlay_packet").isJsonArray()) return null;

        JsonArray array = root.getAsJsonArray("overlay_packet");
        for (JsonElement element : array) {
            OverlayPacket packet = OverlayPacket.tryToBuildThis(element.getAsJsonObject());
            OVERLAY_PACKETS.computeIfAbsent(packet.category(), k -> new ArrayList<>()).add(packet);
        }

        return resource;
    }

    public static List<OverlayPacket> getForCategory(String category) {
        return OVERLAY_PACKETS.getOrDefault(category, Collections.emptyList());
    }

    @SuppressWarnings("deprecation")
    public static List<OverlayEffect> getEffectsForItem(ItemStack item) {
        if (!LOADED || item.isEmpty()) return Collections.emptyList();

        List<OverlayEffect> matchingEffects = new ArrayList<>();
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item.getItem());

        for (List<OverlayPacket> packets : OVERLAY_PACKETS.values()) {
            for (OverlayPacket packet : packets) {
                if (packet.isForAllItem() || itemId.equals(packet.getParseItemRegistry())) {
                    matchingEffects.add(packet.effect());
                }
            }
        }
        return matchingEffects;
    }

    public record OverlayPacket(String category, String triggerItem, ActivationType activationType, OverlayEffect effect) {

        public static OverlayPacket tryToBuildThis(JsonObject obj) {
            String category = GsonUtilities.getAsString(obj, "category", null);
            String triggerItem = GsonUtilities.getAsString(obj, "trigger_item", "all");
            String activationTypeStr = GsonUtilities.getAsString(obj, "activation_type", "").toUpperCase();
            ActivationType activationType = ActivationType.valueOf(activationTypeStr);

            OverlayEffect effect = OverlayEffect.tryToBuildThis(obj.getAsJsonObject("effect"));
            return new OverlayPacket(category, triggerItem, activationType, effect);
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

    public record OverlayEffect(String skill, String style, String animation, Float elapse,
                                OverlayTimeConfigParams overlayTimes,
                                OverlayPathConfigParams overlayPath) {

        public static OverlayEffect tryToBuildThis(JsonObject obj) {
            var skill = GsonUtilities.getAsString(obj, "skill", null);
            var style = GsonUtilities.getAsString(obj, "style", null);
            String animation = GsonUtilities.getAsString(obj, "animation", null);
            Float elapse = GsonUtilities.getAsFloat(obj, "elapse", -1F);

            OverlayTimeConfigParams times = OverlayTimeConfigParams.tryToParseThis(obj.getAsJsonObject("overlay_times"));
            OverlayPathConfigParams path = OverlayPathConfigParams.tryToParseThis(obj.getAsJsonObject("overlay_path"));

            return new OverlayEffect(skill, style, animation, elapse, times, path);
        }

        @Nullable
        public Style getParseStyle() {
            return Style.ENUM_MANAGER.getOrThrow(style);
        }
    }

    public record OverlayTimeConfigParams(int time_in, int time_hold, int time_out) {
        public static OverlayTimeConfigParams tryToParseThis(JsonObject obj) {
            int timeIn = GsonUtilities.getAsInteger(obj, "time_in", 0);
            int timeHold = GsonUtilities.getAsInteger(obj, "time_hold", Integer.MAX_VALUE);
            int timeOut = GsonUtilities.getAsInteger(obj, "time_out", 0);
            return new OverlayTimeConfigParams(timeIn, timeHold, timeOut);
        }
    }

    public record OverlayPathConfigParams(ResourceLocation[] frames, int fps, int size, int alpha) {
        public static OverlayPathConfigParams tryToParseThis(JsonObject obj) {
            int size = GsonUtilities.getAsInteger(obj, "size", 0);
            int fps = GsonUtilities.getAsInteger(obj, "fps", 0);
            int alpha = GsonUtilities.getAsInteger(obj, "alpha", 255);

            String path = GsonUtilities.getAsString(obj, "path", null);
            if (path == null) {
                return new OverlayPathConfigParams(new ResourceLocation[0], fps, size, alpha);
            }

            ResourceLocation[] frames;
            if (path.contains("*")) {
                frames = tryToResolveWildcard(path, size);
            } else {
                ResourceLocation rl = ResourceLocation.tryParse(path);
                frames = rl != null ? new ResourceLocation[]{rl} : new ResourceLocation[0];
            }
            return new OverlayPathConfigParams(frames, fps, size, alpha);
        }

        private static ResourceLocation[] tryToResolveWildcard(String path, int size) {
            int starIndex = path.indexOf('*');
            if (starIndex == -1) return new ResourceLocation[0];

            String prefix = path.substring(0, starIndex);
            String suffix = path.substring(starIndex + 1);

            ResourceLocation base = ResourceLocation.tryParse(prefix + "1" + suffix);
            if (base == null) return new ResourceLocation[0];

            String namespace = base.getNamespace();
            String basePath = prefix.substring(prefix.indexOf(':') + 1);

            ResourceLocation[] frames = new ResourceLocation[size];
            for (int i = 0; i < size; i++) {
                frames[i] = ResourceLocation.fromNamespaceAndPath(
                        namespace,
                        basePath + (i + 1) + suffix
                );
            }
            return frames;
        }
    }
}
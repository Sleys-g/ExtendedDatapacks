package sleys.efedp.system.weapons.json;

import com.google.gson.*;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sleys.efedp.ExtendedDatapacks;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
import sleys.sl.library.util.io.GsonUtilities;
import yesman.epicfight.world.capabilities.item.Style;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WeaponsPassiveParticle extends SimplePreparableReloadListener<Map<ResourceLocation, JsonElement>> {
    private static final Map<ResourceLocation, Map<Style, List<PassiveParticleEntry>>> PASSIVE_PARTICLE_MAP = new HashMap<>();

    public static final WeaponsPassiveParticle INSTANCE = new WeaponsPassiveParticle();
    private static final String DIRECTORY_PATH = "passive_weapons_particles";
    private WeaponsPassiveParticle() {}

    @Override
    protected @NotNull Map<ResourceLocation, JsonElement> prepare(ResourceManager resourceManager,
                                                                  @NotNull ProfilerFiller profiler) {
        Map<ResourceLocation, JsonElement> loaded = new HashMap<>();

        Map<ResourceLocation, Resource> resources = resourceManager.listResources(
                DIRECTORY_PATH,
                path -> path.getPath().endsWith(".json")
        );

        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            try (Reader reader = new BufferedReader(
                    new InputStreamReader(entry.getValue().open(), StandardCharsets.UTF_8))) {

                loaded.put(entry.getKey(), JsonParser.parseReader(reader));

            } catch (Exception e) {
                ExtendedDatapacks.LOGGER.error(
                        "[Weapons Passive Particles] Error reading file: {}",
                        entry.getKey(),
                        e
                );
            }
        }

        return loaded;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> loadedData,
                         @NotNull ResourceManager resourceManager,
                         @NotNull ProfilerFiller profiler) {

        PASSIVE_PARTICLE_MAP.clear();

        if (loadedData.isEmpty()) {
            ExtendedDatapacks.LOGGER.info(
                    "[Weapons Passive Particles] No passive particle configuration files found."
            );
            return;
        }

        for (Map.Entry<ResourceLocation, JsonElement> entry : loadedData.entrySet()) {
            ExecutionTasks.runAndGetResult(
                    ExecutionPolicy.RESIST,
                    () -> parseItemFile(entry.getKey(), entry.getValue())
            ).ifFailure(e ->
                    ExtendedDatapacks.LOGGER.error(
                            "[Weapons Passive Particles] Error loading file: {}",
                            entry.getKey(),
                            e
                    )
            );
        }

        ExtendedDatapacks.LOGGER.info(
                "[Weapons Passive Particles] Loaded {} passive particle definitions.",
                PASSIVE_PARTICLE_MAP.size()
        );
    }

    private static void parseItemFile(ResourceLocation source, JsonElement json) {
        if (!json.isJsonObject()) {
            ExtendedDatapacks.LOGGER.warn(
                    "[Weapons Passive Particles] Root element is not an object: {}",
                    source
            );
            return;
        }

        String path = source.getPath();

        path = path.substring(DIRECTORY_PATH.length() + 1);
        path = path.substring(0, path.length() - ".json".length());

        ResourceLocation itemId =
                ResourceLocation.fromNamespaceAndPath(source.getNamespace(), path);

        JsonObject root = json.getAsJsonObject();

        if (!root.has("emitter")) {
            ExtendedDatapacks.LOGGER.warn(
                    "[Weapons Passive Particles] Missing 'emitter' array in {}",
                    source
            );
            return;
        }

        JsonElement emitterElement = root.get("emitter");

        if (!emitterElement.isJsonArray()) {
            ExtendedDatapacks.LOGGER.warn(
                    "[Weapons Passive Particles] 'emitter' must be an array in {}",
                    source
            );
            return;
        }

        Map<Style, List<PassiveParticleEntry>> styleMap =
                PASSIVE_PARTICLE_MAP.computeIfAbsent(itemId, k -> new HashMap<>());

        for (JsonElement element : emitterElement.getAsJsonArray()) {

            if (!element.isJsonObject()) {
                ExtendedDatapacks.LOGGER.warn(
                        "[Weapons Passive Particles] Invalid emitter in {}",
                        source
                );
                continue;
            }

            JsonObject emitter = element.getAsJsonObject();

            if (!emitter.has("style")
                    || !emitter.has("particle")
                    || !emitter.has("amount")) {

                ExtendedDatapacks.LOGGER.warn(
                        "[Weapons Passive Particles] Incomplete emitter entry in {}: {}",
                        source,
                        emitter
                );
                continue;
            }

            String rawStyle = emitter.get("style").getAsString();
            Style style = Style.ENUM_MANAGER.getOrThrow(rawStyle);

            String particle = emitter.get("particle").getAsString();
            float amount = GsonUtilities.getAsFloat(emitter, "amount", 0.1F);
            Vec3 speed = GsonUtilities.getAsVec3(emitter, "speed", null);

            ResourceLocation particleId = ResourceLocation.tryParse(particle);
            if (particleId == null) {
                ExtendedDatapacks.LOGGER.warn(
                        "[Weapons Passive Particles] Invalid particle '{}' in {}",
                        particle,
                        source
                );
                continue;
            }

            ParticleType<?> type = BuiltInRegistries.PARTICLE_TYPE.get(particleId);

            if (!(type instanceof SimpleParticleType)) {
                ExtendedDatapacks.LOGGER.warn(
                        "[Weapons Passive Particles] Particle '{}' is not a SimpleParticleType.",
                        particle
                );
                continue;
            }

            styleMap.computeIfAbsent(style, s -> new ArrayList<>())
                    .add(new PassiveParticleEntry(particle, amount, speed));
        }
    }

    public static List<PassiveParticleEntry> getEntries(ResourceLocation item, Style style) {
        Map<Style, List<PassiveParticleEntry>> styles = PASSIVE_PARTICLE_MAP.get(item);

        if (styles == null) {
            return List.of();
        }

        return styles.getOrDefault(style, List.of());
    }

    public static Map<ResourceLocation, Map<Style, List<PassiveParticleEntry>>> getPassiveParticleMap() {
        return PASSIVE_PARTICLE_MAP;
    }

    public record PassiveParticleEntry(String passiveParticle, float passiveAmount, Vec3 passiveSpeed) {

        public @Nullable SimpleParticleType getPassiveParticle() {
            ResourceLocation id = ResourceLocation.tryParse(passiveParticle);

            if (id == null) {
                return null;
            }

            ParticleType<?> type = BuiltInRegistries.PARTICLE_TYPE.get(id);

            return type instanceof SimpleParticleType simple
                    ? simple
                    : null;
        }
    }
}
package sleys.efedp.system.animations.json.properties.functional.time.lambda;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.animations.json.properties.functional.datapackets.comparator.NumericComparator;
import sleys.efedp.system.animations.json.properties.functional.time.AnimationsEventInvocation;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.List;
import java.util.Optional;

public record ItemNumberDataReadEvent(ResourceLocation componentId,
                                      NumericComparator numericComparator,
                                      String key,
                                      Number value, Number max, Number min,
                                      List<AnimationsEventInvocation> doEvents) implements IAnimationEventParams {

    public static final MapCodec<ItemNumberDataReadEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("component_id").forGetter(ItemNumberDataReadEvent::componentId),
                    NumericComparator.CODEC.fieldOf("comparator").forGetter(ItemNumberDataReadEvent::numericComparator),
                    Codec.STRING.optionalFieldOf("key", "").forGetter(ItemNumberDataReadEvent::key),

                    Codec.DOUBLE.optionalFieldOf("value")
                            .forGetter(event -> Optional.ofNullable(
                                    event.value() != null ? event.value().doubleValue() : null
                            )),
                    Codec.DOUBLE.optionalFieldOf("max")
                            .forGetter(event -> Optional.ofNullable(
                                    event.max() != null ? event.max().doubleValue() : null
                            )),
                    Codec.DOUBLE.optionalFieldOf("min")
                            .forGetter(event -> Optional.ofNullable(
                                    event.min() != null ? event.min().doubleValue() : null
                            )),

                    AnimationsEventInvocation.CODEC.listOf().optionalFieldOf("do", List.of())
                            .forGetter(ItemNumberDataReadEvent::doEvents)

            ).apply(instance, (componentId, comparator, key,
                               value, max, min,
                               doEvents) ->
                    new ItemNumberDataReadEvent(
                            componentId, comparator, key,
                            value.orElse(null), max.orElse(null), min.orElse(null),
                            doEvents
                    )
            )
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var caster = patch.getOriginal();
        if (this.isInvalid(caster.level(), AnimationEvent.Side.SERVER, "Item Number Data Read Event")) return;
        if (!(caster.level() instanceof ServerLevel)) return;

        if (this.applyMatch(caster)) {
            doEvents.forEach(event -> event.execute(accessor, patch));
        }
    }

    private boolean applyMatch(LivingEntity entity) {
        var componentType = BuiltInRegistries.DATA_COMPONENT_TYPE.get(componentId);
        if (componentType == null) {
            ExtendedDatapacks.LOGGER.warn("[Item Number Data Read Event] The requested data component was not found in the registry: {}", componentId);
            return false;
        }

        Object component = entity.getMainHandItem().get(componentType);
        if (component == null) {
            ExtendedDatapacks.LOGGER.warn("[Item Number Data Read Event] The result of the data retrieval operation is null: {}", componentId);
            return false;
        }

        Codec<?> codec = componentType.codec();
        if (codec == null) {
            ExtendedDatapacks.LOGGER.warn("[Item Number Data Read Event] The requested data component has no codec: {}", componentId);
            return false;
        }

        JsonElement json = encodeComponent(codec, component, entity.level().registryAccess());
        if (json == null) {
            return false;
        }

        JsonElement result = key.isEmpty() ? json : resolvePath(json, key);
        if (result == null || !result.isJsonPrimitive() || !result.getAsJsonPrimitive().isNumber()) {
            return false;
        }

        Number actual;
        try {
            actual = result.getAsJsonPrimitive().getAsNumber();
        } catch (Exception exception) {
            ExtendedDatapacks.LOGGER.warn("[Item Number Data Read Event] The value found at '{}' is not a valid number in component {}", key, componentId);
            return false;
        }


        if (value == null) {
            if (max == null) {
                ExtendedDatapacks.LOGGER.warn(
                        "[Item Number Data Read Event] Missing 'max' field for comparator {} in component {}",
                        numericComparator, componentId
                );
                return false;
            }
            if (min == null) {
                ExtendedDatapacks.LOGGER.warn(
                        "[Item Number Data Read Event] Missing 'min' field for comparator {} in component {}",
                        numericComparator, componentId
                );
                return false;
            }
        }

        Number upperBound = value == null ? max : value;
        return numericComparator.comparate(actual, upperBound, min);
    }

    @SuppressWarnings("unchecked")
    private static JsonElement encodeComponent(Codec<?> codec, Object component, HolderLookup.Provider registries) {
        RegistryOps<JsonElement> ops = registries.createSerializationContext(JsonOps.INSTANCE);
        Codec<Object> objectCodec = (Codec<Object>) codec;

        var result = objectCodec.encodeStart(ops, component);

        result.error().ifPresent(error ->
                ExtendedDatapacks.LOGGER.warn(
                        "[Item Number Data Read Event] Failed to encode data component: {}", error.message()
                )
        );

        return result.result().orElse(null);
    }

    private static JsonElement resolvePath(JsonElement root, String path) {
        JsonElement current = root;
        for (String key : path.split("\\.")) {
            if (current == null || !current.isJsonObject()) return null;
            current = current.getAsJsonObject().get(key);
        }
        return current;
    }
}
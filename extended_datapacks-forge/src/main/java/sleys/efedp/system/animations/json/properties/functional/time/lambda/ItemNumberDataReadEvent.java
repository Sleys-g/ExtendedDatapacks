package sleys.efedp.system.animations.json.properties.functional.time.lambda;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.*;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record ItemNumberDataReadEvent(NumericComparator numericComparator,
                                      String key, Number value, Number max, Number min,
                                      List<AnimationsEventInvocation> doEvents) implements IAnimationEventParams {

    private static final Pattern SELECTOR_PATTERN = Pattern.compile("^([^\\[]+)(?:\\[([^=]+)=([^\\]]+)\\])?$");

    public static final MapCodec<ItemNumberDataReadEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
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

            ).apply(instance, (comparator, key,
                               value, max, min,
                               doEvents) ->
                    new ItemNumberDataReadEvent(
                            comparator, key,
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
            ExtendedDatapacks.LOGGER.info("Aplicando!");
            doEvents.forEach(event -> event.execute(accessor, patch));
        }
    }

    private boolean applyMatch(LivingEntity entity) {
        CompoundTag tag = entity.getMainHandItem().getTag();
        if (tag == null) {
            ExtendedDatapacks.LOGGER.warn("[Item Number Data Read Event] The item has no NBT data to read from");
            return false;
        }

        JsonElement json = tagToJson(tag);
        JsonElement result = key.isEmpty() ? json : resolvePath(json, key);

        if (result == null || !result.isJsonPrimitive() || !result.getAsJsonPrimitive().isNumber()) {
            ExtendedDatapacks.LOGGER.warn("[Item Number Data Read Event] The value found at '{}' is not a valid number", key);
            return false;
        }

        Number actual;
        try {
            actual = result.getAsJsonPrimitive().getAsNumber();
        } catch (Exception exception) {
            ExtendedDatapacks.LOGGER.warn("[Item Number Data Read Event] The value found at '{}' is not a valid number", key);
            return false;
        }

        if (value == null) {
            if (max == null) {
                ExtendedDatapacks.LOGGER.warn(
                        "[Item Number Data Read Event] Missing 'max' field for comparator {}", numericComparator
                );
                return false;
            }
            if (min == null) {
                ExtendedDatapacks.LOGGER.warn(
                        "[Item Number Data Read Event] Missing 'min' field for comparator {}", numericComparator
                );
                return false;
            }
        }

        Number upperBound = value == null ? max : value;
        return numericComparator.comparate(actual, upperBound, min);
    }

    private static JsonElement tagToJson(Tag tag) {
        if (tag instanceof CompoundTag compound) {
            JsonObject object = new JsonObject();
            for (String childKey : compound.getAllKeys()) {
                object.add(childKey, tagToJson(compound.get(childKey)));
            }
            return object;
        }
        if (tag instanceof ListTag list) {
            JsonArray array = new JsonArray();
            for (Tag element : list) {
                array.add(tagToJson(element));
            }
            return array;
        }
        if (tag instanceof StringTag stringTag) {
            return new JsonPrimitive(stringTag.getAsString());
        }
        if (tag instanceof NumericTag numericTag) {
            return new JsonPrimitive(numericTag.getAsNumber());
        }
        return null;
    }

    private static JsonElement resolvePath(JsonElement root, String path) {
        JsonElement current = root;

        for (String segment : path.split("\\.")) {
            if (current == null) return null;

            Matcher matcher = SELECTOR_PATTERN.matcher(segment);
            if (!matcher.matches()) return null;


            String field = matcher.group(1);
            String selectorKey = matcher.group(2);
            String selectorValue = matcher.group(3);

            if (!current.isJsonObject()) return null;
            current = current.getAsJsonObject().get(field);
            if (current == null) return null;


            if (selectorKey == null) continue;
            if (!current.isJsonArray()) return null;
            JsonArray array = current.getAsJsonArray();
            JsonElement matched = null;

            for (JsonElement element : array) {
                if (!element.isJsonObject()) continue;
                JsonElement selector = element.getAsJsonObject().get(selectorKey);
                if (selector == null || !selector.isJsonPrimitive()) continue;

                if (selectorValue.equals(selector.getAsString())) {
                    matched = element;
                    break;
                }
            }

            current = matched;
        }

        return current;
    }
}
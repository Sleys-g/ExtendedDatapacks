package sleys.efedp.system.animations.json.properties.functional.time.lambda;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.animations.json.properties.functional.time.AnimationsEventInvocation;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public record ItemStringDataReadEvent(ResourceLocation componentId,
                                      String value,
                                      List<AnimationsEventInvocation> doEvents) implements IAnimationEventParams {

    public static final MapCodec<ItemStringDataReadEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("component_id").forGetter(ItemStringDataReadEvent::componentId),
                    Codec.STRING.fieldOf("value").forGetter(ItemStringDataReadEvent::value),
                    AnimationsEventInvocation.CODEC.listOf().optionalFieldOf("do", List.of()).forGetter(ItemStringDataReadEvent::doEvents)
            ).apply(instance, ItemStringDataReadEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var caster = patch.getOriginal();
        if (this.isInvalid(caster.level(), AnimationEvent.Side.SERVER, "Item String Data Read Event")) return;
        if (!(caster.level() instanceof ServerLevel)) return;

        if (this.applyMatch(caster, accessor)) {
            doEvents.forEach(events -> events.execute(accessor, patch));
        }
    }

    private <T extends StaticAnimation> boolean applyMatch(LivingEntity entity, AssetAccessor<T> accessor) {
        var componentType = BuiltInRegistries.DATA_COMPONENT_TYPE.get(componentId);
        if (componentType == null) {
            ExtendedDatapacks.LOGGER.warn("[Item String Data Read Event] The requested data component was not found in the registry: {}", componentId);
            return false;
        }

        var stack = entity.getMainHandItem();
        Object actual = stack.get(componentType);
        if (actual == null) {
            ExtendedDatapacks.LOGGER.warn(
                    "[Item String Data Read Event] The result of the data retrieval operation is null, the animation cannot proceed: {}", accessor.registryName()
            );
            return false;
        }


        Codec<?> codec = componentType.codec();
        if (codec != null) {
            JsonElement json = coerceToJson(value.trim());
            if (json != null) {
                var parsedValue = codec.parse(JsonOps.INSTANCE, json);
                if (parsedValue.result().isPresent()) {
                    return Objects.equals(actual, parsedValue.result().get());
                }
            }
        }

        return actual.toString().contains(value);
    }

    private static JsonElement coerceToJson(String raw) {
        if (raw.isEmpty()) return null;

        if (raw.equalsIgnoreCase("true") || raw.equalsIgnoreCase("false")) {
            return new JsonPrimitive(Boolean.parseBoolean(raw));
        }

        try {
            return new JsonPrimitive(new BigDecimal(raw));
        } catch (NumberFormatException ignored) {}

        char first = raw.charAt(0);
        if (first == '{' || first == '[') {
            try {
                return JsonParser.parseString(raw);
            } catch (JsonSyntaxException e) {
                return null;
            }
        }

        return new JsonPrimitive(raw);
    }
}

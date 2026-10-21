package sleys.efedp.neoforge.core.events;

import com.mojang.serialization.*;
import net.minecraft.world.entity.LivingEntity;
import sleys.efedp.neoforge.ExtendedDatapacks;
import sleys.efedp.neoforge.core.events.registry.EventTypeRegistry;
import sleys.efedp.neoforge.core.events.registry.IEventType;
import sleys.sl.library.annotations.Experimental;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.stream.Stream;


@Experimental(
        since = "2.5",
        note = "Enables recursive call functionality for events, primarily for reading data."
)
public record EventInvocation(IEventType type, IEventParams params) {

    public static final Codec<EventInvocation> CODEC = new MapCodec<EventInvocation>() {

        @Override
        public <O> Stream<O> keys(DynamicOps<O> ops) {
            return Stream.of(ops.createString("type"), ops.createString("params"));
        }

        @Override
        public <O> DataResult<EventInvocation> decode(DynamicOps<O> ops, MapLike<O> input) {
            O typeRaw = input.get("type");
            if (typeRaw == null) {
                return DataResult.error(() -> "Missing 'type' field in event invocation");
            }

            return EventTypeRegistry.CODEC.parse(ops, typeRaw).flatMap(type -> {
                O paramsRaw = input.get("params");
                DataResult<? extends IEventParams> paramsResult = type
                        .paramsCodec()
                        .codec()
                        .parse(ops, paramsRaw != null ? paramsRaw : ops.emptyMap());

                paramsResult.error().ifPresent(error ->
                        ExtendedDatapacks.LOGGER.error(
                                "Failed to decode nested event invocation, Type: {}: Error: {}",
                                type, error.message()
                        )
                );

                return paramsResult.map(params -> new EventInvocation(type, params));
            });
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public <O> RecordBuilder<O> encode(EventInvocation input, DynamicOps<O> ops, RecordBuilder<O> prefix) {
            prefix.add("type", EventTypeRegistry.CODEC.encodeStart(ops, input.type()));
            if (input.params() != null) {
                Codec<IEventParams> paramsCodec = (Codec) input.type().paramsCodec().codec();
                prefix.add("params", paramsCodec.encodeStart(ops, input.params()));
            }
            return prefix;
        }
    }.codec();

    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        type.runEvent(params, accessor, patch);
    }

    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntity entity) {
        var tryToPatch = EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
        if (tryToPatch == null) type.runEvent(params, accessor, entity);
        else this.execute(accessor, tryToPatch);
    }

    public void execute(LivingEntityPatch<?> patch) {
        type.runEvent(params, null, patch);
    }

    public void execute(LivingEntity entity) {
        var tryToPatch = EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
        if (tryToPatch == null) type.runEvent(params, null, entity);
        else this.execute(tryToPatch);
    }
}
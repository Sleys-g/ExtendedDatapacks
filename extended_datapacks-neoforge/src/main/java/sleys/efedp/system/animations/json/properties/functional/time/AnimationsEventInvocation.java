package sleys.efedp.system.animations.json.properties.functional.time;

import com.mojang.serialization.*;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.animations.json.properties.functional.time.lambda.IAnimationEventParams;
import sleys.sl.library.annotations.Experimental;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.stream.Stream;


@Experimental(
        since = "2.5",
        note = "Enables recursive call functionality for events, primarily for reading data."
)
public record AnimationsEventInvocation(IAnimationEventType type, IAnimationEventParams params) {

    public static final Codec<AnimationsEventInvocation> CODEC = new MapCodec<AnimationsEventInvocation>() {

        @Override
        public <O> Stream<O> keys(DynamicOps<O> ops) {
            return Stream.of(ops.createString("type"), ops.createString("params"));
        }

        @Override
        public <O> DataResult<AnimationsEventInvocation> decode(DynamicOps<O> ops, MapLike<O> input) {
            O typeRaw = input.get("type");
            if (typeRaw == null) {
                return DataResult.error(() -> "Missing 'type' field in event invocation");
            }

            return AnimationEventTypeRegistry.CODEC.parse(ops, typeRaw).flatMap(type -> {
                O paramsRaw = input.get("params");
                DataResult<? extends IAnimationEventParams> paramsResult = type
                        .paramsCodec()
                        .codec()
                        .parse(ops, paramsRaw != null ? paramsRaw : ops.emptyMap());

                paramsResult.error().ifPresent(error ->
                        ExtendedDatapacks.LOGGER.error(
                                "Failed to decode nested event invocation, Type: {}: Error: {}",
                                type, error.message()
                        )
                );

                return paramsResult.map(params -> new AnimationsEventInvocation(type, params));
            });
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public <O> RecordBuilder<O> encode(AnimationsEventInvocation input, DynamicOps<O> ops, RecordBuilder<O> prefix) {
            prefix.add("type", AnimationEventTypeRegistry.CODEC.encodeStart(ops, input.type()));
            if (input.params() != null) {
                Codec<IAnimationEventParams> paramsCodec = (Codec) input.type().paramsCodec().codec();
                prefix.add("params", paramsCodec.encodeStart(ops, input.params()));
            }
            return prefix;
        }
    }.codec();

    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        type.runEvent(params, accessor, patch);
    }
}
package sleys.efedp.neoforge.core.control.controlable;

import com.mojang.serialization.*;
import net.neoforged.bus.api.Event;
import sleys.efedp.neoforge.core.control.registry.ControlableEventTypeRegistry;
import sleys.efedp.neoforge.core.control.registry.IControlableEventType;

import java.util.stream.Stream;

public record ControlableEvent(IControlableEventType type,
                               IEventController<?> controller) {

    public static final Codec<ControlableEvent> CODEC = new MapCodec<ControlableEvent>() {

        @Override
        public <O> Stream<O> keys(DynamicOps<O> ops) {
            return Stream.of(
                    ops.createString("type"),
                    ops.createString("controller")
            );
        }

        @Override
        public <O> DataResult<ControlableEvent> decode(DynamicOps<O> ops, MapLike<O> input) {
            O typeRaw = input.get("type");
            if (typeRaw == null) return DataResult.error(() -> "Missing 'type' field in controlable skill event");

            O controllerRaw = input.get("controller");
            if (controllerRaw == null) return DataResult.error(() -> "Missing 'controller' field in controlable skill event");

            return ControlableEventTypeRegistry.CODEC
                    .parse(ops, typeRaw)
                    .flatMap(type -> {
                        Codec<? extends IEventController<? extends Event>> controllerCodec = type.controllerCodec().codec();
                        return decodeController(controllerCodec, ops, controllerRaw)
                                .map(controller -> new ControlableEvent(type, controller));
                    });
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public <O> RecordBuilder<O> encode(ControlableEvent input, DynamicOps<O> ops, RecordBuilder<O> prefix) {
            prefix.add("type", ControlableEventTypeRegistry.CODEC.encodeStart(ops, input.type()));

            Codec<IEventController> codec = (Codec) input.type().controllerCodec().codec();
            prefix.add("controller", codec.encodeStart(ops, input.controller()));
            return prefix;
        }

    }.codec();

    private static <O> DataResult<IEventController<? extends Event>> decodeController(
            Codec<? extends IEventController<? extends Event>> codec, DynamicOps<O> ops, O input) {
        return decodeControllerUnchecked(codec, ops, input);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <O> DataResult<IEventController<? extends Event>> decodeControllerUnchecked(
            Codec<? extends IEventController<? extends Event>> codec, DynamicOps<O> ops, O input) {
        return ((Codec) codec).parse(ops, input);
    }
}
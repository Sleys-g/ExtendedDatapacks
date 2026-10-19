package sleys.efedp.neoforge.core.control.controlable;

import com.mojang.serialization.*;
import net.neoforged.bus.api.Event;
import sleys.efedp.neoforge.core.control.ISkillEventController;
import sleys.efedp.neoforge.core.control.registry.ControlableSkillEventTypeRegistry;
import sleys.efedp.neoforge.core.control.registry.IControlableSkillEventType;

import java.util.stream.Stream;

public record ControlableSkillEvent(IControlableSkillEventType type,
                                    ISkillEventController<?> controller) {

    public static final Codec<ControlableSkillEvent> CODEC = new MapCodec<ControlableSkillEvent>() {

        @Override
        public <O> Stream<O> keys(DynamicOps<O> ops) {
            return Stream.of(
                    ops.createString("type"),
                    ops.createString("controller")
            );
        }

        @Override
        public <O> DataResult<ControlableSkillEvent> decode(DynamicOps<O> ops, MapLike<O> input) {
            O typeRaw = input.get("type");
            if (typeRaw == null) return DataResult.error(() -> "Missing 'type' field in controlable skill event");

            O controllerRaw = input.get("controller");
            if (controllerRaw == null) return DataResult.error(() -> "Missing 'controller' field in controlable skill event");

            return ControlableSkillEventTypeRegistry.CODEC
                    .parse(ops, typeRaw)
                    .flatMap(type -> {
                        Codec<? extends ISkillEventController<? extends Event>> controllerCodec = type.controllerCodec().codec();
                        return decodeController(controllerCodec, ops, controllerRaw)
                                .map(controller -> new ControlableSkillEvent(type, controller));
                    });
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public <O> RecordBuilder<O> encode(ControlableSkillEvent input, DynamicOps<O> ops, RecordBuilder<O> prefix) {
            prefix.add("type", ControlableSkillEventTypeRegistry.CODEC.encodeStart(ops, input.type()));

            Codec<ISkillEventController> codec = (Codec) input.type().controllerCodec().codec();
            prefix.add("controller", codec.encodeStart(ops, input.controller()));
            return prefix;
        }

    }.codec();

    private static <O> DataResult<ISkillEventController<? extends Event>> decodeController(
            Codec<? extends ISkillEventController<? extends Event>> codec, DynamicOps<O> ops, O input) {
        return decodeControllerUnchecked(codec, ops, input);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <O> DataResult<ISkillEventController<? extends Event>> decodeControllerUnchecked(
            Codec<? extends ISkillEventController<? extends Event>> codec, DynamicOps<O> ops, O input) {
        return ((Codec) codec).parse(ops, input);
    }
}
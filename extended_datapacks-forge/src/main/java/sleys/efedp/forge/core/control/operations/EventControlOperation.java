package sleys.efedp.forge.core.control.operations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraftforge.eventbus.api.Event;
import sleys.efedp.forge.core.control.IControlableEventParams;
import sleys.sl.library.annotations.Internal;
import yesman.epicfight.skill.SkillContainer;

import java.util.List;

@Internal
public record EventControlOperation<E extends Event>(List<IControlableEventParams<E>> operations,
                                                     List<ControlOperationsTriplet<E>> dataOperations) {

    public static <E extends Event> MapCodec<EventControlOperation<E>> codec(MapCodec<? extends IControlableEventParams<E>> paramsCodec) {
        Codec<IControlableEventParams<E>> codec = IControlableEventParams.paramsCodec(paramsCodec);

        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        codec.listOf()
                                .optionalFieldOf("control", List.of())
                                .forGetter(EventControlOperation::operations),

                        ControlOperationsTriplet.codec(codec).codec().listOf()
                                .optionalFieldOf("data_control", List.of())
                                .forGetter(EventControlOperation::dataOperations)
                ).apply(instance, EventControlOperation<E>::new)
        );
    }

    public void execute(E event, SkillContainer container) {
        operations.forEach(controlable -> controlable.applyEventParams(event, container));
        dataOperations.forEach(controlable -> controlable.execute(event, container));
    }
}
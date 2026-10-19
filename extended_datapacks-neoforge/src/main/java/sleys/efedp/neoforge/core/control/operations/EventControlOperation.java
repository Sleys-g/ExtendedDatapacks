package sleys.efedp.neoforge.core.control.operations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.bus.api.Event;
import sleys.efedp.neoforge.core.control.ISkillEventParams;
import sleys.efedp.neoforge.core.datapackets.ReadDataPacketsGroup;
import sleys.sl.library.annotations.Internal;
import yesman.epicfight.skill.SkillContainer;

import java.util.Optional;

@Internal
public record EventControlOperation<E extends Event>(ReadDataPacketsGroup readData,
                                                     Optional<ISkillEventParams<E>> direct,
                                                     Optional<ISkillEventParams<E>> then,
                                                     Optional<ISkillEventParams<E>> otherwise) {

    public static <E extends Event> MapCodec<EventControlOperation<E>> codec(MapCodec<? extends ISkillEventParams<E>> paramsCodec) {
        Codec<ISkillEventParams<E>> codec = ISkillEventParams.paramsCodec(paramsCodec);

        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        ReadDataPacketsGroup.CODEC
                                .fieldOf("read_data")
                                .forGetter(EventControlOperation::readData),

                        codec.optionalFieldOf("direct")
                                .forGetter(EventControlOperation::direct),

                        codec.optionalFieldOf("then")
                                .forGetter(EventControlOperation::then),

                        codec.optionalFieldOf("otherwise")
                                .forGetter(EventControlOperation::otherwise)
                ).apply(instance, EventControlOperation<E>::new)
        );
    }

    public void execute(E event, SkillContainer container) {
        var livingEntity = container.getExecutor().getOriginal();

        direct.ifPresent(params -> params.applyEventParams(event, container));

        boolean result = readData.evaluate(livingEntity);
        var branch = result ? then : otherwise;

        branch.ifPresent(params -> params.applyEventParams(event, container));
    }
}
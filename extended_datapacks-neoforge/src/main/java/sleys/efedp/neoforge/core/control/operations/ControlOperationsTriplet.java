package sleys.efedp.neoforge.core.control.operations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.bus.api.Event;
import sleys.efedp.neoforge.core.control.IControlableEventParams;
import sleys.efedp.neoforge.core.datapackets.ReadDataPacketsGroup;
import yesman.epicfight.skill.SkillContainer;

import java.util.Optional;

public record ControlOperationsTriplet<E extends Event>(ReadDataPacketsGroup readData,
                                                        Optional<IControlableEventParams<E>> then,
                                                        Optional<IControlableEventParams<E>> otherwise) {

    public static <E extends Event> MapCodec<ControlOperationsTriplet<E>> codec(Codec<IControlableEventParams<E>> codec) {
        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        ReadDataPacketsGroup.CODEC
                                .fieldOf("data_id")
                                .forGetter(ControlOperationsTriplet::readData),

                        codec.optionalFieldOf("then")
                                .forGetter(ControlOperationsTriplet::then),

                        codec.optionalFieldOf("otherwise")
                                .forGetter(ControlOperationsTriplet::otherwise)
                ).apply(instance, ControlOperationsTriplet<E>::new)
        );
    }

    public void execute(E event, SkillContainer container) {
        var livingEntity = container.getExecutor().getOriginal();

        if (then.isEmpty() && otherwise.isEmpty()) return;
        boolean result = readData.evaluate(livingEntity);
        var branch = result ? then : otherwise;

        branch.ifPresent(params -> params.applyEventParams(event, container));
    }
}

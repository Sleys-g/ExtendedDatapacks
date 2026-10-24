package sleys.efedp.forge.core.control.operations;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.forge.core.control.operations.InvocationOperationTriplet;
import sleys.efedp.forge.core.events.EventInvocation;
import sleys.sl.library.annotations.Internal;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import java.util.List;

@Internal
public record EventInvocationOperation(List<EventInvocation> invocations,
                                       List<InvocationOperationTriplet> dataInvocations) {

    public static final MapCodec<EventInvocationOperation> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            EventInvocation.CODEC.listOf()
                                    .optionalFieldOf("invocations", List.of())
                                    .forGetter(EventInvocationOperation::invocations),

                            InvocationOperationTriplet.CODEC
                                    .codec().listOf().optionalFieldOf("data_invocations", List.of())
                                    .forGetter(EventInvocationOperation::dataInvocations)
                    ).apply(instance, EventInvocationOperation::new)
            );

    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, PlayerPatch<?> patch) {
        invocations.forEach(invocation -> invocation.execute(accessor, patch));
        dataInvocations.forEach(invocationsTriplet -> invocationsTriplet.execute(accessor, patch));
    }
}
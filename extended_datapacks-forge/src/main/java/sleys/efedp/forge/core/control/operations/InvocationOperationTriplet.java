package sleys.efedp.forge.core.control.operations;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.forge.core.datapackets.ReadDataPacketsGroup;
import sleys.efedp.forge.core.events.EventInvocation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import java.util.List;

public record InvocationOperationTriplet(ReadDataPacketsGroup readData,
                                         List<EventInvocation> then,
                                         List<EventInvocation> otherwise) {

    public static final MapCodec<InvocationOperationTriplet> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            ReadDataPacketsGroup.CODEC
                                    .fieldOf("data_id")
                                    .forGetter(InvocationOperationTriplet::readData),

                            EventInvocation.CODEC.listOf()
                                    .optionalFieldOf("then", List.of())
                                    .forGetter(InvocationOperationTriplet::then),

                            EventInvocation.CODEC.listOf()
                                    .optionalFieldOf("otherwise", List.of())
                                    .forGetter(InvocationOperationTriplet::otherwise)
                    ).apply(instance, InvocationOperationTriplet::new)
            );

    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, PlayerPatch<?> patch) {
        var livingEntity = patch.getOriginal();

        if (then.isEmpty() && otherwise.isEmpty()) return;
        boolean result = readData.evaluate(livingEntity);
        var branch = result ? then : otherwise;

        branch.forEach(invocation -> invocation.execute(accessor, patch));
    }
}

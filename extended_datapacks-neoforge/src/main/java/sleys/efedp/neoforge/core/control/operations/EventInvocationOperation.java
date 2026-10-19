package sleys.efedp.neoforge.core.control.operations;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.neoforge.core.datapackets.ReadDataPacketsGroup;
import sleys.efedp.neoforge.system.animations.properties.time.AnimationsEventInvocation;
import sleys.sl.library.annotations.Internal;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import java.util.List;

@Internal
public record EventInvocationOperation(ReadDataPacketsGroup readData,
                                       List<AnimationsEventInvocation> direct,
                                       List<AnimationsEventInvocation> then,
                                       List<AnimationsEventInvocation> otherwise) {

    public static final MapCodec<EventInvocationOperation> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            ReadDataPacketsGroup.CODEC
                                    .fieldOf("read_data")
                                    .forGetter(EventInvocationOperation::readData),

                            AnimationsEventInvocation.CODEC.listOf()
                                    .optionalFieldOf("direct", List.of())
                                    .forGetter(EventInvocationOperation::direct),

                            AnimationsEventInvocation.CODEC.listOf()
                                    .optionalFieldOf("then", List.of())
                                    .forGetter(EventInvocationOperation::then),

                            AnimationsEventInvocation.CODEC.listOf()
                                    .optionalFieldOf("otherwise", List.of())
                                    .forGetter(EventInvocationOperation::otherwise)
                    ).apply(instance, EventInvocationOperation::new)
            );

    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, PlayerPatch<?> patch) {
        var livingEntity = patch.getOriginal();

        direct.forEach(invocation -> invocation.execute(accessor, patch));

        boolean result = readData.evaluate(livingEntity);
        var branch = result ? then : otherwise;

        branch.forEach(invocation -> invocation.execute(accessor, patch));
    }
}
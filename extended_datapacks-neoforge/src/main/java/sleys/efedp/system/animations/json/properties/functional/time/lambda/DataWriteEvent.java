package sleys.efedp.system.animations.json.properties.functional.time.lambda;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.animations.json.properties.functional.datapackets.write.IDataWriter;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.List;

public record DataWriteEvent(List<IDataWriter> writers) implements IAnimationEventParams {

    public static final MapCodec<DataWriteEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    IDataWriter.CODEC.codec().listOf().optionalFieldOf("write_data", List.of()).forGetter(DataWriteEvent::writers)
            ).apply(instance, DataWriteEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingEntity = patch.getOriginal();
        var level = livingEntity.level();
        if (this.isInvalid(level, AnimationEvent.Side.BOTH, "Data Write Event")) return;
        writers.forEach(iDataWriter -> iDataWriter.writeData(livingEntity));
    }
}

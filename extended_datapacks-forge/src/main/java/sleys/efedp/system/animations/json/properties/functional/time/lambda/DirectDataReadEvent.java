package sleys.efedp.system.animations.json.properties.functional.time.lambda;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.animations.json.properties.functional.datapackets.ReadDataPacketsGroup;
import sleys.efedp.system.animations.json.properties.functional.time.AnimationsEventInvocation;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.List;

public record DirectDataReadEvent(ReadDataPacketsGroup readData,
                                  List<AnimationsEventInvocation> doEvents) implements IAnimationEventParams {

    public static final MapCodec<DirectDataReadEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ReadDataPacketsGroup.CODEC.fieldOf("read_data").forGetter(DirectDataReadEvent::readData),
                    AnimationsEventInvocation.CODEC.listOf().optionalFieldOf("do", List.of()).forGetter(DirectDataReadEvent::doEvents)
            ).apply(instance, DirectDataReadEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingEntity = patch.getOriginal();
        var level = livingEntity.level();
        if (this.isInvalid(level, AnimationEvent.Side.BOTH, "Direct Data Read Event")) return;

        boolean result = readData.evaluate(livingEntity);
        if (result) doEvents.forEach(events -> events.execute(accessor, patch));
    }
}
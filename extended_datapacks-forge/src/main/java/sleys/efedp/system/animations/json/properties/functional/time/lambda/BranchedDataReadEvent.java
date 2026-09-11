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

public record BranchedDataReadEvent(ReadDataPacketsGroup readData,
                                    List<AnimationsEventInvocation> then,
                                    List<AnimationsEventInvocation> otherwise) implements IAnimationEventParams {

    public static final MapCodec<BranchedDataReadEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ReadDataPacketsGroup.CODEC.fieldOf("read_data").forGetter(BranchedDataReadEvent::readData),
                    AnimationsEventInvocation.CODEC.listOf().optionalFieldOf("then", List.of()).forGetter(BranchedDataReadEvent::then),
                    AnimationsEventInvocation.CODEC.listOf().optionalFieldOf("otherwise", List.of()).forGetter(BranchedDataReadEvent::otherwise)
            ).apply(instance, BranchedDataReadEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingEntity = patch.getOriginal();
        var level = livingEntity.level();
        if (this.isInvalid(level, AnimationEvent.Side.BOTH, "Branched Data Read Event")) return;

        boolean result = readData.evaluate(livingEntity);
        var branch = result ? then : otherwise;
        branch.forEach(invocation -> invocation.execute(accessor, patch));
    }
}
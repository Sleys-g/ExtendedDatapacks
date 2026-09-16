package sleys.efedp.neoforge.system.animations.json.properties.time.events;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.neoforge.system.animations.json.properties.time.AnimationsEventInvocation;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public record RandomDiceEvent(float chance, List<AnimationsEventInvocation> doEvents) implements IAnimationEventParams {

    public static final MapCodec<RandomDiceEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("chance").forGetter(RandomDiceEvent::chance),
                    AnimationsEventInvocation.CODEC.listOf().optionalFieldOf("do", List.of()).forGetter(RandomDiceEvent::doEvents)
            ).apply(instance, RandomDiceEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingEntity = patch.getOriginal();
        var level = livingEntity.level();
        if (this.isInvalid(level, AnimationEvent.Side.BOTH, "Random Dice Event")) return;

        if (ThreadLocalRandom.current().nextFloat(100.0f) < chance) {
            doEvents.forEach(events -> events.execute(accessor, patch));
        }
    }
}

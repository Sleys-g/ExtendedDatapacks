package sleys.efedp.system.animations.json.properties.functional.playback.lambda;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public record ConstantAnimationSpeed(Float constant) implements IPlaySpeedModifierParams {

    public static final MapCodec<ConstantAnimationSpeed> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("constant").forGetter(ConstantAnimationSpeed::constant)
            ).apply(instance, ConstantAnimationSpeed::new)
    );

    @Override
    public <T extends DynamicAnimation> float modify(T self, LivingEntityPatch<?> patch,
                                                    float speed, float prevElapsedTime, float elapsedTime) {
        return constant == null ? 1F : constant;
    }
}

package sleys.efedp.system.animations.json.properties.functional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import yesman.epicfight.api.animation.property.AnimationEvent;

import java.util.Locale;

public final class AnimationEventSideCodec {

    public static final Codec<AnimationEvent.Side> CODEC = Codec.STRING.flatXmap(
            name -> {
                try {
                    return DataResult.success(AnimationEvent.Side.valueOf(name.toUpperCase(Locale.ROOT)));
                } catch (IllegalArgumentException e) {
                    return DataResult.error(() -> "Unknown Side: " + name);
                }
            },
            side -> DataResult.success(side.name())
    );
}

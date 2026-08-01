package sleys.efedp.system.animations.json.properties.coords;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import sleys.efedp.system.animations.json.properties.functional.playback.lambda.IPlaySpeedModifierParams;

public sealed interface ICoordFunction<T> permits DestLocationProviderFn, MoveCoordGetterFn, MoveCoordSetterFn, YRotProviderFn {
    T value();
}

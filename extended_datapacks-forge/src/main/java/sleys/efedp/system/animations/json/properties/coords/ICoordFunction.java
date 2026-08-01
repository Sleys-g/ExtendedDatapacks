package sleys.efedp.system.animations.json.properties.coords;

public sealed interface ICoordFunction<T> permits DestLocationProviderFn, MoveCoordGetterFn, MoveCoordSetterFn, YRotProviderFn {
    T value();
}

package sleys.efedp.system.animations.json.properties.coords;

import com.mojang.serialization.Codec;
import sleys.sl.library.util.data.codec.EnumCodecs;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;

public enum MoveCoordGetterFn {
    MODEL_COORD("model_coord", MoveCoordFunctions.MODEL_COORD),
    WORLD_COORD("world_coord", MoveCoordFunctions.WORLD_COORD),
    ATTACHED("attached", MoveCoordFunctions.ATTACHED);

    public final String id;
    public final MoveCoordFunctions.MoveCoordGetter getter;

    MoveCoordGetterFn(String id, MoveCoordFunctions.MoveCoordGetter getter) {
        this.id = id;
        this.getter = getter;
    }

    public static final Codec<MoveCoordGetterFn> CODEC = EnumCodecs.byId(values(), c -> c.id);
}

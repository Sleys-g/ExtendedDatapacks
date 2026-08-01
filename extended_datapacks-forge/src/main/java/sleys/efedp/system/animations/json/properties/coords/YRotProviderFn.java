package sleys.efedp.system.animations.json.properties.coords;

import com.mojang.serialization.Codec;
import sleys.sl.library.util.data.codec.EnumCodecs;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;

public enum YRotProviderFn implements ICoordFunction<AnimationProperty.YRotProvider> {
    LOOK_DEST("look_dest", MoveCoordFunctions.LOOK_DEST),
    MOB_ATTACK_TARGET_LOOK("mob_attack_target_look", MoveCoordFunctions.MOB_ATTACK_TARGET_LOOK);

    public final String id;
    public final AnimationProperty.YRotProvider yRotProvider;

    YRotProviderFn(String id, AnimationProperty.YRotProvider yRotProvider) {
        this.id = id;
        this.yRotProvider = yRotProvider;
    }

    public static final Codec<YRotProviderFn> CODEC = EnumCodecs.byId(values(), c -> c.id);

    @Override
    public AnimationProperty.YRotProvider value() {
        return this.yRotProvider;
    }
}

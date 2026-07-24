package sleys.efedp.system.animations.json.properties.functional.time.lambda;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec3;
import sleys.sl.library.client.events.CameraControllerEvent;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.Locale;

public record CameraTransitionEvent(
        String id,
        Vec3 offset,
        Float startYaw,
        Float targetYaw,
        CameraControllerEvent.CameraInterpolation inInterpolation,
        Long inTransitionTime,
        Long holdTime,
        CameraControllerEvent.CameraInterpolation outInterpolation,
        Long outTransitionTime,
        Boolean isAbsolute
) implements IAnimationEventParams {

    public static final Codec<CameraControllerEvent.CameraInterpolation> INTERPOLATION_CODEC = Codec.STRING.xmap(
            s -> CameraControllerEvent.CameraInterpolation.valueOf(s.toUpperCase(Locale.ROOT)),
            CameraControllerEvent.CameraInterpolation::name
    );

    public static final MapCodec<CameraTransitionEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.STRING.fieldOf("id").forGetter(CameraTransitionEvent::id),
                    Vec3.CODEC.fieldOf("offset").forGetter(CameraTransitionEvent::offset),
                    Codec.FLOAT.fieldOf("start_yaw").forGetter(CameraTransitionEvent::startYaw),
                    Codec.FLOAT.fieldOf("target_yaw").forGetter(CameraTransitionEvent::targetYaw),
                    INTERPOLATION_CODEC.fieldOf("in_interpolation").forGetter(CameraTransitionEvent::inInterpolation),
                    Codec.LONG.fieldOf("in_transition_time").forGetter(CameraTransitionEvent::inTransitionTime),
                    Codec.LONG.fieldOf("hold_time").forGetter(CameraTransitionEvent::holdTime),
                    INTERPOLATION_CODEC.fieldOf("out_interpolation").forGetter(CameraTransitionEvent::outInterpolation),
                    Codec.LONG.fieldOf("out_transition_time").forGetter(CameraTransitionEvent::outTransitionTime),
                    Codec.BOOL.fieldOf("is_absolute").forGetter(CameraTransitionEvent::isAbsolute)
            ).apply(instance, CameraTransitionEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        if (this.isInvalid(patch.getOriginal().level(), AnimationEvent.Side.CLIENT, "Camera Transition")) {
            return;
        }

        CameraControllerEvent.startTransition(
                id, offset, startYaw, targetYaw,
                inInterpolation, inTransitionTime, holdTime,
                outInterpolation, outTransitionTime, isAbsolute
        );
    }
}

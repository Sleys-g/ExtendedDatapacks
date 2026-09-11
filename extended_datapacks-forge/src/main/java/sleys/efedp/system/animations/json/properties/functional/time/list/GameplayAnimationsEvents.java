package sleys.efedp.system.animations.json.properties.functional.time.list;

import com.mojang.serialization.MapCodec;
import sleys.efedp.system.animations.json.properties.functional.time.IAnimationEventType;
import sleys.efedp.system.animations.json.properties.functional.time.lambda.*;

public enum GameplayAnimationsEvents implements IAnimationEventType {
    PLAY_ANIMATION(PlayAnimationEvent.CODEC),
    COMMAND_PAYLOAD(CommandAnimationEvent.CODEC),
    INVULNERABILITY(InvulnerabilityAnimationEvent.CODEC),
    LASER_TARGET_DAMAGE(LaserTargetDamageEvent.CODEC),
    LASER_VERTICAL_TARGET_DAMAGE(LaserVerticalTargetDamageEvent.CODEC),
    LASER_JOINT_TARGET_DAMAGE(LaserJointTargetDamageEvent.CODEC),
    LASER_VERTICAL_DAMAGE(LaserVerticalDamageEvent.CODEC),
    LASER_JOINT_VERTICAL_DAMAGE(LaserJointVerticalDamageEvent.CODEC),
    LASER_LINE_WORLD_DAMAGE(LaserLineWorldDamageEvent.CODEC),
    LASER_FLOOR_WORLD_DAMAGE(LaserFloorWorldDamageEvent.CODEC)

    ;private final MapCodec<? extends IAnimationEventParams> codec;

    GameplayAnimationsEvents(MapCodec<? extends IAnimationEventParams> codec) {
        this.codec = codec;
    }

    public MapCodec<? extends IAnimationEventParams> paramsCodec() {
        return codec;
    }
}

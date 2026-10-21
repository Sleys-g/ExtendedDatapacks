package sleys.efedp.neoforge.core.events.types;

import com.mojang.serialization.MapCodec;
import sleys.efedp.neoforge.core.events.IEventParams;
import sleys.efedp.neoforge.core.events.list.gameplay.*;
import sleys.efedp.neoforge.core.events.registry.IEventType;

public enum GameplayEvents implements IEventType {
    PLAY_ANIMATION(PlayAnimationEvent.CODEC),
    COMMAND_PAYLOAD(CommandEvent.CODEC),
    INVULNERABILITY(InvulnerabilityEvent.CODEC),
    LASER_TARGET_DAMAGE(LaserTargetDamageEvent.CODEC),
    LASER_VERTICAL_TARGET_DAMAGE(LaserVerticalTargetDamageEvent.CODEC),
    LASER_VERTICAL_IMPACT_DAMAGE(LaserVerticalImpactDamageEvent.CODEC),
    LASER_JOINT_TARGET_DAMAGE(LaserJointTargetDamageEvent.CODEC),
    LASER_LINE_WORLD_DAMAGE(LaserLineWorldDamageEvent.CODEC),
    LASER_FLOOR_WORLD_DAMAGE(LaserFloorWorldDamageEvent.CODEC)

    ;private final MapCodec<? extends IEventParams> codec;

    GameplayEvents(MapCodec<? extends IEventParams> codec) {
        this.codec = codec;
    }

    @Override
    public MapCodec<? extends IEventParams> paramsCodec() {
        return codec;
    }
}

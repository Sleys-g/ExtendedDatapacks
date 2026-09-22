package sleys.efedp.forge.system.animations.json.properties.time.types;

import com.mojang.serialization.MapCodec;
import sleys.efedp.forge.system.animations.json.properties.time.registry.IAnimationEventType;
import sleys.efedp.forge.system.animations.json.properties.time.events.*;

public enum TaskableGameplayAnimationsEvents implements IAnimationEventType {
    TASKABLE_LASER_LINE_WORLD_DAMAGE(TaskableLaserLineWorldDamageEvent.CODEC),
    TASKABLE_LASER_FLOOR_WORLD_DAMAGE(TaskableLaserFloorWorldDamageEvent.CODEC),
    TASKABLE_LASER_SHAPE_WORLD_DAMAGE(TaskableLaserShapeWorldDamageEvent.CODEC),
    TASKABLE_ENTITY_CLONE(TaskableEntityCloneEvent.CODEC),
    TASKABLE_ENTITY_ANIMATED(TaskableEntityAnimatedEvent.CODEC)

    ;private final MapCodec<? extends IAnimationEventParams> codec;

    TaskableGameplayAnimationsEvents(MapCodec<? extends IAnimationEventParams> codec) {
        this.codec = codec;
    }

    @Override
    public MapCodec<? extends IAnimationEventParams> paramsCodec() {
        return codec;
    }
}

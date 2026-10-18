package sleys.efedp.forge.core.events.types;

import com.mojang.serialization.MapCodec;
import sleys.efedp.forge.core.events.IAnimationEventParams;
import sleys.efedp.forge.core.events.list.taskable.*;
import sleys.efedp.forge.core.events.registry.IAnimationEventType;

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

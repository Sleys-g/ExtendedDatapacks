package sleys.efedp.neoforge.core.events.types;

import com.mojang.serialization.MapCodec;
import sleys.efedp.neoforge.core.events.IEventParams;
import sleys.efedp.neoforge.core.events.list.taskable.*;
import sleys.efedp.neoforge.core.events.registry.IEventType;

public enum TaskableGameplayEvents implements IEventType {
    TASKABLE_LASER_LINE_WORLD_DAMAGE(TaskableLaserLineWorldDamageEvent.CODEC),
    TASKABLE_LASER_FLOOR_WORLD_DAMAGE(TaskableLaserFloorWorldDamageEvent.CODEC),
    TASKABLE_LASER_SHAPE_WORLD_DAMAGE(TaskableLaserShapeWorldDamageEvent.CODEC),
    TASKABLE_ENTITY_CLONE(TaskableEntityCloneEvent.CODEC),
    TASKABLE_ENTITY_ANIMATED(TaskableEntityEvent.CODEC)

    ;private final MapCodec<? extends IEventParams> codec;

    TaskableGameplayEvents(MapCodec<? extends IEventParams> codec) {
        this.codec = codec;
    }

    @Override
    public MapCodec<? extends IEventParams> paramsCodec() {
        return codec;
    }
}

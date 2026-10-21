package sleys.efedp.neoforge.core.events.types;

import com.mojang.serialization.MapCodec;
import sleys.efedp.neoforge.core.events.IEventParams;
import sleys.efedp.neoforge.core.events.list.particle.*;
import sleys.efedp.neoforge.core.events.registry.IEventType;

public enum ParticleEvents implements IEventType {
    JOINT_PARTICLES(JointParticleEvent.CODEC),
    WEAPON_SHAPE_PARTICLES(WeaponShapeParticleEvent.CODEC),
    SMALL_EXPLOSION(SmallExplosionEvent.CODEC),
    FLOR_PARTICLES(FloorParticleEvent.CODEC),
    RADIAL_FLOOR_EXPAND(RadialFloorExpandEvent.CODEC),
    COLUMN(ColumnEvent.CODEC),
    COLUMN_LINE(ColumnLineEvent.CODEC),
    DIRECTIONAL_BURST(DirectionalBurstEvent.CODEC),
    CIRCLE_PARTICLES(CircleParticleEvent.CODEC),
    SHAPE_PARTICLES(ShapeParticleEvent.CODEC)
    ;private final MapCodec<? extends IEventParams> codec;

    ParticleEvents(MapCodec<? extends IEventParams> codec) {
        this.codec = codec;
    }

    @Override
    public MapCodec<? extends IEventParams> paramsCodec() {
        return codec;
    }
}

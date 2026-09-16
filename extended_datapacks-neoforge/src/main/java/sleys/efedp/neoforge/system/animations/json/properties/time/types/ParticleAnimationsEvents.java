package sleys.efedp.neoforge.system.animations.json.properties.time.types;

import com.mojang.serialization.MapCodec;
import sleys.efedp.neoforge.system.animations.json.properties.time.events.*;
import sleys.efedp.neoforge.system.animations.json.properties.time.registry.IAnimationEventType;

public enum ParticleAnimationsEvents implements IAnimationEventType {
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
    ;private final MapCodec<? extends IAnimationEventParams> codec;

    ParticleAnimationsEvents(MapCodec<? extends IAnimationEventParams> codec) {
        this.codec = codec;
    }

    @Override
    public MapCodec<? extends IAnimationEventParams> paramsCodec() {
        return codec;
    }
}

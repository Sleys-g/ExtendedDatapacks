package sleys.efedp.system.animations.json.properties.functional.time;

import com.mojang.serialization.MapCodec;
import sleys.efedp.system.animations.json.properties.functional.time.lambda.*;

public enum AnimationsEventsList implements IAnimationEventType {
    WHITE_AFTERIMAGE(WhiteAfterImageEvent.CODEC),
    ENTITY_AFTERIMAGE(EntityAfterImageEvent.CODEC),
    FRACTURE_GROUND(FractureGroundEvent.CODEC),
    PLAY_ANIMATION(PlayAnimationEvent.CODEC),
    COMMAND_PAYLOAD(CommandAnimationEvent.CODEC),
    INVULNERABILITY(InvulnerabilityAnimationEvent.CODEC),
    THUNDER(ThunderAnimationEvent.CODEC),
    CAMERA_TRANSITION(CameraTransitionEvent.CODEC),
    JOINT_PARTICLES(JointParticleEvent.CODEC),

    /// Nuevos 2.3.1
    TRANSLATE(TranslateEvent.CODEC),
    TELEPORT(TeleportEvent.CODEC),
    WEAPON_SHAPE_PARTICLES(WeaponShapeParticleEvent.CODEC),
    SHOOT_PROJECTILE(JointShootProjectileEvent.CODEC), /// BETA
    SUMMON_OWNED_ENTITY(SummonEntityOnTargetEvent.CODEC), /// BETA

    /// Particle Events
    SMALL_EXPLOSION(SmallExplosionEvent.CODEC),
    FLOR_PARTICLES(FloorParticleEvent.CODEC),
    RADIAL_FLOOR_EXPAND(RadialFloorExpandEvent.CODEC),
    COLUMN(ColumnEvent.CODEC),
    COLUMN_LINE(ColumnLineEvent.CODEC),
    DIRECTIONAL_BURST(DirectionalBurstEvent.CODEC),
    CIRCLE_PARTICLES(CircleParticleEvent.CODEC),
    SHAPE_PARTICLES(ShapeParticleEvent.CODEC),

    /// Data
    SYNCED_DATA_WRITE(SyncedDataWriteEvent.CODEC), /// Only Players
    SYNCED_DIRECT_DATA_READ(SyncedDirectDataReadEvent.CODEC), /// Only Players
    SYNCED_BRANCHED_DATA_READ(SyncedBranchedDataReadEvent.CODEC), /// Only Players
    DATA_WRITE(DataWriteEvent.CODEC),
    DIRECT_DATA_READ(DirectDataReadEvent.CODEC),
    BRANCHED_DATA_READ(BranchedDataReadEvent.CODEC)
    ;private final MapCodec<? extends IAnimationEventParams> codec;

    AnimationsEventsList(MapCodec<? extends IAnimationEventParams> codec) {
        this.codec = codec;
    }

    public MapCodec<? extends IAnimationEventParams> paramsCodec() {
        return codec;
    }
}
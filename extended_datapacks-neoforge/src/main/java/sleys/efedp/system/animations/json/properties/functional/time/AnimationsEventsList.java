package sleys.efedp.system.animations.json.properties.functional.time;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import sleys.efedp.system.animations.json.properties.functional.time.lambda.*;
import sleys.sl.library.util.data.codec.EnumCodecs;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.Locale;

public enum AnimationsEventsList {
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
    SHAPE_PARTICLES(ShapeParticleEvent.CODEC)
    ;private final MapCodec<? extends IAnimationEventParams> codec;

    AnimationsEventsList(MapCodec<? extends IAnimationEventParams> codec) {
        this.codec = codec;
    }

    public MapCodec<? extends IAnimationEventParams> paramsCodec() {
        return codec;
    }

    public <T extends StaticAnimation> void runEvent(
            IAnimationEventParams event,
            AssetAccessor<T> accessor,
            LivingEntityPatch<?> livingEntityPatch) {

        event.execute(accessor, livingEntityPatch);
    }

    public static final Codec<AnimationsEventsList> CODEC = EnumCodecs.byId(values(),
            e -> e.name().toUpperCase(Locale.ROOT)
    );
}
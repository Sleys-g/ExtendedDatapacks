package sleys.efedp.system.animations.json.properties.phase;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.InteractionHand;
import yesman.epicfight.api.animation.types.AttackAnimation;

import java.util.Arrays;

public record AnimationPhase(float anticipation, float pre_delay, float contact, float recovery,
                             InteractionHand hand, AttackPhaseProperties properties, ArmatureJointCollider... jointColliders) {

    public static final Codec<InteractionHand> HAND_CODEC = Codec.STRING.xmap(
            s -> s.equalsIgnoreCase("off_hand") ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND,
            h -> h == InteractionHand.OFF_HAND ? "off_hand" : "main_hand"
    );

    public static final MapCodec<AnimationPhase> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("antic").forGetter(AnimationPhase::anticipation),

                    Codec.FLOAT.fieldOf("pre_delay").forGetter(AnimationPhase::pre_delay),

                    Codec.FLOAT.fieldOf("contact").forGetter(AnimationPhase::contact),

                    Codec.FLOAT.fieldOf("recovery").forGetter(AnimationPhase::recovery),

                    HAND_CODEC.fieldOf("hand").forGetter(AnimationPhase::hand),
                    AttackPhaseProperties.CODEC.codec()
                            .optionalFieldOf("properties", AttackPhaseProperties.EMPTY)
                            .forGetter(AnimationPhase::properties),

                    ArmatureJointCollider.CODEC.codec()
                            .listOf()
                            .fieldOf("colliders").forGetter(p -> Arrays.asList(p.jointColliders()))

            ).apply(instance, (antic, preDelay, contact, recovery, hand, properties, colliders) ->
                    new AnimationPhase(antic, preDelay, contact, recovery, hand, properties, colliders.toArray(ArmatureJointCollider[]::new))
            )
    );

    public AttackAnimation.Phase parseToEpicFightPhases(ArmatureType armatureType) {
        AttackAnimation.JointColliderPair[] pairs = Arrays.stream(jointColliders)
                .map(c -> c.resolve(armatureType))
                .toArray(AttackAnimation.JointColliderPair[]::new);

        AttackAnimation.Phase phase = new AttackAnimation.Phase(0F, anticipation, pre_delay, contact, recovery, recovery, hand, pairs);
        properties.applyTo(phase);
        return phase;
    }
}

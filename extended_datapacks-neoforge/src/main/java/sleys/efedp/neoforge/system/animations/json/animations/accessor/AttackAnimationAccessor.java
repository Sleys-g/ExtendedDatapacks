package sleys.efedp.neoforge.system.animations.json.animations.accessor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.neoforge.system.animations.json.animations.registry.IAnimationAccessorType;
import sleys.efedp.neoforge.system.animations.json.animations.types.CombatAnimationAccessors;
import sleys.efedp.neoforge.system.animations.json.properties.armature.registry.ArmatureTypeRegistry;
import sleys.efedp.neoforge.system.animations.json.properties.armature.registry.IArmatureType;
import sleys.efedp.neoforge.system.animations.json.properties.phase.AnimationPhase;
import sleys.efedp.neoforge.system.animations.json.properties.IAnimationProperties;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.AttackAnimation;

import java.util.Arrays;

public record AttackAnimationAccessor(float transitionTime, String animationPath,
                                      IArmatureType armatureType,
                                      AnimationPhase... phases
) implements IAnimationAccessor<AttackAnimation> {

    public static final MapCodec<AttackAnimationAccessor> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("transition_time").forGetter(AttackAnimationAccessor::transitionTime),
                    Codec.STRING.fieldOf("animation").forGetter(AttackAnimationAccessor::animationPath),
                    ArmatureTypeRegistry.CODEC.fieldOf("armature").forGetter(AttackAnimationAccessor::armatureType),
                    AnimationPhase.CODEC.codec().listOf().fieldOf("phases").forGetter(r -> Arrays.asList(r.phases()))
            ).apply(instance, (transition, path, armature, phases) ->
                    new AttackAnimationAccessor(transition, path, armature, phases.toArray(AnimationPhase[]::new))
            )
    );

    @Override
    public IAnimationAccessorType accessorType() {
        return CombatAnimationAccessors.ATTACK;
    }

    @Override
    public AnimationManager.AnimationAccessor<AttackAnimation> register(AnimationManager.AnimationBuilder builder,
                                                                        IAnimationProperties<AttackAnimation> property) {
        return builder.nextAccessor(animationPath, (accessor) -> {
            var animation = new AttackAnimation(
                    transitionTime,
                    accessor,
                    armatureType.armature(),
                    Arrays.stream(phases)
                            .map(animationPhase -> animationPhase.parseToEpicFightPhases(armatureType))
                            .toArray(AttackAnimation.Phase[]::new)
            );
            property.applyTo(animation);
            this.isSuccessful(accessor);
            return animation;
        });
    }
}

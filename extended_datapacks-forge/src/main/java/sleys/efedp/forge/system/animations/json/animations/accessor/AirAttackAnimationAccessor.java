package sleys.efedp.forge.system.animations.json.animations.accessor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.forge.system.animations.json.animations.registry.IAnimationAccessorType;
import sleys.efedp.forge.system.animations.json.animations.types.CombatAnimationAccessors;
import sleys.efedp.forge.system.animations.json.properties.phase.AnimationPhase;
import sleys.efedp.forge.system.animations.json.properties.IAnimationProperties;
import sleys.efedp.forge.system.animations.json.properties.armature.registry.ArmatureTypeRegistry;
import sleys.efedp.forge.system.animations.json.properties.armature.registry.IArmatureType;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.AirSlashAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation;

import java.util.Arrays;

public record AirAttackAnimationAccessor(float transitionTime, String animationPath,
                                         IArmatureType armatureType,
                                         AnimationPhase... phases
) implements IAnimationAccessor<AirSlashAnimation> {

    public static final MapCodec<AirAttackAnimationAccessor> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("transition_time").forGetter(AirAttackAnimationAccessor::transitionTime),
                    Codec.STRING.fieldOf("animation").forGetter(AirAttackAnimationAccessor::animationPath),
                    ArmatureTypeRegistry.CODEC.fieldOf("armature").forGetter(AirAttackAnimationAccessor::armatureType),
                    AnimationPhase.CODEC.codec().listOf().fieldOf("phases").forGetter(r -> Arrays.asList(r.phases()))
            ).apply(instance, (transition, path, armature, phases) ->
                    new AirAttackAnimationAccessor(transition, path, armature, phases.toArray(AnimationPhase[]::new))
            )
    );

    @Override
    public IAnimationAccessorType accessorType() {
        return CombatAnimationAccessors.AIR_ATTACK;
    }

    @Override
    public AnimationManager.AnimationAccessor<AirSlashAnimation> register(AnimationManager.AnimationBuilder builder,
                                                                          IAnimationProperties<AirSlashAnimation> property) {
        return builder.nextAccessor(animationPath, (accessor) -> {
            var animation = new AirSlashAnimation(
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

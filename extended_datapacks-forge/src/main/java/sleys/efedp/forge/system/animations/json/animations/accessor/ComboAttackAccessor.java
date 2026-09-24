package sleys.efedp.forge.system.animations.json.animations.accessor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.forge.system.animations.json.animations.registry.IAnimationAccessorType;
import sleys.efedp.forge.system.animations.json.animations.types.AttackAnimationAccessorType;
import sleys.efedp.forge.system.animations.json.properties.IAnimationProperties;
import sleys.efedp.forge.system.animations.json.properties.phase.AnimationPhase;
import sleys.efedp.forge.system.animations.json.properties.armature.registry.ArmatureTypeRegistry;
import sleys.efedp.forge.system.animations.json.properties.armature.registry.IArmatureType;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.BasicAttackAnimation;

import java.util.Arrays;

public record ComboAttackAccessor(float transitionTime, String animationPath,
                                  IArmatureType armatureType,
                                  AnimationPhase... phases
) implements IAnimationAccessor<BasicAttackAnimation> {

    public static final MapCodec<ComboAttackAccessor> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("transition_time").forGetter(ComboAttackAccessor::transitionTime),
                    Codec.STRING.fieldOf("animation").forGetter(ComboAttackAccessor::animationPath),
                    ArmatureTypeRegistry.CODEC.fieldOf("armature").forGetter(ComboAttackAccessor::armatureType),
                    AnimationPhase.CODEC.codec().listOf().fieldOf("phases").forGetter(r -> Arrays.asList(r.phases()))
            ).apply(instance, (transition, path, armature, phases) ->
                    new ComboAttackAccessor(transition, path, armature, phases.toArray(AnimationPhase[]::new))
            )
    );

    @Override
    public IAnimationAccessorType accessorType() {
        return AttackAnimationAccessorType.COMBO_ATTACK;
    }

    @Override
    public AnimationManager.AnimationAccessor<BasicAttackAnimation> register(AnimationManager.AnimationBuilder builder,
                                                                             IAnimationProperties<BasicAttackAnimation> property) {
        return builder.nextAccessor(animationPath, (accessor) -> {
            var animation = new BasicAttackAnimation(
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

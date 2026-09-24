package sleys.efedp.forge.system.animations.json.animations.accessor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.forge.system.animations.json.animations.registry.IAnimationAccessorType;
import sleys.efedp.forge.system.animations.json.animations.types.AttackAnimationAccessorType;
import sleys.efedp.forge.system.animations.json.properties.IAnimationProperties;
import sleys.efedp.forge.system.animations.json.properties.armature.registry.ArmatureTypeRegistry;
import sleys.efedp.forge.system.animations.json.properties.armature.registry.IArmatureType;
import sleys.efedp.forge.system.animations.json.properties.phase.AnimationPhase;
import sleys.sl.library.exceptions.RegistryObjectException;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.RangedAttackAnimation;

import java.util.Arrays;

public record RangedAttackAnimationAccessor(float transitionTime, String animationPath,
                                            IArmatureType armatureType,
                                            AnimationPhase... phases
) implements IAnimationAccessor<RangedAttackAnimation> {

    public static final MapCodec<RangedAttackAnimationAccessor> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("transition_time").forGetter(RangedAttackAnimationAccessor::transitionTime),
                    Codec.STRING.fieldOf("animation").forGetter(RangedAttackAnimationAccessor::animationPath),
                    ArmatureTypeRegistry.CODEC.fieldOf("armature").forGetter(RangedAttackAnimationAccessor::armatureType),
                    AnimationPhase.CODEC.codec().listOf().fieldOf("phases").forGetter(r -> Arrays.asList(r.phases()))
            ).apply(instance, (transition, path, armature, phases) ->
                    new RangedAttackAnimationAccessor(transition, path, armature, phases.toArray(AnimationPhase[]::new))
            )
    );

    @Override
    public IAnimationAccessorType accessorType() {
        return AttackAnimationAccessorType.RANGED_ATTACK;
    }

    @Override
    public AnimationManager.AnimationAccessor<RangedAttackAnimation> register(AnimationManager.AnimationBuilder builder,
                                                                             IAnimationProperties<RangedAttackAnimation> property) {
        var phase = Arrays.stream(phases)
                .map(animationPhase -> animationPhase.parseToEpicFightPhases(armatureType))
                .toArray(AttackAnimation.Phase[]::new);

        var monoPhase = phase[0];
        if (monoPhase == null) throw new RegistryObjectException(
                "Invalid operation, because the primary phase obtained from the ranged attack animation is partial, invalid," +
                        " or nonexistent for the accessory type '" + accessorType() + "' and animation '" + animationPath +"'."
        );

        var colliders = monoPhase.colliders;
        if (colliders.length == 0) {
            throw new RegistryObjectException(
                    "Invalid operation, because the primary phase of ranged attack animation '" + animationPath +
                            "' does not contain a collider."
            );
        }

        var collider = colliders[0].getSecond();
        var joint = colliders[0].getFirst();

        return builder.nextAccessor(animationPath, (accessor) -> {
            var animation = new RangedAttackAnimation(
                    transitionTime,
                    monoPhase.antic,
                    monoPhase.preDelay,
                    monoPhase.contact,
                    monoPhase.recovery,
                    collider, joint,
                    accessor, armatureType.armature()
            );
            property.applyTo(animation);
            this.isSuccessful(accessor);
            return animation;
        });
    }
}

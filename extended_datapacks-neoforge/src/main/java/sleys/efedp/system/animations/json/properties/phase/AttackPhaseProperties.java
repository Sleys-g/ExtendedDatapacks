package sleys.efedp.system.animations.json.properties.phase;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.capability.data.HitParticleCache;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.world.damagesource.StunType;

import java.util.Optional;

public record AttackPhaseProperties(
        Optional<Float> maxStrikes,
        Optional<Float> damageMultiplier,
        Optional<Float> armorNegation,
        Optional<Float> impact,
        Optional<StunType> stunType,
        Optional<ResourceLocation> swingSound,
        Optional<ResourceLocation> hitSound,
        Optional<ResourceLocation> particle
) {
    public static final AttackPhaseProperties EMPTY = new AttackPhaseProperties(
            Optional.empty(), Optional.empty(),
            Optional.empty(), Optional.empty(),
            Optional.empty(), Optional.empty(),
            Optional.empty(), Optional.empty()
    );

    public static final MapCodec<AttackPhaseProperties> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.optionalFieldOf("max_strikes").forGetter(AttackPhaseProperties::maxStrikes),
                    Codec.FLOAT.optionalFieldOf("damage").forGetter(AttackPhaseProperties::damageMultiplier),
                    Codec.FLOAT.optionalFieldOf("armor_negation").forGetter(AttackPhaseProperties::armorNegation),
                    Codec.FLOAT.optionalFieldOf("impact").forGetter(AttackPhaseProperties::impact),
                    PhaseStunType.CODEC.optionalFieldOf("stun_type").forGetter(AttackPhaseProperties::stunType),
                    ResourceLocation.CODEC.optionalFieldOf("swing_sound").forGetter(AttackPhaseProperties::swingSound),
                    ResourceLocation.CODEC.optionalFieldOf("hit_sound").forGetter(AttackPhaseProperties::hitSound),
                    ResourceLocation.CODEC.optionalFieldOf("particle").forGetter(AttackPhaseProperties::particle)
            ).apply(instance, AttackPhaseProperties::new)
    );

    public void applyTo(AttackAnimation.Phase phase) {
        maxStrikes.ifPresent(value ->
                phase.addProperty(
                        AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER,
                        ValueModifier.adder(value)
                )
        );

        damageMultiplier.ifPresent(value ->
                phase.addProperty(
                        AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER,
                        ValueModifier.multiplier(value)
                )
        );

        armorNegation.ifPresent(value ->
                phase.addProperty(
                        AnimationProperty.AttackPhaseProperty.ARMOR_NEGATION_MODIFIER,
                        ValueModifier.adder(value)
                )
        );

        impact.ifPresent(value ->
                phase.addProperty(
                        AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER,
                        ValueModifier.multiplier(value)
                )
        );

        stunType.ifPresent(value ->
                phase.addProperty(
                        AnimationProperty.AttackPhaseProperty.STUN_TYPE,
                        value
                )
        );

        swingSound.ifPresent(id -> {
            SoundEvent sound = BuiltInRegistries.SOUND_EVENT.get(id);

            if (sound != null) {
                phase.addProperty(
                        AnimationProperty.AttackPhaseProperty.SWING_SOUND,
                        sound
                );
            } else {
                LogError.SWING_SOUND.logError(id);
            }
        });

        hitSound.ifPresent(id -> {
            SoundEvent sound = BuiltInRegistries.SOUND_EVENT.get(id);

            if (sound != null) {
                phase.addProperty(
                        AnimationProperty.AttackPhaseProperty.HIT_SOUND,
                        sound
                );
            } else {
                LogError.HIT_SOUND.logError(id);
            }
        });

        particle.ifPresent(id -> {
            DeferredHolder<ParticleType<?>, HitParticleType> hitParticle =
                    HitParticleCache.getParticleDeferred(id);

            if (hitParticle != null) {
                phase.addProperty(
                        AnimationProperty.AttackPhaseProperty.PARTICLE,
                        hitParticle
                );
            } else {
                LogError.PARTICLE.logError(id);
            }
        });
    }

    private enum LogError {
        SWING_SOUND("Swing Sound"),
        HIT_SOUND("Hit Sound"),
        PARTICLE("Hit Particle");

        private final String name;

        LogError(String name) {
            this.name = name;
        }

        private void logError(Object key) {
            ExtendedDatapacks.LOGGER.warn(
                    "[Attack Phase Properties] The attempt to assign the {} failed: {}",
                    this.name,
                    key
            );
        }
    }
}
package sleys.efedp.system.innates.json.properties;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.capability.data.HitParticleCache;
import sleys.efedp.system.animations.json.properties.phase.PhaseStunType;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;
import yesman.epicfight.world.damagesource.StunType;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public record InnatePhaseProperties(
        Optional<Float> maxStrikes,
        Optional<Float> damageMultiplier,
        Optional<Float> armorNegation,
        Optional<Float> impact,
        Optional<StunType> stunType,
        Optional<Boolean> extraDamage,
        Optional<ResourceLocation> swingSound,
        Optional<ResourceLocation> hitSound,
        Optional<ResourceLocation> particle
) {

    public static final InnatePhaseProperties EMPTY = new InnatePhaseProperties(
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty()
    );

    public static final MapCodec<InnatePhaseProperties> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.optionalFieldOf("max_strikes").forGetter(InnatePhaseProperties::maxStrikes),
                    Codec.FLOAT.optionalFieldOf("damage_multiplier").forGetter(InnatePhaseProperties::damageMultiplier),
                    Codec.FLOAT.optionalFieldOf("armor_negation").forGetter(InnatePhaseProperties::armorNegation),
                    Codec.FLOAT.optionalFieldOf("impact").forGetter(InnatePhaseProperties::impact),
                    PhaseStunType.CODEC.optionalFieldOf("stun_type").forGetter(InnatePhaseProperties::stunType),
                    Codec.BOOL.optionalFieldOf("extra_damage").forGetter(InnatePhaseProperties::extraDamage),
                    ResourceLocation.CODEC.optionalFieldOf("swing_sound").forGetter(InnatePhaseProperties::swingSound),
                    ResourceLocation.CODEC.optionalFieldOf("hit_sound").forGetter(InnatePhaseProperties::hitSound),
                    ResourceLocation.CODEC.optionalFieldOf("particle").forGetter(InnatePhaseProperties::particle)
            ).apply(instance, InnatePhaseProperties::new)
    );

    public <T extends WeaponInnateSkill> void applyTo(T builder) {
        var phaseBuilder = builder.newProperty();

        maxStrikes.ifPresent(value ->
                phaseBuilder.addProperty(
                        AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER,
                        ValueModifier.adder(value)
                )
        );

        damageMultiplier.ifPresent(value ->
                phaseBuilder.addProperty(
                        AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER,
                        ValueModifier.multiplier(value)
                )
        );

        armorNegation.ifPresent(value ->
                phaseBuilder.addProperty(
                        AnimationProperty.AttackPhaseProperty.ARMOR_NEGATION_MODIFIER,
                        ValueModifier.adder(value)
                )
        );

        impact.ifPresent(value ->
                phaseBuilder.addProperty(
                        AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER,
                        ValueModifier.multiplier(value)
                )
        );

        stunType.ifPresent(value ->
                phaseBuilder.addProperty(
                        AnimationProperty.AttackPhaseProperty.STUN_TYPE,
                        value
                )
        );

        extraDamage.filter(Boolean::booleanValue).ifPresent(value -> 
                phaseBuilder.addProperty(
                        AnimationProperty.AttackPhaseProperty.EXTRA_DAMAGE, 
                        Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[1]))
                )
        );

        swingSound.ifPresent(id -> {
            SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(id);
            if (sound != null) {
                phaseBuilder.addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, sound);
            } else {
                LogError.SWING_SOUND.logError(id);
            }
        });

        hitSound.ifPresent(id -> {
            SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(id);
            if (sound != null) {
                phaseBuilder.addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, sound);
            } else {
                LogError.HIT_SOUND.logError(id);
            }
        });

        particle.ifPresent(id -> {
            RegistryObject<HitParticleType> hitParticle = HitParticleCache.getParticleDeferred(id);

            if (hitParticle != null) {
                phaseBuilder.addProperty(
                        AnimationProperty.AttackPhaseProperty.PARTICLE,
                        hitParticle
                );
            } else {
                LogError.PARTICLE.logError(id);
            }
        });
    }

    public Map<AnimationProperty.AttackPhaseProperty<?>, Object> saveTo(Map<AnimationProperty.AttackPhaseProperty<?>, Object> phaseProperties) {
        maxStrikes.ifPresent(value ->
                phaseProperties.put(
                        AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER,
                        ValueModifier.adder(value)
                )
        );

        damageMultiplier.ifPresent(value ->
                phaseProperties.put(
                        AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER,
                        ValueModifier.multiplier(value)
                )
        );

        armorNegation.ifPresent(value ->
                phaseProperties.put(
                        AnimationProperty.AttackPhaseProperty.ARMOR_NEGATION_MODIFIER,
                        ValueModifier.adder(value)
                )
        );

        impact.ifPresent(value ->
                phaseProperties.put(
                        AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER,
                        ValueModifier.multiplier(value)
                )
        );

        stunType.ifPresent(value ->
                phaseProperties.put(
                        AnimationProperty.AttackPhaseProperty.STUN_TYPE,
                        value
                )
        );

        extraDamage.filter(Boolean::booleanValue).ifPresent(value ->
                phaseProperties.put(
                        AnimationProperty.AttackPhaseProperty.EXTRA_DAMAGE,
                        Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[1]))
                )
        );

        swingSound.ifPresent(id -> {
            SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(id);
            if (sound != null) {
                phaseProperties.put(AnimationProperty.AttackPhaseProperty.SWING_SOUND, sound);
            } else {
                LogError.SWING_SOUND.logError(id);
            }
        });

        hitSound.ifPresent(id -> {
            SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(id);
            if (sound != null) {
                phaseProperties.put(AnimationProperty.AttackPhaseProperty.HIT_SOUND, sound);
            } else {
                LogError.HIT_SOUND.logError(id);
            }
        });

        particle.ifPresent(id -> {
            RegistryObject<HitParticleType> hitParticle = HitParticleCache.getParticleDeferred(id);

            if (hitParticle != null) {
                phaseProperties.put(
                        AnimationProperty.AttackPhaseProperty.PARTICLE,
                        hitParticle
                );
            } else {
                LogError.PARTICLE.logError(id);
            }
        });

        return phaseProperties;
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
                    "[Innate Phase Properties] The attempt to assign the {} failed: {}",
                    this.name,
                    key
            );
        }
    }
}
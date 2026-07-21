package sleys.efedp.system.animations.json.properties.phase;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;
import sleys.efedp.capability.data.HitParticleCache;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.world.damagesource.StunType;

import java.util.Optional;

public record AttackPhaseProperties(
        Float maxStrikes,
        Float damageMultiplier,
        Float armorNegation,
        Float impact,
        StunType stunType,
        ResourceLocation swingSound,
        ResourceLocation hitSound,
        ResourceLocation particle
) {
    public static final AttackPhaseProperties EMPTY = new AttackPhaseProperties(
            null, null, null,
            null, null, null, null, null
    );

    public static final MapCodec<AttackPhaseProperties> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.optionalFieldOf("max_strikes").forGetter(p -> Optional.ofNullable(p.maxStrikes())),
                    Codec.FLOAT.optionalFieldOf("damage").forGetter(p -> Optional.ofNullable(p.damageMultiplier())),
                    Codec.FLOAT.optionalFieldOf("armor_negation").forGetter(p -> Optional.ofNullable(p.armorNegation())),
                    Codec.FLOAT.optionalFieldOf("impact").forGetter(p -> Optional.ofNullable(p.impact())),
                    PhaseStunType.CODEC.optionalFieldOf("stun_type").forGetter(p -> Optional.ofNullable(p.stunType())),
                    ResourceLocation.CODEC.optionalFieldOf("swing_sound").forGetter(p -> Optional.ofNullable(p.swingSound())),
                    ResourceLocation.CODEC.optionalFieldOf("hit_sound").forGetter(p -> Optional.ofNullable(p.hitSound())),
                    ResourceLocation.CODEC.optionalFieldOf("particle").forGetter(p -> Optional.ofNullable(p.particle()))
            ).apply(instance, (maxStrikes, damage, armor, impact, stunType, swing, hit, particle) ->
                    new AttackPhaseProperties(
                            maxStrikes.orElse(null), damage.orElse(null), armor.orElse(null), impact.orElse(null),
                            stunType.orElse(null), swing.orElse(null), hit.orElse(null), particle.orElse(null)
                    )
            )
    );

    public void applyTo(AttackAnimation.Phase phase) {
        if (maxStrikes != null) phase.addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(maxStrikes));
        if (damageMultiplier != null) phase.addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(damageMultiplier));
        if (armorNegation != null) phase.addProperty(AnimationProperty.AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(armorNegation));
        if (impact != null) phase.addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(impact));
        if (stunType != null) phase.addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, stunType);

        if (swingSound != null) {
            SoundEvent sound = BuiltInRegistries.SOUND_EVENT.get(swingSound);
            if (sound != null) phase.addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, sound);
        }

        if (hitSound != null) {
            SoundEvent sound = BuiltInRegistries.SOUND_EVENT.get(hitSound);
            if (sound != null) phase.addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, sound);
        }

        if (particle != null) {
            RegistryObject<HitParticleType> hitParticle = HitParticleCache.getParticleDeferred(particle);
            if (hitParticle != null) phase.addProperty(AnimationProperty.AttackPhaseProperty.PARTICLE, hitParticle);
        }
    }
}
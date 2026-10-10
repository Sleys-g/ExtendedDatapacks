package sleys.efedp.forge.system.innates.json.definitions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import sleys.efedp.forge.system.innates.json.builder.wrapper.timed.WTimedInnateSkill;
import sleys.efedp.forge.system.innates.json.properties.InnatePhaseProperties;
import sleys.sl.library.util.io.JsonComponentArgs;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.skill.SkillCategories;

import java.util.List;
import java.util.Optional;


public record TimedInnateSkillDefinition(
        String name, String animation,
        Optional<ResourceLocation> soundEvent,
        boolean disableTooltipProperties,
        List<InnatePhaseProperties> properties,
        List<JsonComponentArgs> tooltip
) implements IInnateSkillDefinition<WTimedInnateSkill> {

    public static final MapCodec<TimedInnateSkillDefinition> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.STRING.fieldOf("name").forGetter(TimedInnateSkillDefinition::name),
                    Codec.STRING.fieldOf("animation").forGetter(TimedInnateSkillDefinition::animation),
                    ResourceLocation.CODEC.optionalFieldOf("sound").forGetter(TimedInnateSkillDefinition::soundEvent),

                    Codec.BOOL.optionalFieldOf("disableTooltipProperties", false)
                            .forGetter(TimedInnateSkillDefinition::disableTooltipProperties),

                    InnatePhaseProperties.CODEC.codec()
                            .listOf()
                            .optionalFieldOf("properties", List.of())
                            .forGetter(skillDefinitions -> skillDefinitions.properties),

                    JsonComponentArgs.CODEC
                            .listOf()
                            .optionalFieldOf("tooltip", List.of())
                            .forGetter(skillDefinitions -> skillDefinitions.tooltip)

                    ).apply(instance, TimedInnateSkillDefinition::new
            )
    );

    @SuppressWarnings("deprecation")
    public WTimedInnateSkill.Builder createBuilder(AnimationManager.AnimationAccessor<? extends StaticAnimation> animationAccessor) {
        return WTimedInnateSkill
                .createTimedBuilder()
                .setAnimations(animationAccessor)
                .setTooltipArray(tooltip)
                .setDisableTooltipProperties(disableTooltipProperties)
                .setActivationSound(soundEvent.map(BuiltInRegistries.SOUND_EVENT::get))
                .setCategory(SkillCategories.WEAPON_INNATE);
    }

    @Override
    public void applyProperties(WTimedInnateSkill skill) {
        properties.forEach(property -> property.applyTo(skill));
    }
}

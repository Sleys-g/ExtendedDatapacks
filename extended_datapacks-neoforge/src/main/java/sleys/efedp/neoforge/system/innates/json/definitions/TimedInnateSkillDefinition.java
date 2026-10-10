package sleys.efedp.neoforge.system.innates.json.definitions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import sleys.efedp.neoforge.system.innates.json.builder.wrapper.simple.WSimpleInnateSkill;
import sleys.efedp.neoforge.system.innates.json.builder.wrapper.timed.WTimedInnateSkill;
import sleys.efedp.neoforge.system.innates.json.properties.InnatePhaseProperties;
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
) implements IInnateSkillDefinition<WTimedInnateSkill.Builder> {

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

    public WTimedInnateSkill.Builder createBuilder(AnimationManager.AnimationAccessor<? extends StaticAnimation> animationAccessor) {
        return WTimedInnateSkill
                .createTimedBuilder(WTimedInnateSkill::new)
                .setAnimations(animationAccessor)
                .setTooltipArray(tooltip)
                .setDisableTooltipProperties(disableTooltipProperties)
                .setActivationSound(soundEvent.map(BuiltInRegistries.SOUND_EVENT::get))
                .setCategory(SkillCategories.WEAPON_INNATE);
    }

    @Override
    public void applyProperties(WTimedInnateSkill.Builder builder) {
        properties.forEach(property -> property.applyTo(builder));
    }
}

package sleys.efedp.system.innates.json.definitions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.innates.json.builder.wrapper.simple.WSimpleInnateSkill;
import sleys.efedp.system.innates.json.properties.InnatePhaseProperties;
import sleys.sl.library.util.io.JsonComponentArgs;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.skill.SkillCategories;

import java.util.List;


public record SimpleInnateSkillDefinition(
        String name, String animation,
        boolean disableTooltipProperties,
        List<InnatePhaseProperties> properties,
        List<JsonComponentArgs> tooltip
) implements IInnateSkillDefinition<WSimpleInnateSkill> {

    public static final MapCodec<SimpleInnateSkillDefinition> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.STRING.fieldOf("name").forGetter(SimpleInnateSkillDefinition::name),
                    Codec.STRING.fieldOf("animation").forGetter(SimpleInnateSkillDefinition::animation),

                    Codec.BOOL.optionalFieldOf("disableTooltipProperties", false)
                            .forGetter(SimpleInnateSkillDefinition::disableTooltipProperties),

                    InnatePhaseProperties.CODEC.codec()
                            .listOf()
                            .optionalFieldOf("properties", List.of())
                            .forGetter(skillDefinitions -> skillDefinitions.properties),

                    JsonComponentArgs.CODEC
                            .listOf()
                            .optionalFieldOf("tooltip", List.of())
                            .forGetter(skillDefinitions -> skillDefinitions.tooltip)

                    ).apply(instance, SimpleInnateSkillDefinition::new
            )
    );

    public WSimpleInnateSkill.Builder createBuilder(AnimationManager.AnimationAccessor<? extends StaticAnimation> animationAccessor) {
        return WSimpleInnateSkill
                .createSimpleWeaponInnateBuilder()
                .setAnimations(animationAccessor)
                .setTooltipArray(tooltip)
                .setDisableTooltipProperties(disableTooltipProperties)
                .setCategory(SkillCategories.WEAPON_INNATE);
    }

    @Override
    public void applyProperties(WSimpleInnateSkill skill) {
        properties.forEach(property -> property.applyTo(skill));
    }
}

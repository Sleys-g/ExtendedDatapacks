package sleys.efedp.system.innates.json.definitions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.innates.json.builder.data.SequentialAnimationData;
import sleys.efedp.system.innates.json.builder.wrapper.sequential.WSequentialInnateSkill;
import sleys.sl.library.util.io.JsonComponentArgs;
import yesman.epicfight.skill.SkillCategories;

import java.util.List;


public record SequentialInnateSkillDefinition(
        String name, boolean disableTooltipProperties,
        List<SequentialAnimationData> sequentialAnimationData,
        List<JsonComponentArgs> tooltip
) implements IInnateSkillDefinition<WSequentialInnateSkill.Builder> {

    public static final MapCodec<SequentialInnateSkillDefinition> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.STRING.fieldOf("name").forGetter(SequentialInnateSkillDefinition::name),
                    Codec.BOOL.optionalFieldOf("disableTooltipProperties", false)
                            .forGetter(SequentialInnateSkillDefinition::disableTooltipProperties),

                    SequentialAnimationData.CODEC.codec()
                            .listOf()
                            .optionalFieldOf("sequential_list", List.of())
                            .forGetter(skillDefinitions -> skillDefinitions.sequentialAnimationData),

                    JsonComponentArgs.CODEC
                            .listOf()
                            .optionalFieldOf("tooltip", List.of())
                            .forGetter(skillDefinitions -> skillDefinitions.tooltip)

                    ).apply(instance, SequentialInnateSkillDefinition::new
            )
    );

    public WSequentialInnateSkill.Builder createBuilder() {
        return WSequentialInnateSkill
                .createSequentialBuilder()
                .setTooltipArray(tooltip)
                .setDisableTooltipProperties(disableTooltipProperties)
                .setCategory(SkillCategories.WEAPON_INNATE);
    }
}

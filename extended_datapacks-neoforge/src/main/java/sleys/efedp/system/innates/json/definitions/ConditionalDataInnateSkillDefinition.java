package sleys.efedp.system.innates.json.definitions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.innates.json.builder.data.ConditionalDataAnimationData;
import sleys.efedp.system.innates.json.builder.wrapper.conditional.WConditionalDataInnateSkill;
import sleys.sl.library.util.io.JsonComponentArgs;

import javax.annotation.Nullable;
import java.util.List;

public record ConditionalDataInnateSkillDefinition(
        String name,
        boolean disableTooltipProperties,
        @Nullable List<JsonComponentArgs> tooltip,
        List<ConditionalDataAnimationData> conditionalAnimationData
) implements IInnateSkillDefinition<WConditionalDataInnateSkill.Builder> {

    public static final MapCodec<ConditionalDataInnateSkillDefinition> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.STRING.fieldOf("name").forGetter(ConditionalDataInnateSkillDefinition::name),

                    Codec.BOOL.optionalFieldOf("disableTooltipProperties", false)
                            .forGetter(ConditionalDataInnateSkillDefinition::disableTooltipProperties),

                    JsonComponentArgs.CODEC
                            .listOf()
                            .optionalFieldOf("tooltip", List.of())
                            .forGetter(skillDefinitions -> skillDefinitions.tooltip),

                    ConditionalDataAnimationData.CODEC
                            .codec().listOf()
                            .optionalFieldOf("conditions", List.of())
                            .forGetter(ConditionalDataInnateSkillDefinition::conditionalAnimationData)

            ).apply(instance, ConditionalDataInnateSkillDefinition::new)
    );

    public WConditionalDataInnateSkill.Builder createBuilder() {
        return WConditionalDataInnateSkill.createConditionalBuilder(WConditionalDataInnateSkill::new)
                .setTooltipArray(tooltip)
                .setDisableTooltipProperties(disableTooltipProperties);
    }
}

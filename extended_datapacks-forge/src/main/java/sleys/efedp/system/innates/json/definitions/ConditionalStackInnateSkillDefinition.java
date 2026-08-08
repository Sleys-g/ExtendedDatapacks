package sleys.efedp.system.innates.json.definitions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.innates.json.builder.wrapper.conditional.WConditionalStackInnateSkill;
import sleys.efedp.system.innates.json.data.ConditionalStackAnimationData;
import sleys.efedp.system.innates.json.data.ConditionalType;
import sleys.sl.library.util.io.JsonComponentArgs;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public record ConditionalStackInnateSkillDefinition(
        String name,
        boolean disableTooltipProperties,
        @Nullable List<JsonComponentArgs> tooltip,
        Map<ConditionalType, ConditionalStackAnimationData> conditionalAnimationData
) implements IInnateSkillDefinition<WConditionalStackInnateSkill> {

    public static final MapCodec<ConditionalStackInnateSkillDefinition> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.STRING.fieldOf("name").forGetter(ConditionalStackInnateSkillDefinition::name),

                    Codec.BOOL.optionalFieldOf("disableTooltipProperties", false)
                            .forGetter(ConditionalStackInnateSkillDefinition::disableTooltipProperties),


                    JsonComponentArgs.CODEC
                            .listOf()
                            .optionalFieldOf("tooltip", List.of())
                            .forGetter(skillDefinitions -> skillDefinitions.tooltip),

                    Codec.unboundedMap(ConditionalType.CODEC, ConditionalStackAnimationData.CODEC.codec())
                            .fieldOf("conditions")
                            .forGetter(ConditionalStackInnateSkillDefinition::conditionalAnimationData)

            ).apply(instance, ConditionalStackInnateSkillDefinition::new)
    );

    public WConditionalStackInnateSkill.Builder createBuilder() {
        return WConditionalStackInnateSkill.createConditionalBuilder()
                .setTooltipArray(tooltip)
                .setDisableTooltipProperties(disableTooltipProperties);
    }
}

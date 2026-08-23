package sleys.efedp.system.innates.json.definitions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.innates.json.builder.wrapper.conditional.WConditionalInnateSkill;
import sleys.efedp.system.innates.json.builder.data.AnimationData;
import sleys.efedp.system.innates.json.builder.data.ConditionalType;
import sleys.sl.library.util.io.JsonComponentArgs;

import javax.annotation.Nullable;
import java.util.*;

public record ConditionalInnateSkillDefinition(
        String name,
        boolean disableTooltipProperties,
        @Nullable List<JsonComponentArgs> tooltip,
        Map<ConditionalType, AnimationData> conditionalAnimationData
) implements IInnateSkillDefinition<WConditionalInnateSkill.Builder> {

    public static final MapCodec<ConditionalInnateSkillDefinition> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.STRING.fieldOf("name").forGetter(ConditionalInnateSkillDefinition::name),

                    Codec.BOOL.optionalFieldOf("disableTooltipProperties", false)
                            .forGetter(ConditionalInnateSkillDefinition::disableTooltipProperties),

                    JsonComponentArgs.CODEC
                            .listOf()
                            .optionalFieldOf("tooltip", List.of())
                            .forGetter(skillDefinitions -> skillDefinitions.tooltip),

                    Codec.unboundedMap(ConditionalType.CODEC, AnimationData.CODEC.codec())
                            .fieldOf("conditions")
                            .forGetter(ConditionalInnateSkillDefinition::conditionalAnimationData)

            ).apply(instance, ConditionalInnateSkillDefinition::new)
    );

    public WConditionalInnateSkill.Builder createBuilder() {
        return WConditionalInnateSkill.createConditionalBuilder(WConditionalInnateSkill::new)
                .setTooltipArray(tooltip)
                .setDisableTooltipProperties(disableTooltipProperties);
    }
}

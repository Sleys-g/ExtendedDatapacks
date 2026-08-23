package sleys.efedp.system.innates.json.definitions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.innates.json.builder.data.PerComboAnimationData;
import sleys.efedp.system.innates.json.builder.wrapper.combo.WPerComboInnateSkill;
import sleys.sl.library.util.io.JsonComponentArgs;
import yesman.epicfight.skill.SkillCategories;

import java.util.List;

public record PerComboInnateSkillDefinition(
        String name, boolean disableTooltipProperties,
        List<PerComboAnimationData> perComboAnimationData,
        List<JsonComponentArgs> tooltip
) implements IInnateSkillDefinition<WPerComboInnateSkill.Builder> {
    
    public static final MapCodec<PerComboInnateSkillDefinition> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.STRING.fieldOf("name").forGetter(PerComboInnateSkillDefinition::name),
                    Codec.BOOL.optionalFieldOf("disableTooltipProperties", false)
                            .forGetter(PerComboInnateSkillDefinition::disableTooltipProperties),

                    PerComboAnimationData.CODEC.codec()
                            .listOf()
                            .optionalFieldOf("per_combo_list", List.of())
                            .forGetter(skillDefinitions -> skillDefinitions.perComboAnimationData),

                    JsonComponentArgs.CODEC
                            .listOf()
                            .optionalFieldOf("tooltip", List.of())
                            .forGetter(skillDefinitions -> skillDefinitions.tooltip)

            ).apply(instance, PerComboInnateSkillDefinition::new
            )
    );

    public WPerComboInnateSkill.Builder createBuilder() {
        return WPerComboInnateSkill
                .createPerComboBuilder()
                .setTooltipArray(tooltip)
                .setDisableTooltipProperties(disableTooltipProperties)
                .setCategory(SkillCategories.WEAPON_INNATE);
    }
}

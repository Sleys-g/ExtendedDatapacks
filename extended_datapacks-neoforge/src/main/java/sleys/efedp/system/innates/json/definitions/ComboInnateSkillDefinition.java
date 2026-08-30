package sleys.efedp.system.innates.json.definitions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.innates.json.builder.wrapper.combo.WComboInnateSkill;
import sleys.efedp.system.innates.json.builder.data.ComboEntryPointData;
import sleys.efedp.system.innates.json.builder.data.ComboNodeData;
import sleys.sl.library.util.io.JsonComponentArgs;
import yesman.epicfight.skill.SkillCategories;

import java.util.List;
import java.util.Map;

public record ComboInnateSkillDefinition(
        String name,
        List<JsonComponentArgs> tooltip,
        List<ComboEntryPointData> entryPoints,
        Map<String, ComboNodeData> nodes
) implements IInnateSkillDefinition<WComboInnateSkill.Builder> {

    public static final MapCodec<ComboInnateSkillDefinition> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.STRING.fieldOf("name").forGetter(ComboInnateSkillDefinition::name),
                    JsonComponentArgs.CODEC.listOf().optionalFieldOf("tooltip", List.of())
                            .forGetter(ComboInnateSkillDefinition::tooltip),
                    ComboEntryPointData.CODEC.listOf().fieldOf("entry_points")
                            .forGetter(ComboInnateSkillDefinition::entryPoints),
                    Codec.unboundedMap(Codec.STRING, ComboNodeData.CODEC.codec()).fieldOf("nodes")
                            .forGetter(ComboInnateSkillDefinition::nodes)
            ).apply(instance, ComboInnateSkillDefinition::new));

    public WComboInnateSkill.Builder createBuilder() {
        return WComboInnateSkill
                .createComboBuilder()
                .setTooltipArray(tooltip)
                .setCategory(SkillCategories.WEAPON_INNATE);
    }
}

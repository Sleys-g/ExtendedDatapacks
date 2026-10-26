package sleys.efedp.neoforge.system.skills.icon.definitions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record IconSkillModifierDefinitions(List<RawIconSkillBuilderData> iconSkillBuilder) {

    public static final Codec<IconSkillModifierDefinitions> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    RawIconSkillBuilderData.CODEC.listOf()
                            .fieldOf("icon_skill_builder")
                            .forGetter(IconSkillModifierDefinitions::iconSkillBuilder)
            ).apply(instance, IconSkillModifierDefinitions::new));
}

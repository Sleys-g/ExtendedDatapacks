package sleys.efedp.forge.system.skills.passive.definitions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record PassiveSkillModifierDefinitions(List<RawSkillBuilderData> skillBuilder) {

    public static final Codec<PassiveSkillModifierDefinitions> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    RawSkillBuilderData.CODEC.listOf()
                            .fieldOf("skill_builder")
                            .forGetter(PassiveSkillModifierDefinitions::skillBuilder)
            ).apply(instance, PassiveSkillModifierDefinitions::new));
}

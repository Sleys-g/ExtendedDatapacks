package sleys.efedp.forge.system.skills.guard.definitions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record GuardSkillModifierDefinitions(List<RawGuardBuilderData> skillGuardBuilder) {
    public static final Codec<GuardSkillModifierDefinitions> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    RawGuardBuilderData.CODEC.listOf()
                            .fieldOf("skill_guard_builder")
                            .forGetter(GuardSkillModifierDefinitions::skillGuardBuilder)
            ).apply(instance, GuardSkillModifierDefinitions::new));
}

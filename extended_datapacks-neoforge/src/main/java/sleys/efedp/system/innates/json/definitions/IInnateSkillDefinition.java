package sleys.efedp.system.innates.json.definitions;

import sleys.efedp.system.innates.json.properties.InnatePhaseProperties;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.skill.SkillBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public sealed interface IInnateSkillDefinition<T extends SkillBuilder<T>> permits ConditionalDataInnateSkillDefinition, ConditionalInnateSkillDefinition, ConditionalStackInnateSkillDefinition, HoldableInnateSkillDefinition, PerComboInnateSkillDefinition, SequentialInnateSkillDefinition, SimpleInnateSkillDefinition {

    default void applyProperties(T builder) {}

    default List<Map<AnimationProperty.AttackPhaseProperty<?>, Object>> saveProperties(List<InnatePhaseProperties> properties) {
        List<Map<AnimationProperty.AttackPhaseProperty<?>, Object>> conditionPhases = new ArrayList<>();
        properties.forEach(property -> conditionPhases.add(property.saveTo(new HashMap<>())));
        return conditionPhases;
    }
}

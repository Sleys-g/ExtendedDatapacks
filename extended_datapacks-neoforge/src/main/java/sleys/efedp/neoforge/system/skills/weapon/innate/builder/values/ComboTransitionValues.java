package sleys.efedp.neoforge.system.skills.weapon.innate.builder.values;

import sleys.efedp.neoforge.system.skills.weapon.innate.builder.data.ConditionalType;

import javax.annotation.Nullable;

public record ComboTransitionValues(String targetId, @Nullable ConditionalType physicalCondition) {}

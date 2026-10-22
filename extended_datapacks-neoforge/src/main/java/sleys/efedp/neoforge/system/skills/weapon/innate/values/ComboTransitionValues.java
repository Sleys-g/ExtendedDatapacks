package sleys.efedp.neoforge.system.skills.weapon.innate.values;

import sleys.efedp.neoforge.system.skills.weapon.innate.data.ConditionalType;

import javax.annotation.Nullable;

public record ComboTransitionValues(String targetId, @Nullable ConditionalType physicalCondition) {}

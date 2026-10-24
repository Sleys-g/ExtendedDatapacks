package sleys.efedp.forge.system.skills.weapon.innate.values;

import sleys.efedp.forge.system.skills.weapon.innate.data.ConditionalType;

import javax.annotation.Nullable;

public record ComboTransitionValues(String targetId, @Nullable ConditionalType physicalCondition) {}

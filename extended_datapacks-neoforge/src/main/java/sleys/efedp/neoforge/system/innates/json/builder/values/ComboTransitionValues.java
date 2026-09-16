package sleys.efedp.neoforge.system.innates.json.builder.values;

import sleys.efedp.neoforge.system.innates.json.builder.data.ConditionalType;

import javax.annotation.Nullable;

public record ComboTransitionValues(String targetId, @Nullable ConditionalType physicalCondition) {}

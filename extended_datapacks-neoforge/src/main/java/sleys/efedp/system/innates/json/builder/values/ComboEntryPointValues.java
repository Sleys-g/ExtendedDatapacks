package sleys.efedp.system.innates.json.builder.values;

import sleys.efedp.system.innates.json.builder.data.ConditionalType;

public record ComboEntryPointValues(String nodeId, Boolean global, ConditionalType physicalCondition) {}

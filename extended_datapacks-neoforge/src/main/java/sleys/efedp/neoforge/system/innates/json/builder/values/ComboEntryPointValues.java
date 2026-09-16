package sleys.efedp.neoforge.system.innates.json.builder.values;

import sleys.efedp.neoforge.system.innates.json.builder.data.ConditionalType;

public record ComboEntryPointValues(String nodeId, Boolean global, ConditionalType physicalCondition) {}

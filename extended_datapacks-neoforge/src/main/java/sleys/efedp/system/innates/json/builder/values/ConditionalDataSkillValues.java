package sleys.efedp.system.innates.json.builder.values;

import sleys.efedp.system.animations.json.properties.functional.datapackets.ReadDataPacketsGroup;
import sleys.efedp.system.innates.json.builder.data.ConditionalType;
import sleys.sl.library.annotations.Linked;

import java.util.Optional;

@Linked(range = Linked.DependentRange.HIGH, packageId = "sleys.efedp.system.animations.json.properties.functional.datapackets")
public record ConditionalDataSkillValues(ConditionalType physicalCondition, Optional<String> tooltipHead, Optional<ReadDataPacketsGroup> readData) {}

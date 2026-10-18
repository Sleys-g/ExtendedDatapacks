package sleys.efedp.neoforge.system.skills.weapon.innate.builder.values;

import sleys.efedp.neoforge.core.datapackets.ReadDataPacketsGroup;
import sleys.efedp.neoforge.system.skills.weapon.innate.builder.data.ConditionalType;
import sleys.sl.library.annotations.Linked;

import java.util.Optional;

@Linked(range = Linked.DependentRange.HIGH, packageId = "sleys.efedp.neoforge.system.animations.json.properties.datapackets")
public record ConditionalDataSkillValues(ConditionalType physicalCondition, Optional<String> tooltipHead, Optional<ReadDataPacketsGroup> readData) {}

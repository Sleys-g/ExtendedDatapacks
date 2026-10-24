package sleys.efedp.forge.system.skills.weapon.innate.values;

import sleys.efedp.forge.core.datapackets.ReadDataPacketsGroup;
import sleys.efedp.forge.system.skills.weapon.innate.data.ConditionalType;
import sleys.sl.library.annotations.Linked;

import java.util.Optional;

@Linked(range = Linked.DependentRange.HIGH, packageId = "sleys.efedp.forge.system.animations.json.properties.functional.datapackets")
public record ConditionalDataSkillValues(ConditionalType physicalCondition, Optional<String> tooltipHead, Optional<ReadDataPacketsGroup> readData) {}

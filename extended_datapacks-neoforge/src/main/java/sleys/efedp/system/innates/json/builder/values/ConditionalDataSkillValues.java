package sleys.efedp.system.innates.json.builder.values;

import sleys.efedp.system.animations.json.properties.functional.datapackets.ReadDataPacketsGroup;
import sleys.efedp.system.innates.json.builder.data.ConditionalType;

import java.util.Optional;

public record ConditionalDataSkillValues(ConditionalType physicalCondition, Optional<String> tooltipHead, Optional<ReadDataPacketsGroup> readData) {}

package sleys.efedp.system.innates.json.builder.values;

import java.util.List;

public record ComboNodeValues(String id, AnimationSkillValues animation, List<ComboTransitionValues> next) {}


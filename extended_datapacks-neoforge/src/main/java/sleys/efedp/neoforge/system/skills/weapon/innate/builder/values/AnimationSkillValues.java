package sleys.efedp.neoforge.system.skills.weapon.innate.builder.values;

import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.DynamicAnimation;

import java.util.List;
import java.util.Map;

public record AnimationSkillValues(AnimationManager.AnimationAccessor<? extends DynamicAnimation> animationAccessor,
                                   List<Map<AnimationProperty.AttackPhaseProperty<?>, Object>> properties) {}

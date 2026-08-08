package sleys.efedp.system.innates.json.data;

import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.DynamicAnimation;

import java.util.List;
import java.util.Map;

public record ConditionalSkillValues(AnimationManager.AnimationAccessor<? extends DynamicAnimation> animationAccessor,
                                     List<Map<AnimationProperty.AttackPhaseProperty<?>, Object>> properties) {}

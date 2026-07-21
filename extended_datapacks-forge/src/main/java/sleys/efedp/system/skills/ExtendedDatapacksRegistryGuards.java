package sleys.efedp.system.skills;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import sleys.efedp.system.skills.json.GuardSkillBuilderModifier;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.skill.guard.GuardSkill;
import yesman.epicfight.world.capabilities.item.Style;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtendedDatapacksRegistryGuards {

    @SubscribeEvent
    public static void addAnyParameterToGuards(SkillBuildEvent.ModRegistryWorker.SkillCreateEvent<GuardSkill.Builder> event) {
        var skillRegistry = event.getRegistryName();
        if (skillRegistry == null) return;

        var skillBuilder = event.getSkillBuilder();
        if (skillBuilder == null) return;

        var dataList = GuardSkillBuilderModifier.get(skillRegistry.toString());
        if (dataList == null || dataList.isEmpty()) return;
        for (var data : dataList) {
            var weaponCategory = data.getParseWeaponCategory();
            var skillID = data.getParseSkill();
            if (!data.isSameSkill(skillRegistry, skillID)) continue;

            /// Segmento de Guard Motion
            var isGuardPerStyle = data.guardMotion().isPerStyle();
            if (!isGuardPerStyle) {
                var guardMotion = data.guardMotion().getAnimationForStyle("return");
                var guardAnimation = data.getParseMotions(guardMotion);

                skillBuilder.addGuardMotion(
                        weaponCategory,
                        (item, player) -> guardAnimation
                );
            } else {
                var styleSet = data.guardMotion().getDeclaredStyles();
                Map<Style, AnimationManager.AnimationAccessor<? extends StaticAnimation>> styleToAnim = new HashMap<>();

                for (var styleKey : styleSet) {
                    var guardMotion = data.guardMotion().getAnimationForStyle(styleKey);
                    if (guardMotion == null) continue;

                    var guardAnimation = data.getParseMotions(guardMotion);
                    if (guardAnimation != null) {
                        var parseStyle = data.getParseStyle(styleKey);
                        if (parseStyle != null) {
                            styleToAnim.put(parseStyle, guardAnimation);
                        }
                    }
                }

                skillBuilder.addGuardMotion(
                        weaponCategory,
                        (item, player) ->
                                styleToAnim.get(item.getStyle(player))
                );
            }

            /// Segmento de Guard Break Motion
            var guardBreakMotion = data.guardBreakMotion().getAnimationForStyle("return");
            var guardBreakAnimation = data.getParseMotions(guardBreakMotion);
            skillBuilder.addGuardBreakMotion(
                    weaponCategory,
                    (item, player) -> guardBreakAnimation

            );

            /// Segmento de Guard Advanced Motion
            var implementAdvancedMotion = data.hasAdvancedMotion();
            var existAdvancedMotion = data.guardAdvancedMotion() != null;
            if (implementAdvancedMotion && existAdvancedMotion) {
                var isAdvancedPerStyle = data.guardAdvancedMotion().isPerStyle();
                var isAdvancedMulti = data.guardAdvancedMotion().isMulti();
                if (!isAdvancedMulti) {
                    if (!isAdvancedPerStyle) {
                        var advancedMotion = data.guardAdvancedMotion().getAnimationForStyle("return");
                        var advancedAnimation = data.getParseMotions(advancedMotion);
                        skillBuilder.addAdvancedGuardMotion(
                                weaponCategory,
                                (item, player) -> advancedAnimation
                        );
                    } else {
                        var styleSet = data.guardAdvancedMotion().getDeclaredStyles();
                        Map<Style, AnimationManager.AnimationAccessor<? extends StaticAnimation>> styleToAnim = new HashMap<>();

                        for (var styleKey : styleSet) {
                            var advancedMotion = data.guardAdvancedMotion().getAnimationForStyle(styleKey);
                            if (advancedMotion == null) continue;

                            var advancedAnimation = data.getParseMotions(advancedMotion);
                            if (advancedAnimation != null) {
                                var parseStyle = data.getParseStyle(styleKey);
                                if (parseStyle != null) {
                                    styleToAnim.put(parseStyle, advancedAnimation);
                                }
                            }
                        }

                        skillBuilder.addAdvancedGuardMotion(
                                weaponCategory,
                                (item, player) ->
                                        styleToAnim.get(item.getStyle(player))
                        );
                    }
                } else {
                    if (!isAdvancedPerStyle) {
                        var advancedMotions = data.guardAdvancedMotion().getAnimationsForStyle("return");
                        if (advancedMotions != null && !advancedMotions.isEmpty()) {
                            List<AnimationManager.AnimationAccessor<? extends StaticAnimation>> advancedAnimations = new ArrayList<>();
                            for (var advancedMotion : advancedMotions) {
                                var advancedAnimation = data.getParseMotions(advancedMotion);
                                if (advancedAnimation == null) continue;
                                advancedAnimations.add(advancedAnimation);
                            }

                            if (!advancedAnimations.isEmpty()) {
                                skillBuilder.addAdvancedGuardMotion(
                                        weaponCategory,
                                        (item, player) -> advancedAnimations
                                );
                            }
                        }
                    } else {
                        var styleSet = data.guardAdvancedMotion().getDeclaredStyles();
                        Map<Style, List<AnimationManager.AnimationAccessor<? extends StaticAnimation>>> styleToAnim = new HashMap<>();

                        for (var styleKey : styleSet) {
                            var advancedMotions = data.guardAdvancedMotion().getAnimationsForStyle(styleKey);
                            if (advancedMotions != null && !advancedMotions.isEmpty()) {
                                List<AnimationManager.AnimationAccessor<? extends StaticAnimation>> advancedAnimations = new ArrayList<>();
                                for (var advancedMotion : advancedMotions) {
                                    var advancedAnimation = data.getParseMotions(advancedMotion);
                                    if (advancedAnimation == null) continue;
                                    advancedAnimations.add(advancedAnimation);
                                }

                                if (!advancedAnimations.isEmpty()) {
                                    var parseStyle = data.getParseStyle(styleKey);
                                    if (parseStyle != null) {
                                        styleToAnim.put(parseStyle, advancedAnimations);
                                    }
                                }
                            }
                        }

                        skillBuilder.addAdvancedGuardMotion(
                                weaponCategory,
                                (item, player) ->
                                        styleToAnim.get(item.getStyle(player))
                        );
                    }
                }
            }
        }
    }
}

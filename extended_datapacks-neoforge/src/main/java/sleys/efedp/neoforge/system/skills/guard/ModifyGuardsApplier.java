package sleys.efedp.neoforge.system.skills.guard;

import net.minecraft.resources.ResourceLocation;
import sleys.efedp.neoforge.api.registry.RegistrySkillTrackers;
import sleys.efedp.neoforge.system.skills.guard.definitions.GuardMotionData;
import sleys.efedp.neoforge.system.skills.guard.definitions.RawGuardBuilderData;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.guard.GuardSkill;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ModifyGuardsApplier {

    private ModifyGuardsApplier() {}

    public static void addAnyParameterToGuards(ResourceLocation skillRegistry, SkillBuilder<?> skillBuilder) {
        if (skillRegistry == null || !(skillBuilder instanceof GuardSkill.Builder guardBuilder)) return;

        for (var dataSet : RegistrySkillTrackers.GUARD_SKILL_MODIFIER.getDefinitionData().values()) {
            for (var dataDefinition : dataSet) {
                for (var data : dataDefinition.skillGuardBuilder()) {
                    if (!data.isSameSkill(skillRegistry, data.getParseSkill())) continue;
                    applyGuardData(guardBuilder, data);
                }
            }
        }
    }

    private static void applyGuardData(GuardSkill.Builder builder, RawGuardBuilderData data) {
        var category = data.getParseWeaponCategory();

        applyGuardMotion(builder, category, data);
        applyGuardBreakMotion(builder, category, data);
        applyAdvancedMotion(builder, category, data);
    }

    private static void applyGuardMotion(GuardSkill.Builder builder, WeaponCategory category, RawGuardBuilderData data) {
        var motion = data.guardMotion();

        if (!motion.isPerStyle()) {
            var animation = parseDefault(data, motion);
            builder.addGuardMotion(category, (item, player) -> animation);
            return;
        }

        var animationByStyle = parseByStyle(data, motion);
        builder.addGuardMotion(category, (item, player) -> animationByStyle.get(item.getStyle(player)));
    }

    private static void applyGuardBreakMotion(GuardSkill.Builder builder, WeaponCategory category, RawGuardBuilderData data) {
        var animation = parseDefault(data, data.guardBreakMotion());
        builder.addGuardBreakMotion(category, (item, player) -> animation);
    }

    private static void applyAdvancedMotion(GuardSkill.Builder builder, WeaponCategory category, RawGuardBuilderData data) {
        var motion = data.guardAdvancedMotion();
        if (!data.hasAdvancedMotion() || motion == null) return;

        boolean perStyle = motion.isPerStyle();

        if (motion.isMulti()) {
            applyAdvancedMulti(builder, category, data, motion, perStyle);
        } else if (perStyle) {
            var animationByStyle = parseByStyle(data, motion);
            builder.addAdvancedGuardMotion(category, (item, player) -> animationByStyle.get(item.getStyle(player)));
        } else {
            var animation = parseDefault(data, motion);
            builder.addAdvancedGuardMotion(category, (item, player) -> animation);
        }
    }

    private static void applyAdvancedMulti(GuardSkill.Builder builder, WeaponCategory category,
                                           RawGuardBuilderData data, GuardMotionData motion, boolean perStyle) {
        if (!perStyle) {
            var animations = parseAll(data, motion.getReturnAnimations());
            if (animations.isEmpty()) return;

            builder.addAdvancedGuardMotion(category, (item, player) -> animations);
            return;
        }

        var animationsByStyle = parseAllByStyle(data, motion);
        builder.addAdvancedGuardMotion(category, (item, player) -> animationsByStyle.get(item.getStyle(player)));
    }

    private static AnimationManager.AnimationAccessor<? extends StaticAnimation> parseDefault(RawGuardBuilderData data, GuardMotionData motion) {
        return data.getParseMotions(motion.getReturnAnimation());
    }

    private static Map<Style, AnimationManager.AnimationAccessor<? extends StaticAnimation>> parseByStyle(RawGuardBuilderData data, GuardMotionData motion) {
        Map<Style, AnimationManager.AnimationAccessor<? extends StaticAnimation>> result = new HashMap<>();

        for (var styleKey : motion.getDeclaredStyles()) {
            var motionName = motion.getAnimationForStyle(styleKey);
            if (motionName == null) continue;

            var animation = data.getParseMotions(motionName);
            if (animation == null) continue;

            var style = data.getParseStyle(styleKey);
            if (style != null) result.put(style, animation);
        }
        return result;
    }

    private static List<AnimationManager.AnimationAccessor<? extends StaticAnimation>> parseAll(RawGuardBuilderData data, Iterable<String> motionNames) {
        List<AnimationManager.AnimationAccessor<? extends StaticAnimation>> result = new ArrayList<>();
        if (motionNames == null) return result;

        for (var motionName : motionNames) {
            var animation = data.getParseMotions(motionName);
            if (animation != null) result.add(animation);
        }
        return result;
    }

    private static Map<Style, List<AnimationManager.AnimationAccessor<? extends StaticAnimation>>> parseAllByStyle(RawGuardBuilderData data, GuardMotionData motion) {
        Map<Style, List<AnimationManager.AnimationAccessor<? extends StaticAnimation>>> result = new HashMap<>();

        for (var styleKey : motion.getDeclaredStyles()) {
            var animations = parseAll(data, motion.getAnimationsForStyle(styleKey));
            if (animations.isEmpty()) continue;

            var style = data.getParseStyle(styleKey);
            if (style != null) result.put(style, animations);
        }
        return result;
    }
}
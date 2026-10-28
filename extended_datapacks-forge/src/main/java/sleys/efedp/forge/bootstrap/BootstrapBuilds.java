package sleys.efedp.forge.bootstrap;

import sleys.efedp.forge.ExtendedDatapacks;
import sleys.efedp.forge.api.registry.RegistryAnimationsTrackers;
import sleys.efedp.forge.api.registry.RegistryInnateSkillTrackers;
import sleys.efedp.forge.api.registry.RegistrySkillTrackers;
import sleys.efedp.forge.api.registry.RegistryWeaponTrackers;

public class BootstrapBuilds {

    protected static void Initialize() {
        ExtendedDatapacks.LOGGER.info("[Extended Datapacks - Bootstrap] Initializing Builds & Start To Tracking...");
        startWeaponBuilders();
        startSkillBuilds();
        startAnimationsBuilds();
        startInnateSkillsBuilds();
    }

    private static void startWeaponBuilders() {
        RegistryWeaponTrackers.WEAPON_CATEGORY_REGISTRY.startToTracking();
    }

    private static void startSkillBuilds() {
        RegistrySkillTrackers.PASSIVE_SKILL_MODIFIER.startToTracking();
        RegistrySkillTrackers.GUARD_SKILL_MODIFIER.startToTracking();
        RegistrySkillTrackers.CATEGORY_SKILL_ICON.startToTracking();
    }

    private static void startInnateSkillsBuilds() {
        RegistryInnateSkillTrackers.SIMPLE_INNATE_SKILL.startToTracking();
        RegistryInnateSkillTrackers.HOLDABLE_INNATE_SKILL.startToTracking();
        RegistryInnateSkillTrackers.CONDITIONAL_INNATE_SKILL.startToTracking();
        RegistryInnateSkillTrackers.STACKS_CONDITIONAL_INNATE_SKILL.startToTracking();
        RegistryInnateSkillTrackers.DATA_CONDITIONAL_INNATE_SKILL.startToTracking();
        RegistryInnateSkillTrackers.SEQUENTIAL_INNATE_SKILL.startToTracking();
        RegistryInnateSkillTrackers.PER_COMBO_INNATE_SKILL.startToTracking();
        RegistryInnateSkillTrackers.COMBO_INNATE_SKILL.startToTracking();
        RegistryInnateSkillTrackers.HOLDABLE_CONDITIONAL_INNATE_SKILL.startToTracking();
        RegistryInnateSkillTrackers.TIMED_INNATE_SKILL.startToTracking();
        RegistryInnateSkillTrackers.TIMED_ADVANCED_INNATE_SKILL.startToTracking();
    }

    private static void startAnimationsBuilds() {
        RegistryAnimationsTrackers.ANIMATION_REGISTRY.startToTracking();
        RegistryAnimationsTrackers.ANIMATION_VIRTUALIZATION.startToTracking();
        RegistryAnimationsTrackers.ANIMATION_CONFIGURATION.startToTracking();
    }
}

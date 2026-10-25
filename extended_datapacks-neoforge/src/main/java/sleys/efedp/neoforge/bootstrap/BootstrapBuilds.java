package sleys.efedp.neoforge.bootstrap;

import net.neoforged.fml.loading.FMLPaths;
import sleys.efedp.neoforge.ExtendedDatapacks;
import sleys.efedp.neoforge.api.registry.RegistryAnimationsTrackers;
import sleys.efedp.neoforge.api.registry.RegistryInnateSkillTrackers;
import sleys.efedp.neoforge.api.registry.RegistrySkillTrackers;
import sleys.efedp.neoforge.system.skills.guard.builder.GuardSkillModifierBuilder;
import sleys.efedp.neoforge.system.skills.icon.builder.IconSkillModifierBuilder;
import sleys.efedp.neoforge.system.category.builder.WeaponCategoryAdderBuilder;
import sleys.sl.library.annotations.LegacyFunction;
import sleys.sl.library.util.io.BuildPathOrFile;

import java.nio.file.Path;
import java.util.function.Consumer;

public class BootstrapBuilds {

    protected static void Initialize() {
        ExtendedDatapacks.LOGGER.info("[Extended Datapacks - Bootstrap] Initializing Builds & Start To Tracking...");
        startSkillBuilds();
        startInnateSkillsBuilds();
        startAnimationsBuilds();
    }

    private static void buildConfigTracker(String category, String subfolder, Consumer<Path> tracker) {
        Path dir = FMLPaths.CONFIGDIR.get()
                .resolve("epicfight_edp")
                .resolve(category)
                .resolve(subfolder);
        BuildPathOrFile.buildPathFolder(dir);
        tracker.accept(dir);
    }

    @LegacyFunction(since = "2.5.5", value = "They will move to DFU in the future")
    private static void startSkillBuilds() {
        RegistrySkillTrackers.PASSIVE_SKILL_MODIFIER.startToTracking();
        buildConfigTracker("skill_builder", "guard_skills", GuardSkillModifierBuilder::startToTracking);
        buildConfigTracker("skill_builder", "category_icon", IconSkillModifierBuilder::startToTracking);
        buildConfigTracker("weapon_builder", "category", WeaponCategoryAdderBuilder::startToTracking);
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

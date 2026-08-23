package sleys.efedp.bootstrap;

import net.neoforged.fml.loading.FMLPaths;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.animations.json.definitions.AnimationsConfigBuilder;
import sleys.efedp.system.animations.json.definitions.AnimationsRegistryBuilder;
import sleys.efedp.system.animations.json.definitions.AnimationsVirtualBuilder;
import sleys.efedp.system.innates.json.builder.*;
import sleys.efedp.system.skills.json.GuardSkillModifierBuilder;
import sleys.efedp.system.skills.json.PassiveSkillModifierBuilder;
import sleys.efedp.system.skills.json.IconSkillModifierBuilder;
import sleys.efedp.system.weapons.json.WeaponCategoryAdderBuilder;
import sleys.sl.library.util.io.BuildPathOrFile;

import java.nio.file.Path;
import java.util.function.Consumer;

public class BootstrapBuilds {

    protected static void Initialize() {
        ExtendedDatapacks.LOGGER.info("[Extended Datapacks - Bootstrap] Initializing Builds & start To Tracking...");
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

    private static void startSkillBuilds() {
        buildConfigTracker("skill_builder", "passive_skills", PassiveSkillModifierBuilder::startToTracking);
        buildConfigTracker("skill_builder", "guard_skills", GuardSkillModifierBuilder::startToTracking);
        buildConfigTracker("skill_builder", "category_icon", IconSkillModifierBuilder::startToTracking);
        buildConfigTracker("weapon_builder", "category", WeaponCategoryAdderBuilder::startToTracking);
    }

    private static void startInnateSkillsBuilds() {
        buildConfigTracker("innate_skill_builder", "simple_innate_skill", SimpleInnateSkillBuilder::startToTracking);
        buildConfigTracker("innate_skill_builder", "holdable_innate_skill", HoldableInnateSkillBuilder::startToTracking);
        buildConfigTracker("innate_skill_builder", "conditional_innate_skill", ConditionalInnateSkillBuilder::startToTracking);
        buildConfigTracker("innate_skill_builder", "stacks_conditional_innate_skill", ConditionalStackInnateSkillBuilder::startToTracking);

        /// V2
        buildConfigTracker("innate_skill_builder", "data_conditional_innate_skill", ConditionalDataInnateSkillBuilder::startToTracking);
        buildConfigTracker("innate_skill_builder", "sequential_innate_skill", SequentialInnateSkillBuilder::startToTracking);
        buildConfigTracker("innate_skill_builder", "per_combo_innate_skill", PerComboInnateSkillBuilder::startToTracking);
        buildConfigTracker("innate_skill_builder", "combo_innate_skill", ComboInnateSkillBuilder::startToTracking);
        buildConfigTracker("innate_skill_builder", "holdable_conditional_innate_skill", HoldableConditionalInnateSkillBuilder::startToTracking);
    }

    private static void startAnimationsBuilds() {
        buildConfigTracker("animations", "registry", AnimationsRegistryBuilder::startToTracking);
        buildConfigTracker("animations", "config", AnimationsConfigBuilder::startToTracking);
        buildConfigTracker("animations", "virtualization", AnimationsVirtualBuilder::startToTracking);
    }
}

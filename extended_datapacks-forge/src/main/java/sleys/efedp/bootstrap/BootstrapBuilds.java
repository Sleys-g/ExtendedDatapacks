package sleys.efedp.bootstrap;

import net.minecraftforge.fml.loading.FMLPaths;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.animations.json.definitions.AnimationsConfigBuilder;
import sleys.efedp.system.animations.json.definitions.AnimationsRegistryBuilder;
import sleys.efedp.system.animations.json.definitions.AnimationsVirtualBuilder;
import sleys.efedp.system.innates.json.ConditionalInnateSkillBuilder;
import sleys.efedp.system.innates.json.HoldableInnateSkillBuilder;
import sleys.efedp.system.innates.json.SimpleInnateSkillBuilder;
import sleys.efedp.system.innates.json.StacksConditionalInnateSkillBuilder;
import sleys.efedp.system.skills.json.GuardSkillBuilderModifier;
import sleys.efedp.system.skills.json.PassiveSkillBuilderModifier;
import sleys.efedp.system.skills.json.SkillIconBuilderModifier;
import sleys.efedp.system.thirdparty.combatevolution.json.ExecutionAnimationBuilderModifier;
import sleys.efedp.system.weapons.json.WeaponCategoryAdder;
import sleys.sl.library.util.io.BuildPathOrFile;

import java.nio.file.Path;
import java.util.function.Consumer;

public class BootstrapBuilds {

    protected static void Initialize() {
        ExtendedDatapacks.LOGGER.info("[Extended Datapacks - Bootstrap] Initializing Builds & start To Tracking...");
        startSkillBuilds();
        startInnateSkillsBuilds();
        startAnimationsBuilds();
        startThirdPartyBuilds();
    }

    private static void buildConfigTracker(String category, String subfolder, Consumer<Path> tracker) {
        Path dir = FMLPaths.CONFIGDIR.get()
                .resolve("epicfight_edp")
                .resolve(category)
                .resolve(subfolder);
        BuildPathOrFile.buildPathFolder(dir);
        tracker.accept(dir);
    }

    @SuppressWarnings("all")
    private static void buildThirdPartyConfigTracker(String category, String subfolder, Consumer<Path> tracker) {
        Path dir = FMLPaths.CONFIGDIR.get()
                .resolve("epicfight_edp")
                .resolve("third_party")
                .resolve(category)
                .resolve(subfolder);
        BuildPathOrFile.buildPathFolder(dir);
        tracker.accept(dir);
    }

    private static void startSkillBuilds() {
        buildConfigTracker("skill_builder", "passive_skills", PassiveSkillBuilderModifier::startToTracking);
        buildConfigTracker("skill_builder", "guard_skills", GuardSkillBuilderModifier::startToTracking);
        buildConfigTracker("skill_builder", "category_icon", SkillIconBuilderModifier::startToTracking);
        buildConfigTracker("weapon_builder", "category", WeaponCategoryAdder::startToTracking);
    }

    private static void startInnateSkillsBuilds() {
        buildConfigTracker("innate_skill_builder", "simple_innate_skill", SimpleInnateSkillBuilder::startToTracking);
        buildConfigTracker("innate_skill_builder", "conditional_innate_skill", ConditionalInnateSkillBuilder::startToTracking);
        buildConfigTracker("innate_skill_builder", "holdable_innate_skill", HoldableInnateSkillBuilder::startToTracking);
        buildConfigTracker("innate_skill_builder", "stacks_conditional_innate_skill", StacksConditionalInnateSkillBuilder::startToTracking);
    }

    private static void startAnimationsBuilds() {
        buildConfigTracker("animations", "registry", AnimationsRegistryBuilder::startToTracking);
        buildConfigTracker("animations", "config", AnimationsConfigBuilder::startToTracking);
        buildConfigTracker("animations", "virtualization", AnimationsVirtualBuilder::startToTracking);
    }

    private static void startThirdPartyBuilds() {
        buildThirdPartyConfigTracker("combat_evolution", "execution", ExecutionAnimationBuilderModifier::startToTracking);
    }
}

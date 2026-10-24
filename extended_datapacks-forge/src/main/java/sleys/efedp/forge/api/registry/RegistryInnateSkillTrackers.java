package sleys.efedp.forge.api.registry;

import net.minecraftforge.fml.loading.FMLPaths;
import sleys.efedp.forge.system.skills.weapon.innate.definitions.*;
import sleys.sl.library.util.io.BuildPathOrFile;

import java.nio.file.Path;

public final class RegistryInnateSkillTrackers {

    public static final GenericRegistryBuilder<SimpleInnateSkillDefinition> SIMPLE_INNATE_SKILL =
            new GenericRegistryBuilder<>(
                    SimpleInnateSkillDefinition.CODEC.codec(),
                    GenericRegistryBuilder.BuilderContext.WITH_MOD_ID,
                    "innate_skill_builder/simple_innate_skill",
                    "Simple Innate Skills",
                    getConfigInnateSkillTracker("simple_innate_skill")
            );

    public static final GenericRegistryBuilder<HoldableInnateSkillDefinition> HOLDABLE_INNATE_SKILL =
            new GenericRegistryBuilder<>(
                    HoldableInnateSkillDefinition.CODEC.codec(),
                    GenericRegistryBuilder.BuilderContext.WITH_MOD_ID,
                    "innate_skill_builder/holdable_innate_skill",
                    "Holdable Innate Skills",
                    getConfigInnateSkillTracker("holdable_innate_skill")
            );

    public static final GenericRegistryBuilder<ConditionalInnateSkillDefinition> CONDITIONAL_INNATE_SKILL =
            new GenericRegistryBuilder<>(
                    ConditionalInnateSkillDefinition.CODEC.codec(),
                    GenericRegistryBuilder.BuilderContext.WITH_MOD_ID,
                    "innate_skill_builder/conditional_innate_skill",
                    "Conditional Innate Skills",
                    getConfigInnateSkillTracker("conditional_innate_skill")
            );

    public static final GenericRegistryBuilder<ConditionalStackInnateSkillDefinition> STACKS_CONDITIONAL_INNATE_SKILL =
            new GenericRegistryBuilder<>(
                    ConditionalStackInnateSkillDefinition.CODEC.codec(),
                    GenericRegistryBuilder.BuilderContext.WITH_MOD_ID,
                    "innate_skill_builder/stacks_conditional_innate_skill",
                    "Stacks Conditional Innate Skills",
                    getConfigInnateSkillTracker("stacks_conditional_innate_skill")
            );

    public static final GenericRegistryBuilder<ConditionalDataInnateSkillDefinition> DATA_CONDITIONAL_INNATE_SKILL =
            new GenericRegistryBuilder<>(
                    ConditionalDataInnateSkillDefinition.CODEC.codec(),
                    GenericRegistryBuilder.BuilderContext.WITH_MOD_ID,
                    "innate_skill_builder/data_conditional_innate_skill",
                    "Data Conditional Innate Skills",
                    getConfigInnateSkillTracker("data_conditional_innate_skill")
            );

    public static final GenericRegistryBuilder<SequentialInnateSkillDefinition> SEQUENTIAL_INNATE_SKILL =
            new GenericRegistryBuilder<>(
                    SequentialInnateSkillDefinition.CODEC.codec(),
                    GenericRegistryBuilder.BuilderContext.WITH_MOD_ID,
                    "innate_skill_builder/sequential_innate_skill",
                    "Sequential Innate Skills",
                    getConfigInnateSkillTracker("sequential_innate_skill")
            );

    public static final GenericRegistryBuilder<PerComboInnateSkillDefinition> PER_COMBO_INNATE_SKILL =
            new GenericRegistryBuilder<>(
                    PerComboInnateSkillDefinition.CODEC.codec(),
                    GenericRegistryBuilder.BuilderContext.WITH_MOD_ID,
                    "innate_skill_builder/per_combo_innate_skill",
                    "Per Combo Innate Skills",
                    getConfigInnateSkillTracker("per_combo_innate_skill")
            );

    public static final GenericRegistryBuilder<ComboInnateSkillDefinition> COMBO_INNATE_SKILL =
            new GenericRegistryBuilder<>(
                    ComboInnateSkillDefinition.CODEC.codec(),
                    GenericRegistryBuilder.BuilderContext.WITH_MOD_ID,
                    "innate_skill_builder/combo_innate_skill",
                    "Combo Innate Skills",
                    getConfigInnateSkillTracker("combo_innate_skill")
            );

    public static final GenericRegistryBuilder<HoldableConditionalInnateSkillDefinition> HOLDABLE_CONDITIONAL_INNATE_SKILL =
            new GenericRegistryBuilder<>(
                    HoldableConditionalInnateSkillDefinition.CODEC.codec(),
                    GenericRegistryBuilder.BuilderContext.WITH_MOD_ID,
                    "innate_skill_builder/holdable_conditional_innate_skill",
                    "Holdable Conditional Innate Skills",
                    getConfigInnateSkillTracker("holdable_conditional_innate_skill")
            );

    public static final GenericRegistryBuilder<TimedInnateSkillDefinition> TIMED_INNATE_SKILL =
            new GenericRegistryBuilder<>(
                    TimedInnateSkillDefinition.CODEC.codec(),
                    GenericRegistryBuilder.BuilderContext.WITH_MOD_ID,
                    "innate_skill_builder/timed_innate_skill",
                    "Timed Innate Skills",
                    getConfigInnateSkillTracker("timed_innate_skill")
            );

    public static final GenericRegistryBuilder<TimedAdvancedInnateSkillDefinition> TIMED_ADVANCED_INNATE_SKILL =
            new GenericRegistryBuilder<>(
                    TimedAdvancedInnateSkillDefinition.CODEC.codec(),
                    GenericRegistryBuilder.BuilderContext.WITH_MOD_ID,
                    "innate_skill_builder/timed_advanced_innate_skill",
                    "Timed Status Innate Skills",
                    getConfigInnateSkillTracker("timed_advanced_innate_skill")
            );

    private static Path getConfigInnateSkillTracker(String subfolder) {
        Path dir = FMLPaths.CONFIGDIR.get()
                .resolve("epicfight_edp")
                .resolve("innate_skill_builder")
                .resolve(subfolder);
        BuildPathOrFile.buildPathFolder(dir);
        return dir;
    }
}

package sleys.efedp.neoforge.api.registry;

import net.neoforged.fml.loading.FMLPaths;
import sleys.efedp.neoforge.ExtendedDatapacks;
import sleys.efedp.neoforge.system.skills.weapon.innate.definitions.*;
import sleys.sl.library.util.data.codec.CodecRegistryBuilder;
import sleys.sl.library.util.io.BuildPathOrFile;

import java.nio.file.Path;

public final class RegistryInnateSkillTrackers {

    public static final CodecRegistryBuilder<SimpleInnateSkillDefinition> SIMPLE_INNATE_SKILL =
            new CodecRegistryBuilder<>(
                    ExtendedDatapacks.LOGGER,
                    SimpleInnateSkillDefinition.CODEC.codec(),
                    CodecRegistryBuilder.BuilderContext.WITH_MOD_ID,
                    "innate_skill_builder/simple_innate_skill",
                    "Simple Innate Skills",
                    getConfigInnateSkillTracker("simple_innate_skill")
            );

    public static final CodecRegistryBuilder<HoldableInnateSkillDefinition> HOLDABLE_INNATE_SKILL =
            new CodecRegistryBuilder<>(
                    ExtendedDatapacks.LOGGER,
                    HoldableInnateSkillDefinition.CODEC.codec(),
                    CodecRegistryBuilder.BuilderContext.WITH_MOD_ID,
                    "innate_skill_builder/holdable_innate_skill",
                    "Holdable Innate Skills",
                    getConfigInnateSkillTracker("holdable_innate_skill")
            );

    public static final CodecRegistryBuilder<ConditionalInnateSkillDefinition> CONDITIONAL_INNATE_SKILL =
            new CodecRegistryBuilder<>(
                    ExtendedDatapacks.LOGGER,
                    ConditionalInnateSkillDefinition.CODEC.codec(),
                    CodecRegistryBuilder.BuilderContext.WITH_MOD_ID,
                    "innate_skill_builder/conditional_innate_skill",
                    "Conditional Innate Skills",
                    getConfigInnateSkillTracker("conditional_innate_skill")
            );

    public static final CodecRegistryBuilder<ConditionalStackInnateSkillDefinition> STACKS_CONDITIONAL_INNATE_SKILL =
            new CodecRegistryBuilder<>(
                    ExtendedDatapacks.LOGGER,
                    ConditionalStackInnateSkillDefinition.CODEC.codec(),
                    CodecRegistryBuilder.BuilderContext.WITH_MOD_ID,
                    "innate_skill_builder/stacks_conditional_innate_skill",
                    "Stacks Conditional Innate Skills",
                    getConfigInnateSkillTracker("stacks_conditional_innate_skill")
            );

    public static final CodecRegistryBuilder<ConditionalDataInnateSkillDefinition> DATA_CONDITIONAL_INNATE_SKILL =
            new CodecRegistryBuilder<>(
                    ExtendedDatapacks.LOGGER,
                    ConditionalDataInnateSkillDefinition.CODEC.codec(),
                    CodecRegistryBuilder.BuilderContext.WITH_MOD_ID,
                    "innate_skill_builder/data_conditional_innate_skill",
                    "Data Conditional Innate Skills",
                    getConfigInnateSkillTracker("data_conditional_innate_skill")
            );

    public static final CodecRegistryBuilder<SequentialInnateSkillDefinition> SEQUENTIAL_INNATE_SKILL =
            new CodecRegistryBuilder<>(
                    ExtendedDatapacks.LOGGER,
                    SequentialInnateSkillDefinition.CODEC.codec(),
                    CodecRegistryBuilder.BuilderContext.WITH_MOD_ID,
                    "innate_skill_builder/sequential_innate_skill",
                    "Sequential Innate Skills",
                    getConfigInnateSkillTracker("sequential_innate_skill")
            );

    public static final CodecRegistryBuilder<PerComboInnateSkillDefinition> PER_COMBO_INNATE_SKILL =
            new CodecRegistryBuilder<>(
                    ExtendedDatapacks.LOGGER,
                    PerComboInnateSkillDefinition.CODEC.codec(),
                    CodecRegistryBuilder.BuilderContext.WITH_MOD_ID,
                    "innate_skill_builder/per_combo_innate_skill",
                    "Per Combo Innate Skills",
                    getConfigInnateSkillTracker("per_combo_innate_skill")
            );

    public static final CodecRegistryBuilder<ComboInnateSkillDefinition> COMBO_INNATE_SKILL =
            new CodecRegistryBuilder<>(
                    ExtendedDatapacks.LOGGER,
                    ComboInnateSkillDefinition.CODEC.codec(),
                    CodecRegistryBuilder.BuilderContext.WITH_MOD_ID,
                    "innate_skill_builder/combo_innate_skill",
                    "Combo Innate Skills",
                    getConfigInnateSkillTracker("combo_innate_skill")
            );

    public static final CodecRegistryBuilder<HoldableConditionalInnateSkillDefinition> HOLDABLE_CONDITIONAL_INNATE_SKILL =
            new CodecRegistryBuilder<>(
                    ExtendedDatapacks.LOGGER,
                    HoldableConditionalInnateSkillDefinition.CODEC.codec(),
                    CodecRegistryBuilder.BuilderContext.WITH_MOD_ID,
                    "innate_skill_builder/holdable_conditional_innate_skill",
                    "Holdable Conditional Innate Skills",
                    getConfigInnateSkillTracker("holdable_conditional_innate_skill")
            );

    public static final CodecRegistryBuilder<TimedInnateSkillDefinition> TIMED_INNATE_SKILL =
            new CodecRegistryBuilder<>(
                    ExtendedDatapacks.LOGGER,
                    TimedInnateSkillDefinition.CODEC.codec(),
                    CodecRegistryBuilder.BuilderContext.WITH_MOD_ID,
                    "innate_skill_builder/timed_innate_skill",
                    "Timed Innate Skills",
                    getConfigInnateSkillTracker("timed_innate_skill")
            );

    public static final CodecRegistryBuilder<TimedAdvancedInnateSkillDefinition> TIMED_ADVANCED_INNATE_SKILL =
            new CodecRegistryBuilder<>(
                    ExtendedDatapacks.LOGGER,
                    TimedAdvancedInnateSkillDefinition.CODEC.codec(),
                    CodecRegistryBuilder.BuilderContext.WITH_MOD_ID,
                    "innate_skill_builder/timed_advanced_innate_skill",
                    "Timed Adbvanced Innate Skills",
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

package sleys.efedp.neoforge.api.registry;

import net.neoforged.fml.loading.FMLPaths;
import sleys.efedp.neoforge.ExtendedDatapacks;
import sleys.efedp.neoforge.system.skills.guard.definitions.GuardSkillModifierDefinitions;
import sleys.efedp.neoforge.system.skills.icon.definitions.IconSkillModifierDefinitions;
import sleys.efedp.neoforge.system.skills.passive.definitions.PassiveSkillModifierDefinitions;
import sleys.sl.library.util.data.codec.CodecRegistryBuilder;
import sleys.sl.library.util.io.BuildPathOrFile;

import java.nio.file.Path;

public final class RegistrySkillTrackers {

    public static final CodecRegistryBuilder<PassiveSkillModifierDefinitions> PASSIVE_SKILL_MODIFIER =
            new CodecRegistryBuilder<>(
                    ExtendedDatapacks.LOGGER,
                    PassiveSkillModifierDefinitions.CODEC,
                    CodecRegistryBuilder.BuilderContext.WITHOUT_MOD_ID,
                    "skill_builder/passive_skills",
                    "Passive Skill Adder",
                    getConfigSkillTracker("passive_skills")
            );

    public static final CodecRegistryBuilder<GuardSkillModifierDefinitions> GUARD_SKILL_MODIFIER =
            new CodecRegistryBuilder<>(
                    ExtendedDatapacks.LOGGER,
                    GuardSkillModifierDefinitions.CODEC,
                    CodecRegistryBuilder.BuilderContext.WITHOUT_MOD_ID,
                    "skill_builder/guard_skills",
                    "Guard Skill Adder",
                    getConfigSkillTracker("guard_skills")
            );

    public static final CodecRegistryBuilder<IconSkillModifierDefinitions> CATEGORY_SKILL_ICON =
            new CodecRegistryBuilder<>(
                    ExtendedDatapacks.LOGGER,
                    IconSkillModifierDefinitions.CODEC,
                    CodecRegistryBuilder.BuilderContext.WITHOUT_MOD_ID,
                    "skill_builder/category_icon",
                    "Category Icon Adder",
                    getConfigSkillTracker("category_icon")
            );

    private static Path getConfigSkillTracker(String subfolder) {
        Path dir = FMLPaths.CONFIGDIR.get()
                .resolve("epicfight_edp")
                .resolve("skill_builder")
                .resolve(subfolder);
        BuildPathOrFile.buildPathFolder(dir);
        return dir;
    }
}

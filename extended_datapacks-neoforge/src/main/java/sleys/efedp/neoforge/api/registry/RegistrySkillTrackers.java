package sleys.efedp.neoforge.api.registry;

import net.neoforged.fml.loading.FMLPaths;
import sleys.efedp.neoforge.system.skills.passive.definitions.PassiveSkillModifierDefinitions;
import sleys.sl.library.util.io.BuildPathOrFile;

import java.nio.file.Path;

public final class RegistrySkillTrackers {

    public static final GenericRegistryBuilder<PassiveSkillModifierDefinitions> PASSIVE_SKILL_MODIFIER = new GenericRegistryBuilder<>(
            PassiveSkillModifierDefinitions.CODEC,
            GenericRegistryBuilder.BuilderContext.WITHOUT_MOD_ID,
            "skill_builder/passive_skills",
            "Passive Skill Adder",
            getConfigSkillTracker("passive_skills")
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

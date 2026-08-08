package sleys.efedp.system.innates;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.innates.json.builder.SimpleInnateSkillBuilder;
import sleys.efedp.system.innates.json.builder.helper.AnimationBuilderHelper;
import sleys.efedp.system.innates.json.builder.helper.RegistryErrorHelper;
import sleys.efedp.system.innates.json.builder.wrapper.simple.WSimpleInnateSkill;
import sleys.efedp.system.innates.json.definitions.SimpleInnateSkillDefinition;
import sleys.sl.library.exceptions.RegistryObjectException;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;

import java.util.*;

public class SimpleInnateSkillsRegistry {
    private static final List<String> RUNTIME_ERRORS = Collections.synchronizedList(new ArrayList<>());

    private SimpleInnateSkillsRegistry() {}

    @SubscribeEvent
    public static void initialize(SkillBuildEvent build) {
        ExtendedDatapacks.LOGGER.info("[Simple Innate Skill Registry] Registering JSON skills");

        var data = SimpleInnateSkillBuilder.getSimpleInnateSkillBuildData();
        if (data.isEmpty()) {
            ExtendedDatapacks.LOGGER.info("[Simple Innate Skill Registry] No JSON skills found");
            return;
        }

        data.forEach((modId, skills) -> {
            var modRegistry = build.createRegistryWorker(modId);
            skills.forEach(skillData -> registerSkill(build, modRegistry, modId, skillData));
        });
    }

    private static void registerSkill(SkillBuildEvent build, SkillBuildEvent.ModRegistryWorker modRegistry,
                                      String modId, SimpleInnateSkillDefinition skillData) {
        var name = skillData.name();
        var animationId = ResourceLocation.tryParse(skillData.animation());

        if (animationId == null) {
            RUNTIME_ERRORS.add(RegistryErrorHelper.getError(
                    RegistryErrorHelper.ErrorsType.UNPARSEABLE, name, modId, skillData.animation(), null));
            return;
        }

        try {
            buildSkill(modRegistry, modId, name, animationId, skillData);
        } catch (Exception e) {
            RegistryErrorHelper.handleRegistrationError(build, modId, name, animationId, RUNTIME_ERRORS, e);
        }
    }

    private static void buildSkill(SkillBuildEvent.ModRegistryWorker modRegistry,
                                   String modId, String name, ResourceLocation animationId,
                                   SimpleInnateSkillDefinition skillData) {
        var attackAnimationKey = AnimationBuilderHelper.resolveAnimation(
                modId, name, animationId, RUNTIME_ERRORS
        );
        if (attackAnimationKey == null) return;

        var builder = skillData.createBuilder(attackAnimationKey);
        WSimpleInnateSkill skill = modRegistry.build(name, WSimpleInnateSkill::new,  builder);
        skillData.applyProperties(skill);

        ExtendedDatapacks.LOGGER.info(
                "[Simple Innate Skill Registry] Registration process completed for Skill: {} signed under modID: {} for animation: {}",
                name, modId, attackAnimationKey
        );
    }

    @SubscribeEvent
    public static void onClientModBusEvent(final FMLLoadCompleteEvent event) {
        if (!RUNTIME_ERRORS.isEmpty()) {
            throw new RegistryObjectException(
                    "Failure during the operation to create a Simple Innate Skill...\n" +
                            "Total number of registry failures: " + RUNTIME_ERRORS.size() +
                            "\n\nProblematic Skills\n\n" + String.join("\n", RUNTIME_ERRORS));
        }
    }
}
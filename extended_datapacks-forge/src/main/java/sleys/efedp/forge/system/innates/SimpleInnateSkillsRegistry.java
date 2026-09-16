package sleys.efedp.forge.system.innates;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import sleys.efedp.forge.ExtendedDatapacks;
import sleys.efedp.forge.system.innates.json.builder.SimpleInnateSkillBuilder;
import sleys.efedp.forge.system.innates.json.builder.helper.AnimationBuilderHelper;
import sleys.efedp.forge.system.innates.json.builder.helper.RegistryErrorHelper;
import sleys.efedp.forge.system.innates.json.builder.wrapper.simple.WSimpleInnateSkill;
import sleys.efedp.forge.system.innates.json.definitions.SimpleInnateSkillDefinition;
import sleys.sl.library.exceptions.RegistryObjectException;
import sleys.sl.library.execution.policy.ErrorPolicy;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
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

        ExecutionTasks.runAndGetResult(
                        ExecutionPolicy.RESIST,
                        ErrorPolicy.DEPURATE,
                        "[Simple Innate Skill Registry] Registering Skill '{" + name  +"}'",
                        () -> buildSkill(modRegistry, modId, name, animationId, skillData)
                )
                .peek(process ->  ExtendedDatapacks.LOGGER.info(
                        "[Simple Innate Skill Registry] Registered Skill: {} under modID: {}", name, modId
                ))
                .peekError(exception -> ExtendedDatapacks.LOGGER.fatal(
                        "[Simple Innate Skill Registry] Error Stack: ", exception
                ))
                .ifFailure(exception -> RegistryErrorHelper.handleRegistrationError(
                        build, modId, name, animationId, RUNTIME_ERRORS, exception
                ));
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
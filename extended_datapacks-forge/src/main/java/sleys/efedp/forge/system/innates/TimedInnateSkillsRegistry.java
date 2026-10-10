package sleys.efedp.forge.system.innates;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import sleys.efedp.forge.ExtendedDatapacks;
import sleys.efedp.forge.system.innates.json.builder.TimedInnateSkillBuilder;
import sleys.efedp.forge.system.innates.json.builder.helper.AnimationBuilderHelper;
import sleys.efedp.forge.system.innates.json.builder.helper.RegistryErrorHelper;
import sleys.efedp.forge.system.innates.json.builder.wrapper.timed.WTimedInnateSkill;
import sleys.efedp.forge.system.innates.json.definitions.TimedInnateSkillDefinition;
import sleys.sl.library.exceptions.RegistryObjectException;
import sleys.sl.library.execution.policy.ErrorPolicy;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimedInnateSkillsRegistry {
    private static final List<String> RUNTIME_ERRORS = Collections.synchronizedList(new ArrayList<>());

    private TimedInnateSkillsRegistry() {}

    @SubscribeEvent
    public static void initialize(SkillBuildEvent build) {
        ExtendedDatapacks.LOGGER.info("[Timed Innate Skill Registry] Registering JSON skills");

        var data = TimedInnateSkillBuilder.getTimedInnateSkillBuildData();
        if (data.isEmpty()) {
            ExtendedDatapacks.LOGGER.info("[Timed Innate Skill Registry] No JSON skills found");
            return;
        }

        data.forEach((modId, skills) -> {
            var modRegistry = build.createRegistryWorker(modId);
            skills.forEach(skillData -> registerSkill(build, modRegistry, modId, skillData));
        });
    }

    private static void registerSkill(SkillBuildEvent build, SkillBuildEvent.ModRegistryWorker modRegistry,
                                      String modId, TimedInnateSkillDefinition skillData) {
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
                        "[Timed Innate Skill Registry] Registering Skill '{" + name  +"}'",
                        () -> buildSkill(modRegistry, modId, name, animationId, skillData)
                )
                .peek(process ->  ExtendedDatapacks.LOGGER.info(
                        "[Timed Innate Skill Registry] Registered Skill: {} under modID: {}", name, modId
                ))
                .peekError(exception -> ExtendedDatapacks.LOGGER.fatal(
                        "[Timed Innate Skill Registry] Error Stack: ", exception
                ))
                .ifFailure(exception -> RegistryErrorHelper.handleRegistrationError(
                        build, modId, name, animationId, RUNTIME_ERRORS, exception
                ));
    }

    private static void buildSkill(SkillBuildEvent.ModRegistryWorker modRegistry,
                                   String modId, String name, ResourceLocation animationId,
                                   TimedInnateSkillDefinition skillData) {
        var attackAnimationKey = AnimationBuilderHelper.resolveAnimation(
                modId, name, animationId, RUNTIME_ERRORS
        );
        if (attackAnimationKey == null) return;

        var builder = skillData.createBuilder(attackAnimationKey);
        var skill = modRegistry.build(name, WTimedInnateSkill::new,  builder);
        skillData.applyProperties(skill);

        ExtendedDatapacks.LOGGER.info(
                "[Timed Innate Skill Registry] Registration process completed for Skill: {} signed under modID: {} for animation: {}",
                name, modId, attackAnimationKey
        );
    }

    @SubscribeEvent
    public static void onClientModBusEvent(final FMLLoadCompleteEvent event) {
        if (!RUNTIME_ERRORS.isEmpty()) {
            throw new RegistryObjectException(
                    "Failure during the operation to create a Timed Innate Skill...\n" +
                            "Total number of registry failures: " + RUNTIME_ERRORS.size() +
                            "\n\nProblematic Skills\n\n" + String.join("\n", RUNTIME_ERRORS));
        }
    }
}
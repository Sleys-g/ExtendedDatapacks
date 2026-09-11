package sleys.efedp.system.innates;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.innates.json.builder.SequentialInnateSkillBuilder;
import sleys.efedp.system.innates.json.builder.helper.AnimationBuilderHelper;
import sleys.efedp.system.innates.json.builder.helper.FriendlyCountConverter;
import sleys.efedp.system.innates.json.builder.helper.RegistryErrorHelper;
import sleys.efedp.system.innates.json.builder.values.AnimationSkillValues;
import sleys.efedp.system.innates.json.builder.wrapper.sequential.WSequentialInnateSkill;
import sleys.efedp.system.innates.json.definitions.SequentialInnateSkillDefinition;
import sleys.sl.library.exceptions.RegistryObjectException;
import sleys.sl.library.execution.policy.ErrorPolicy;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;

import java.util.*;

public class SequentialInnateSkillsRegistry {
    private static final List<String> RUNTIME_ERRORS = Collections.synchronizedList(new ArrayList<>());

    private SequentialInnateSkillsRegistry() {}

    @SubscribeEvent
    public static void initialize(SkillBuildEvent build) {
        ExtendedDatapacks.LOGGER.info("[Sequential Innate Skill Registry] Registering JSON skills");

        var data = SequentialInnateSkillBuilder.getSequentialInnateSkillBuildData();
        if (data.isEmpty()) {
            ExtendedDatapacks.LOGGER.info("[Sequential Innate Skill Registry] No JSON skills found");
            return;
        }

        data.forEach((modId, skills) -> {
            var modRegistry = build.createRegistryWorker(modId);
            skills.forEach(skillData -> registerSkill(build, modRegistry, modId, skillData));
        });
    }

    private static void registerSkill(SkillBuildEvent build, SkillBuildEvent.ModRegistryWorker modRegistry,
                                      String modId, SequentialInnateSkillDefinition skillData) {
        var name = skillData.name();
        ExecutionTasks.runAndGetResult(
                        ExecutionPolicy.RESIST,
                        ErrorPolicy.DEPURATE,
                        "[Sequential Innate Skill Registry] Registering Skill '{" + name  +"}'",
                        () -> buildSkill(modRegistry, modId, name, skillData)
                )
                .peek(process ->  ExtendedDatapacks.LOGGER.info(
                        "[Sequential Innate Skill Registry] Registered Skill: {} under modID: {}", name, modId
                ))
                .peekError(exception -> ExtendedDatapacks.LOGGER.fatal(
                        "[Sequential Innate Skill Registry] Error Stack: ", exception
                ))
                .ifFailure(exception -> RegistryErrorHelper.handleRegistrationError(
                        build, modId, name, skillData.sequentialAnimationData(), RUNTIME_ERRORS, exception
                ));
    }

    private static void buildSkill(SkillBuildEvent.ModRegistryWorker modRegistry,
                                   String modId, String name, SequentialInnateSkillDefinition skillData) {
        var builder = skillData.createBuilder();
        var animationDataList = skillData.sequentialAnimationData();

        for (int i = 0; i < animationDataList.size(); i++) {
            var animationData = animationDataList.get(i);
            var animation = animationData.animation();
            var properties = animationData.properties();

            var animationId = ResourceLocation.tryParse(animation);
            if (animationId == null) {
                RUNTIME_ERRORS.add(RegistryErrorHelper.getError(
                        RegistryErrorHelper.ErrorsType.UNPARSEABLE,
                        name, modId, animation, null)
                );

                return;
            }

            var postfix = FriendlyCountConverter.as(i);
            var attackAnimationKey = postfix != null ?
                    AnimationBuilderHelper.resolveAnimation(modId, name, postfix, animationId, RUNTIME_ERRORS) :
                    AnimationBuilderHelper.resolveAnimation(modId, name, animationId, RUNTIME_ERRORS);

            if (attackAnimationKey == null) return;

            var animationProperties = skillData.saveProperties(properties);
            var animationSkillValues = new AnimationSkillValues(attackAnimationKey, animationProperties);
            builder.putAnimationData(animationSkillValues);
        }

        modRegistry.build(name, WSequentialInnateSkill::new,  builder);
    }


    @SubscribeEvent
    public static void onClientModBusEvent(final FMLLoadCompleteEvent event) {
        if (!RUNTIME_ERRORS.isEmpty()) {
            throw new RegistryObjectException(
                    "Failure during the operation to create a Conditional Innate Skill...\n" +
                            "Total number of registry failures: " + RUNTIME_ERRORS.size() +
                            "\n\nProblematic Skills\n\n" + String.join("\n", RUNTIME_ERRORS));
        }
    }
}
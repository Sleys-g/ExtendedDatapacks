package sleys.efedp.system.innates;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.innates.json.builder.wrapper.conditional.WConditionalInnateSkill;
import sleys.efedp.system.innates.json.builder.values.AnimationSkillValues;
import sleys.efedp.system.innates.json.builder.data.ConditionalType;
import sleys.efedp.system.innates.json.builder.ConditionalInnateSkillBuilder;
import sleys.efedp.system.innates.json.builder.helper.AnimationBuilderHelper;
import sleys.efedp.system.innates.json.builder.helper.RegistryErrorHelper;
import sleys.efedp.system.innates.json.definitions.ConditionalInnateSkillDefinition;
import sleys.sl.library.exceptions.RegistryObjectException;
import sleys.sl.library.execution.policy.ErrorPolicy;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConditionalInnateSkillsRegistry {
    private static final List<String> RUNTIME_ERRORS = Collections.synchronizedList(new ArrayList<>());

    private ConditionalInnateSkillsRegistry() {}

    @SubscribeEvent
    public static void initialize(SkillBuildEvent build) {
        ExtendedDatapacks.LOGGER.info("[Conditional Innate Skill Registry] Registering JSON skills");

        var data = ConditionalInnateSkillBuilder.getConditionalInnateSkillBuildData();
        if (data.isEmpty()) {
            ExtendedDatapacks.LOGGER.info("[Conditional Innate Skill Registry] No JSON skills found");
            return;
        }

        data.forEach((modId, skills) -> {
            var modRegistry = build.createRegistryWorker(modId);
            skills.forEach(skillData -> registerSkill(build, modRegistry, modId, skillData));
        });
    }

    private static void registerSkill(SkillBuildEvent build, SkillBuildEvent.ModRegistryWorker modRegistry,
                                      String modId, ConditionalInnateSkillDefinition skillData) {
        var name = skillData.name();
        ExecutionTasks.runAndGetResult(
                        ExecutionPolicy.RESIST,
                        ErrorPolicy.DEPURATE,
                        "[Conditional Innate Skill Registry] Registering Skill '{" + name  +"}'",
                        () -> buildSkill(modRegistry, modId, name, skillData)
                )
                .peek(process ->  ExtendedDatapacks.LOGGER.info(
                        "[Conditional Innate Skill Registry] Registered Skill: {} under modID: {}", name, modId
                ))
                .peekError(exception -> ExtendedDatapacks.LOGGER.fatal(
                        "[Conditional Innate Skill Registry] Error Stack: ", exception
                ))
                .ifFailure(exception -> RegistryErrorHelper.handleRegistrationError(
                        build, modId, name, skillData.conditionalAnimationData(), RUNTIME_ERRORS, exception
                ));
    }

    private static void buildSkill(SkillBuildEvent.ModRegistryWorker modRegistry,
                                   String modId, String name,
                                   ConditionalInnateSkillDefinition skillData) {
        var builder = skillData.createBuilder();
        boolean hasNormalCondition = false;
        for (var conditionEntry : skillData.conditionalAnimationData().entrySet()) {
            var conditionalTypes = conditionEntry.getKey();
            var conditionalAnimationData = conditionEntry.getValue();
            var animation = conditionalAnimationData.animation();


            var animationId = ResourceLocation.tryParse(animation);
            if (animationId == null) {
                RUNTIME_ERRORS.add(RegistryErrorHelper.getError(
                        RegistryErrorHelper.ErrorsType.UNPARSEABLE,
                        name, modId, animation, null)
                );
                return;
            }

            var attackAnimationKey = AnimationBuilderHelper.resolveAnimation(
                    modId, name, conditionalTypes, animationId, RUNTIME_ERRORS
            );
            if (attackAnimationKey == null) return;


            if (conditionalTypes == ConditionalType.NORMAL) hasNormalCondition = true;
            var conditionalProperties = skillData.saveProperties(conditionalAnimationData.properties());
            var animationSkillValues = new AnimationSkillValues(attackAnimationKey, conditionalProperties);
            builder.putConditionData(conditionalTypes, animationSkillValues);
        }

        if (!hasNormalCondition) {
            RUNTIME_ERRORS.add(
                    RegistryErrorHelper.getError(
                            RegistryErrorHelper.ErrorsType.REGISTRY_BUILDER,
                            name, modId, null,
                            "Missing NORMAL predicate."
                    )
            );
            return;
        }

        modRegistry.build(name, WConditionalInnateSkill::new,  builder);
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
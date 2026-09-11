package sleys.efedp.system.innates;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.innates.json.builder.ConditionalDataInnateSkillBuilder;
import sleys.efedp.system.innates.json.builder.data.ConditionalDataAnimationData;
import sleys.efedp.system.innates.json.builder.helper.AnimationBuilderHelper;
import sleys.efedp.system.innates.json.builder.helper.FriendlyCountConverter;
import sleys.efedp.system.innates.json.builder.helper.RegistryErrorHelper;
import sleys.efedp.system.innates.json.builder.values.AnimationSkillValues;
import sleys.efedp.system.innates.json.builder.data.ConditionalType;
import sleys.efedp.system.innates.json.builder.values.ConditionalDataSkillValues;
import sleys.efedp.system.innates.json.builder.wrapper.conditional.WConditionalDataInnateSkill;
import sleys.efedp.system.innates.json.definitions.ConditionalDataInnateSkillDefinition;
import sleys.sl.library.exceptions.RegistryObjectException;
import sleys.sl.library.execution.policy.ErrorPolicy;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;

import java.util.*;

public class ConditionalDataInnateSkillsRegistry {
    private static final List<String> RUNTIME_ERRORS = Collections.synchronizedList(new ArrayList<>());

    private ConditionalDataInnateSkillsRegistry() {}

    @SubscribeEvent
    public static void initialize(SkillBuildEvent build) {
        ExtendedDatapacks.LOGGER.info("[Data Conditional Innate Skills Registry] Registering JSON skills");

        var data = ConditionalDataInnateSkillBuilder.getDatapacketInnateSkillBuildData();
        if (data.isEmpty()) {
            ExtendedDatapacks.LOGGER.info("[Data Conditional Innate Skills Registry] No JSON skills found");
            return;
        }

        data.forEach((modId, skills) -> {
                    var modRegistry = build.createRegistryWorker(modId);
                    skills.forEach(skillData -> registerSkill(build, modRegistry, modId, skillData));
                }
        );
    }

    private static void registerSkill(SkillBuildEvent build, SkillBuildEvent.ModRegistryWorker modRegistry,
                                      String modId, ConditionalDataInnateSkillDefinition skillData) {
        var name = skillData.name();
        ExecutionTasks.runAndGetResult(
                        ExecutionPolicy.RESIST,
                        ErrorPolicy.DEPURATE,
                        "[Data Conditional Innate Skills Registry] Registering Skill '{" + name  +"}'",
                        () -> buildSkill(modRegistry, modId, name, skillData)
                )
                .peek(process ->  ExtendedDatapacks.LOGGER.info(
                        "[Data Conditional Innate Skills Registry] Registered Skill: {} under modID: {}", name, modId
                ))
                .peekError(exception -> ExtendedDatapacks.LOGGER.fatal(
                        "[Data Conditional Innate Skills Registry] Error Stack: ", exception
                ))
                .ifFailure(exception -> RegistryErrorHelper.handleRegistrationError(
                        build, modId, name, skillData.conditionalAnimationData(), RUNTIME_ERRORS, exception
                ));
    }

    private static void buildSkill(SkillBuildEvent.ModRegistryWorker modRegistry,
                                    String modId, String name,
                                    ConditionalDataInnateSkillDefinition skillData) {
        var builder = skillData.createBuilder();
        boolean hasNormalCondition = false;
        List<ConditionalDataAnimationData> sortedConditionalAnimationData = skillData
                .conditionalAnimationData()
                .stream()
                .sorted(Comparator.comparingInt(data -> data.physicalCondition().ordinal()))
                .toList();

        Map<ConditionalType, Integer> occurrenceCount = new HashMap<>();

        for (var animationData : sortedConditionalAnimationData) {
            var physicalCondition = animationData.physicalCondition();
            var readDataGroups = animationData.readData();
            var properties = animationData.properties();
            var animation = animationData.animation();
            var tooltipHead = animationData.tooltipHead();

            var animationId = ResourceLocation.tryParse(animation);
            if (animationId == null) {
                RUNTIME_ERRORS.add(RegistryErrorHelper.getError(
                        RegistryErrorHelper.ErrorsType.UNPARSEABLE,
                        name, modId, animation, null)
                );

                return;
            }

            int occurrence = occurrenceCount.merge(physicalCondition, 1, Integer::sum) - 1;
            String postFix = FriendlyCountConverter.as(occurrence);

            var attackAnimationKey = AnimationBuilderHelper.resolveAnimation(
                    modId, name, postFix, physicalCondition, animationId, RUNTIME_ERRORS
            );

            if (attackAnimationKey == null) return;
            if (physicalCondition == ConditionalType.NORMAL) hasNormalCondition = true;

            var conditionalProperties = skillData.saveProperties(properties);
            var animationSkillValues = new AnimationSkillValues(attackAnimationKey, conditionalProperties);
            var datapacketSkillValues = new ConditionalDataSkillValues(physicalCondition, tooltipHead, readDataGroups);
            builder.putDatapacketData(animationSkillValues, datapacketSkillValues);
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

        modRegistry.build(name, WConditionalDataInnateSkill::new, builder);
    }

    @SubscribeEvent
    public static void onClientModBusEvent(final FMLLoadCompleteEvent event) {
        if (!RUNTIME_ERRORS.isEmpty()) {
            throw new RegistryObjectException(
                    "Failure during the operation to create a Data Conditional Innate Skill...\n" +
                            "Total number of registry failures: " + RUNTIME_ERRORS.size() +
                            "\n\nProblematic Skills\n\n" + String.join("\n", RUNTIME_ERRORS));
        }
    }
}
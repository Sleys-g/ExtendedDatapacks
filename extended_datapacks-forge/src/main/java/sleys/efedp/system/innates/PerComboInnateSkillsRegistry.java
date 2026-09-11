package sleys.efedp.system.innates;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.innates.json.builder.PerComboInnateSkillBuilder;
import sleys.efedp.system.innates.json.builder.helper.AnimationBuilderHelper;
import sleys.efedp.system.innates.json.builder.helper.FriendlyCountConverter;
import sleys.efedp.system.innates.json.builder.helper.RegistryErrorHelper;
import sleys.efedp.system.innates.json.builder.values.AnimationSkillValues;
import sleys.efedp.system.innates.json.builder.wrapper.combo.WPerComboInnateSkill;
import sleys.efedp.system.innates.json.definitions.PerComboInnateSkillDefinition;
import sleys.sl.library.exceptions.RegistryObjectException;
import sleys.sl.library.execution.policy.ErrorPolicy;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;

import java.util.*;

public class PerComboInnateSkillsRegistry {
    private static final List<String> RUNTIME_ERRORS = Collections.synchronizedList(new ArrayList<>());

    private PerComboInnateSkillsRegistry() {}

    @SubscribeEvent
    public static void initialize(SkillBuildEvent build) {
        ExtendedDatapacks.LOGGER.info("[Per Combo Innate Skill Registry] Registering JSON skills");

        var data = PerComboInnateSkillBuilder.getPerComboInnateSkillBuildData();
        if (data.isEmpty()) {
            ExtendedDatapacks.LOGGER.info("[Per Combo Innate Skill Registry] No JSON skills found");
            return;
        }

        data.forEach((modId, skills) -> {
                    var modRegistry = build.createRegistryWorker(modId);
                    skills.forEach(skillData ->
                            registerSkill(build, modRegistry, modId, skillData)
                    );
                }
        );
    }

    private static void registerSkill(SkillBuildEvent build, SkillBuildEvent.ModRegistryWorker modRegistry,
                                      String modId, PerComboInnateSkillDefinition skillData) {
        var name = skillData.name();
        ExecutionTasks.runAndGetResult(
                        ExecutionPolicy.RESIST,
                        ErrorPolicy.DEPURATE,
                        "[Per Combo Innate Skill Registry] Registering Skill '{" + name  +"}'",
                        () -> buildSkill(modRegistry, modId, name, skillData)
                )
                .peek(process ->  ExtendedDatapacks.LOGGER.info(
                        "[Per Combo Innate Skill Registry] Registered Skill: {} under modID: {}", name, modId
                ))
                .peekError(exception -> ExtendedDatapacks.LOGGER.fatal(
                        "[Per Combo Innate Skill Registry] Error Stack: ", exception
                ))
                .ifFailure(exception -> RegistryErrorHelper.handleRegistrationError(
                        build, modId, name, skillData.perComboAnimationData(), RUNTIME_ERRORS, exception
                ));
    }

    private static void buildSkill(SkillBuildEvent.ModRegistryWorker modRegistry,
                                   String modId, String name,
                                   PerComboInnateSkillDefinition skillData) {
        var builder = skillData.createBuilder();

        var animationDataList = skillData.perComboAnimationData();
        for (int i = 0; i < animationDataList.size(); i++) {
            var animationData = animationDataList.get(i);
            var targetAnimation = animationData.targetAnimation();
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

            var targetAnimationId = ResourceLocation.tryParse(targetAnimation);
            if (targetAnimationId == null) {
                RUNTIME_ERRORS.add(RegistryErrorHelper.getError(
                        RegistryErrorHelper.ErrorsType.UNPARSEABLE,
                        name, modId, targetAnimation, null)
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
            builder.putPerComboAnimationData(AnimationManager.byKey(targetAnimationId), animationSkillValues);
        }

        modRegistry.build(name, WPerComboInnateSkill::new, builder);
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
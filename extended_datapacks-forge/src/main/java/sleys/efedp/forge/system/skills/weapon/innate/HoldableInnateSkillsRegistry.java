package sleys.efedp.forge.system.skills.weapon.innate;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import sleys.efedp.forge.ExtendedDatapacks;
import sleys.efedp.forge.api.registry.RegistryInnateSkillTrackers;
import sleys.efedp.forge.system.skills.weapon.innate.helper.AnimationBuilderHelper;
import sleys.efedp.forge.system.skills.weapon.innate.helper.RegistryErrorHelper;
import sleys.efedp.forge.system.skills.weapon.innate.wrapper.holdable.WHoldableInnateSkill;
import sleys.efedp.forge.system.skills.weapon.innate.definitions.HoldableInnateSkillDefinition;
import sleys.sl.library.exceptions.RegistryObjectException;
import sleys.sl.library.execution.policy.ErrorPolicy;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HoldableInnateSkillsRegistry {
    private static final List<String> RUNTIME_ERRORS = Collections.synchronizedList(new ArrayList<>());

    private HoldableInnateSkillsRegistry() {}

    @SubscribeEvent
    public static void initialize(SkillBuildEvent build) {
        ExtendedDatapacks.LOGGER.info("[Holdable Innate Skill Registry] Registering JSON skills");

        var data = RegistryInnateSkillTrackers.HOLDABLE_INNATE_SKILL.getDefinitionData();
        if (data.isEmpty()) {
            ExtendedDatapacks.LOGGER.info("[Holdable Innate Skill Registry] No JSON skills found");
            return;
        }

        data.forEach((modId, skills) -> {
            var modRegistry = build.createRegistryWorker(modId);
            skills.forEach(skillData -> registerSkill(build, modRegistry, modId, skillData));
        });
    }

    private static void registerSkill(SkillBuildEvent build, SkillBuildEvent.ModRegistryWorker modRegistry,
                                      String modId, HoldableInnateSkillDefinition skillData) {
        var name = skillData.name();
        var animationId = ResourceLocation.tryParse(skillData.animation());
        var chargedAnimationId = ResourceLocation.tryParse(skillData.chargeAnimation());

        if (animationId == null) {
            RUNTIME_ERRORS.add(RegistryErrorHelper.getError(
                    RegistryErrorHelper.ErrorsType.UNPARSEABLE, name, modId, skillData.animation(), null));
            return;
        }

        if (chargedAnimationId == null) {
            RUNTIME_ERRORS.add(RegistryErrorHelper.getError(
                    RegistryErrorHelper.ErrorsType.UNPARSEABLE, name,
                    modId, skillData.chargeAnimation(), null
            ));
            return;
        }

        ExecutionTasks.runAndGetResult(
                        ExecutionPolicy.RESIST,
                        ErrorPolicy.DEPURATE,
                        "[Holdable Innate Skill Registry] Registering Skill '{" + name  +"}'",
                        () -> buildSkill(modRegistry, modId, name, animationId, chargedAnimationId, skillData)
                )
                .peek(process ->  ExtendedDatapacks.LOGGER.info(
                        "[Holdable Innate Skill Registry] Registered Skill: {} under modID: {}", name, modId
                ))
                .peekError(exception -> ExtendedDatapacks.LOGGER.fatal(
                        "[Holdable Innate Skill Registry] Error Stack: ", exception
                ))
                .ifFailure(exception -> RegistryErrorHelper.handleRegistrationError(
                        build, modId, name, animationId, RUNTIME_ERRORS, exception
                ));
    }

    private static void buildSkill(SkillBuildEvent.ModRegistryWorker modRegistry,
                                   String modId, String name,
                                   ResourceLocation animationId,
                                   ResourceLocation chargeAnimationId,
                                   HoldableInnateSkillDefinition skillData) {
        var attackAnimationKey = AnimationBuilderHelper.resolveAnimation(
                modId, name, animationId, RUNTIME_ERRORS
        );
        var chargeAnimationKey = AnimationBuilderHelper.resolveChargingAnimation(
                modId, name, chargeAnimationId, RUNTIME_ERRORS
        );

        if (attackAnimationKey == null) return;
        if (chargeAnimationKey == null) return;

        var builder = skillData.createBuilder(modId, attackAnimationKey, chargeAnimationKey);
        var skill = modRegistry.build(name, WHoldableInnateSkill::new,  builder);
        skillData.applyProperties(skill);
    }

    @SubscribeEvent
    public static void onClientModBusEvent(final FMLLoadCompleteEvent event) {
        if (!RUNTIME_ERRORS.isEmpty()) {
            throw new RegistryObjectException(
                    "Failure during the operation to create a Holdable Innate Skill...\n" +
                            "Total number of registry failures: " + RUNTIME_ERRORS.size() +
                            "\n\nProblematic Skills\n\n" + String.join("\n", RUNTIME_ERRORS));
        }
    }
}
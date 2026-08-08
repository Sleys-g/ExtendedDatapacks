package sleys.efedp.system.innates;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.innates.json.builder.HoldableInnateSkillBuilder;
import sleys.efedp.system.innates.json.builder.helper.AnimationBuilderHelper;
import sleys.efedp.system.innates.json.builder.helper.RegistryErrorHelper;
import sleys.efedp.system.innates.json.builder.wrapper.holdable.WHoldableInnateSkill;
import sleys.efedp.system.innates.json.definitions.HoldableInnateSkillDefinition;
import sleys.sl.library.exceptions.RegistryObjectException;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HoldableInnateSkillsRegistry {
    private static final List<String> RUNTIME_ERRORS = Collections.synchronizedList(new ArrayList<>());

    private HoldableInnateSkillsRegistry() {}

    @SubscribeEvent
    public static void initialize(SkillBuildEvent build) {
        ExtendedDatapacks.LOGGER.info("[Charged Innate Skill Registry] Registering JSON skills");

        var data = HoldableInnateSkillBuilder.getHoldableInnateSkillBuildData();
        if (data.isEmpty()) {
            ExtendedDatapacks.LOGGER.info("[Charged Innate Skill Registry] No JSON skills found");
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

        try {
            buildSkill(modRegistry, modId, name, animationId, chargedAnimationId, skillData);
        } catch (Exception e) {
            RegistryErrorHelper.handleRegistrationError(build, modId, name, animationId, RUNTIME_ERRORS, e);
        }
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

        ExtendedDatapacks.LOGGER.info(
                "[Charged Innate Skill Registry] Registration process completed for Skill: {} signed under modID: {} for animation: {}",
                name, modId, attackAnimationKey
        );
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
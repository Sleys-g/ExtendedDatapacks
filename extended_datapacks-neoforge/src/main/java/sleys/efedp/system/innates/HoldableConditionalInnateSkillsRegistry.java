package sleys.efedp.system.innates;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.innates.json.builder.ConditionalInnateSkillBuilder;
import sleys.efedp.system.innates.json.builder.HoldableConditionalInnateSkillBuilder;
import sleys.efedp.system.innates.json.builder.data.ConditionalType;
import sleys.efedp.system.innates.json.builder.helper.AnimationBuilderHelper;
import sleys.efedp.system.innates.json.builder.helper.RegistryErrorHelper;
import sleys.efedp.system.innates.json.builder.values.AnimationSkillValues;
import sleys.efedp.system.innates.json.builder.wrapper.conditional.WConditionalInnateSkill;
import sleys.efedp.system.innates.json.definitions.ConditionalInnateSkillDefinition;
import sleys.efedp.system.innates.json.definitions.HoldableConditionalInnateSkillDefinition;
import sleys.efedp.system.innates.json.definitions.HoldableInnateSkillDefinition;
import sleys.sl.library.exceptions.RegistryObjectException;
import yesman.epicfight.registry.EpicFightRegistries;
import yesman.epicfight.skill.Skill;

import java.util.*;

public class HoldableConditionalInnateSkillsRegistry {
    private static final List<String> RUNTIME_ERRORS = Collections.synchronizedList(new ArrayList<>());
    private static final Map<String, DeferredRegister<Skill>> REGISTRIES = new HashMap<>();
    private static boolean initialized = false;

    private HoldableConditionalInnateSkillsRegistry() {}

    public static void initialize(IEventBus modBus) {
        if (initialized) return;
        initialized = true;
        ExtendedDatapacks.LOGGER.info("[Holdable Conditional Innate Skill Registry] Registering JSON skills");

        var data = HoldableConditionalInnateSkillBuilder.getHoldableConditionalInnateSkillBuildData();
        if (data.isEmpty()) {
            ExtendedDatapacks.LOGGER.info("[Holdable Conditional Innate Skill Registry] No JSON skills found");
            return;
        }

        data.forEach((modId, skills) ->
                skills.forEach(skillData ->
                        registerSkill(getOrCreateRegistry(modId, modBus), modId, skillData)
                )
        );
    }

    private static DeferredRegister<Skill> getOrCreateRegistry(String modId, IEventBus modBus) {
        return REGISTRIES.computeIfAbsent(modId, id -> {
            DeferredRegister<Skill> reg = DeferredRegister.create(EpicFightRegistries.Keys.SKILL, id);
            reg.register(modBus);
            return reg;
        });
    }

    private static void registerSkill(DeferredRegister<Skill> registry, String modId, HoldableConditionalInnateSkillDefinition skillData) {
        var name = skillData.name();
        var chargedAnimationId = ResourceLocation.tryParse(skillData.chargeAnimation());

        if (chargedAnimationId == null) {
            RUNTIME_ERRORS.add(RegistryErrorHelper.getError(
                    RegistryErrorHelper.ErrorsType.UNPARSEABLE, name,
                    modId, skillData.chargeAnimation(), null
            ));
            return;
        }
        registry.register(name, key -> buildSkill(registry, modId, name, chargedAnimationId, skillData, key));
    }

    private static Skill buildSkill(DeferredRegister<Skill> registry,
                                    String modId, String name,
                                    ResourceLocation chargeAnimationId,
                                    HoldableConditionalInnateSkillDefinition skillData,
                                    ResourceLocation key) {

        boolean hasNormalCondition = false;
        try {
            var chargeAnimationKey = AnimationBuilderHelper.resolveChargingAnimation(
                    modId, name, chargeAnimationId, RUNTIME_ERRORS
            );

            if (chargeAnimationKey == null) return Skill.EMPTY;
            var builder = skillData.createBuilder(modId, chargeAnimationKey);

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

                    return Skill.EMPTY;
                }

                var attackAnimationKey = AnimationBuilderHelper.resolveAnimation(
                        modId, name, conditionalTypes, animationId, RUNTIME_ERRORS
                );
                if (attackAnimationKey == null) return Skill.EMPTY;


                if (conditionalTypes == ConditionalType.NORMAL) hasNormalCondition = true;

                var conditionalProperties = skillData.saveProperties(conditionalAnimationData.properties());
                var conditionalValues =new AnimationSkillValues(attackAnimationKey, conditionalProperties);
                builder.putConditionData(conditionalTypes, conditionalValues);
            }

            if (!hasNormalCondition) {
                RUNTIME_ERRORS.add(
                        RegistryErrorHelper.getError(
                                RegistryErrorHelper.ErrorsType.REGISTRY_BUILDER,
                                name, modId, null,
                                "Missing NORMAL predicate."
                        )
                );
                return Skill.EMPTY;
            }

            ExtendedDatapacks.LOGGER.info(
                    "[Holdable Conditional Innate Skill Registry] Registered Skill: {} under modID: {}",
                    name, modId
            );

            return builder.build(key);
        } catch (Exception e) {
            ExtendedDatapacks.LOGGER.fatal("[Holdable Conditional Innate Skill Registry] Error Stack: ", e);
            return RegistryErrorHelper.handleRegistrationError(registry, modId, name, skillData.conditionalAnimationData(), RUNTIME_ERRORS, e);
        }
    }

    @SubscribeEvent
    public static void onClientModBusEvent(final FMLLoadCompleteEvent event) {
        if (!RUNTIME_ERRORS.isEmpty()) {
            throw new RegistryObjectException(
                    "Failure during the operation to create a Holdable Conditional Innate Skill...\n" +
                            "Total number of registry failures: " + RUNTIME_ERRORS.size() +
                            "\n\nProblematic Skills\n\n" + String.join("\n", RUNTIME_ERRORS));
        }
    }
}
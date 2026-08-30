package sleys.efedp.system.innates;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.innates.json.builder.helper.AnimationBuilderHelper;
import sleys.efedp.system.innates.json.builder.helper.RegistryErrorHelper;
import sleys.efedp.system.innates.json.builder.HoldableInnateSkillBuilder;
import sleys.efedp.system.innates.json.definitions.HoldableInnateSkillDefinition;
import sleys.sl.library.exceptions.RegistryObjectException;
import yesman.epicfight.registry.EpicFightRegistries;
import yesman.epicfight.skill.Skill;

import java.util.*;

public class HoldableInnateSkillsRegistry {
    private static final List<String> RUNTIME_ERRORS = Collections.synchronizedList(new ArrayList<>());
    private static final Map<String, DeferredRegister<Skill>> REGISTRIES = new HashMap<>();
    private static boolean initialized = false;

    private HoldableInnateSkillsRegistry() {}

    public static void initialize(IEventBus modBus) {
        if (initialized) return;
        initialized = true;
        ExtendedDatapacks.LOGGER.info("[Charged Innate Skill Registry] Registering JSON skills");

        var data = HoldableInnateSkillBuilder.getHoldableInnateSkillBuildData();
        if (data.isEmpty()) {
            ExtendedDatapacks.LOGGER.info("[Charged Innate Skill Registry] No JSON skills found");
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

    private static void registerSkill(DeferredRegister<Skill> registry, String modId, HoldableInnateSkillDefinition skillData) {
        var name = skillData.name();
        var animationId = ResourceLocation.tryParse(skillData.animation());
        var chargedAnimationId = ResourceLocation.tryParse(skillData.chargeAnimation());

        if (animationId == null) {
            RUNTIME_ERRORS.add(RegistryErrorHelper.getError(
                    RegistryErrorHelper.ErrorsType.UNPARSEABLE,
                    name, modId, skillData.animation(), null
            ));
            return;
        }

        if (chargedAnimationId == null) {
            RUNTIME_ERRORS.add(RegistryErrorHelper.getError(
                    RegistryErrorHelper.ErrorsType.UNPARSEABLE, name,
                    modId, skillData.chargeAnimation(), null
            ));
            return;
        }

        registry.register(name, key -> buildSkill(
                registry, modId, name, animationId,
                chargedAnimationId, skillData, key
        ));
    }

    private static Skill buildSkill(DeferredRegister<Skill> registry, String modId, String name,
                                    ResourceLocation animationId, ResourceLocation chargeAnimationId,
                                    HoldableInnateSkillDefinition skillData, ResourceLocation key) {
        try {
            var attackAnimationKey = AnimationBuilderHelper.resolveAnimation(
                    modId, name, animationId, RUNTIME_ERRORS
            );
            var chargeAnimationKey = AnimationBuilderHelper.resolveChargingAnimation(
                    modId, name, chargeAnimationId, RUNTIME_ERRORS
            );

            if (attackAnimationKey == null) return Skill.EMPTY;
            if (chargeAnimationKey == null) return Skill.EMPTY;

            var builder = skillData.createBuilder(modId, attackAnimationKey, chargeAnimationKey);
            skillData.applyProperties(builder);

            ExtendedDatapacks.LOGGER.info(
                    "[Charged Innate Skill Registry] Registration process completed for Skill: {} signed under modID: {} for animation: {}",
                    name, modId, attackAnimationKey
            );

            return builder.build(key);
        } catch (Exception e) {
            ExtendedDatapacks.LOGGER.fatal("[Charged Innate Skill Registry] Error Stack: ", e);
            return RegistryErrorHelper.handleRegistrationError(registry, modId, name, animationId, RUNTIME_ERRORS, e);
        }
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
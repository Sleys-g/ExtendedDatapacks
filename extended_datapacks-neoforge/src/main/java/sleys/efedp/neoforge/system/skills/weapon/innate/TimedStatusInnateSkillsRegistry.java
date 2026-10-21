package sleys.efedp.neoforge.system.skills.weapon.innate;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import sleys.efedp.neoforge.ExtendedDatapacks;
import sleys.efedp.neoforge.system.skills.weapon.innate.builder.TimedAdvancedInnateSkillBuilder;
import sleys.efedp.neoforge.system.skills.weapon.innate.builder.helper.AnimationBuilderHelper;
import sleys.efedp.neoforge.system.skills.weapon.innate.builder.helper.RegistryErrorHelper;
import sleys.efedp.neoforge.system.skills.weapon.innate.definitions.TimedAdvancedInnateSkillDefinition;
import sleys.sl.library.exceptions.RegistryObjectException;
import sleys.sl.library.execution.policy.ErrorPolicy;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
import yesman.epicfight.registry.EpicFightRegistries;
import yesman.epicfight.skill.Skill;

import java.util.*;

public class TimedStatusInnateSkillsRegistry {
    private static final List<String> RUNTIME_ERRORS = Collections.synchronizedList(new ArrayList<>());
    private static final Map<String, DeferredRegister<Skill>> REGISTRIES = new HashMap<>();
    private static boolean initialized = false;

    private TimedStatusInnateSkillsRegistry() {}

    public static void initialize(IEventBus modBus) {
        if (initialized) return;
        initialized = true;
        ExtendedDatapacks.LOGGER.info("[Timed Advanced Innate Skill Registry] Registering JSON skills");

        var data = TimedAdvancedInnateSkillBuilder.getTimedAdvancedInnateSkillBuildData();
        if (data.isEmpty()) {
            ExtendedDatapacks.LOGGER.info("[Timed Advanced Innate Skill Registry] No JSON skills found");
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

    private static void registerSkill(DeferredRegister<Skill> registry, String modId, TimedAdvancedInnateSkillDefinition skillData) {
        var name = skillData.name();
        var animationId = ResourceLocation.tryParse(skillData.animation());

        if (animationId == null) {
            RUNTIME_ERRORS.add(RegistryErrorHelper.getError(
                    RegistryErrorHelper.ErrorsType.UNPARSEABLE, name, modId, skillData.animation(), null));
            return;
        }

        registry.register(name, key -> buildSkillSafely(registry, modId, name, animationId, skillData, key));
    }

    private static Skill buildSkillSafely(DeferredRegister<Skill> registry, String modId, String name,
                                          ResourceLocation animationId, TimedAdvancedInnateSkillDefinition skillData,
                                          ResourceLocation key) {
        return ExecutionTasks.getRaw(
                        ExecutionPolicy.RESIST,
                        ErrorPolicy.DEPURATE,
                        "[Timed Advanced Innate Skill Registry] Registering Skill '{" + name + "}'",
                        () -> buildSkill(modId, name, animationId, skillData, key)
                )
                .peek(skill -> ExtendedDatapacks.LOGGER.info(
                        "[Timed Advanced Innate Skill Registry] Registered Skill: {} under modID: {}", name, modId)
                )
                .peekError(error -> ExtendedDatapacks.LOGGER.fatal(
                        "[Timed Advanced Innate Skill Registry] Error Stack: ", error)
                )
                .fold(
                        skill -> skill,
                        exception -> RegistryErrorHelper.handleRegistrationError(
                                registry, modId, name, animationId, RUNTIME_ERRORS, exception
                        )
                );
    }

    private static Skill buildSkill(String modId, String name, ResourceLocation animationId,
                                    TimedAdvancedInnateSkillDefinition skillData, ResourceLocation key) {
        var attackAnimationKey = AnimationBuilderHelper.resolveAnimation(
                modId, name, animationId, RUNTIME_ERRORS
        );

        if (attackAnimationKey == null) return Skill.EMPTY;

        var builder = skillData.createBuilder(attackAnimationKey, modId);
        skillData.applyProperties(builder);

        ExtendedDatapacks.LOGGER.info(
                "[Timed Advanced Innate Skill Registry] Registration process completed for Skill: {} signed under modID: {} for animation: {}",
                name, modId, attackAnimationKey
        );

        return builder.build(key);
    }

    @SubscribeEvent
    public static void onClientModBusEvent(final FMLLoadCompleteEvent event) {
        if (!RUNTIME_ERRORS.isEmpty()) {
            throw new RegistryObjectException(
                    "Failure during the operation to create a Timed Advanced Innate Skill...\n" +
                            "Total number of registry failures: " + RUNTIME_ERRORS.size() +
                            "\n\nProblematic Skills\n\n" + String.join("\n", RUNTIME_ERRORS));
        }
    }
}
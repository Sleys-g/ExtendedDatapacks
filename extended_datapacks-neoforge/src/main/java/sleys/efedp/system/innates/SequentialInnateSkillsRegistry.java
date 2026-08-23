package sleys.efedp.system.innates;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.innates.json.builder.SequentialInnateSkillBuilder;
import sleys.efedp.system.innates.json.builder.helper.AnimationBuilderHelper;
import sleys.efedp.system.innates.json.builder.helper.FriendlyCountConverter;
import sleys.efedp.system.innates.json.builder.helper.RegistryErrorHelper;
import sleys.efedp.system.innates.json.builder.values.AnimationSkillValues;
import sleys.efedp.system.innates.json.builder.wrapper.sequential.WSequentialInnateSkill;
import sleys.efedp.system.innates.json.definitions.SequentialInnateSkillDefinition;
import sleys.sl.library.exceptions.RegistryObjectException;
import yesman.epicfight.registry.EpicFightRegistries;
import yesman.epicfight.skill.Skill;

import java.util.*;

public class SequentialInnateSkillsRegistry {
    private static final List<String> RUNTIME_ERRORS = Collections.synchronizedList(new ArrayList<>());
    private static final Map<String, DeferredRegister<Skill>> REGISTRIES = new HashMap<>();
    private static boolean initialized = false;

    private SequentialInnateSkillsRegistry() {}

    public static void initialize(IEventBus modBus) {
        if (initialized) return;
        initialized = true;
        ExtendedDatapacks.LOGGER.info("[Sequential Innate Skill Registry] Registering JSON skills");

        var data = SequentialInnateSkillBuilder.getSequentialInnateSkillBuildData();
        if (data.isEmpty()) {
            ExtendedDatapacks.LOGGER.info("[Sequential Innate Skill Registry] No JSON skills found");
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

    private static void registerSkill(DeferredRegister<Skill> registry, String modId, SequentialInnateSkillDefinition skillData) {
        var name = skillData.name();
        var skillBuilder = skillData.createBuilder();
        registry.register(name, key -> buildSkill(registry, skillBuilder, modId, name, skillData, key));
    }

    private static Skill buildSkill(DeferredRegister<Skill> registry,
                                    WSequentialInnateSkill.Builder builder,
                                    String modId, String name,
                                    SequentialInnateSkillDefinition skillData,
                                    ResourceLocation key) {
        try {
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

                    return Skill.EMPTY;
                }

                var postfix = FriendlyCountConverter.as(i);
                var attackAnimationKey = postfix != null ?
                        AnimationBuilderHelper.resolveAnimation(modId, name, postfix, animationId, RUNTIME_ERRORS) :
                        AnimationBuilderHelper.resolveAnimation(modId, name, animationId, RUNTIME_ERRORS);

                if (attackAnimationKey == null) return Skill.EMPTY;

                var animationProperties = skillData.saveProperties(properties);
                var animationSkillValues = new AnimationSkillValues(attackAnimationKey, animationProperties);
                builder.putAnimationData(animationSkillValues);
            }

            ExtendedDatapacks.LOGGER.info(
                    "[Sequential Innate Skill Registry] Registered Skill: {} under modID: {}",
                    name, modId
            );

            return builder.build(key);
        } catch (Exception e) {
            ExtendedDatapacks.LOGGER.fatal("[Sequential Innate Skill Registry] Error Stack: ", e);
            return RegistryErrorHelper.handleRegistrationError(registry, modId, name, skillData.sequentialAnimationData(), RUNTIME_ERRORS, e);
        }
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
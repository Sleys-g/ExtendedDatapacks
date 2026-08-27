package sleys.efedp.system.innates;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.innates.json.builder.ConditionalDataInnateSkillBuilder;
import sleys.efedp.system.innates.json.builder.data.ConditionalDataAnimationData;
import sleys.efedp.system.innates.json.builder.helper.AnimationBuilderHelper;
import sleys.efedp.system.innates.json.builder.helper.RegistryErrorHelper;
import sleys.efedp.system.innates.json.builder.values.AnimationSkillValues;
import sleys.efedp.system.innates.json.builder.data.ConditionalType;
import sleys.efedp.system.innates.json.builder.values.ConditionalDataSkillValues;
import sleys.efedp.system.innates.json.builder.wrapper.conditional.WConditionalDataInnateSkill;
import sleys.efedp.system.innates.json.definitions.ConditionalDataInnateSkillDefinition;
import sleys.sl.library.exceptions.RegistryObjectException;
import yesman.epicfight.registry.EpicFightRegistries;
import yesman.epicfight.skill.Skill;

import java.util.*;

public class ConditionalDataInnateSkillsRegistry {
    private static final List<String> RUNTIME_ERRORS = Collections.synchronizedList(new ArrayList<>());
    private static final Map<String, DeferredRegister<Skill>> REGISTRIES = new HashMap<>();
    private static boolean initialized = false;

    private ConditionalDataInnateSkillsRegistry() {}

    public static void initialize(IEventBus modBus) {
        if (initialized) return;
        initialized = true;
        ExtendedDatapacks.LOGGER.info("[Data Conditional Innate Skills Registry] Registering JSON skills");

        var data = ConditionalDataInnateSkillBuilder.getDatapacketInnateSkillBuildData();
        if (data.isEmpty()) {
            ExtendedDatapacks.LOGGER.info("[Data Conditional Innate Skills Registry] No JSON skills found");
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

    private static void registerSkill(DeferredRegister<Skill> registry, String modId, ConditionalDataInnateSkillDefinition skillData) {
        var name = skillData.name();
        var skillBuilder = skillData.createBuilder();
        registry.register(name, key -> buildSkill(registry, skillBuilder, modId, name, skillData, key));
    }

    private static Skill buildSkill(DeferredRegister<Skill> registry,
                                    WConditionalDataInnateSkill.Builder builder,
                                    String modId, String name,
                                    ConditionalDataInnateSkillDefinition skillData,
                                    ResourceLocation key) {

        boolean hasNormalCondition = false;
        try {
            /// I hate this shit
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

                    return Skill.EMPTY;
                }

                int occurrence = occurrenceCount.merge(physicalCondition, 1, Integer::sum) - 1;
                String postFix = occurrence == 0 ? physicalCondition.id : physicalCondition.id + "_" + occurrence;

                var attackAnimationKey = AnimationBuilderHelper.resolveAnimation(
                        modId, name, postFix, physicalCondition, animationId, RUNTIME_ERRORS
                );

                if (attackAnimationKey == null) return Skill.EMPTY;
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
                return Skill.EMPTY;
            }

            ExtendedDatapacks.LOGGER.info(
                    "[Data Conditional Innate Skills Registry] Registered Skill: {} under modID: {}",
                    name, modId
            );

            return builder.build(key);
        } catch (Exception e) {
            return RegistryErrorHelper.handleRegistrationError(registry, modId, name, skillData.conditionalAnimationData(), RUNTIME_ERRORS, e);
        }
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
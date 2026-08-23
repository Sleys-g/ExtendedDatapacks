package sleys.efedp.system.innates;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.innates.json.builder.ComboInnateSkillBuilder;
import sleys.efedp.system.innates.json.builder.data.ConditionalType;
import sleys.efedp.system.innates.json.builder.helper.AnimationBuilderHelper;
import sleys.efedp.system.innates.json.builder.helper.RegistryErrorHelper;
import sleys.efedp.system.innates.json.builder.values.AnimationSkillValues;
import sleys.efedp.system.innates.json.builder.values.ComboEntryPointValues;
import sleys.efedp.system.innates.json.builder.values.ComboNodeValues;
import sleys.efedp.system.innates.json.builder.values.ComboTransitionValues;
import sleys.efedp.system.innates.json.builder.wrapper.combo.WComboInnateSkill;
import sleys.efedp.system.innates.json.definitions.ComboInnateSkillDefinition;
import sleys.sl.library.exceptions.RegistryObjectException;
import yesman.epicfight.registry.EpicFightRegistries;
import yesman.epicfight.skill.Skill;

import java.util.*;

public class ComboInnateSkillsRegistry {
    private static final List<String> RUNTIME_ERRORS = Collections.synchronizedList(new ArrayList<>());
    private static final Map<String, DeferredRegister<Skill>> REGISTRIES = new HashMap<>();
    private static boolean initialized = false;

    private ComboInnateSkillsRegistry() {}

    public static void initialize(IEventBus modBus) {
        if (initialized) return;
        initialized = true;

        var data = ComboInnateSkillBuilder.getComboInnateSkillBuildData();
        if (data.isEmpty()) return;

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

    private static void registerSkill(DeferredRegister<Skill> registry, String modId, ComboInnateSkillDefinition skillData) {
        var name = skillData.name();
        var skillBuilder = skillData.createBuilder();
        registry.register(name, key -> buildSkill(registry, skillBuilder, modId, name, skillData, key));
    }

    private static Skill buildSkill(DeferredRegister<Skill> registry,
                                    WComboInnateSkill.Builder builder,
                                    String modId, String name,
                                    ComboInnateSkillDefinition skillData,
                                    ResourceLocation key) {
        try {
            Map<String, ComboNodeValues> resolvedNodes = new HashMap<>();
            for (var entryNodes : skillData.nodes().entrySet()) {
                var nodeId = entryNodes.getKey();
                var nodeDef = entryNodes.getValue();
                var nodeProperties = nodeDef.properties();

                var animationId = ResourceLocation.tryParse(nodeDef.animation());
                if (animationId == null) {
                    RUNTIME_ERRORS.add(RegistryErrorHelper.getError(
                            RegistryErrorHelper.ErrorsType.UNPARSEABLE, name,
                            modId, nodeDef.animation(), null)
                    );
                    return Skill.EMPTY;
                }

                var attackAnimationKey = AnimationBuilderHelper.resolveAnimation(
                        modId, name, nodeId.toLowerCase(Locale.ROOT), animationId, RUNTIME_ERRORS
                );

                if (attackAnimationKey == null) return Skill.EMPTY;

                var animationValues = new AnimationSkillValues(attackAnimationKey, skillData.saveProperties(nodeProperties));
                var transitions = nodeDef
                        .next()
                        .stream()
                        .map(transition -> new ComboTransitionValues(
                                transition.node(), transition.physicalCondition().orElse(null))
                        )
                        .toList();

                resolvedNodes.put(nodeId, new ComboNodeValues(nodeId, animationValues, transitions));
            }

            for (var node : resolvedNodes.values()) {
                for (var t : node.next()) {
                    if (!resolvedNodes.containsKey(t.targetId())) {
                        RUNTIME_ERRORS.add(RegistryErrorHelper.getError(
                                RegistryErrorHelper.ErrorsType.REGISTRY_BUILDER, name, modId, node.id(),
                                "[Combo Innate Skills Registry] The node '" + node.id() + "' references a non-existent node: '" + t.targetId() + "'"));
                        return Skill.EMPTY;
                    }
                }
            }

            boolean hasNormalEntry = false;
            List<ComboEntryPointValues> entryPoints = new ArrayList<>();
            var sortedEntries = skillData.entryPoints().stream()
                    .sorted(Comparator.comparingInt(e -> e.physicalCondition().ordinal()))
                    .toList();

            for (var entryDef : sortedEntries) {
                if (!resolvedNodes.containsKey(entryDef.node())) {
                    RUNTIME_ERRORS.add(RegistryErrorHelper.getError(
                            RegistryErrorHelper.ErrorsType.REGISTRY_BUILDER, name, modId, entryDef.node(),
                            "[Combo Innate Skills Registry] 'entry_points' reference a non-existent node: '" + entryDef.node() + "'"));
                    return Skill.EMPTY;
                }
                if (entryDef.physicalCondition() == ConditionalType.NORMAL) hasNormalEntry = true;
                entryPoints.add(new ComboEntryPointValues(entryDef.node(), entryDef.global(), entryDef.physicalCondition()));
            }

            if (!hasNormalEntry) {
                RUNTIME_ERRORS.add(RegistryErrorHelper.getError(
                        RegistryErrorHelper.ErrorsType.REGISTRY_BUILDER, name, modId, null,
                        "Missing NORMAL entry point."));
                return Skill.EMPTY;
            }

            resolvedNodes.forEach(builder::putNodes);
            entryPoints.forEach(builder::putEntryPoints);

            ExtendedDatapacks.LOGGER.info("[Combo Innate Skills Registry] Registered Skill: {} under modID: {}", name, modId);
            return builder.build(key);
        } catch (Exception e) {
            ExtendedDatapacks.LOGGER.fatal("[Combo Innate Skills Registry] Error Stack: ", e);
            return RegistryErrorHelper.handleRegistrationError(registry, modId, name, skillData.nodes(), RUNTIME_ERRORS, e);
        }
    }

    @SubscribeEvent
    public static void onClientModBusEvent(final FMLLoadCompleteEvent event) {
        if (!RUNTIME_ERRORS.isEmpty()) {
            throw new RegistryObjectException(
                    "Failure during the operation to create a Combo Innate Skill...\n" +
                            "Total number of registry failures: " + RUNTIME_ERRORS.size() +
                            "\n\nProblematic Skills\n\n" + String.join("\n", RUNTIME_ERRORS));
        }
    }
}
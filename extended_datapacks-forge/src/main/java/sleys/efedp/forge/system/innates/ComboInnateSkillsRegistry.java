package sleys.efedp.forge.system.innates;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import sleys.efedp.forge.ExtendedDatapacks;
import sleys.efedp.forge.system.innates.json.builder.ComboInnateSkillBuilder;
import sleys.efedp.forge.system.innates.json.builder.data.ConditionalType;
import sleys.efedp.forge.system.innates.json.builder.helper.AnimationBuilderHelper;
import sleys.efedp.forge.system.innates.json.builder.helper.RegistryErrorHelper;
import sleys.efedp.forge.system.innates.json.builder.values.AnimationSkillValues;
import sleys.efedp.forge.system.innates.json.builder.values.ComboEntryPointValues;
import sleys.efedp.forge.system.innates.json.builder.values.ComboNodeValues;
import sleys.efedp.forge.system.innates.json.builder.values.ComboTransitionValues;
import sleys.efedp.forge.system.innates.json.builder.wrapper.combo.WComboInnateSkill;
import sleys.efedp.forge.system.innates.json.definitions.ComboInnateSkillDefinition;
import sleys.sl.library.exceptions.RegistryObjectException;
import sleys.sl.library.execution.policy.ErrorPolicy;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;

import java.util.*;

public class ComboInnateSkillsRegistry {
    private static final List<String> RUNTIME_ERRORS = Collections.synchronizedList(new ArrayList<>());

    private ComboInnateSkillsRegistry() {}

    @SubscribeEvent
    public static void initialize(SkillBuildEvent build) {
        ExtendedDatapacks.LOGGER.info("[Combo Innate Skills Registry] Registering JSON skills");

        var data = ComboInnateSkillBuilder.getComboInnateSkillBuildData();
        if (data.isEmpty()) {
            ExtendedDatapacks.LOGGER.info("[Combo Innate Skills Registry] No JSON skills found");
            return;
        }

        data.forEach((modId, skills) -> {
                    var modRegistry = build.createRegistryWorker(modId);
                    skills.forEach(skillData -> registerSkill(build, modRegistry, modId, skillData));
                }
        );
    }

    private static void registerSkill(SkillBuildEvent build, SkillBuildEvent.ModRegistryWorker modRegistry,
                                      String modId, ComboInnateSkillDefinition skillData) {
        var name = skillData.name();
        ExecutionTasks.runAndGetResult(
                        ExecutionPolicy.RESIST,
                        ErrorPolicy.DEPURATE,
                        "[Combo Innate Skills Registry] Registering Skill '{" + name  +"}'",
                        () -> buildSkill(modRegistry, modId, name, skillData)
                )
                .peek(process ->  ExtendedDatapacks.LOGGER.info(
                        "[Combo Innate Skills Registry] Registered Skill: {} under modID: {}", name, modId
                ))
                .peekError(exception -> ExtendedDatapacks.LOGGER.fatal(
                        "[Combo Innate Skills Registry] Error Stack: ", exception
                ))
                .ifFailure(exception -> RegistryErrorHelper.handleRegistrationError(
                        build, modId, name, skillData.nodes(), RUNTIME_ERRORS, exception
                ));
    }

    private static void buildSkill(SkillBuildEvent.ModRegistryWorker modRegistry,
                                    String modId, String name,
                                    ComboInnateSkillDefinition skillData) {
        var builder = skillData.createBuilder();
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
                return;
            }

            var attackAnimationKey = AnimationBuilderHelper.resolveAnimation(
                    modId, name, nodeId.toLowerCase(Locale.ROOT), animationId, RUNTIME_ERRORS
            );

            if (attackAnimationKey == null) return;

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
                    return;
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
                return;
            }
            if (entryDef.physicalCondition() == ConditionalType.NORMAL) hasNormalEntry = true;
            entryPoints.add(new ComboEntryPointValues(entryDef.node(), entryDef.global(), entryDef.physicalCondition()));
        }

        if (!hasNormalEntry) {
            RUNTIME_ERRORS.add(RegistryErrorHelper.getError(
                    RegistryErrorHelper.ErrorsType.REGISTRY_BUILDER, name, modId, null,
                    "Missing NORMAL entry point."));
            return;
        }

        resolvedNodes.forEach(builder::putNodes);
        entryPoints.forEach(builder::putEntryPoints);
        modRegistry.build(name, WComboInnateSkill::new, builder);
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
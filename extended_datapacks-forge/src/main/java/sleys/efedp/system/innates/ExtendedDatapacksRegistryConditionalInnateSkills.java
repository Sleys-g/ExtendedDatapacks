package sleys.efedp.system.innates;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.capability.skill.MultiConditionalWeaponInnateSkill;
import sleys.efedp.helper.RegistryErrorHelper;
import sleys.efedp.system.innates.json.ConditionalInnateSkillBuilder;
import sleys.efedp.system.innates.json.ConditionsType;
import sleys.sl.epicfight.util.helper.animation.VirtualAnimationRegistry;
import sleys.sl.library.exceptions.RegistryObjectException;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;

import java.util.*;

public class ExtendedDatapacksRegistryConditionalInnateSkills {
    private static final List<String> RUNTIME_ERRORS = Collections.synchronizedList(new ArrayList<>());

    private ExtendedDatapacksRegistryConditionalInnateSkills() {}

    @SubscribeEvent
    public static void initialize(SkillBuildEvent build) {
        ExtendedDatapacks.LOGGER.info("[MultiConditional Innate Skill Registry] Registering JSON skills");

        var data = ConditionalInnateSkillBuilder.getData();
        if (data.isEmpty()) return;

        for (var entry : data.entrySet()) {
            String modId = entry.getKey();
            SkillBuildEvent.ModRegistryWorker modRegistry = build.createRegistryWorker(modId);

            for (var skillData : entry.getValue()) {
                String name = skillData.name();
                var skillBuilder = MultiConditionalWeaponInnateSkill.createMultiConditionalBuilder();
                boolean hasNormal = false;

                try {
                    for (var conditionEntry : skillData.animations().entrySet()) {
                        ConditionsType type = conditionEntry.getKey();
                        ConditionalInnateSkillBuilder.ConditionalAnimationData conditionData = conditionEntry.getValue();
                        ResourceLocation animId = ResourceLocation.tryParse(conditionData.animation());

                        var virtualAnimationId = VirtualAnimationRegistry.getParsedSkill(modId, getFormatedPathFromCondition(name, type));
                        VirtualAnimationRegistry.manualVirtualizationProtocol(
                                animId,
                                virtualAnimationId
                        );

                        var keyManager = AnimationManager.byKey(virtualAnimationId);
                        if (keyManager == null) {
                            RUNTIME_ERRORS.add(
                                    RegistryErrorHelper.getError(
                                            RegistryErrorHelper.ErrorsType.NULL_ANIMATION_KEY,
                                            name, modId, animId, null
                                    )
                            );
                            continue;
                        }

                        var accessor = RegistryErrorHelper.getAttackAnimationAccessor(keyManager);
                        List<Map<AnimationProperty.AttackPhaseProperty<?>, Object>> conditionPhases = new ArrayList<>();
                        for (var phase : conditionData.phases()) {
                            Map<AnimationProperty.AttackPhaseProperty<?>, Object> phaseProperties = new HashMap<>();
                            phaseProperties.put(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(phase.maxStrikes()));
                            phaseProperties.put(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(phase.damageMultiplier()));
                            phaseProperties.put(AnimationProperty.AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(phase.armorNegation()));
                            phaseProperties.put(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(phase.impact()));
                            phaseProperties.put(AnimationProperty.AttackPhaseProperty.STUN_TYPE, phase.getParsedStuntype());

                            if (phase.extraDamage()) {
                                phaseProperties.put(AnimationProperty.AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[1])));
                            }
                            conditionPhases.add(phaseProperties);
                        }

                        if (type == ConditionsType.NORMAL) {
                            hasNormal = true;
                        }

                        skillBuilder.addConditionData(type, new MultiConditionalWeaponInnateSkill.ConditionData(accessor, conditionPhases));
                    }

                    if (!hasNormal) {
                        RUNTIME_ERRORS.add(
                                RegistryErrorHelper.getError(
                                        RegistryErrorHelper.ErrorsType.REGISTRY_BUILDER,
                                        name, modId, null, "Missing NORMAL condition."
                                )
                        );
                        continue;
                    }

                    var isImplementTooltip = skillData.tooltip() != null;
                    modRegistry.build(name, key -> new MultiConditionalWeaponInnateSkill(skillBuilder) {

                        @OnlyIn(Dist.CLIENT) @Override
                        public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerCap) {
                            if (!isImplementTooltip) {
                                List<Component> list = super.getTooltipOnItem(itemStack, cap, playerCap);
                                this.addTooltipArgs(list, itemStack, cap, playerCap);
                                return list;
                            }

                            List<Component> list = new ArrayList<>();
                            String translatableText = this.getTranslationKey();
                            list.add(Component.translatable(translatableText)
                                    .append(Component.literal(String.format(" [%.0f]", this.consumption))
                                            .withStyle(ChatFormatting.AQUA))
                            );
                            list.add(skillData.getFormattedAdditional(translatableText + ".tooltip", itemStack));

                            this.addTooltipArgs(list, itemStack, cap, playerCap);
                            return list;
                        }

                        public void addTooltipArgs(List<Component> list, ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerCap) {
                            if (this.conditionMap.containsKey(ConditionsType.NORMAL)) {
                                var normalData = this.conditionMap.get(ConditionsType.NORMAL);
                                if (!normalData.properties().isEmpty()) {
                                    this.generateTooltipforPhase(list, itemStack, cap, playerCap, normalData.properties().get(0), "On Impact:");
                                }
                            }
                            if (this.conditionMap.containsKey(ConditionsType.IN_AIR)) {
                                var inAir = this.conditionMap.get(ConditionsType.IN_AIR);
                                if (!inAir.properties().isEmpty()) {
                                    this.generateTooltipforPhase(list, itemStack, cap, playerCap, inAir.properties().get(0), "Air-Slash Attack:");
                                }
                            }
                            if (this.conditionMap.containsKey(ConditionsType.SPRINTING)) {
                                var sprinting = this.conditionMap.get(ConditionsType.SPRINTING);
                                if (!sprinting.properties().isEmpty()) {
                                    this.generateTooltipforPhase(list, itemStack, cap, playerCap, sprinting.properties().get(0), "Dash Attack:");
                                }
                            }
                            if (this.conditionMap.containsKey(ConditionsType.USE_ITEM)) {
                                var useItem = this.conditionMap.get(ConditionsType.USE_ITEM);
                                if (!useItem.properties().isEmpty()) {
                                    this.generateTooltipforPhase(list, itemStack, cap, playerCap, useItem.properties().get(0), "Hold Attack:");
                                }
                            }
                            if (this.conditionMap.containsKey(ConditionsType.KNEELING)) {
                                var kneeling = this.conditionMap.get(ConditionsType.KNEELING);
                                if (!kneeling.properties().isEmpty()) {
                                    this.generateTooltipforPhase(list, itemStack, cap, playerCap, kneeling.properties().get(0), "Kneel Attack:");
                                }
                            }
                        }
                    }, skillBuilder.setCategory(SkillCategories.WEAPON_INNATE));
                    ExtendedDatapacks.LOGGER.info("[MultiConditional Innate Skill Registry] Registered Skill: {} under modID: {}", name, modId);
                } catch (Exception e) {
                    for (var registryId : build.getAllSkills()) {
                        var registry = registryId.getRegistryName();
                        if (registry.equals(ResourceLocation.fromNamespaceAndPath(modId, name))) {
                            RUNTIME_ERRORS.add(RegistryErrorHelper.getError(
                                    RegistryErrorHelper.ErrorsType.DUPE,
                                    name, modId, skillData.animations(),
                                    e.getCause()
                            ));
                            return;
                        }
                    }
                    RUNTIME_ERRORS.add(
                            RegistryErrorHelper.getError(
                                    RegistryErrorHelper.ErrorsType.REGISTRY_BUILDER,
                                    name, modId, skillData.animations(), e.getCause()
                            )
                    );
                }
            }
        }
    }

    private static String getFormatedPathFromCondition(String path, ConditionsType type) {
        return path + "_" + type.toString().toLowerCase(Locale.ROOT);
    }

    @SubscribeEvent
    public static void onClientModBusEvent(final FMLLoadCompleteEvent event) {
        if (!RUNTIME_ERRORS.isEmpty()) {
            String errorSummary = String.join("\n", RUNTIME_ERRORS);
            throw new RegistryObjectException(
                    "Failure during the operation to create a Conditional Innate Skill...\n" +
                            "Total number of registry failures: " + RUNTIME_ERRORS.size() +
                            "\n\nProblematic Skills\n\n" + errorSummary
            );
        }
    }
}
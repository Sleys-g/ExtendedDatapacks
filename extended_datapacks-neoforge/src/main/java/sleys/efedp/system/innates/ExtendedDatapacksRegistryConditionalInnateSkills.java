package sleys.efedp.system.innates;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.capability.skills.MultiConditionalWeaponInnateSkill;
import sleys.efedp.system.innates.json.ConditionalInnateSkillBuilder;
import sleys.efedp.system.innates.json.ConditionsType;
import sleys.sl.epicfight.util.helper.animation.VirtualAnimationRegistry;
import sleys.sl.library.exceptions.RegistryObjectException;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.registry.EpicFightRegistries;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;

import java.util.*;

public class ExtendedDatapacksRegistryConditionalInnateSkills {
    private static final List<String> RUNTIME_ERRORS = Collections.synchronizedList(new ArrayList<>());

    private ExtendedDatapacksRegistryConditionalInnateSkills() {}

    private static final Map<String, DeferredRegister<Skill>> REGISTRIES = new HashMap<>();
    private static boolean initialized = false;

    public static void initialize(IEventBus modBus) {
        if (initialized) return;
        initialized = true;
        ExtendedDatapacks.LOGGER.info("[Conditional Innate Skill Registry] Registering JSON skills");

        var data = ConditionalInnateSkillBuilder.getData();
        if (data.isEmpty()) {
            ExtendedDatapacks.LOGGER.info("[Conditional Innate Skill Registry] No JSON skills found");
            return;
        }


        for (var entry : data.entrySet()) {
            String modId = entry.getKey();
            DeferredRegister<Skill> registry = REGISTRIES.computeIfAbsent(modId, id -> {
                DeferredRegister<Skill> reg = DeferredRegister.create(EpicFightRegistries.Keys.SKILL, id);
                reg.register(modBus);
                return reg;
            });

            for (var skillData : entry.getValue()) {
                String name = skillData.name();
                registry.register(name, key -> {

                    var isImplementTooltip = skillData.tooltip() != null;
                    var skillBuilder = new MultiConditionalWeaponInnateSkill.Builder(keyBuilder -> new MultiConditionalWeaponInnateSkill(keyBuilder) {
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
                                    this.generateTooltipforPhase(list, itemStack, cap, playerCap, normalData.properties().getFirst(), "On Impact:");
                                }
                            }
                            if (this.conditionMap.containsKey(ConditionsType.IN_AIR)) {
                                var inAir = this.conditionMap.get(ConditionsType.IN_AIR);
                                if (!inAir.properties().isEmpty()) {
                                    this.generateTooltipforPhase(list, itemStack, cap, playerCap, inAir.properties().getFirst(), "Air-Slash Attack:");
                                }
                            }
                            if (this.conditionMap.containsKey(ConditionsType.SPRINTING)) {
                                var sprinting = this.conditionMap.get(ConditionsType.SPRINTING);
                                if (!sprinting.properties().isEmpty()) {
                                    this.generateTooltipforPhase(list, itemStack, cap, playerCap, sprinting.properties().getFirst(), "Dash Attack:");
                                }
                            }
                            if (this.conditionMap.containsKey(ConditionsType.USE_ITEM)) {
                                var useItem = this.conditionMap.get(ConditionsType.USE_ITEM);
                                if (!useItem.properties().isEmpty()) {
                                    this.generateTooltipforPhase(list, itemStack, cap, playerCap, useItem.properties().getFirst(), "Hold Attack:");
                                }
                            }
                            if (this.conditionMap.containsKey(ConditionsType.KNEELING)) {
                                var kneeling = this.conditionMap.get(ConditionsType.KNEELING);
                                if (!kneeling.properties().isEmpty()) {
                                    this.generateTooltipforPhase(list, itemStack, cap, playerCap, kneeling.properties().getFirst(), "Kneel Attack:");
                                }
                            }
                        }
                    }).setCategory(SkillCategories.WEAPON_INNATE);

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
                                        RegistryInnateHelper.getError(
                                                RegistryInnateHelper.ErrorsType.NULL_ANIMATION_KEY,
                                                name, modId, animId, null
                                        )
                                );
                                return Skill.EMPTY;
                            }

                            var accessor = RegistryInnateHelper.getAttackAnimationAccessor(keyManager);
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

                            skillBuilder.addConditionData(
                                    type,
                                    new MultiConditionalWeaponInnateSkill.ConditionData(accessor, conditionPhases)
                            );
                        }

                        if (!hasNormal) {
                            RUNTIME_ERRORS.add(
                                    RegistryInnateHelper.getError(
                                            RegistryInnateHelper.ErrorsType.REGISTRY_BUILDER,
                                            name, modId, null, "Missing NORMAL condition."
                                    )
                            );
                            return Skill.EMPTY;
                        }

                        ExtendedDatapacks.LOGGER.info(
                                "[MultiConditional Innate Skill Registry] Registered Skill: {} under modID: {}",
                                name, modId
                        );
                        return skillBuilder.build(key);
                    } catch (Exception e) {
                        for (var registryId : registry.getEntries()) {
                            var registryName = registryId.get().getRegistryName();
                            if (registryName.equals(ResourceLocation.fromNamespaceAndPath(modId, name))) {
                                RUNTIME_ERRORS.add(RegistryInnateHelper.getError(
                                        RegistryInnateHelper.ErrorsType.DUPE,
                                        name, modId, skillData.animations(), e.getCause()
                                ));
                                return Skill.EMPTY;
                            }
                        }
                        RUNTIME_ERRORS.add(
                                RegistryInnateHelper.getError(
                                        RegistryInnateHelper.ErrorsType.REGISTRY_BUILDER,
                                        name, modId, skillData.animations(), e.getCause()
                                )
                        );
                        return Skill.EMPTY;
                    }
                });
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
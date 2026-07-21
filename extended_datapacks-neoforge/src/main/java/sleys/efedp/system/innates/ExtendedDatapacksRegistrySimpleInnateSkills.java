package sleys.efedp.system.innates;

import com.google.common.collect.Lists;
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
import sleys.efedp.system.innates.json.SimpleInnateSkillBuilder;
import sleys.sl.epicfight.util.helper.animation.VirtualAnimationRegistry;
import sleys.sl.library.exceptions.RegistryObjectException;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.registry.EpicFightRegistries;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.weaponinnate.SimpleWeaponInnateSkill;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;

import java.util.*;

public class ExtendedDatapacksRegistrySimpleInnateSkills {
    private static final List<String> RUNTIME_ERRORS = Collections.synchronizedList(new ArrayList<>());

    private ExtendedDatapacksRegistrySimpleInnateSkills() {}

    private static final Map<String, DeferredRegister<Skill>> REGISTRIES = new HashMap<>();
    private static boolean initialized = false;

    public static void initialize(IEventBus modBus) {
        if (initialized) return;
        initialized = true;
        ExtendedDatapacks.LOGGER.info("[Simple Innate Skill Registry] Registering JSON skills");

        var data = SimpleInnateSkillBuilder.getData();
        if (data.isEmpty()) {
            ExtendedDatapacks.LOGGER.info("[Simple Innate Skill Registry] No JSON skills found");
            return;
        }


        for (var entry : data.entrySet()) {

            String modId = entry.getKey();
            DeferredRegister<Skill> registry =
                    REGISTRIES.computeIfAbsent(modId, id -> {
                        DeferredRegister<Skill> reg =
                                DeferredRegister.create(EpicFightRegistries.Keys.SKILL, id);

                        reg.register(modBus);
                        return reg;
                    });

            for (var skillData : entry.getValue()) {
                String name = skillData.name();
                String animationName = skillData.animation();
                ResourceLocation animationId = ResourceLocation.tryParse(animationName);
                if (animationId == null) {
                    RUNTIME_ERRORS.add(
                            RegistryInnateHelper.getError(
                                    RegistryInnateHelper.ErrorsType.UNPARSEABLE,
                                    name, modId, animationName, null
                            )
                    );
                    continue;
                }

                registry.register(name, key -> {
                    try {
                        var virtualAnimationId = VirtualAnimationRegistry.getParsedSkill(modId, name);
                        VirtualAnimationRegistry.manualVirtualizationProtocol(
                                animationId,
                                virtualAnimationId
                        );

                        var animationKey = AnimationManager.byKey(virtualAnimationId);
                        if (animationKey == null) {
                            RUNTIME_ERRORS.add(
                                    RegistryInnateHelper.getError(
                                            RegistryInnateHelper.ErrorsType.NULL_ANIMATION_KEY,
                                            name, modId, virtualAnimationId, null
                                    )
                            );
                            return Skill.EMPTY;
                        }

                        var attackAnimationKey = RegistryInnateHelper.getAttackAnimationAccessor(animationKey);
                        var isImplementTooltip = skillData.tooltip() != null;
                        var builder = SimpleWeaponInnateSkill
                                .createSimpleWeaponInnateBuilder(builderKey -> new SimpleWeaponInnateSkill(builderKey) {

                                    @Override
                                    public WeaponInnateSkill registerPropertiesToAnimation() {
                                        ExecutionTasks.runAndGetResult(
                                                ExecutionPolicy.RESIST,
                                                this::registryAnimationsData
                                        ).ifFailure(e ->
                                                ExtendedDatapacks.LOGGER.error(
                                                        "[Simple - InnateSkill] Fatal error caught during property assignment attempt... For Skill: {}, under NameSpaces: {}",
                                                        this.registryName.getPath(), this.registryName.getNamespace()
                                                )
                                        );
                                        return this;
                                    }

                                    private void registryAnimationsData() {
                                        AttackAnimation anim = this.attackAnimation.get();
                                        for(AttackAnimation.Phase phase : anim.phases) {
                                            phase.addProperties(this.properties.getFirst().entrySet());
                                        }
                                    }

                                    @OnlyIn(Dist.CLIENT) @Override
                                    public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerCap) {
                                        if (!isImplementTooltip) return super.getTooltipOnItem(itemStack, cap, playerCap);

                                        List<Component> list = Lists.newArrayList();
                                        String translatableText = this.getTranslationKey();

                                        list.add(Component.translatable(translatableText)
                                                .append(Component.literal(String.format(" [%.0f]", this.consumption))
                                                .withStyle(ChatFormatting.AQUA))
                                        );
                                        list.add(skillData.getFormattedAdditional(translatableText + ".tooltip", itemStack));

                                        this.generateTooltipforPhase(
                                                list, itemStack, cap, playerCap,
                                                this.properties.getFirst(),
                                                "Each Strike:"
                                        );
                                        return list;
                                    }
                                })
                                .setAnimations(attackAnimationKey)
                                .setCategory(SkillCategories.WEAPON_INNATE);

                        for (var phase : skillData.phases()) {

                            builder.newProperty()
                                    .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER,
                                            ValueModifier.adder(phase.maxStrikes()))

                                    .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER,
                                            ValueModifier.multiplier(phase.damageMultiplier()))

                                    .addProperty(AnimationProperty.AttackPhaseProperty.ARMOR_NEGATION_MODIFIER,
                                            ValueModifier.adder(phase.armorNegation()))

                                    .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER,
                                            ValueModifier.multiplier(phase.impact()))

                                    .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE,
                                            phase.getParsedStuntype());

                            if (phase.extraDamage()) {
                                builder.addProperty(
                                        AnimationProperty.AttackPhaseProperty.EXTRA_DAMAGE,
                                        Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[1]))
                                );
                            }
                        }

                        ExtendedDatapacks.LOGGER.info(
                                "[Simple Innate Skill Registry] Registration process completed for Skill: {} signed under modID: {} for animation: {}",
                                name, modId, attackAnimationKey);
                        return builder.build(key);

                    } catch (Exception e) {
                        for (var registryId : registry.getEntries()) {
                            var registryName = registryId.get().getRegistryName();
                            if (registryName.equals(ResourceLocation.fromNamespaceAndPath(modId, name))) {
                                RUNTIME_ERRORS.add(RegistryInnateHelper.getError(
                                        RegistryInnateHelper.ErrorsType.DUPE,
                                        name, modId, animationId,e.getCause()
                                ));
                                return Skill.EMPTY;
                            }
                        }
                        RUNTIME_ERRORS.add(
                                RegistryInnateHelper.getError(
                                        RegistryInnateHelper.ErrorsType.REGISTRY_BUILDER,
                                        name, modId, animationId, e.getCause()
                                )
                        );
                        return Skill.EMPTY;
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public static void onClientModBusEvent(final FMLLoadCompleteEvent event) {
        if (!RUNTIME_ERRORS.isEmpty()) {
            String errorSummary = String.join("\n", RUNTIME_ERRORS);
            throw new RegistryObjectException(
                    "Failure during the operation to create a Simple Innate Skill...\n" +
                            "Total number of registry failures: " + RUNTIME_ERRORS.size() +
                            "\n\nProblematic Skills\n\n" + errorSummary
            );
        }
    }
}
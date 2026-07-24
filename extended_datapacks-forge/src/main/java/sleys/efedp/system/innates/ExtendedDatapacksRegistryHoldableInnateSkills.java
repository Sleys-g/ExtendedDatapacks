package sleys.efedp.system.innates;

import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.capability.skill.HoldableInnateSkill;
import sleys.efedp.helper.RegistryErrorHelper;
import sleys.efedp.system.innates.json.HoldableInnateSkillBuilder;
import sleys.sl.epicfight.client.events.EFMovementInputEvent;
import sleys.sl.epicfight.util.helper.animation.VirtualAnimationRegistry;
import sleys.sl.library.exceptions.RegistryObjectException;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.SynchedAnimationVariableKeys;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.client.events.engine.ControlEngine;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;

import java.util.*;

public class ExtendedDatapacksRegistryHoldableInnateSkills {
    private static final List<String> RUNTIME_ERRORS = Collections.synchronizedList(new ArrayList<>());

    private ExtendedDatapacksRegistryHoldableInnateSkills() {}

    @SubscribeEvent
    public static void initialize(SkillBuildEvent build) {
        ExtendedDatapacks.LOGGER.info("[Charged Innate Skill Registry] Registering JSON skills");

        var data = HoldableInnateSkillBuilder.getData();
        if (data.isEmpty()) {
            ExtendedDatapacks.LOGGER.info("[Charged Innate Skill Registry] No JSON skills found");
            return;
        }


        for (var entry : data.entrySet()) {

            String modId = entry.getKey();
            SkillBuildEvent.ModRegistryWorker modRegistry = build.createRegistryWorker(modId);

            for (var skillData : entry.getValue()) {
                String name = skillData.name();
                String chargeAnimation = skillData.chargeAnimation();
                String animationName = skillData.animation();
                ResourceLocation animationId = ResourceLocation.tryParse(animationName);
                ResourceLocation chargeAnimationId = ResourceLocation.tryParse(chargeAnimation);

                if (animationId == null) {
                    RUNTIME_ERRORS.add(RegistryErrorHelper.getError(RegistryErrorHelper.ErrorsType.UNPARSEABLE, name, modId, animationName, null));
                    continue;
                }

                if (chargeAnimationId == null) {
                    RUNTIME_ERRORS.add(RegistryErrorHelper.getError(RegistryErrorHelper.ErrorsType.UNPARSEABLE, name, modId, chargeAnimation, null));
                    continue;
                }

                try {
                    var virtualAnimationId = VirtualAnimationRegistry.getParsedSkill(modId, name);
                    VirtualAnimationRegistry.manualVirtualizationProtocol(
                            animationId,
                            virtualAnimationId
                    );

                    var virtualChargeAnimationId = VirtualAnimationRegistry.getParsedSkill(modId, name + "_charging");
                    VirtualAnimationRegistry.manualVirtualizationProtocol(
                            chargeAnimationId,
                            virtualChargeAnimationId
                    );

                    var animationKey = AnimationManager.byKey(virtualAnimationId);
                    if (animationKey == null) {
                        RUNTIME_ERRORS.add(RegistryErrorHelper.getError(
                                RegistryErrorHelper.ErrorsType.NULL_ANIMATION_KEY,
                                name, modId, animationId, null)
                        );
                        continue;
                    }

                    var chargeAnimationKey = AnimationManager.byKey(virtualChargeAnimationId);
                    if (chargeAnimationKey == null) {
                        RUNTIME_ERRORS.add(RegistryErrorHelper.getError(
                                RegistryErrorHelper.ErrorsType.NULL_ANIMATION_KEY,
                                name, modId, chargeAnimationId, null
                        ));
                        continue;
                    }

                    var attackAnimationKey = RegistryErrorHelper.getAttackAnimationAccessor(animationKey);
                    var chargedAnimationKey = RegistryErrorHelper.getStaticAnimationAccessor(chargeAnimationKey);
                    var maxAllowedCharging = skillData.maxAllowedCharging();
                    var maxChargingTicks = skillData.maxChargingTicks();
                    var minChargingTicks = skillData.minChargingTicks();
                    final var reduceSpeed = skillData.reduceSpeed();
                    var isImplementTooltip = skillData.tooltip() != null;

                    var builder = WeaponInnateSkill.createWeaponInnateBuilder().setActivateType(Skill.ActivateType.HELD);
                    WeaponInnateSkill skill = modRegistry.build(
                            name,
                            key -> new HoldableInnateSkill(
                                    key, chargedAnimationKey, attackAnimationKey) {

                                @Override
                                public String putCaller() {
                                    return modId;
                                }

                                @Override
                                public ResourceLocation putSkill() {
                                    return ResourceLocation.fromNamespaceAndPath(modId, name);
                                }

                                @Override
                                public int getAllowedMaxChargingTicks() {
                                    return maxAllowedCharging;
                                }

                                @Override
                                public int getMaxChargingTicks() {
                                    return maxChargingTicks;
                                }

                                @Override
                                public int getMinChargingTicks() {
                                    return minChargingTicks;
                                }

                                @Override @OnlyIn(Dist.CLIENT)
                                public void onMovementInputEvent(EFMovementInputEvent.InputEvent movementInput, SkillContainer container) {
                                    if (container.getExecutor().isHoldingSkill(this) && reduceSpeed) {
                                        movementInput.getPlayer().setSprinting(false);
                                        ((net.minecraft.client.player.LocalPlayer) movementInput.getPlayer()).sprintTriggerTime = -1;
                                        ControlEngine.setSprintingKeyStateNotDown();
                                        var input = movementInput.getInput();

                                        if (container.getExecutor() != null && input != null) {
                                            float chargeProgress = container.getExecutor().getSkillChargingTicks() / 30.0F;
                                            float slowFactor = 1.0F - 0.8F * chargeProgress;
                                            input.forwardImpulse *= slowFactor;
                                            input.leftImpulse *= slowFactor;

                                            if (slowFactor < 0.5F) {
                                                input.leftImpulse *= 0.7F;
                                            }
                                        }
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
                                            this.properties.get(0),
                                            "Each Strike:"
                                    );
                                    return list;
                                }
                            },
                            builder.setCategory(SkillCategories.WEAPON_INNATE)
                    );

                    for (var phase : skillData.phases()) {

                        skill.newProperty()
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
                            skill.addProperty(
                                    AnimationProperty.AttackPhaseProperty.EXTRA_DAMAGE,
                                    Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[1]))
                            );
                        }
                    }

                    chargedAnimationKey.get().addProperty(
                            AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER,
                            Animations.ReusableSources.CHARGING
                    );
                    animationKey.get().addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, (self, entitypatch, speed, prevElapsedTime, elapsedTime) -> {
                        if (elapsedTime < 1.05F) {
                            int chargingPower = entitypatch.getAnimator().getVariables().get(
                                    SynchedAnimationVariableKeys.CHARGING_TICKS.get(),
                                    self.getRealAnimation()).orElse(0
                            );
                            return 0.6666F + (float)chargingPower / 20.0F;
                        } else {
                            return 1.0F;
                        }
                    });

                    ExtendedDatapacks.LOGGER.info(
                            "[Charged Innate Skill Registry] Registration process completed for Skill: {} signed under modID: {} for animation: {}",
                            name, modId, attackAnimationKey
                    );
                } catch (Exception e) {
                    for (var registryId : build.getAllSkills()) {
                        var registry = registryId.getRegistryName();
                        if (registry.equals(ResourceLocation.fromNamespaceAndPath(modId, name))) {
                            RUNTIME_ERRORS.add(RegistryErrorHelper.getError(
                                    RegistryErrorHelper.ErrorsType.DUPE,
                                    name, modId, animationId,e.getCause()
                            ));
                            return;
                        }
                    }
                    RUNTIME_ERRORS.add(RegistryErrorHelper.getError(RegistryErrorHelper.ErrorsType.REGISTRY_BUILDER, name, modId, animationId, e.getCause()));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onClientModBusEvent(final FMLLoadCompleteEvent event) {
        if (!RUNTIME_ERRORS.isEmpty()) {
            String errorSummary = String.join("\n", RUNTIME_ERRORS);
            throw new RegistryObjectException(
                    "Failure during the operation to create a Charged Innate Skill...\n" +
                            "Total number of registry failures: " + RUNTIME_ERRORS.size() +
                            "\n\nProblematic Skills\n\n" + errorSummary
            );
        }
    }
}
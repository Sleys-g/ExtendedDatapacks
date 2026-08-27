package sleys.efedp.system.innates.json.builder.wrapper.conditional;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.innates.json.builder.data.ConditionalType;
import sleys.efedp.system.innates.json.builder.helper.SkillTooltipHelper;
import sleys.efedp.system.innates.json.builder.values.AnimationSkillValues;
import sleys.efedp.system.innates.json.builder.values.ConditionalDataSkillValues;
import sleys.sl.library.annotations.ErrorHandled;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
import sleys.sl.library.util.io.JsonComponentArgs;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

import java.util.*;
import java.util.function.Function;

public class WConditionalDataInnateSkill extends WeaponInnateSkill {
    protected Map<AnimationSkillValues, ConditionalDataSkillValues> dataPacketSkillValues;
    protected List<JsonComponentArgs> tooltipComponents;
    protected boolean disableTooltipProperties;

    public static WConditionalDataInnateSkill.Builder createConditionalBuilder(
            Function<WConditionalDataInnateSkill.Builder, ? extends WConditionalDataInnateSkill> constructor) {
        return new WConditionalDataInnateSkill.Builder(constructor)
                .setCategory(SkillCategories.WEAPON_INNATE)
                .setResource(Resource.WEAPON_CHARGE);
    }

    public WConditionalDataInnateSkill(WConditionalDataInnateSkill.Builder builder) {
        super(builder);
        this.dataPacketSkillValues = builder.dataPacketSkillValues;
        this.tooltipComponents = builder.tooltipComponents;
        this.disableTooltipProperties = builder.disableTooltipProperties;
    }

    @Override
    public WeaponInnateSkill registerPropertiesToAnimation() {
        for (var entry : this.dataPacketSkillValues.entrySet()) {
            ExecutionTasks.operateAndGetResult(
                    ExecutionPolicy.RESIST,
                    entry.getKey(), this::registryAnimationsData
            ).ifFailure(e ->
                    ExtendedDatapacks.LOGGER.error(
                            "[Data Packet - Innate Skill] Fatal error caught during property assignment attempt... For Skill: {}, under NameSpaces: {}",
                            this.registryName.getPath(), this.registryName.getNamespace()
                    )
            );
        }
        return this;
    }

    @ErrorHandled
    private AnimationSkillValues registryAnimationsData(AnimationSkillValues entry) {
        var animation = entry.animationAccessor().get();
        if (!(animation instanceof AttackAnimation attack)) {
            ExtendedDatapacks.LOGGER.warn("[Data Packet - Innate Skill] The animation: {}, It is NOT an attack animation or one that inherits from it; it will proceed, however, the attempt to apply properties is suppressed....", animation);
            return null;
        }

        for (int i = 0; i < Math.min(attack.phases.length, entry.properties().size()); i++) {
            attack.phases[i].addProperties(entry.properties().get(i).entrySet());
        }
        return entry;
    }

    @Override
    public void executeOnServer(SkillContainer container, CompoundTag args) {
        this.playSkillAnimation(container.getServerExecutor());
        super.executeOnServer(container, args);
    }

    protected void playSkillAnimation(ServerPlayerPatch executor) {
        var player = executor.getOriginal();
        for (var entry : this.dataPacketSkillValues.entrySet()) {
            var dataPacketValues = entry.getValue();
            var dataReadable = dataPacketValues.readData();
            var dataResult = dataReadable.isEmpty() || dataReadable.get().syncedEvaluate(player);
            var predicate = dataPacketValues.physicalCondition().predicate;
            var keys = entry.getKey();

            if ( keys != null && predicate.test(player) && dataResult) {
                executor.playAnimationSynchronized(
                        keys.animationAccessor().get().getRealAnimation(),
                        0.0F
                );
                break;
            }
        }
    }

    @OnlyIn(Dist.CLIENT) @Override
    public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerCap) {
        if (tooltipComponents.isEmpty()) {
            List<Component> list = super.getTooltipOnItem(itemStack, cap, playerCap);
            if (this.disableTooltipProperties) return list;

            this.applyPhaseProperties(list, itemStack, cap, playerCap);
            return list;
        }

        List<Component> list = new ArrayList<>();
        String translatableText = this.getTranslationKey();
        list.add(Component.translatable(translatableText)
                .append(Component.literal(String.format(" [%.0f]", this.consumption))
                        .withStyle(ChatFormatting.AQUA))
        );
        list.add(JsonComponentArgs.getFormattedAdditionalTooltip(tooltipComponents, translatableText + ".tooltip", itemStack));

        if (this.disableTooltipProperties) return list;
        this.applyPhaseProperties(list, itemStack, cap, playerCap);
        return list;
    }

    private void applyPhaseProperties(List<Component> list, ItemStack itemStack,
                                      CapabilityItem cap, PlayerPatch<?> playerCap) {

        this.dataPacketSkillValues.forEach((animationSkillValues, dataPacketSkillValues) -> {
            if (animationSkillValues.animationAccessor().get() instanceof AttackAnimation attackAnimation) {
                AttackAnimation.Phase[] phases = attackAnimation.phases;
                var physicalCondition = dataPacketSkillValues.physicalCondition();
                var conditions = physicalCondition == null ? ConditionalType.NORMAL : physicalCondition;
                var properties = animationSkillValues.properties();
                var phaseLength = phases.length;
                var propertiesSize = properties.size();
                for (int i = 0; i < Math.min(phaseLength, propertiesSize); i++) {
                    var tooltipHead = dataPacketSkillValues.tooltipHead();
                    if (tooltipHead.isPresent()) {
                        this.generateTooltipforPhase(
                                list, itemStack, cap, playerCap, properties.get(i),
                                SkillTooltipHelper.intToOrdinalString(
                                        i, phaseLength - 1,
                                        tooltipHead.get(), conditions
                                )
                        );
                    } else {
                        this.generateTooltipforPhase(
                                list, itemStack, cap, playerCap, properties.get(i),
                                SkillTooltipHelper.intToOrdinalString(
                                        i, phaseLength - 1,
                                        conditions
                                )
                        );
                    }
                }
            }
        });
    }

    public static class Builder extends WeaponInnateSkill.Builder<WConditionalDataInnateSkill.Builder> {
        private final Map<AnimationSkillValues, ConditionalDataSkillValues> dataPacketSkillValues = new HashMap<>();
        private List<JsonComponentArgs> tooltipComponents;
        private boolean disableTooltipProperties;

        public Builder(Function<WConditionalDataInnateSkill.Builder, ? extends Skill> constructor) {
            super(constructor);
        }

        public void putDatapacketData(AnimationSkillValues animationValues, ConditionalDataSkillValues packetValues) {
            this.dataPacketSkillValues.put(animationValues, packetValues);
        }

        public WConditionalDataInnateSkill.Builder setTooltipArray(List<JsonComponentArgs> tooltipComponents) {
            this.tooltipComponents = tooltipComponents;
            return this;
        }

        public WConditionalDataInnateSkill.Builder setDisableTooltipProperties(boolean disableTooltipProperties) {
            this.disableTooltipProperties = disableTooltipProperties;
            return this;
        }
    }
}
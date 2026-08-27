package sleys.efedp.system.innates.json.builder.wrapper.conditional;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.innates.json.builder.values.AnimationSkillValues;
import sleys.efedp.system.innates.json.builder.data.ConditionalType;
import sleys.efedp.system.innates.json.builder.helper.SkillTooltipHelper;
import sleys.sl.library.annotations.ErrorHandled;
import sleys.sl.library.execution.policy.ExecutionTasks;
import sleys.sl.library.execution.policy.ExecutionPolicy;
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

public class WConditionalInnateSkill extends WeaponInnateSkill {
    protected Map<ConditionalType, AnimationSkillValues> conditionMap;
    protected List<JsonComponentArgs> tooltipComponents;
    protected boolean disableTooltipProperties;

    public static WConditionalInnateSkill.Builder createConditionalBuilder(
            Function<WConditionalInnateSkill.Builder, ? extends WConditionalInnateSkill> constructor) {
        return new WConditionalInnateSkill.Builder(constructor)
                .setCategory(SkillCategories.WEAPON_INNATE)
                .setResource(Resource.WEAPON_CHARGE);
    }

    public WConditionalInnateSkill(WConditionalInnateSkill.Builder builder) {
        super(builder);
        this.conditionMap = builder.conditionMap;
        this.tooltipComponents = builder.tooltipComponents;
        this.disableTooltipProperties = builder.disableTooltipProperties;
    }

    @Override
    public WeaponInnateSkill registerPropertiesToAnimation() {
        for (var entry : this.conditionMap.values()) {
            ExecutionTasks.operateAndGetResult(
                    ExecutionPolicy.RESIST,
                    entry, this::registryAnimationsData
            ).ifFailure(e ->
                    ExtendedDatapacks.LOGGER.error(
                            "[Conditional - Innate Skill] Fatal error caught during property assignment attempt... For Skill: {}, under NameSpaces: {}",
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
            ExtendedDatapacks.LOGGER.warn("[Conditional - Innate Skill] The animation: {}, It is NOT an attack animation or one that inherits from it; it will proceed, however, the attempt to apply properties is suppressed....", animation);
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
        for (var entry : this.conditionMap.entrySet()) {
            var condition = entry.getKey().predicate;
            var values = entry.getValue();

            if (condition.test(player) && values != null) {
                executor.playAnimationSynchronized(
                        values.animationAccessor().get().getRealAnimation(),
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

        this.conditionMap.forEach((conditions, values) -> {
            if (values.animationAccessor().get() instanceof AttackAnimation attackAnimation) {
                AttackAnimation.Phase[] phases = attackAnimation.phases;
                var properties = values.properties();
                var phaseLength = phases.length;
                var propertiesSize = properties.size();
                for (int i = 0; i < Math.min(phaseLength, propertiesSize); i++) {
                    this.generateTooltipforPhase(
                            list, itemStack, cap, playerCap, properties.get(i),
                            SkillTooltipHelper.intToOrdinalString(i, phaseLength - 1, conditions)
                    );
                }
            }
        });
    }

    public static class Builder extends WeaponInnateSkill.Builder<WConditionalInnateSkill.Builder> {
        private final Map<ConditionalType, AnimationSkillValues> conditionMap = new EnumMap<>(ConditionalType.class);
        private List<JsonComponentArgs> tooltipComponents;
        private boolean disableTooltipProperties;

        public Builder(Function<WConditionalInnateSkill.Builder, ? extends Skill> constructor) {
            super(constructor);
        }

        public void putConditionData(ConditionalType type, AnimationSkillValues data) {
            this.conditionMap.put(type, data);
        }

        public WConditionalInnateSkill.Builder setTooltipArray(List<JsonComponentArgs> tooltipComponents) {
            this.tooltipComponents = tooltipComponents;
            return this;
        }

        public WConditionalInnateSkill.Builder setDisableTooltipProperties(boolean disableTooltipProperties) {
            this.disableTooltipProperties = disableTooltipProperties;
            return this;
        }
    }
}
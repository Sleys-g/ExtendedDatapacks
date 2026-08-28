package sleys.efedp.system.innates.json.builder.wrapper.sequential;

import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.innates.json.builder.helper.SkillTooltipHelper;
import sleys.efedp.system.innates.json.builder.values.AnimationSkillValues;
import sleys.sl.library.annotations.ErrorHandled;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
import sleys.sl.library.util.io.JsonComponentArgs;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class WSequentialInnateSkill extends WeaponInnateSkill {
    private static final String COMBO_COUNT = "sequential_innate.skill.combo";
            
    private final List<AnimationSkillValues> animationSkillValues = new ArrayList<>();
    protected List<JsonComponentArgs> tooltipComponents;
    protected boolean disableTooltipProperties;

    public static WSequentialInnateSkill.Builder createSequentialBuilder() {
        return new Builder(WSequentialInnateSkill::new)
                .setCategory(SkillCategories.WEAPON_INNATE)
                .setResource(Resource.WEAPON_CHARGE);
    }

    public WSequentialInnateSkill(WSequentialInnateSkill.Builder builder) {
        super(builder);
        animationSkillValues.addAll(builder.animationSkillValues);
        this.tooltipComponents = builder.tooltipComponents;
        this.disableTooltipProperties = builder.disableTooltipProperties;
    }

    public void executeOnServer(SkillContainer container, CompoundTag arguments) {
        var playerPatch = container.getExecutor();
        var player = playerPatch.getOriginal();
        var data = player.getPersistentData();
        var counter = data.getInt(COMBO_COUNT);
        var listSize = this.animationSkillValues.size();
        var skillValues = listSize < counter ?
                this.animationSkillValues.getLast() :
                this.animationSkillValues.get(counter);

        var animation = skillValues.animationAccessor().get().getRealAnimation();
        var actuallyCombo = (counter + 1) % listSize;
        data.putInt(COMBO_COUNT, actuallyCombo);
        playerPatch.playAnimationSynchronized(animation, 0.0F);
        super.executeOnServer(container, arguments);
    }

    @Override
    public void onRemoved(SkillContainer container) {
        super.onRemoved(container);
        var playerPatch = container.getExecutor();
        var player = playerPatch.getOriginal();
        var data = player.getPersistentData();
        data.remove(COMBO_COUNT);
    }

    @OnlyIn(Dist.CLIENT) @Override
    public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerCap) {
        if (tooltipComponents.isEmpty()) {
            List<Component> list = super.getTooltipOnItem(itemStack, cap, playerCap);
            if (this.disableTooltipProperties) return list;

            this.applyPhaseProperties(list, itemStack, cap, playerCap);
            return list;
        }

        List<Component> list = Lists.newArrayList();
        var translatableText = this.getTranslationKey();

        list.add(Component.translatable(translatableText)
                .append(Component.literal(String.format(" [%.0f]", this.consumption))
                        .withStyle(ChatFormatting.AQUA))
        );

        list.add(JsonComponentArgs.getFormattedAdditionalTooltip(
                this.tooltipComponents, translatableText + ".tooltip", itemStack)
        );


        /// Fix bypass No-Attack Anim
        if (disableTooltipProperties) return list; /// Delegate to JsonComponentArgs
        this.applyPhaseProperties(list, itemStack, cap, playerCap);
        return list;
    }

    private void applyPhaseProperties(List<Component> list, ItemStack itemStack,
                                      CapabilityItem cap, PlayerPatch<?> playerCap) {
        this.animationSkillValues.forEach(skillValues ->  {
                    var animationAccessor = skillValues.animationAccessor();
                    if (animationAccessor.get() instanceof AttackAnimation attackAnimation) {
                        AttackAnimation.Phase[] phases = attackAnimation.phases;
                        var properties = skillValues.properties();
                        var phaseLength = phases.length;
                        var propertiesSize = properties.size();
                        for (int i = 0; i < Math.min(phaseLength, propertiesSize); i++) {
                            this.generateTooltipforPhase(
                                    list, itemStack, cap, playerCap, properties.get(i),
                                    SkillTooltipHelper.intToOrdinalString(i, phaseLength - 1)
                            );
                        }
                    }
                }
        );
    }

    @Override
    public WeaponInnateSkill registerPropertiesToAnimation() {
        this.animationSkillValues.forEach(skillValues ->
                ExecutionTasks.operateAndGetResult(
                        ExecutionPolicy.RESIST,
                        skillValues.animationAccessor(), this::registryAnimationsData
                ).ifFailure(e ->
                        ExtendedDatapacks.LOGGER.error(
                                "[Sequential - Innate Skill] Fatal error caught during property assignment attempt... For Skill: {}, under NameSpaces: {}",
                                this.registryName.getPath(), this.registryName.getNamespace()
                        )
        ));
        return this;
    }

    @ErrorHandled
    private AnimationManager.AnimationAccessor<? extends DynamicAnimation> registryAnimationsData(
            AnimationManager.AnimationAccessor<? extends DynamicAnimation> animationAccessor) {

        if (!(animationAccessor.get() instanceof AttackAnimation attack)) {
            ExtendedDatapacks.LOGGER.warn("[Sequential - Innate Skill] The animation: {}, It is NOT an attack animation or one that inherits from it; it will proceed, however, the attempt to apply properties is suppressed....", animationAccessor);
            return null;
        }

        AttackAnimation.Phase[] phases = attack.phases;
        for (int i = 0; i < Math.min(phases.length, this.properties.size()); i++) {
            phases[i].addProperties(this.properties.get(i).entrySet());
        }
        return animationAccessor;
    }

    public static final class Builder extends WeaponInnateSkill.Builder<WSequentialInnateSkill.Builder> {
        private final List<AnimationSkillValues> animationSkillValues = new ArrayList<>();
        private List<JsonComponentArgs> tooltipComponents;
        private boolean disableTooltipProperties;

        public Builder(Function<WSequentialInnateSkill.Builder, ? extends Skill> constructor) {
            super(constructor);
        }

        public void putAnimationData(AnimationSkillValues animationData) {
            this.animationSkillValues.add(animationData);
        }

        public WSequentialInnateSkill.Builder setTooltipArray(List<JsonComponentArgs> tooltipComponents) {
            this.tooltipComponents = tooltipComponents;
            return this;
        }

        public WSequentialInnateSkill.Builder setDisableTooltipProperties(boolean disableTooltipProperties) {
            this.disableTooltipProperties = disableTooltipProperties;
            return this;
        }
    }
}

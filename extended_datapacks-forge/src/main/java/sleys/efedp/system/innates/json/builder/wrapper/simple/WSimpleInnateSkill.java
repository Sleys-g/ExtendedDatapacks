package sleys.efedp.system.innates.json.builder.wrapper.simple;

import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.innates.json.builder.helper.SkillTooltipHelper;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
import sleys.sl.library.util.io.JsonComponentArgs;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

import java.util.List;

public class WSimpleInnateSkill extends WeaponInnateSkill {
    protected AnimationManager.AnimationAccessor<? extends StaticAnimation> animation;
    protected List<JsonComponentArgs> tooltipComponents;
    protected boolean disableTooltipProperties;

    public static Builder createSimpleWeaponInnateBuilder() {
        return new WSimpleInnateSkill.Builder()
                .setCategory(SkillCategories.WEAPON_INNATE)
                .setResource(Resource.WEAPON_CHARGE);
    }

    public WSimpleInnateSkill(Builder builder) {
        super(builder);
        this.animation = builder.animation;
        this.tooltipComponents = builder.tooltipComponents;
        this.disableTooltipProperties = builder.disableTooltipProperties;
    }

    public void executeOnServer(SkillContainer container, FriendlyByteBuf arguments) {
        container.getExecutor().playAnimationSynchronized(this.animation, 0.0F);
        super.executeOnServer(container, arguments);
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
        if (this.animation.get() instanceof AttackAnimation attackAnimation) {
            AttackAnimation.Phase[] phases = attackAnimation.phases;
            var phaseLength = phases.length;
            var propertiesSize = this.properties.size();
            for (int i = 0; i < Math.min(phaseLength, propertiesSize); i++) {
                this.generateTooltipforPhase(
                        list, itemStack, cap, playerCap, this.properties.get(i),
                        SkillTooltipHelper.intToOrdinalString(i, phaseLength - 1)
                );
            }
        }
    }

    @Override
    public WeaponInnateSkill registerPropertiesToAnimation() {
        ExecutionTasks.runAndGetResult(
                ExecutionPolicy.RESIST,
                this::registryAnimationsData
        ).ifFailure(e ->
                ExtendedDatapacks.LOGGER.error(
                        "[Simple - Innate Skill] Fatal error caught during property assignment attempt... For Skill: {}, under NameSpaces: {}",
                        this.registryName.getPath(), this.registryName.getNamespace()
                )
        );
        return this;
    }

    private void registryAnimationsData() {
        if (!(this.animation.get() instanceof AttackAnimation attackAnimation)) return;
        AttackAnimation.Phase[] phases = attackAnimation.phases;
        for (int i = 0; i < Math.min(phases.length, this.properties.size()); i++) {
            phases[i].addProperties(this.properties.get(i).entrySet());
        }
    }

    public static final class Builder extends SkillBuilder<WSimpleInnateSkill> {
        private AnimationManager.AnimationAccessor<? extends StaticAnimation> animation;
        private List<JsonComponentArgs> tooltipComponents;
        private boolean disableTooltipProperties;

        public Builder setAnimations(AnimationManager.AnimationAccessor<? extends StaticAnimation> animation) {
            this.animation = animation;
            return this;
        }

        public Builder setTooltipArray(List<JsonComponentArgs> tooltipComponents) {
            this.tooltipComponents = tooltipComponents;
            return this;
        }

        public Builder setDisableTooltipProperties(boolean disableTooltipProperties) {
            this.disableTooltipProperties = disableTooltipProperties;
            return this;
        }
    }
}

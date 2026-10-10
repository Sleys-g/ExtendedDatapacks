package sleys.efedp.forge.system.innates.json.builder.wrapper.timed;


import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sleys.efedp.forge.ExtendedDatapacks;
import sleys.efedp.forge.system.innates.json.builder.helper.SkillTooltipHelper;
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
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class WTimedInnateSkill extends WeaponInnateSkill {
    protected final AnimationManager.AnimationAccessor<? extends StaticAnimation> animation;
    protected final List<JsonComponentArgs> tooltipComponents;
    protected final boolean disableTooltipProperties;
    protected final Optional<SoundEvent> soundEvent;

    public static Builder createTimedBuilder() {
        return new Builder()
                .setCategory(SkillCategories.WEAPON_INNATE)
                .setResource(Resource.WEAPON_CHARGE)
                .setActivateType(ActivateType.DURATION);
    }

    public WTimedInnateSkill(Builder builder) {
        super(builder);
        this.animation = builder.animation;
        this.tooltipComponents = builder.tooltipComponents;
        this.disableTooltipProperties = builder.disableTooltipProperties;
        this.soundEvent = builder.soundEvent;
    }

    @Override
    public void setParams(CompoundTag parameters) {
        super.setParams(parameters);
    }

    @Override
    public void executeOnServer(SkillContainer container, FriendlyByteBuf args) {
        soundEvent.ifPresent(sound -> container.getExecutor().playSound(sound, 0.0F, 0.0F));
        if (this.isActivated(container)) this.cancelOnServer(container, args);
        else {
            super.executeOnServer(container, args);
            container.activate();
            container.getServerExecutor().modifyLivingMotionByCurrentItem(false);
            container.getExecutor().playAnimationSynchronized(animation, 0.0F);
        }
    }

    @Override
    public void cancelOnServer(SkillContainer container, FriendlyByteBuf args) {
        container.deactivate();
        super.cancelOnServer(container, args);
        container.getServerExecutor().modifyLivingMotionByCurrentItem(false);
    }

    @Override
    public void executeOnClient(SkillContainer container, FriendlyByteBuf args) {
        super.executeOnClient(container, args);
        container.activate();
    }

    @Override
    public void cancelOnClient(SkillContainer container, FriendlyByteBuf args) {
        super.cancelOnClient(container, args);
        container.deactivate();
    }

    @Override
    public boolean canExecute(SkillContainer container) {
        if (container.getExecutor().isLogicalClient()) return super.canExecute(container);
        else {
            var itemstack = container.getExecutor().getOriginal().getMainHandItem();
            return EpicFightCapabilities.getItemStackCapability(itemstack).getInnateSkill(container.getExecutor(), itemstack) == this
                    && container.getExecutor().getOriginal().getVehicle() == null;
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
                        "[Timed - Innate Skill] Fatal error caught during property assignment attempt... For Skill: {}, under NameSpaces: {}",
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

    public static final class Builder extends SkillBuilder<WTimedInnateSkill> {
        private AnimationManager.AnimationAccessor<? extends StaticAnimation> animation;
        private List<JsonComponentArgs> tooltipComponents;
        private boolean disableTooltipProperties;
        private Optional<SoundEvent> soundEvent;

        public Builder setActivationSound(Optional<SoundEvent> soundEvent) {
            this.soundEvent = soundEvent;
            return this;
        }

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

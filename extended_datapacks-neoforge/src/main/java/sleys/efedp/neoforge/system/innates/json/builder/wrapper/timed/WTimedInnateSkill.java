package sleys.efedp.neoforge.system.innates.json.builder.wrapper.timed;


import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import sleys.efedp.neoforge.ExtendedDatapacks;
import sleys.efedp.neoforge.system.innates.json.builder.helper.SkillTooltipHelper;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
import sleys.sl.library.util.io.JsonComponentArgs;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class WTimedInnateSkill extends WeaponInnateSkill {
    protected final AnimationManager.AnimationAccessor<? extends StaticAnimation> animation;
    protected final List<JsonComponentArgs> tooltipComponents;
    protected final boolean disableTooltipProperties;
    protected final Optional<SoundEvent> soundEvent;

    public static WTimedInnateSkill.Builder createTimedBuilder(
            Function<WTimedInnateSkill.Builder, ? extends WTimedInnateSkill> constructor) {
        return new WTimedInnateSkill.Builder(constructor)
                .setCategory(SkillCategories.WEAPON_INNATE)
                .setResource(Resource.WEAPON_CHARGE)
                .setActivateType(ActivateType.DURATION);
    }

    public WTimedInnateSkill(WTimedInnateSkill.Builder builder) {
        super(builder);
        this.animation = builder.animation;
        this.tooltipComponents = builder.tooltipComponents;
        this.disableTooltipProperties = builder.disableTooltipProperties;
        this.soundEvent = builder.soundEvent;
    }

    @Override
    public void loadDatapackParameters(CompoundTag parameters) {
        super.loadDatapackParameters(parameters);
    }

    @Override
    public void executeOnServer(SkillContainer container, CompoundTag arguments) {
        soundEvent.ifPresent(sound -> container.getExecutor().playSound(sound, 0.0F, 0.0F));
        if (this.isActivated(container)) this.cancelOnServer(container, arguments);
        else {
            super.executeOnServer(container, arguments);
            container.activate();
            container.getServerExecutor().modifyLivingMotionByCurrentItem(false);
            container.getExecutor().playAnimationSynchronized(animation, 0.0F);
        }
    }

    @Override
    public void cancelOnServer(SkillContainer container, CompoundTag arguments) {
        container.deactivate();
        super.cancelOnServer(container, arguments);
        container.getServerExecutor().modifyLivingMotionByCurrentItem(false);
    }

    @Override
    public void executeOnClient(SkillContainer container, CompoundTag arguments) {
        super.executeOnClient(container, arguments);
        container.activate();
    }

    @Override
    public void cancelOnClient(SkillContainer container, CompoundTag arguments) {
        super.cancelOnClient(container, arguments);
        container.deactivate();
    }

    @Override
    public boolean canExecute(SkillContainer container) {
        if (container.getExecutor().isLogicalClient()) return super.canExecute(container);
        else {
            var hand = container.getExecutor().getPrimaryHand();
            var itemstack = container.getExecutor().getOriginal().getItemInHand(hand);
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

    public static final class Builder extends WeaponInnateSkill.Builder<WTimedInnateSkill.Builder> {
        private AnimationManager.AnimationAccessor<? extends StaticAnimation> animation;
        private List<JsonComponentArgs> tooltipComponents;
        private boolean disableTooltipProperties;
        private Optional<SoundEvent> soundEvent;

        public Builder(Function<WTimedInnateSkill.Builder, ? extends Skill> constructor) {
            super(constructor);
        }

        public WTimedInnateSkill.Builder setActivationSound(Optional<SoundEvent> soundEvent) {
            this.soundEvent = soundEvent;
            return this;
        }

        public WTimedInnateSkill.Builder setAnimations(AnimationManager.AnimationAccessor<? extends StaticAnimation> animation) {
            this.animation = animation;
            return this;
        }

        public WTimedInnateSkill.Builder setTooltipArray(List<JsonComponentArgs> tooltipComponents) {
            this.tooltipComponents = tooltipComponents;
            return this;
        }

        public WTimedInnateSkill.Builder setDisableTooltipProperties(boolean disableTooltipProperties) {
            this.disableTooltipProperties = disableTooltipProperties;
            return this;
        }
    }
}

package sleys.efedp.system.innates.json.builder.wrapper.combo;

import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.innates.json.builder.values.ComboEntryPointValues;
import sleys.efedp.system.innates.json.builder.values.ComboNodeValues;
import sleys.efedp.system.innates.json.builder.values.ComboTransitionValues;
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
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class WComboInnateSkill extends WeaponInnateSkill {
    private static final String COMBO_NODE = "combo_innate.skill.node";
    private final boolean alwaysAllow;
    private final Map<String, ComboNodeValues> nodes;
    private final List<ComboEntryPointValues> entryPoints;
    private final List<JsonComponentArgs> tooltipComponents;

    public static WComboInnateSkill.Builder createComboBuilder() {
        return new WComboInnateSkill.Builder(WComboInnateSkill::new)
                .setCategory(SkillCategories.WEAPON_INNATE)
                .setResource(Resource.WEAPON_CHARGE);
    }

    public WComboInnateSkill(WComboInnateSkill.Builder builder) {
        super(builder);
        this.nodes = builder.nodes;
        this.entryPoints = builder.entryPoints;
        this.tooltipComponents = builder.tooltipComponents;
        this.alwaysAllow = builder.alwaysAllow;
    }

    public void executeOnServer(SkillContainer container, CompoundTag arguments) {
        this.setConsumptionSynchronize(container, container.getMaxResource());

        var playerPatch = container.getExecutor();
        var player = playerPatch.getOriginal();
        var data = player.getPersistentData();

        var currentId = data.contains(COMBO_NODE) ? data.getString(COMBO_NODE) : null;
        var current = currentId != null ? this.nodes.get(currentId) : null;
        var next = current != null ? resolveNext(current, player) : resolveEntry(player);
        if (next == null) next = resolveEntry(player); /// Fallback - Fallback

        if (next == null) {
            data.remove(COMBO_NODE);
            return;
        }

        data.putString(COMBO_NODE, next.id());
        playerPatch.playAnimationSynchronized(next.animation().animationAccessor().get().getRealAnimation(), 0.0F);
        super.executeOnServer(container, arguments);
    }

    private ComboNodeValues resolveNext(ComboNodeValues current, Player player) {
        ComboTransitionValues fallback = null;

        for (var transition : current.next()) {
            if (transition.physicalCondition() == null) {
                fallback = transition;
                continue;
            }
            if (transition.physicalCondition().predicate.test(player)) {
                return nodes.get(transition.targetId());
            }
        }

        for (var entry : entryPoints) {
            if (entry.global() && entry.physicalCondition().predicate.test(player)) {
                return nodes.get(entry.nodeId());
            }
        }

        return fallback != null ? nodes.get(fallback.targetId()) : null;
    }

    private ComboNodeValues resolveEntry(Player player) {
        for (var entry : this.entryPoints) {
            if (entry.physicalCondition().predicate.test(player)) return this.nodes.get(entry.nodeId());
        }
        return null;
    }

    @OnlyIn(Dist.CLIENT) @Override
    public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerCap) {
        if (tooltipComponents.isEmpty()) {
            return super.getTooltipOnItem(itemStack, cap, playerCap);
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

        return list;
    }

    @Override
    public WeaponInnateSkill registerPropertiesToAnimation() {
        this.nodes.forEach((nodeId, comboNodes) ->
                ExecutionTasks.operateAndGetResult(
                        ExecutionPolicy.RESIST,
                        comboNodes.animation().animationAccessor(), this::registryAnimationsData
                ).ifFailure(e ->
                        ExtendedDatapacks.LOGGER.error(
                                "[Combo - Innate Skill] Fatal error caught during property assignment attempt... For Skill: {}, under NameSpaces: {}",
                                this.registryName.getPath(), this.registryName.getNamespace()
                        )
                )
        );
        return this;
    }

    @ErrorHandled
    private AnimationManager.AnimationAccessor<? extends DynamicAnimation> registryAnimationsData(
            AnimationManager.AnimationAccessor<? extends DynamicAnimation> animationAccessor) {

        if (!(animationAccessor.get() instanceof AttackAnimation attack)) {
            ExtendedDatapacks.LOGGER.warn("[Combo - Innate Skill] The animation: {}, It is NOT an attack animation or one that inherits from it; it will proceed, however, the attempt to apply properties is suppressed....", animationAccessor);
            return null;
        }

        AttackAnimation.Phase[] phases = attack.phases;
        for (int i = 0; i < Math.min(phases.length, this.properties.size()); i++) {
            phases[i].addProperties(this.properties.get(i).entrySet());
        }
        return animationAccessor;
    }

    @Override
    public void onRemoved(SkillContainer container) {
        super.onRemoved(container);
        container.getExecutor().getOriginal().getPersistentData().remove(COMBO_NODE);
    }

    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        if (alwaysAllow && !container.getExecutor().getOriginal().level().isClientSide()) {
            var resource = container.getResource();
            var maxResource = container.getMaxResource();
            if (resource != maxResource) {
                this.setConsumptionSynchronize(container, container.getMaxResource());
            }
        }
    }

    public static final class Builder extends WeaponInnateSkill.Builder<WComboInnateSkill.Builder> {
        private final Map<String, ComboNodeValues> nodes = new HashMap<>();
        private final List<ComboEntryPointValues> entryPoints = new ArrayList<>();
        private List<JsonComponentArgs> tooltipComponents;
        private boolean alwaysAllow;

        public Builder(Function<WComboInnateSkill.Builder, ? extends Skill> constructor) {
            super(constructor);
        }

        public void putNodes(String id, ComboNodeValues nodeValues) {
            this.nodes.put(id, nodeValues);
        }

        public void putEntryPoints(ComboEntryPointValues comboEntryPointValues) {
            this.entryPoints.add(comboEntryPointValues);
        }

        public WComboInnateSkill.Builder setAlwaysAllow(Boolean alwaysAllow) {
            this.alwaysAllow = alwaysAllow;
            return this;
        }

        public WComboInnateSkill.Builder setTooltipArray(List<JsonComponentArgs> tooltipComponents) {
            this.tooltipComponents = tooltipComponents;
            return this;
        }
    }
}

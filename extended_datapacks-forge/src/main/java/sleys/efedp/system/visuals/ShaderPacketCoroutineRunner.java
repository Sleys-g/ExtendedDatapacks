package sleys.efedp.system.visuals;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.visuals.json.ActivationType;
import sleys.efedp.system.visuals.json.ShaderAssetsPacksSystem;
import sleys.sl.library.execution.policy.ResultProtocol;
import sleys.sl.library.execution.task.CoroutineTask;
import sleys.sl.shaders.chains.IChainEffect;
import sleys.sl.shaders.chains.IPhaseChain;
import sleys.sl.shaders.chains.ShaderEffectList;
import sleys.sl.shaders.data.*;
import sleys.sl.shaders.system.ShaderEventListener;
import sleys.sl.shaders.system.ShaderPhaseManager;
import yesman.epicfight.api.animation.types.LayerOffAnimation;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.Style;

import java.util.*;
import java.util.function.Supplier;


public class ShaderPacketCoroutineRunner extends CoroutineTask {
    private sealed interface EffectCondition permits StyleCondition, SkillCondition {}
    private record StyleCondition(Item weapon, Style style, IShaderParameters params) implements EffectCondition {}
    private record SkillCondition(Item weapon, ResourceLocation skill, IShaderParameters params) implements EffectCondition {}

    private final Map<ShaderEffectList, List<Map.Entry<EffectCondition, IChainEffect>>> ACTIVE_SHADERS = new EnumMap<>(ShaderEffectList.class);

    private ResourceLocation ANIMATION;
    private ResourceLocation ACTUALLY_SKILL;
    private float ELAPSE;

    private final Minecraft MINECRAFT = Minecraft.getInstance();
    public ShaderPacketCoroutineRunner() {
        ExtendedDatapacks.LOGGER.info("[Shader Packet - Coroutine Runner] Cleaning & Rebooting Shader Coroutine!");
        clearAllEffects();
    }

    @Override
    protected boolean run() {
        LocalPlayer player = MINECRAFT.player;
        ClientLevel level = MINECRAFT.level;

        if (!sleys.sl.library.util.helper.player.PlayerHelper.isValidPlayerAllowDeath(player) || level == null) return true;


        PlayerPatch<?> playerPatch = EpicFightCapabilities.getPlayerPatch(player);
        if (playerPatch == null) return true;

        updatePlayerState(playerPatch);
        onPlayerClientTick(player, playerPatch);
        return true;
    }

    private void updatePlayerState(PlayerPatch<?> playerPatch) {
        updateCurrentAnimation(playerPatch);
        updateCurrentSkill(playerPatch);
    }

    private void updateCurrentAnimation(PlayerPatch<?> playerPatch) {
        ANIMATION = null; /// Bug Fix #32, Fx Looping
        for (var layer : playerPatch.getClientAnimator().getAllLayers()) {
            var animationPlayer = layer.animationPlayer;
            if (Float.isNaN(animationPlayer.getElapsedTime())) continue;
            if (animationPlayer.isEmpty()) continue;

            var animation = animationPlayer.getAnimation();
            if (animation instanceof LayerOffAnimation) continue;
            var stringLayer = layer.toString();
            if (stringLayer.contains("Composite Layer(MIDDLE")) continue;
            if (stringLayer.contains("Base Layer(LOWEST)")) continue;

            var animationRegistry = animationPlayer.getAnimation().registryName();
            if (animationRegistry == null) continue;

            ANIMATION = animationRegistry;
            ELAPSE = animationPlayer.getElapsedTime();
        }
    }

    private void updateCurrentSkill(PlayerPatch<?> playerPatch) {
        var container = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
        if (container == null) return;


        var skill = container.getSkill();
        if (skill == null) return;


        if (!skill.isActivated(container)) return;
        ACTUALLY_SKILL = skill.getRegistryName();
    }

    private void onPlayerClientTick(Player player, PlayerPatch<?> playerPatch) {
        if (player.isDeadOrDying()) { clearAllEffects(); return; }

        var itemInHand = playerPatch.getOriginal().getItemInHand(InteractionHand.MAIN_HAND);
        if (itemInHand.isEmpty()) { pruneHoldEffects(Set.of()); return; }

        var itemCapabilityOptional = EpicFightCapabilities.getItemCapability(itemInHand);
        if (itemCapabilityOptional.isEmpty()) { pruneHoldEffects(Set.of()); return; }

        var itemCapability = itemCapabilityOptional.get();
        var itemCategory = itemCapability.getWeaponCategory();
        var itemStyle = itemCapability.getStyle(playerPatch);
        var weaponItem = itemInHand.getItem();
        var categoryKey = itemCategory.toString().toLowerCase(Locale.ROOT);
        var dataList = ShaderAssetsPacksSystem.getForCategory(categoryKey);

        if (dataList == null || dataList.isEmpty()) { pruneHoldEffects(Set.of()); return; }

        Set<EffectCondition> validThisTick = new HashSet<>();
        for (var data : dataList) {
            if (!data.isForAllItem() && data.getParseItem() != weaponItem) continue;
            if (data.getParseWeaponCategory() != itemCategory) continue;

            switch (data.activationType()) {
                case STYLE -> onStylePayload(data, itemStyle, weaponItem, validThisTick);
                case SKILL -> onSkillPayload(data, weaponItem, validThisTick);
                case ANIMATION -> onAnimationPayload(data);
                default -> ExtendedDatapacks.LOGGER.error("[Shader Coroutine] Activation Type unknown: {}", data.activationType());
            }
        }

        pruneHoldEffects(validThisTick);
    }

    private void onStylePayload(ShaderAssetsPacksSystem.ShaderPacket packet, Style style, Item weapon, Set<EffectCondition> validThisTick) {
        if (packet.activationType() != ActivationType.STYLE) return;
        var effect = packet.effect();
        if (style != effect.getParseStyle()) return;

        var condition = new StyleCondition(weapon, style, effect.shader());
        validThisTick.add(condition);
        dispatchHoldShader(condition, effect.shader());
    }

    private void onSkillPayload(ShaderAssetsPacksSystem.ShaderPacket packet, Item weapon, Set<EffectCondition> validThisTick) {
        if (packet.activationType() != ActivationType.SKILL) return;
        var effect = packet.effect();
        if (ACTUALLY_SKILL == null || !ACTUALLY_SKILL.toString().equals(effect.skill())) return;

        var condition = new SkillCondition(weapon, ACTUALLY_SKILL, effect.shader());
        validThisTick.add(condition);
        dispatchHoldShader(condition, effect.shader());
    }

    private void onAnimationPayload(ShaderAssetsPacksSystem.ShaderPacket packet) {
        if (packet.activationType() != ActivationType.ANIMATION) return;

        var effect = packet.effect();
        var animation = effect.animation();
        var actuallyAnimationKey = ANIMATION;
        if (actuallyAnimationKey == null || !animation.equals(actuallyAnimationKey.toString())) return;

        var elapseTime = effect.elapse();
        var actuallyElapseTime = ELAPSE;

        if (Math.abs(elapseTime - actuallyElapseTime) < 0.15f) {
            dispatchOneshotShader(effect.shader(), elapseTime);
        }
    }

    private void registerChainEffect(ShaderEffectList key, EffectCondition condition, Supplier<IChainEffect> factory) {
        var active = ACTIVE_SHADERS.computeIfAbsent(key, k -> new ArrayList<>());
        for (var entry : active) if (entry.getKey().equals(condition)) return;

        IChainEffect effect = factory.get();
        if (effect == null) return;
        active.add(Map.entry(condition, effect));
    }

    private void dispatchHoldShader(EffectCondition condition, IShaderParameters shader) {
        var id = getHoldShaderId(condition, shader);
        registerChainEffect(shader.effectType(), condition, () -> shader.create(id));
    }

    private String getHoldShaderId(EffectCondition condition, IShaderParameters shader) {
        if (condition instanceof StyleCondition sc) {
            return "HOLD-" + ForgeRegistries.ITEMS.getKey(sc.weapon()) + "-" + sc.style() + "-" + shader.effectType();
        } else if (condition instanceof SkillCondition sk) {
            return "HOLD-" + ForgeRegistries.ITEMS.getKey(sk.weapon()) + "-" + sk.skill() + "-" + shader.effectType();
        }

        throw new IllegalStateException("The operation fell into an unattainable state");
    }

    private void dispatchOneshotShader(IShaderParameters shader, float elapse) {
        var id = getOneshotShaderId(shader, elapse);
        if (!isAlreadyActive(shader, id)) {
            if (shader instanceof ImpactFrameParams i ) { i.sendCTS(id); }
            else if (shader instanceof ColoredImpactFrameParams ci) { ci.sendCTS(id); }
            shader.create(id);
        }
    }

    private String getOneshotShaderId(IShaderParameters shader, float elapse) {
        return "ONESHOT-" + ANIMATION + "-" + elapse + "-" + shader.effectType();
    }

    private boolean isAlreadyActive(IShaderParameters shader, String id) {
        for (var effect : ShaderEventListener.activeEffects()) {
            if (effect.effectType() != shader.effectType()) continue;
            var activeId = effect.shaderId();
            if (activeId == null || activeId.isEmpty()) continue;
            if (activeId.equals(id)) return true;
        }

        return false;
    }

    private void pruneHoldEffects(Set<EffectCondition> validThisTick) {
        for (ShaderEffectList key : ShaderEffectList.values()) {
            var active = ACTIVE_SHADERS.get(key);
            if (active == null || active.isEmpty()) continue;

            var iterator = active.iterator();
            while (iterator.hasNext()) {
                var entry = iterator.next();
                if (!validThisTick.contains(entry.getKey())) {
                    if (entry.getValue() instanceof IPhaseChain phaseChain) {
                        phaseChain.forceStartOutPhase(ShaderPhaseManager.ForceOutMode.QUEUED);
                    } else {
                        entry.getValue().dispose();
                    }
                    iterator.remove();
                }
            }
        }
    }

    private void clearAllEffects() {
        for (var chain : ShaderEventListener.activeEffects()) {
            chain.dispose();
        }
    }
}
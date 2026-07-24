package sleys.efedp.system.visuals;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.visuals.json.OverlayAssetPacksSystem;
import sleys.sl.library.execution.task.CoroutineTask;
import sleys.sl.library.execution.task.IEventListener;
import sleys.sl.library.util.ui.OverlaysUtilities;
import yesman.epicfight.api.animation.types.LayerOffAnimation;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.Style;

import javax.annotation.Nullable;

public class OverlayPacketCoroutineRunner extends CoroutineTask implements IEventListener {
    private ResourceLocation ACTUALLY_SKILL = null;
    private ResourceLocation ACTUALLY_ANIMATION = null;
    private float ACTUALLY_ELAPSE = 0F;
    private final Minecraft MINECRAFT = Minecraft.getInstance();

    private enum OverlayPhase {
        OFF, IN, HOLD, OUT
    }

    private OverlayPhase currentPhase = OverlayPhase.OFF;
    private int phaseTicks = 0;
    private float currentAlphaMultiplier = 0.0f;

    private OverlayAssetPacksSystem.OverlayEffect currentEffect = null;
    private ItemStack lastItem = null;

    public OverlayPacketCoroutineRunner() {
        ExtendedDatapacks.LOGGER.info("[Overlay Packet - Coroutine Runner] Registry events, Cleaning & Rebooting Overlay Coroutine!");
    }

    @Override
    protected boolean run() {
        var player = MINECRAFT.player;
        if (player == null || player.isDeadOrDying()) {
            this.resetToOff();
            return true;
        }

        var playerPatch = EpicFightCapabilities.getLocalPlayerPatch(player);
        if (playerPatch == null) {
            this.triggerFadeOut();
            this.updatePhaseLogic();
            return true;
        }

        this.onClientAnimator(playerPatch);
        this.onClientSkill(playerPatch);
        this.startToMatching(player, playerPatch);
        this.updatePhaseLogic();
        return true;
    }

    private void startToMatching(Player player, LocalPlayerPatch playerPatch) {
        var currentItem = player.getMainHandItem();
        var matchingEffect = findMatchingEffect(playerPatch, currentItem);

        if (matchingEffect != null) {
            if (currentItem != lastItem || !matchingEffect.equals(currentEffect)) {
                if (currentPhase == OverlayPhase.IN || currentPhase == OverlayPhase.HOLD) {
                    this.triggerFadeOut();
                }
            }

            if (currentPhase == OverlayPhase.OFF) {
                currentEffect = matchingEffect;
                lastItem = currentItem;
                currentPhase = OverlayPhase.IN;
                phaseTicks = 0;
            }
        } else {
            if (currentPhase == OverlayPhase.IN || currentPhase == OverlayPhase.HOLD) {
                this.triggerFadeOut();
            }
        }

        if (currentPhase == OverlayPhase.HOLD && currentEffect != null) {
            boolean conditionStillMet = checkConditionStillMet(playerPatch, currentItem, currentEffect);
            if (currentEffect.overlayTimes().time_hold() == Integer.MAX_VALUE && !conditionStillMet) {
                this.triggerFadeOut();
            }
        }
    }

    private void onClientAnimator(PlayerPatch<?> playerPatch) {
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

            ACTUALLY_ANIMATION = animationRegistry;
            ACTUALLY_ELAPSE = animationPlayer.getElapsedTime();
        }
    }

    private void onClientSkill(PlayerPatch<?> playerPatch) {
        var skillContainer = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
        if (skillContainer == null) return;

        var skill = skillContainer.getSkill();
        if (skill == null) return;

        if (skill.isActivated(skillContainer)) {
            var skillRegistry = skill.getRegistryName();
            if (skillRegistry == null) return;

            if (ACTUALLY_SKILL == null) ACTUALLY_SKILL = skillRegistry;
            if (!skillRegistry.equals(ACTUALLY_SKILL)) {
                ACTUALLY_SKILL = skillRegistry;
            }
        } else if (ACTUALLY_SKILL != null) {
            ACTUALLY_SKILL = null;
        }
    }

    @Nullable
    private OverlayAssetPacksSystem.OverlayEffect findMatchingEffect(LocalPlayerPatch playerPatch, ItemStack item) {
        var effects = OverlayAssetPacksSystem.getEffectsForItem(item);
        if (effects.isEmpty()) return null;

        OverlayAssetPacksSystem.OverlayEffect bestStyleOrSkillEffect = null;
        for (OverlayAssetPacksSystem.OverlayEffect effect : effects) {
            if (effect.animation() != null) {
                ResourceLocation currentAnim = ACTUALLY_ANIMATION;
                if (currentAnim != null && currentAnim.toString().equals(effect.animation())) {
                    float currentElapse = ACTUALLY_ELAPSE;
                    if (Math.abs(effect.elapse() - currentElapse) < 0.15f) {
                        return effect;
                    }
                }
            }

            if (effect.skill() != null && bestStyleOrSkillEffect == null) {
                if (ACTUALLY_SKILL != null && ACTUALLY_SKILL.toString().equals(effect.skill())) {
                    bestStyleOrSkillEffect = effect;
                }
            }

            if (effect.style() != null && bestStyleOrSkillEffect == null) {
                var capabilityItem = EpicFightCapabilities.getItemCapability(item);
                if (capabilityItem.isPresent()) {
                    Style currentStyle = capabilityItem.get().getStyle(playerPatch);
                    if (currentStyle.equals(effect.getParseStyle())) {
                        bestStyleOrSkillEffect = effect;
                    }
                }
            }
        }

        return bestStyleOrSkillEffect;
    }

    private boolean checkConditionStillMet(LocalPlayerPatch playerPatch, ItemStack item, OverlayAssetPacksSystem.OverlayEffect effect) {
        if (item != lastItem) return false;

        if (effect.skill() != null) {
            return ACTUALLY_SKILL != null && ACTUALLY_SKILL.toString().equals(effect.skill());
        }
        if (effect.style() != null) {
            var capabilityItem = EpicFightCapabilities.getItemCapability(item);
            return capabilityItem.map(cap -> cap.getStyle(playerPatch) == effect.getParseStyle()).orElse(false);
        }
        if (effect.animation() != null) {
            ResourceLocation currentAnim = ACTUALLY_ANIMATION;
            if (currentAnim == null || !currentAnim.toString().equals(effect.animation())) return false;
            if (effect.elapse() > 0) {
                return ACTUALLY_ELAPSE <= effect.elapse();
            }
            return true;
        }
        return false;
    }

    private void triggerFadeOut() {
        currentPhase = OverlayPhase.OUT;
        phaseTicks = 0;
    }

    private void resetToOff() {
        currentPhase = OverlayPhase.OFF;
        currentAlphaMultiplier = 0.0f;
        currentEffect = null;
        lastItem = null;
    }

    private void updatePhaseLogic() {
        if (currentEffect == null) {
            resetToOff();
            return;
        }

        phaseTicks++;
        var times = currentEffect.overlayTimes();

        switch (currentPhase) {
            case IN -> {
                if (times.time_in() <= 0) {
                    currentAlphaMultiplier = 1.0f;
                    currentPhase = OverlayPhase.HOLD;
                    phaseTicks = 0;
                } else {
                    currentAlphaMultiplier = Math.min(1.0f, (float) phaseTicks / times.time_in());
                    if (phaseTicks >= times.time_in()) {
                        currentPhase = OverlayPhase.HOLD;
                        phaseTicks = 0;
                    }
                }
            }
            case HOLD -> {
                currentAlphaMultiplier = 1.0f;
                if (phaseTicks >= times.time_hold()) {
                    this.triggerFadeOut();
                }
            }
            case OUT -> {
                if (times.time_out() <= 0) {
                    this.resetToOff();
                } else {
                    currentAlphaMultiplier = Math.max(0.0f, 1.0f - ((float) phaseTicks / times.time_out()));
                    if (phaseTicks >= times.time_out()) {
                        this.resetToOff();
                    }
                }
            }
            case OFF -> currentAlphaMultiplier = 0.0f;
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRenderHud(RenderGuiEvent.Pre event) {
        if (currentPhase == OverlayPhase.OFF || currentAlphaMultiplier <= 0f || currentEffect == null) {
            return;
        }

        Window window = MINECRAFT.getWindow();
        var pathConfig = currentEffect.overlayPath();
        if (pathConfig == null || pathConfig.frames().length == 0) return;

        float resX = window.getGuiScaledWidth();
        float resY = window.getGuiScaledHeight();


        float finalAlpha = (pathConfig.alpha() / 255.0f) * currentAlphaMultiplier;
        this.runOverlay(resX, resY, finalAlpha, pathConfig.frames(), pathConfig.fps(),
                texture -> RenderSystem.setShaderTexture(0, texture)
        );
    }

    private void runOverlay(float screenWidth, float screenHeight, float alpha, ResourceLocation[] resource, int fps,
                            OverlaysUtilities.AnimatedRenderCall renderEntry) {

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, alpha);

        if (fps == 0) {
            renderEntry.render(resource[0]);
        } else {
            renderEntry.render(OverlaysUtilities.getAnimatedTextures(resource, fps));
        }

        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder bb = tess.getBuilder();
        bb.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        bb.vertex(0.0, 0.0, 0.0).uv(0.0f, 0.0f).endVertex();
        bb.vertex(0.0, screenHeight, 0.0).uv(0.0f, 1.0f).endVertex();
        bb.vertex(screenWidth, screenHeight, 0.0).uv(1.0f, 1.0f).endVertex();
        bb.vertex(screenWidth, 0.0, 0.0).uv(1.0f, 0.0f).endVertex();

        tess.end();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
    }
}
package sleys.efedp.forge.client.render.entitypatchs;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import sleys.efedp.forge.client.render.entities.OwnableWitherGhostRender;
import sleys.efedp.forge.world.entities.OwnableWitherGhost;
import sleys.efedp.forge.world.entitypatchs.OwnableWitherGhostPatch;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.client.mesh.WitherMesh;
import yesman.epicfight.client.renderer.patched.entity.PWitherRenderer;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;

public class OwnableWitherGhostPatchRender extends PatchedEntityRenderer<OwnableWitherGhost, OwnableWitherGhostPatch, OwnableWitherGhostRender, WitherMesh> {

    @Override
    public void render(OwnableWitherGhost entity, OwnableWitherGhostPatch entitypatch,
                       OwnableWitherGhostRender renderer, MultiBufferSource buffer,
                       PoseStack poseStack, int packedLight, float partialTicks) {

        RenderType renderType = RenderType.entityTranslucent(PWitherRenderer.WITHER_INVULNERABLE_LOCATION);
        WitherMesh mesh = this.getMeshProvider(entitypatch).get();
        Armature armature = entitypatch.getArmature();
        float transparency = entity.isNoAi() ? 0.6F : Mth.sin((entity.tickCount + partialTicks) * 0.025F * Mth.PI) * 0.6F;

        poseStack.pushPose();
        this.mulPoseStack(poseStack, armature, entity, entitypatch, partialTicks);
        this.setArmaturePose(entitypatch, armature, partialTicks);
        mesh.draw(poseStack, buffer, renderType, packedLight, 1.0F, 1.0F, 1.0F, transparency, OverlayTexture.NO_OVERLAY, entitypatch.getArmature(), armature.getPoseMatrices());

        if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
            for (Layer.Priority priority : Layer.Priority.values()) {
                AnimationPlayer animPlayer = entitypatch.getClientAnimator().getCompositeLayer(priority).animationPlayer;
                float playTime = animPlayer.getPrevElapsedTime() + (animPlayer.getElapsedTime() - animPlayer.getPrevElapsedTime()) * partialTicks;
                animPlayer.getAnimation().get().renderDebugging(poseStack, buffer, entitypatch, playTime, partialTicks);
            }
        }

        poseStack.popPose();
    }

    @Override
    public AssetAccessor<WitherMesh> getDefaultMesh() {
        return Meshes.WITHER;
    }
}
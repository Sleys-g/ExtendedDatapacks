package sleys.efedp.forge.client.render.entities;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import sleys.efedp.forge.world.entities.OwnableWitherGhost;
import yesman.epicfight.client.renderer.entity.NoopLivingEntityRenderer;
import yesman.epicfight.client.renderer.patched.entity.PWitherRenderer;

public class OwnableWitherGhostRender extends NoopLivingEntityRenderer<OwnableWitherGhost> {

    public OwnableWitherGhostRender(EntityRendererProvider.Context context) {
        super(context, 1.0F);
    }

    @Override
    protected int getBlockLightLevel(@NotNull OwnableWitherGhost witherBoss, @NotNull BlockPos blockpos) {
        return 15;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull OwnableWitherGhost entity) {
        return PWitherRenderer.WITHER_INVULNERABLE_LOCATION;
    }
}
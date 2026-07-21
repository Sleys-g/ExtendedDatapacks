package sleys.efedp.capability;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("deprecation")
public record StyleWrappedBakedModel(BakedModel original, BakedModel dynamic) implements BakedModel {

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        return dynamic.getQuads(state, side, rand);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return dynamic.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return dynamic.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return dynamic.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return dynamic.isCustomRenderer();
    }

    @Override
    public @NotNull TextureAtlasSprite getParticleIcon() {
        return dynamic.getParticleIcon();
    }

    @Override
    public @NotNull ItemTransforms getTransforms() {
        return dynamic.getTransforms();
    }

    @Override
    public @NotNull ItemOverrides getOverrides() {
        return original.getOverrides();
    }

    @Override
    public @NotNull BakedModel applyTransform(@NotNull ItemDisplayContext transformType, @NotNull PoseStack poseStack, boolean applyLeftHandTransform) {
        return dynamic.applyTransform(transformType, poseStack, applyLeftHandTransform);
    }
}
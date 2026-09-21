package sleys.efedp.neoforge.client.render.entities;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import sleys.efedp.neoforge.world.entities.OwnableClonePlayer;

import javax.annotation.Nullable;

public class OwnableClonePlayerRenderer extends MobRenderer<OwnableClonePlayer, PlayerModel<OwnableClonePlayer>> {

    private static final ResourceLocation DEFAULT_SKIN =
            ResourceLocation.withDefaultNamespace("textures/entity/player/wide/steve.png");

    public OwnableClonePlayerRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new PlayerModel<>(ctx.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
        this.addLayer(new ItemInHandLayer<>(this, ctx.getItemInHandRenderer()));
        this.addLayer(new HumanoidArmorLayer<>(
                this,
                new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
                ctx.getModelManager()
        ));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull OwnableClonePlayer entity) {
        var owner = resolveOwnerAsPlayer(entity);
        return owner != null ? owner.getSkin().texture() : DEFAULT_SKIN;
    }

    @Nullable
    private AbstractClientPlayer resolveOwnerAsPlayer(OwnableClonePlayer entity) {
        var ownerId = entity.getOwnerUUID();
        if (ownerId == null) return null;

        var owner = entity.level().getPlayerByUUID(ownerId);
        return owner instanceof AbstractClientPlayer player ? player : null;
    }
}

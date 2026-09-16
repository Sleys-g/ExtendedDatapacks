package sleys.efedp.forge.system.animations.json.properties.time.events;

import com.mojang.serialization.MapCodec;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.network.EntityPairingPacketTypes;
import yesman.epicfight.network.server.SPEntityPairingPacket;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public record AdrenalinePairEvent() implements IAnimationEventParams {

    public static final MapCodec<AdrenalinePairEvent> CODEC = MapCodec.unit(AdrenalinePairEvent::new);

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingCaster = patch.getOriginal();

        if (this.isInvalid(livingCaster.level(), AnimationEvent.Side.SERVER,"Adrenaline Pairing Event")) return;
        patch.sendToAllPlayersTrackingMe(new SPEntityPairingPacket(livingCaster.getId(), EntityPairingPacketTypes.ADRENALINE_ACTIVATED));
    }
}

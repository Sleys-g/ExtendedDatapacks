package sleys.efedp.neoforge.core.events.list.entity;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.network.PacketDistributor;
import sleys.efedp.neoforge.core.events.IAnimationEventParams;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.network.EntityPairingPacketTypes;
import yesman.epicfight.network.server.SPEntityPairingPacket;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public record ScapeEmergencePairEvent() implements IAnimationEventParams {

    public static final MapCodec<ScapeEmergencePairEvent> CODEC = MapCodec.unit(ScapeEmergencePairEvent::new);

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingCaster = patch.getOriginal();

        if (this.isInvalid(livingCaster.level(), AnimationEvent.Side.SERVER,"Scape Emergence Pair Event")) return;
        float yRot = patch.getYRot();
        SPEntityPairingPacket pairingPacket = new SPEntityPairingPacket(livingCaster.getId(), EntityPairingPacketTypes.EMERGENCY_ESCAPE_ACTIVATED);
        pairingPacket.buffer().writeFloat(yRot);

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(livingCaster, pairingPacket);
    }
}

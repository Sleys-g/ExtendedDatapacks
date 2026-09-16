package sleys.efedp.forge.system.animations.json.properties.time.events;

import com.mojang.serialization.MapCodec;
import net.minecraftforge.network.PacketDistributor;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.network.EntityPairingPacketTypes;
import yesman.epicfight.network.EpicFightNetworkManager;
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
        pairingPacket.getBuffer().writeFloat(yRot);

        EpicFightNetworkManager.sendToClient(pairingPacket, PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> livingCaster));
    }
}

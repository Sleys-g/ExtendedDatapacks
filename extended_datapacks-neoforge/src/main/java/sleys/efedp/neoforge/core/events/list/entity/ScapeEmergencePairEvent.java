package sleys.efedp.neoforge.core.events.list.entity;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import sleys.efedp.neoforge.core.events.IEventParams;
import sleys.efedp.neoforge.core.ProcessSides;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.network.EntityPairingPacketTypes;
import yesman.epicfight.network.server.SPEntityPairingPacket;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public record ScapeEmergencePairEvent() implements IEventParams {

    public static final MapCodec<ScapeEmergencePairEvent> CODEC = MapCodec.unit(ScapeEmergencePairEvent::new);

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingCaster = patch.getOriginal();
        this.executeEvent(livingCaster);
    }

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntity livingCaster) {
        this.executeEvent(livingCaster);
    }

    private void executeEvent(LivingEntity livingCaster) {
        if (this.isInvalid(livingCaster.level(), ProcessSides.SERVER,"Scape Emergence Pair Event")) return;
        float yRot = livingCaster.getYRot();

        SPEntityPairingPacket pairingPacket = new SPEntityPairingPacket(
                livingCaster.getId(),
                EntityPairingPacketTypes.EMERGENCY_ESCAPE_ACTIVATED
        );

        pairingPacket.buffer().writeFloat(yRot);

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(livingCaster, pairingPacket);
    }
}

package sleys.efedp.forge.system.animations.json.properties.time.events;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraftforge.network.PacketDistributor;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.network.EntityPairingPacketTypes;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPEntityPairingPacket;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public record FlashWhitePairEvent(int durationTick, int maxOverlay,
                                  int maxBrightness, boolean disableRedOverlay) implements IAnimationEventParams {

    public static final MapCodec<FlashWhitePairEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.INT.fieldOf("duration_tick").forGetter(FlashWhitePairEvent::durationTick),
                    Codec.INT.fieldOf("max_overlay").forGetter(FlashWhitePairEvent::maxOverlay),
                    Codec.INT.fieldOf("max_brightness").forGetter(FlashWhitePairEvent::maxOverlay),
                    Codec.BOOL.fieldOf("disable_red_overlay").forGetter(FlashWhitePairEvent::disableRedOverlay)
            ).apply(instance, FlashWhitePairEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingCaster = patch.getOriginal();

        if (this.isInvalid(livingCaster.level(), AnimationEvent.Side.SERVER,"Flash White Pair Event")) return;
        SPEntityPairingPacket pairingPacket = new SPEntityPairingPacket(livingCaster.getId(), EntityPairingPacketTypes.FLASH_WHITE);

        pairingPacket.getBuffer().writeInt(durationTick);          /// Duration Tick
        pairingPacket.getBuffer().writeInt(maxOverlay);            /// Max Overlay
        pairingPacket.getBuffer().writeInt(maxBrightness);         /// Max Brightness
        pairingPacket.getBuffer().writeBoolean(disableRedOverlay); /// Disable Red Overlay

        EpicFightNetworkManager.sendToClient(pairingPacket, PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> livingCaster));
    }
}

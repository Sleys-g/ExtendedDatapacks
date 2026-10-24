package sleys.efedp.forge.core.events.list.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.PacketDistributor;
import sleys.efedp.forge.core.events.IEventParams;
import sleys.efedp.forge.core.ProcessSides;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.network.EntityPairingPacketTypes;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPEntityPairingPacket;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public record FlashWhitePairEvent(int durationTick, int maxOverlay,
                                  int maxBrightness, boolean disableRedOverlay) implements IEventParams {

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
        this.executeEvent(livingCaster);
    }

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntity livingCaster) {
        this.executeEvent(livingCaster);
    }

    public void executeEvent(LivingEntity livingCaster) {
        if (this.isInvalid(livingCaster.level(), ProcessSides.SERVER,"Flash White Pair Event")) return;
        SPEntityPairingPacket pairingPacket = new SPEntityPairingPacket(livingCaster.getId(), EntityPairingPacketTypes.FLASH_WHITE);

        pairingPacket.getBuffer().writeInt(durationTick);          /// Duration Tick
        pairingPacket.getBuffer().writeInt(maxOverlay);            /// Max Overlay
        pairingPacket.getBuffer().writeInt(maxBrightness);         /// Max Brightness
        pairingPacket.getBuffer().writeBoolean(disableRedOverlay); /// Disable Red Overlay

        EpicFightNetworkManager.sendToClient(pairingPacket, PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> livingCaster));
    }
}

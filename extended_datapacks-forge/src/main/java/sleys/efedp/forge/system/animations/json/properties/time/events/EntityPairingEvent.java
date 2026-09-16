package sleys.efedp.forge.system.animations.json.properties.time.events;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.forge.ExtendedDatapacks;
import sleys.sl.library.annotations.Internal;
import sleys.sl.library.util.data.codec.EnumCodecs;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.network.EntityPairingPacketTypes;
import yesman.epicfight.network.server.SPEntityPairingPacket;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.Locale;
import java.util.function.BiConsumer;

public record EntityPairingEvent(PairingTypes types) implements IAnimationEventParams {

    public static final MapCodec<EntityPairingEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    PairingTypes.CODEC.fieldOf("types").forGetter(EntityPairingEvent::types)
            ).apply(instance, EntityPairingEvent::new)
    );

    @Internal
    public enum PairingTypes {
        BONE_BREAKER_BEGIN(PairingTypes::onSendPairing, EntityPairingPacketTypes.BONEBREAKER_BEGIN),
        BONE_BREAKER_MAX_STACK(PairingTypes::onSendPairing, EntityPairingPacketTypes.BONEBREAKER_MAX_STACK),
        BONE_BREAKER_CLEAR(PairingTypes::onRemovePairing, EntityPairingPacketTypes.BONEBREAKER_CLEAR),
        VENGEANCE_OVERLAY(PairingTypes::onSendPairing, EntityPairingPacketTypes.VENGEANCE_OVERLAY),
        VENGEANCE_CLEAR(PairingTypes::onRemovePairing, EntityPairingPacketTypes.VENGEANCE_TARGET_CANCEL);

        final BiConsumer<PairingTypes, LivingEntityPatch<?>> onProcessPairing;
        final EntityPairingPacketTypes precursorPairing;

        PairingTypes(BiConsumer<PairingTypes,LivingEntityPatch<?>> cancelPairing, EntityPairingPacketTypes precursorPairing) {
            this.onProcessPairing = cancelPairing;
            this.precursorPairing = precursorPairing;
        }

        private static final Codec<PairingTypes> CODEC = EnumCodecs.byId(
                values(), e -> e.name().toUpperCase(Locale.ROOT)
        );

        public void trackingAndOperate(LivingEntityPatch<?> patch) {
            this.onProcessPairing.accept(this, patch);
        }

        public static void onSendPairing(PairingTypes pairingTypes, LivingEntityPatch<?> patch) {
            patch.sendToAllPlayersTrackingMe(new SPEntityPairingPacket(patch.getOriginal().getId(), pairingTypes.precursorPairing));
        }

        public static void onRemovePairing(PairingTypes pairingTypes, LivingEntityPatch<?> patch) {
            patch.sendToAllPlayersTrackingMe(new SPEntityPairingPacket(patch.getOriginal().getId(), pairingTypes.precursorPairing));
        }
    }

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingCaster = patch.getOriginal();

        if (this.isInvalid(livingCaster.level(), AnimationEvent.Side.SERVER,"Entity Pairing Event")) return;

        var pairing = types.precursorPairing;
        if (pairing == null) {
            ExtendedDatapacks.LOGGER.warn("[Entity Pairing Event] Outrange Pairing Type in animation: '{}'", accessor);
            return;
        }

        types.trackingAndOperate(patch);
    }
}

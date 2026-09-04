package sleys.efedp.system.animations.json.properties.functional.time.lambda;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.sl.library.util.data.codec.EnumCodecs;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.network.EntityPairingPacketTypes;
import yesman.epicfight.network.server.SPEntityPairingPacket;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.Locale;

public record EntityPairingEvent(SimplyPairingTypes types) implements IAnimationEventParams {

    public static final MapCodec<EntityPairingEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    SimplyPairingTypes.CODEC.fieldOf("types").forGetter(EntityPairingEvent::types)
            ).apply(instance, EntityPairingEvent::new)
    );

    private enum SimplyPairingTypes {
        ADRENALINE_ACTIVATED,
        BONEBREAKER_BEGIN,
        BONEBREAKER_MAX_STACK,
        BONEBREAKER_CLEAR,
        STAMINA_PILLAGER_BODY_ASHES,
        TECHNICIAN_ACTIVATED,
        VENGEANCE_OVERLAY,
        VENGEANCE_TARGET_CANCEL;

        private static final Codec<SimplyPairingTypes> CODEC = EnumCodecs.byId(
                values(), e -> e.name().toUpperCase(Locale.ROOT)
        );

        private EntityPairingPacketTypes thisToEntityPairingPacketTypes() {
            return EntityPairingPacketTypes.valueOf(this.name().toUpperCase(Locale.ROOT));
        }
    }


    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingCaster = patch.getOriginal();

        if (this.isInvalid(livingCaster.level(), AnimationEvent.Side.SERVER,"Entity Pairing Event")) return;
        patch.sendToAllPlayersTrackingMe(new SPEntityPairingPacket(livingCaster.getId(), types.thisToEntityPairingPacketTypes()));
    }
}

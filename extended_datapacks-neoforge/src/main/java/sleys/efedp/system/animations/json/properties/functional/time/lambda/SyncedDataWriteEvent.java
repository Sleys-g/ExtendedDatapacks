package sleys.efedp.system.animations.json.properties.functional.time.lambda;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import sleys.efedp.system.animations.json.properties.functional.datapackets.write.IDataWriter;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.List;

public record SyncedDataWriteEvent(List<IDataWriter> writers) implements IAnimationEventParams {

    public static final MapCodec<SyncedDataWriteEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    IDataWriter.CODEC.codec().listOf().optionalFieldOf("write_data", List.of()).forGetter(SyncedDataWriteEvent::writers)
            ).apply(instance, SyncedDataWriteEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingEntity = patch.getOriginal();
        var level = livingEntity.level();
        if (!(livingEntity instanceof Player player)) return;
        if (this.isInvalid(level, AnimationEvent.Side.BOTH, "Synced Write Data")) return;
        writers.forEach(iDataWriter -> iDataWriter.writeSyncData(player));
    }
}

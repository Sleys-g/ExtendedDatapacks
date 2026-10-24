package sleys.efedp.forge.core.events.list.data;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import sleys.efedp.forge.core.datapackets.write.IDataWriter;
import sleys.efedp.forge.core.events.IEventParams;
import sleys.efedp.forge.core.ProcessSides;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.List;

public record SyncedDataWriteEvent(List<IDataWriter> writers) implements IEventParams {

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
        if (this.isInvalid(level, ProcessSides.BOTH, "Synced Write Data Event")) return;
        writers.forEach(iDataWriter -> iDataWriter.writeSyncData(player));
    }

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntity livingEntity) {
        var level = livingEntity.level();
        if (!(livingEntity instanceof Player player)) return;
        if (this.isInvalid(level, ProcessSides.BOTH, "Synced Write Data Event")) return;
        writers.forEach(iDataWriter -> iDataWriter.writeSyncData(player));
    }
}

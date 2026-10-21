package sleys.efedp.neoforge.core.events.list.data;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import sleys.efedp.neoforge.core.datapackets.ReadDataPacketsGroup;
import sleys.efedp.neoforge.core.events.EventInvocation;
import sleys.efedp.neoforge.core.events.IEventParams;
import sleys.efedp.neoforge.core.ProcessSides;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.List;

public record SyncedBranchedDataReadEvent(ReadDataPacketsGroup readData,
                                          List<EventInvocation> then,
                                          List<EventInvocation> otherwise) implements IEventParams {

    public static final MapCodec<SyncedBranchedDataReadEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ReadDataPacketsGroup.CODEC.fieldOf("read_data").forGetter(SyncedBranchedDataReadEvent::readData),
                    EventInvocation.CODEC.listOf().optionalFieldOf("then", List.of()).forGetter(SyncedBranchedDataReadEvent::then),
                    EventInvocation.CODEC.listOf().optionalFieldOf("otherwise", List.of()).forGetter(SyncedBranchedDataReadEvent::otherwise)
            ).apply(instance, SyncedBranchedDataReadEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingEntity = patch.getOriginal();
        var level = livingEntity.level();
        if (!(livingEntity instanceof Player player)) return;
        if (this.isInvalid(level, ProcessSides.BOTH, "Synced Branched Read Data Event")) return;

        boolean result = readData.syncedEvaluate(player);
        var branch = result ? then : otherwise;
        branch.forEach(invocation -> invocation.execute(accessor, patch));
    }

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntity livingEntity) {
        var level = livingEntity.level();
        if (!(livingEntity instanceof Player player)) return;
        if (this.isInvalid(level, ProcessSides.BOTH, "Synced Branched Read Data Event")) return;

        boolean result = readData.syncedEvaluate(player);
        var branch = result ? then : otherwise;
        branch.forEach(invocation -> invocation.execute(accessor, livingEntity));
    }
}
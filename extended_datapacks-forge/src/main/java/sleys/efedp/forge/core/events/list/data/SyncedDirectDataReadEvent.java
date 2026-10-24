package sleys.efedp.forge.core.events.list.data;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import sleys.efedp.forge.core.datapackets.ReadDataPacketsGroup;
import sleys.efedp.forge.core.events.IEventParams;
import sleys.efedp.forge.core.events.EventInvocation;
import sleys.efedp.forge.core.ProcessSides;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.List;

public record SyncedDirectDataReadEvent(ReadDataPacketsGroup readData,
                                        List<EventInvocation> doEvents) implements IEventParams {

    public static final MapCodec<SyncedDirectDataReadEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ReadDataPacketsGroup.CODEC.fieldOf("read_data").forGetter(SyncedDirectDataReadEvent::readData),
                    EventInvocation.CODEC.listOf().optionalFieldOf("do", List.of()).forGetter(SyncedDirectDataReadEvent::doEvents)
            ).apply(instance, SyncedDirectDataReadEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingEntity = patch.getOriginal();
        var level = livingEntity.level();
        if (!(livingEntity instanceof Player player)) return;
        if (this.isInvalid(level, ProcessSides.BOTH, "Synced Direct Data Read Event")) return;

        boolean result = readData.syncedEvaluate(player);
        if (result) doEvents.forEach(events -> events.execute(accessor, patch));
    }

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntity livingEntity) {
        var level = livingEntity.level();
        if (!(livingEntity instanceof Player player)) return;
        if (this.isInvalid(level, ProcessSides.BOTH, "Synced Direct Data Read Event")) return;

        boolean result = readData.syncedEvaluate(player);
        if (result) doEvents.forEach(events -> events.execute(accessor, livingEntity));
    }
}
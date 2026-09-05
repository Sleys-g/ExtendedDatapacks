package sleys.efedp.system.animations.json.properties.functional.time.lambda;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import sleys.efedp.system.animations.json.properties.functional.datapackets.ReadDataPacketsGroup;
import sleys.efedp.system.animations.json.properties.functional.time.AnimationsEventInvocation;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.List;

public record SyncedBranchedDataReadEvent(ReadDataPacketsGroup readData,
                                          List<AnimationsEventInvocation> then,
                                          List<AnimationsEventInvocation> otherwise) implements IAnimationEventParams {

    public static final MapCodec<SyncedBranchedDataReadEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ReadDataPacketsGroup.CODEC.fieldOf("read_data").forGetter(SyncedBranchedDataReadEvent::readData),
                    AnimationsEventInvocation.CODEC.listOf().optionalFieldOf("then", List.of()).forGetter(SyncedBranchedDataReadEvent::then),
                    AnimationsEventInvocation.CODEC.listOf().optionalFieldOf("otherwise", List.of()).forGetter(SyncedBranchedDataReadEvent::otherwise)
            ).apply(instance, SyncedBranchedDataReadEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingEntity = patch.getOriginal();
        var level = livingEntity.level();
        if (!(livingEntity instanceof Player player)) return;
        if (this.isInvalid(level, AnimationEvent.Side.BOTH, "Synced Branched Read Data Event")) return;

        boolean result = readData.syncedEvaluate(player);
        var branch = result ? then : otherwise;
        branch.forEach(invocation -> invocation.execute(accessor, patch));
    }
}
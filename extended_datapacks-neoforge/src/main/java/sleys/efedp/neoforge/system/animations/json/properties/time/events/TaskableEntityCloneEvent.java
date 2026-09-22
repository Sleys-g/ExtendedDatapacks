package sleys.efedp.neoforge.system.animations.json.properties.time.events;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import sleys.efedp.neoforge.world.entities.OwnableClonePlayer;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.Optional;

public record TaskableEntityCloneEvent(int duration,
                                       Optional<Float> damageFactor,
                                       Optional<Double> lateralOffset,
                                       Optional<Double> verticalOffset,
                                       Optional<Double> avanceOffset) implements IAnimationEventParams {

    public static final MapCodec<TaskableEntityCloneEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.INT.fieldOf("duration").forGetter(TaskableEntityCloneEvent::duration),
                    Codec.FLOAT.optionalFieldOf("damage_factor").forGetter(TaskableEntityCloneEvent::damageFactor),
                    Codec.DOUBLE.optionalFieldOf("lateral_offset").forGetter(TaskableEntityCloneEvent::lateralOffset),
                    Codec.DOUBLE.optionalFieldOf("vertical_offset").forGetter(TaskableEntityCloneEvent::verticalOffset),
                    Codec.DOUBLE.optionalFieldOf("avance_offset").forGetter(TaskableEntityCloneEvent::avanceOffset)
            ).apply(instance, TaskableEntityCloneEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingCaster = patch.getOriginal();
        if (!(livingCaster instanceof Player player)) return; /// Fix funny entity-recursivity call
        if (this.isInvalid(livingCaster.level(), AnimationEvent.Side.SERVER, "Taskable Entity Clone Event")) return;

        var level = (ServerLevel) player.level();
        var cloneEntity = new OwnableClonePlayer(
                level, new Vec3(lateralOffset.orElse(0.0), verticalOffset.orElse(0.0), avanceOffset.orElse(0.0)),
                player.position(), player, this.duration()
        );
        damageFactor.ifPresent(cloneEntity::setMultiplier);
        level.addFreshEntity(cloneEntity);
    }
}
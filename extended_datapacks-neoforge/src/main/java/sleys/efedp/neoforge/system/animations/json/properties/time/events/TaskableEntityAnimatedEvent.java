package sleys.efedp.neoforge.system.animations.json.properties.time.events;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import sleys.efedp.neoforge.world.entities.OwnableAnimatedPlayer;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.Optional;

public record TaskableEntityAnimatedEvent(ResourceLocation animation,
                                          Optional<Float> damageFactor,
                                          Optional<Double> lateralOffset,
                                          Optional<Double> verticalOffset,
                                          Optional<Double> avanceOffset) implements IAnimationEventParams {

    public static final MapCodec<TaskableEntityAnimatedEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("animation").forGetter(TaskableEntityAnimatedEvent::animation),
                    Codec.FLOAT.optionalFieldOf("damage_factor").forGetter(TaskableEntityAnimatedEvent::damageFactor),
                    Codec.DOUBLE.optionalFieldOf("lateral_offset").forGetter(TaskableEntityAnimatedEvent::lateralOffset),
                    Codec.DOUBLE.optionalFieldOf("vertical_offset").forGetter(TaskableEntityAnimatedEvent::verticalOffset),
                    Codec.DOUBLE.optionalFieldOf("avance_offset").forGetter(TaskableEntityAnimatedEvent::avanceOffset)
            ).apply(instance, TaskableEntityAnimatedEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingCaster = patch.getOriginal();
        if (!(livingCaster instanceof Player player)) return; /// Fix funny entity-recursivity call
        if (this.isInvalid(livingCaster.level(), AnimationEvent.Side.SERVER, "Taskable Entity Clone Event")) return;

        var level = (ServerLevel) player.level();
        var animatedEntity = new OwnableAnimatedPlayer(
                level, new Vec3(lateralOffset.orElse(0.0), verticalOffset.orElse(0.0), avanceOffset.orElse(0.0)),
                player.position(), player, patch.getTarget(), AnimationManager.byKey(animation)
        );
        damageFactor.ifPresent(animatedEntity::setMultiplier);
        level.addFreshEntity(animatedEntity);
    }
}

package sleys.efedp.forge.core.events.list.game;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import sleys.efedp.forge.core.events.IEventParams;
import sleys.efedp.forge.core.events.EventInvocation;
import sleys.efedp.forge.core.ProcessSides;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public record RandomDiceEvent(float chance, List<EventInvocation> doEvents) implements IEventParams {

    public static final MapCodec<RandomDiceEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("chance").forGetter(RandomDiceEvent::chance),
                    EventInvocation.CODEC.listOf().optionalFieldOf("do", List.of()).forGetter(RandomDiceEvent::doEvents)
            ).apply(instance, RandomDiceEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingEntity = patch.getOriginal();
        var level = livingEntity.level();
        if (this.isInvalid(level, ProcessSides.SERVER, "Random Dice Event")) return;

        if (ThreadLocalRandom.current().nextFloat(100.0f) < chance) {
            doEvents.forEach(events -> events.execute(accessor, patch));
        }
    }

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntity livingEntity) {
        var level = livingEntity.level();
        if (this.isInvalid(level, ProcessSides.SERVER, "Random Dice Event")) return;

        if (ThreadLocalRandom.current().nextFloat(100.0f) < chance) {
            doEvents.forEach(events -> events.execute(accessor, livingEntity));
        }
    }
}

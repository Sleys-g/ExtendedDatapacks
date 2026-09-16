package sleys.efedp.forge.system.animations.json.properties.time.events;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import sleys.efedp.forge.ExtendedDatapacks;
import sleys.efedp.forge.system.animations.json.properties.time.AnimationsEventInvocation;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.List;

public record ItemStringDataReadEvent(String key,
                                      String value,
                                      List<AnimationsEventInvocation> doEvents) implements IAnimationEventParams {

    public static final MapCodec<ItemStringDataReadEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.STRING.fieldOf("key").forGetter(ItemStringDataReadEvent::key),
                    Codec.STRING.fieldOf("value").forGetter(ItemStringDataReadEvent::value),
                    AnimationsEventInvocation.CODEC.listOf().optionalFieldOf("do", List.of()).forGetter(ItemStringDataReadEvent::doEvents)
            ).apply(instance, ItemStringDataReadEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var caster = patch.getOriginal();
        if (this.isInvalid(caster.level(), AnimationEvent.Side.SERVER, "Item String Data Read Event")) return;
        if (!(caster.level() instanceof ServerLevel)) return;

        if (this.applyMatch(caster)) {
            doEvents.forEach(events -> events.execute(accessor, patch));
        }
    }

    private boolean applyMatch(LivingEntity entity) {
        var stack = entity.getMainHandItem();
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(key)) {
            ExtendedDatapacks.LOGGER.warn(
                    "[Item String Data Read Event] The requested NBT key was not found on the item: {}", key
            );
            return false;
        }

        Tag actual = tag.get(key);
        if (actual == null) {
            ExtendedDatapacks.LOGGER.warn(
                    "[Item String Data Read Event] The result of the data retrieval operation is null, the animation cannot proceed: {}", key
            );
            return false;
        }

        if (actual instanceof StringTag stringTag) {
            return stringTag.getAsString().equals(value.trim()) || stringTag.getAsString().contains(value);
        }
        if (actual instanceof NumericTag numericTag) {
            return numericTag.getAsString().equals(value.trim());
        }

        return actual.toString().contains(value);
    }
}

package sleys.efedp.forge.core.control;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraftforge.eventbus.api.Event;
import sleys.sl.library.annotations.Internal;
import yesman.epicfight.skill.SkillContainer;

@Internal
public interface IControlableEventParams<E extends Event> {
    void applyEventParams(E event, SkillContainer container);

    @SuppressWarnings({"unchecked", "rawtypes"})
    static <E extends Event> Codec<IControlableEventParams<E>> paramsCodec(MapCodec<? extends IControlableEventParams<E>> codec) {
        return (Codec) codec.codec();
    }
}

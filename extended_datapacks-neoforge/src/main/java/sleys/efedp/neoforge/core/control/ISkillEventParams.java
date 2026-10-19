package sleys.efedp.neoforge.core.control;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.Event;
import sleys.sl.library.annotations.Internal;
import yesman.epicfight.skill.SkillContainer;

@Internal
public interface ISkillEventParams<E extends Event> {
    void applyEventParams(E event, SkillContainer container);

    @SuppressWarnings({"unchecked", "rawtypes"})
    static <E extends Event> Codec<ISkillEventParams<E>> paramsCodec(MapCodec<? extends ISkillEventParams<E>> codec) {
        return (Codec) codec.codec();
    }
}

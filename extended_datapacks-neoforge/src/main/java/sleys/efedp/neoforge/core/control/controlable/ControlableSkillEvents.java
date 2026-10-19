package sleys.efedp.neoforge.core.control.controlable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.neoforged.bus.api.Event;
import sleys.efedp.neoforge.core.control.ISkillEventController;
import sleys.efedp.neoforge.core.control.registry.ControlableSkillEventTypeRegistry;
import sleys.efedp.neoforge.core.control.registry.IControlableSkillEventType;
import yesman.epicfight.skill.SkillContainer;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record ControlableSkillEvents(Map<IControlableSkillEventType, ISkillEventController<?>> byType) {

    public static final ControlableSkillEvents EMPTY = new ControlableSkillEvents(Map.of());

    public static final Codec<ControlableSkillEvents> CODEC =
            ControlableSkillEvent.CODEC.listOf().flatXmap(
                    ControlableSkillEvents::fromList,
                    events -> DataResult.success(events.toList())
            );

    public static DataResult<ControlableSkillEvents> fromList(List<ControlableSkillEvent> list) {
        Map<IControlableSkillEventType, ISkillEventController<?>> map = new LinkedHashMap<>();

        for (ControlableSkillEvent entry : list) {
            if (map.putIfAbsent(entry.type(), entry.controller()) != null) {
                String key = ControlableSkillEventTypeRegistry.keyOf(entry.type());
                return DataResult.error(
                        () -> "Duplicate controlable skill event '" + key + "': only one controller per event type is allowed per skill"
                );
            }
        }
        return DataResult.success(new ControlableSkillEvents(Collections.unmodifiableMap(map)));
    }

    public List<ControlableSkillEvent> toList() {
        return byType.entrySet().stream()
                .map(e -> new ControlableSkillEvent(e.getKey(), e.getValue()))
                .toList();
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> void run(IControlableSkillEventType type, T event, SkillContainer container) {
        ISkillEventController<?> controller = byType.get(type);
        if (controller == null) return;
        type.runEvent((ISkillEventController<T>) controller, event, container);
    }
}

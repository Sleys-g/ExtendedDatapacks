package sleys.efedp.neoforge.core.control.controlable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.neoforged.bus.api.Event;
import sleys.efedp.neoforge.core.control.registry.ControlableEventTypeRegistry;
import sleys.efedp.neoforge.core.control.registry.IControlableEventType;
import yesman.epicfight.skill.SkillContainer;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record ControlableEvents(Map<IControlableEventType, IEventController<?>> byType) {

    public static final ControlableEvents EMPTY = new ControlableEvents(Map.of());

    public static final Codec<ControlableEvents> CODEC =
            ControlableEvent.CODEC.listOf().flatXmap(
                    ControlableEvents::fromList,
                    events -> DataResult.success(events.toList())
            );

    public static DataResult<ControlableEvents> fromList(List<ControlableEvent> list) {
        Map<IControlableEventType, IEventController<?>> map = new LinkedHashMap<>();

        for (ControlableEvent entry : list) {
            if (map.putIfAbsent(entry.type(), entry.controller()) != null) {
                String key = ControlableEventTypeRegistry.keyOf(entry.type());
                return DataResult.error(
                        () -> "Duplicate controlable skill event '" + key + "': only one controller per event type is allowed per skill"
                );
            }
        }
        return DataResult.success(new ControlableEvents(Collections.unmodifiableMap(map)));
    }

    public List<ControlableEvent> toList() {
        return byType.entrySet().stream()
                .map(e -> new ControlableEvent(e.getKey(), e.getValue()))
                .toList();
    }

    @SuppressWarnings("unchecked")
    public <E extends Event> void run(IControlableEventType type, E event, SkillContainer container) {
        IEventController<?> controller = byType.get(type);
        if (controller == null) return;
        type.runEvent((IEventController<E>) controller, event, container);
    }
}

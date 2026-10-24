package sleys.efedp.forge.core.control.registry;

import sleys.sl.library.util.io.FriendlyErrorBuilder;

import java.util.List;
import java.util.Map;

final class ControlableEventTypeRegistryDiagnostics {

    private ControlableEventTypeRegistryDiagnostics() {}

    static String describeUnknownKey(String failedKey, Map<String, IControlableEventType> byKey) {
        StringBuilder sb = new StringBuilder("Unknown controlable event type: '" + failedKey + "'");

        List<String> suggestions = FriendlyErrorBuilder.findClosestMatches(failedKey, byKey.keySet());
        if (!suggestions.isEmpty()) {
            sb.append("\n\nMaybe you meant?: ")
                    .append(String.join(", ", suggestions));
        }

        sb.append("\n\nRegistered Controlable Event Types:\n")
                .append(FriendlyErrorBuilder.formatRegisteredTypes(byKey.keySet()));

        sb.append("\n\nType example: epicfight_edp:living_heal_skill_event\n");

        return sb.toString();
    }
}

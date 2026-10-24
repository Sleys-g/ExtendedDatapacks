package sleys.efedp.neoforge.core.events.registry;

import sleys.sl.library.util.io.FriendlyErrorBuilder;

import java.util.*;

final class EventTypeRegistryDiagnostics {

    private EventTypeRegistryDiagnostics() {}

    static String describeUnknownKey(String failedKey, Map<String, IEventType> byKey) {
        StringBuilder sb = new StringBuilder("Unknown event type: '" + failedKey + "'");

        List<String> suggestions = FriendlyErrorBuilder.findClosestMatches(failedKey, byKey.keySet());
        if (!suggestions.isEmpty()) {
            sb.append("\n\nMaybe you meant?: ")
                    .append(String.join(", ", suggestions));
        }

        sb.append("\n\nRegistered Event Types:\n")
                .append(FriendlyErrorBuilder.formatRegisteredTypes(byKey.keySet()));

        sb.append("\n\nType example: epicfight_edp:thunder\n");

        return sb.toString();
    }
}

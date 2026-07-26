package sleys.efedp.system.animations.json.properties.functional.time;

import sleys.sl.library.util.io.FriendlyErrorBuilder;

import java.util.*;

final class AnimationEventTypeRegistryDiagnostics {

    private AnimationEventTypeRegistryDiagnostics() {}

    static String describeUnknownKey(String failedKey, Map<String, IAnimationEventType> byKey) {
        StringBuilder sb = new StringBuilder("Unknown animation event type: '" + failedKey + "'");

        List<String> suggestions = FriendlyErrorBuilder.findClosestMatches(failedKey, byKey.keySet());
        if (!suggestions.isEmpty()) {
            sb.append("\n\nMaybe you meant?: ")
                    .append(String.join(", ", suggestions));
        }

        sb.append("\n\nRegistered Animation Event Types:\n")
                .append(FriendlyErrorBuilder.formatRegisteredTypes(byKey.keySet()));

        sb.append("\n\nType example: epicfight_edp:thunder\n");

        return sb.toString();
    }
}

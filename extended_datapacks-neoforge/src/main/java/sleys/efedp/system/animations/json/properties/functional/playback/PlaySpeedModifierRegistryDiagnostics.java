package sleys.efedp.system.animations.json.properties.functional.playback;

import sleys.efedp.system.animations.json.properties.functional.time.IAnimationEventType;
import sleys.sl.library.util.io.FriendlyErrorBuilder;

import java.util.List;
import java.util.Map;

final class PlaySpeedModifierRegistryDiagnostics {

    private PlaySpeedModifierRegistryDiagnostics() {}

    static String describeUnknownKey(String failedKey, Map<String, IPlaySpeedModifierType> byKey) {
        StringBuilder sb = new StringBuilder("Unknown playback speed modifier: '" + failedKey + "'");

        List<String> suggestions = FriendlyErrorBuilder.findClosestMatches(failedKey, byKey.keySet());
        if (!suggestions.isEmpty()) {
            sb.append("\n\nMaybe you meant?: ")
                    .append(String.join(", ", suggestions));
        }

        sb.append("\n\nRegistered Playback Speed Modifier:\n")
                .append(FriendlyErrorBuilder.formatRegisteredTypes(byKey.keySet()));

        sb.append("\n\nType example: epicfight_edp:air_loop\n");

        return sb.toString();
    }
}

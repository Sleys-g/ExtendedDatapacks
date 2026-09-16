package sleys.efedp.neoforge.system.animations.json.groups.registry;

import sleys.sl.library.util.io.FriendlyErrorBuilder;

import java.util.List;
import java.util.Map;

final class AnimationGroupDiagnostics {

    private AnimationGroupDiagnostics() {}

    static String describeUnknownKey(String failedKey, Map<String, IAnimationGroupType> byKey) {
        StringBuilder sb = new StringBuilder("Unknown animation group type: '" + failedKey + "'");

        List<String> suggestions = FriendlyErrorBuilder.findClosestMatches(failedKey, byKey.keySet());
        if (!suggestions.isEmpty()) {
            sb.append("\n\nMaybe you meant?: ")
                    .append(String.join(", ", suggestions));
        }

        sb.append("\n\nAnimation Accessor Types:\n")
                .append(FriendlyErrorBuilder.formatRegisteredTypes(byKey.keySet()));

        sb.append("\n\nType example: epicfight_edp:static_group\n");

        return sb.toString();
    }
}

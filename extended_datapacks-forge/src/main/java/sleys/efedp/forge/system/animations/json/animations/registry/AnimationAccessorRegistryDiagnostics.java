package sleys.efedp.forge.system.animations.json.animations.registry;

import sleys.sl.library.util.io.FriendlyErrorBuilder;

import java.util.List;
import java.util.Map;

final class AnimationAccessorRegistryDiagnostics {

    private AnimationAccessorRegistryDiagnostics() {}

    static String describeUnknownKey(String failedKey, Map<String, IAnimationAccessorType> byKey) {
        StringBuilder sb = new StringBuilder("Unknown animation accessor type: '" + failedKey + "'");

        List<String> suggestions = FriendlyErrorBuilder.findClosestMatches(failedKey, byKey.keySet());
        if (!suggestions.isEmpty()) {
            sb.append("\n\nMaybe you meant?: ")
                    .append(String.join(", ", suggestions));
        }

        sb.append("\n\nAnimation Accessor Types:\n")
                .append(FriendlyErrorBuilder.formatRegisteredTypes(byKey.keySet()));

        sb.append("\n\nType example: epicfight_edp:attack\n");

        return sb.toString();
    }
}

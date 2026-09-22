package sleys.efedp.neoforge.system.animations.json.properties.armature.registry;

import sleys.sl.library.util.io.FriendlyErrorBuilder;

import java.util.List;
import java.util.Map;

final class ArmatureTypeRegistryDiagnostics {

    private ArmatureTypeRegistryDiagnostics() {}

    static String describeUnknownKey(String failedKey, Map<String, IArmatureType> byKey) {
        StringBuilder sb = new StringBuilder("Unknown armature type: '" + failedKey + "'");

        List<String> suggestions = FriendlyErrorBuilder.findClosestMatches(failedKey, byKey.keySet());
        if (!suggestions.isEmpty()) {
            sb.append("\n\nMaybe you meant?: ")
                    .append(String.join(", ", suggestions));
        }

        sb.append("\n\nRegistered Armature Types:\n")
                .append(FriendlyErrorBuilder.formatRegisteredTypes(byKey.keySet()));

        sb.append("\n\nType example: epicfight:entity/biped\n");

        return sb.toString();
    }
}

package sleys.efedp.system.animations.json.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ConfigAnimationsErrorPool {
    private static final List<String> RUNTIME_ERRORS = Collections.synchronizedList(new ArrayList<>());

    static void addError(String errorKey) {
        RUNTIME_ERRORS.add(errorKey);
    }

    public static boolean hasErrors() {
        return !RUNTIME_ERRORS.isEmpty();
    }

    public static String getConfigError() {
        if (RUNTIME_ERRORS.isEmpty()) return null;
        return "Failure during the operation to config a Animation...\n" +
                "Total number of modification failures: " + RUNTIME_ERRORS.size() +
                "\n\nProblematic process\n\n" + String.join("\n", RUNTIME_ERRORS);
    }
}

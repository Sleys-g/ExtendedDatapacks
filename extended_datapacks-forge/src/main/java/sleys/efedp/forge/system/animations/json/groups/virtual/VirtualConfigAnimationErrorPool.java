package sleys.efedp.forge.system.animations.json.groups.virtual;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VirtualConfigAnimationErrorPool {
    private static final List<String> RUNTIME_ERRORS = Collections.synchronizedList(new ArrayList<>());

    static void addError(String errorKey) {
        RUNTIME_ERRORS.add(errorKey);
    }

    public static boolean hasErrors() {
        return !RUNTIME_ERRORS.isEmpty();
    }

    public static String getVirtualConfigError() {
        if (RUNTIME_ERRORS.isEmpty()) return null;
        return "Failure during the operation to config a Virtual Animation...\n" +
                "Total number of modification failures: " + RUNTIME_ERRORS.size() +
                "\n\nProblematic process\n\n" + String.join("\n", RUNTIME_ERRORS);
    }
}
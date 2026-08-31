package sleys.efedp.system.thirdparty.combatevolution.json;

import javax.annotation.Nullable;

public final class RegistryCombatEvolutionErrorHelper {

    public enum ErrorsCombatEvolutionType {
        REGISTRY_BUILDER,
        NULL_ANIMATION_KEY,
        UNPARSEABLE
    }

    public static String getError(ErrorsCombatEvolutionType type, Object errorComodin, @Nullable Object errorExtra) {
        return switch (type) {
            case UNPARSEABLE -> String.format("""
                    Execution Animation Registry
                    Animations JSON entry: %s
                    [Unparseable Entry] The value assigned to this animation entry ("%s") could not be resolved into a valid ResourceLocation.
                    
                    """, errorComodin, errorComodin);

            case NULL_ANIMATION_KEY -> String.format("""
                    Execution Animation Registry
                    Animation ID: %s
                    [Null Animation Key] Registration aborted: the animation key is null for animation ID "%s".
                    
                    """, errorComodin, errorComodin);

            case REGISTRY_BUILDER -> String.format("""
                    Execution Animation Registry
                    Animation ID: %s
                    [Registry Builder Error] Registration failed while assigning properties. This usually means one or more property values are invalid, or the supplied object is null.
                    Primary cause: %s
                    
                    """, errorComodin, errorExtra);
        };
    }
}
package sleys.efedp.helper;

import net.minecraft.resources.ResourceLocation;
import sleys.sl.epicfight.util.helper.animation.AnimationHelper;

import javax.annotation.Nullable;

public final class RegistryErrorHelper extends AnimationHelper {

    public enum ErrorsType {
        DUPE,
        REGISTRY_BUILDER,
        NULL_ANIMATION_KEY,
        UNPARSEABLE
    }

    public static String getError(ErrorsType type, String name, String modId, Object errorComodin, @Nullable Object errorExtra) {
        return switch (type) {
            case UNPARSEABLE -> String.format("""
                    Skill name: %s
                    Domain space: %s
                    Animations JSON entry: %s
                    [Unparseable Entry] The value assigned to this animation entry ("%s") could not be resolved into a valid ResourceLocation.
                    
                    """, name, modId, errorComodin, errorComodin);

            case NULL_ANIMATION_KEY -> String.format("""
                    Skill name: %s
                    Domain space: %s
                    Animation ID: %s
                    [Null Animation Key] Registration aborted: the animation key is null for animation ID "%s".
                    
                    """, name, modId, errorComodin, errorComodin);

            case REGISTRY_BUILDER -> String.format("""
                    Skill name: %s
                    Domain space: %s
                    Animation ID: %s
                    [Registry Builder Error] Registration failed while assigning properties. This usually means one or more property values are invalid, or the supplied object is null.
                    Primary cause: %s
                    
                    """, name, modId, errorComodin, errorExtra);

            case DUPE -> String.format("""
                    Skill name: %s
                    Domain space: %s
                    Animation ID: %s
                    [Registry Builder Error] Registration failed: the ResourceLocation "%s" is already registered.
                    Primary cause: %s
                    
                    """, name, modId, errorComodin,
                    ResourceLocation.fromNamespaceAndPath(modId, name), errorExtra);
        };
    }
}
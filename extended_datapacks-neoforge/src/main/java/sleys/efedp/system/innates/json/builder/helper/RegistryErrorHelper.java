package sleys.efedp.system.innates.json.builder.helper;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;
import yesman.epicfight.skill.Skill;

import javax.annotation.Nullable;
import java.util.List;

public final class RegistryErrorHelper {

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

    public static Skill handleRegistrationError(DeferredRegister<Skill> registry,
                                                 String modId, String name,
                                                 Object animationId,
                                                 List<String> errors,
                                                 Exception e) {
        boolean isDupe = registry
                .getEntries()
                .stream()
                .anyMatch(entry ->
                        ResourceLocation.fromNamespaceAndPath(modId, name).equals(entry.get().getRegistryName())
                );

        errors.add(RegistryErrorHelper.getError(
                isDupe ? RegistryErrorHelper.ErrorsType.DUPE : RegistryErrorHelper.ErrorsType.REGISTRY_BUILDER,
                name, modId, animationId, e.getCause()));

        return Skill.EMPTY;
    }
}
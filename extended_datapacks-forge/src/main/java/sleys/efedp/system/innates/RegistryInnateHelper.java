package sleys.efedp.system.innates;

import net.minecraft.resources.ResourceLocation;
import sleys.sl.epicfight.helper.animation.AnimationHelper;

import javax.annotation.Nullable;

public final class RegistryInnateHelper extends AnimationHelper {

    public enum ErrorsType {
        DUPE,
        REGISTRY_BUILDER,
        NULL_ANIMATION_KEY,
        UNPARSEABLE;
    }

    public static String getError(ErrorsType type, String name, String modId, Object errorComodin, @Nullable Object errorExtra) {
        switch (type) {
            case UNPARSEABLE -> {
                return String.format(
                        "Skill name: " + name +
                                "\nDomain Space: " + modId +
                                "\nAnimations JSON Entry: " + errorComodin +
                                "\n[Unparseable Entry Error] The address assigned as animation: " + errorComodin + " is not valid because it cannot be converted into a Resource Location...\n\n"
                );
            }
            case NULL_ANIMATION_KEY -> {
                return  String.format(
                        "Skill name: " + name +
                                "\nDomain Space: " + modId +
                                "\nAnimations ID: " + errorComodin +
                                "\n[Null Animation-Key Error] The function cannot be executed because animationKey is: null for its animationId: " + errorComodin + "\n\n"
                );
            }
            case REGISTRY_BUILDER -> {
                return String.format(
                        "Skill name: " + name +
                                "\nDomain Space: " + modId +
                                "\nAnimations ID: " + errorComodin +
                                "\n[Registry-Builder error] During the property assignment process, the registry may bounce. This occurs because you have assigned incorrect values to the properties or your animation is not of type Animation...\n\n"+
                                "\nPrimary cause: " + errorExtra
                );
            }
            case DUPE -> {
                return String.format(
                        "Skill name: " + name +
                                "\nDomain Space: " + modId +
                                "\nAnimations ID: " + errorComodin +
                                "\n[Registry-Builder error] The registration attempt bounced because the ResourceLocation address exists in memory and is duplicated for: " + ResourceLocation.fromNamespaceAndPath(modId, name) + "\n\n"+
                                "\nPrimary cause: " + errorExtra
                );
            }
        }
        return "";
    }
}

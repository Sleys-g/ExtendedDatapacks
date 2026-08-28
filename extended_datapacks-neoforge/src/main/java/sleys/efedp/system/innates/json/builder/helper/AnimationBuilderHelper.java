package sleys.efedp.system.innates.json.builder.helper;

import net.minecraft.resources.ResourceLocation;
import sleys.efedp.system.innates.json.builder.data.ConditionalType;
import sleys.sl.epicfight.util.helper.animation.AnimationHelper;
import sleys.sl.epicfight.util.helper.animation.VirtualAnimationRegistry;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;

import java.util.List;
import java.util.Locale;

public final class AnimationBuilderHelper extends AnimationHelper {

    public static AnimationManager.AnimationAccessor<? extends StaticAnimation> resolveAnimation(
            String modId, String name, String postfix, ResourceLocation animationId, List<String> errors) {

        var virtualAnimationId = VirtualAnimationRegistry.getParsedSkill(modId, name + "_" + postfix);
        VirtualAnimationRegistry.manualVirtualizationProtocol(animationId, virtualAnimationId);

        var animationKey = AnimationManager.byKey(virtualAnimationId);
        if (animationKey == null) {
            errors.add(RegistryErrorHelper.getError(
                    RegistryErrorHelper.ErrorsType.NULL_ANIMATION_KEY, name, modId, virtualAnimationId, null));
            return null;
        }

        return AnimationHelper.getStaticAnimationAccessor(animationKey);
    }

    public static AnimationManager.AnimationAccessor<? extends StaticAnimation> resolveAnimation(
            String modId, String name, ResourceLocation animationId, List<String> errors) {

        var virtualAnimationId = VirtualAnimationRegistry.getParsedSkill(modId, name);
        VirtualAnimationRegistry.manualVirtualizationProtocol(animationId, virtualAnimationId);

        var animationKey = AnimationManager.byKey(virtualAnimationId);
        if (animationKey == null) {
            errors.add(RegistryErrorHelper.getError(
                    RegistryErrorHelper.ErrorsType.NULL_ANIMATION_KEY, name, modId, virtualAnimationId, null));
            return null;
        }

        return AnimationHelper.getStaticAnimationAccessor(animationKey);
    }

    public static AnimationManager.AnimationAccessor<? extends StaticAnimation> resolveAnimation(
            String modId, String name, ConditionalType type, ResourceLocation animationId, List<String> errors) {
        var stringKey = type.toString().toLowerCase(Locale.ROOT);
        var virtualAnimationId = VirtualAnimationRegistry.getParsedSkill(modId, name + "_" + stringKey);
        VirtualAnimationRegistry.manualVirtualizationProtocol(animationId, virtualAnimationId);

        var animationKey = AnimationManager.byKey(virtualAnimationId);
        if (animationKey == null) {
            errors.add(RegistryErrorHelper.getError(
                    RegistryErrorHelper.ErrorsType.NULL_ANIMATION_KEY, name, modId, virtualAnimationId, null));
            return null;
        }

        return AnimationHelper.getStaticAnimationAccessor(animationKey);
    }

    public static AnimationManager.AnimationAccessor<? extends StaticAnimation> resolveAnimation(
            String modId, String name, String postfix, ConditionalType type, ResourceLocation animationId, List<String> errors) {
        var stringKey = type.toString().toLowerCase(Locale.ROOT);
        var virtualAnimationId = VirtualAnimationRegistry.getParsedSkill(modId, name + "_" + postfix + "_" + stringKey);
        VirtualAnimationRegistry.manualVirtualizationProtocol(animationId, virtualAnimationId);

        var animationKey = AnimationManager.byKey(virtualAnimationId);
        if (animationKey == null) {
            errors.add(RegistryErrorHelper.getError(
                    RegistryErrorHelper.ErrorsType.NULL_ANIMATION_KEY, name, modId, virtualAnimationId, null));
            return null;
        }

        return AnimationHelper.getStaticAnimationAccessor(animationKey);
    }

    public static AnimationManager.AnimationAccessor<? extends StaticAnimation> resolveChargingAnimation(
            String modId, String name, ResourceLocation animationId, List<String> errors) {
        var virtualAnimationId = VirtualAnimationRegistry.getParsedSkill(modId, name + "_charging");
        VirtualAnimationRegistry.manualVirtualizationProtocol(animationId, virtualAnimationId);

        var animationKey = AnimationManager.byKey(virtualAnimationId);
        if (animationKey == null) {
            errors.add(RegistryErrorHelper.getError(
                    RegistryErrorHelper.ErrorsType.NULL_ANIMATION_KEY, name, modId, virtualAnimationId, null));
            return null;
        }

        return AnimationHelper.getStaticAnimationAccessor(animationKey);
    }
}

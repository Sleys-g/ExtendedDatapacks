package sleys.efedp.mixins.epicfight;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sleys.efedp.ExtendedDatapacks;
import yesman.epicfight.registry.EpicFightRegistries;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.WeaponCapability;
import yesman.epicfight.world.capabilities.item.WeaponTypeReloadListener;

@Mixin(WeaponTypeReloadListener.class)
public abstract class WeaponTypeReloadListenerMixin {

    @SuppressWarnings("removal")
    @Inject(
            method = "deserializeWeaponCapabilityBuilder",
            at = @At("RETURN"),
            cancellable = true,
            remap = false
    )
    private static void injectPassiveSkill(ResourceLocation rl, CompoundTag tag, CallbackInfoReturnable<WeaponCapability.Builder> cir) {
        WeaponCapability.Builder builder = cir.getReturnValue();
        if (tag.contains("passive_skill")) {
            String skillId = tag.getString("passive_skill");
            Skill skill = EpicFightRegistries.SKILL.get(ResourceLocation.tryParse(skillId));
            if (skill != null) {
                if (skill.getCategory().equals(SkillCategories.WEAPON_PASSIVE)) {
                    builder.passiveSkill(skill);
                    ExtendedDatapacks.LOGGER.info("[Weapon Type Builder] Assigning the passive skill '{}' to the category '{}'", skill, rl);
                } else {
                    ExtendedDatapacks.LOGGER.warn("[Weapon Type Builder] An attempt was made to assign a skill to the category '{}', but '{}' is not a passive weapon skill.", rl, skill);
                }
            } else {
                ExtendedDatapacks.LOGGER.warn("[Weapon Type Builder] The passive skill '{}' was not found, it failed to be assigned to the category '{}'", skillId, rl);
            }
        }

        if (tag.contains("zoom_in_type")) {
            String zoom_in_type = tag.getString("zoom_in_type");
            CapabilityItem.ZoomInType zoomInType = null;
            switch (zoom_in_type) {
                case "none" -> zoomInType = CapabilityItem.ZoomInType.NONE;
                case "always" -> zoomInType = CapabilityItem.ZoomInType.ALWAYS;
                case "use_tick" -> zoomInType = CapabilityItem.ZoomInType.USE_TICK;
                case "aiming" -> zoomInType = CapabilityItem.ZoomInType.AIMING;
                case "custom" -> zoomInType = CapabilityItem.ZoomInType.CUSTOM;
            }

            if (zoomInType != null) {
                builder.zoomInType(zoomInType);
                ExtendedDatapacks.LOGGER.info("[Weapon Type Builder] Assigning zoom type '{}' to category '{}'", zoomInType, rl);
            } else {
                ExtendedDatapacks.LOGGER.info("[Weapon Type Builder] The zoom '{}' is not a convertible zoom input and is valid for '{}'", zoom_in_type, rl);
            }
        }
        cir.setReturnValue(builder);
    }
}




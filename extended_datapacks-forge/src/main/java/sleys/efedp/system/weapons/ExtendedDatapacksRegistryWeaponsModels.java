package sleys.efedp.system.weapons;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sleys.efedp.system.weapons.json.WeaponPerStyleModelBaker;
import sleys.sl.library.runtime.policy.error.ErrorPolicy;

import java.util.HashMap;
import java.util.Map;

public class ExtendedDatapacksRegistryWeaponsModels {
    private static final Map<ResourceLocation, ModelResourceLocation> MODEL_RESOURCE_LOCATION = new HashMap<>();

    public static Map<ResourceLocation, ModelResourceLocation> getModelResourceLocation() {
        return MODEL_RESOURCE_LOCATION;
    }

    public static ModelResourceLocation getModelResourceLocation(ResourceLocation model) {
        return MODEL_RESOURCE_LOCATION.get(model);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void registryStyleModels(ModelEvent.RegisterAdditional registerAdditional) {
        var modelsSet = WeaponPerStyleModelBaker.getAllModels();
        for (var model : modelsSet) {
            ErrorPolicy.DEPURATE_ERROR.executeTaskWithMsg(
                    () -> setModelResourceLocation(registerAdditional, model),
                    "[Registry Style Models] Fatal error when trying to register the model: " + model
            );
        }
    }

    private static void setModelResourceLocation(ModelEvent.RegisterAdditional registerAdditional, ResourceLocation rawModel) {
        ResourceLocation model = getModelPath(rawModel);
        var modelResourceLocation = new ModelResourceLocation(model, "inventory");
        registerAdditional.register(modelResourceLocation);
        MODEL_RESOURCE_LOCATION.put(rawModel, modelResourceLocation);
    }

    private static ResourceLocation getModelPath(ResourceLocation rawModel) {
        ResourceLocation model = rawModel;

        if (rawModel.getPath().startsWith("item/")) {
            String cleanPath = rawModel.getPath().substring("item/".length());
            model = ResourceLocation.fromNamespaceAndPath(rawModel.getNamespace(), cleanPath);
        }

        return model;
    }
}

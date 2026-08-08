package sleys.efedp.system.weapons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.weapons.json.WeaponPerStyleModelBakerBuilder;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;

import java.util.HashMap;
import java.util.Map;

public class WeaponsModelsRegistry {
    private static final Map<ResourceLocation, ModelResourceLocation> MODEL_RESOURCE_LOCATION = new HashMap<>();

    public static Map<ResourceLocation, ModelResourceLocation> getModelResourceLocation() {
        return MODEL_RESOURCE_LOCATION;
    }

    public static ModelResourceLocation getModelResourceLocation(ResourceLocation model) {
        return MODEL_RESOURCE_LOCATION.get(model);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registryStyleModels(ModelEvent.RegisterAdditional registerAdditional) {
        var modelsSet = WeaponPerStyleModelBakerBuilder.getAllModels();
        for (var model : modelsSet) {
            ExecutionTasks.runAndGetResult(
                    ExecutionPolicy.RESIST,
                    () -> setModelResourceLocation(registerAdditional, model)
            ).ifFailure(e ->
                    ExtendedDatapacks.LOGGER.error(
                            "[Registry Style Models] Fatal error when trying to register the model: {}",
                            model
                    )
            );
        }
    }

    private static ResourceLocation getModelPath(ResourceLocation rawModel) {
        ResourceLocation model = rawModel;

        if (rawModel.getPath().startsWith("item/")) {
            String cleanPath = rawModel.getPath().substring("item/".length());
            model = ResourceLocation.fromNamespaceAndPath(rawModel.getNamespace(), cleanPath);
        }

        return model;
    }

    private static void setModelResourceLocation(ModelEvent.RegisterAdditional registerAdditional, ResourceLocation rawModel) {
        ResourceLocation model = getModelPath(rawModel);

        if (!modelFileExists(model)) {
            ExtendedDatapacks.LOGGER.warn("[Registry Style Models] Model not found, omitted: {}", model);
            return;
        }

        var modelResourceLocation = new ModelResourceLocation(model, "inventory");
        registerAdditional.register(modelResourceLocation);
        MODEL_RESOURCE_LOCATION.put(rawModel, modelResourceLocation);
    }

    private static boolean modelFileExists(ResourceLocation model) {
        ResourceLocation modelFile = ResourceLocation.fromNamespaceAndPath(
                model.getNamespace(),
                "models/item/" + model.getPath() + ".json"
        );
        return Minecraft.getInstance()
                .getResourceManager()
                .getResource(modelFile)
                .isPresent();
    }
}

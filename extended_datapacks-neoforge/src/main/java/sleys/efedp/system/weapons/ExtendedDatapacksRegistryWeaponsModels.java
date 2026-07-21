package sleys.efedp.system.weapons;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.weapons.json.WeaponPerStyleModelBaker;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;

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

    private static void setModelResourceLocation(ModelEvent.RegisterAdditional registerAdditional,
                                                 ResourceLocation model) {
        var modelResourceLocation = ModelResourceLocation.standalone(model);
        registerAdditional.register(modelResourceLocation);
        MODEL_RESOURCE_LOCATION.put(model, modelResourceLocation);
    }
}

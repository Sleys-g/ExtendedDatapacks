package sleys.efedp.system.thirdparty.combatevolution;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.shelmarow.combat_evolution.api.event.RegisterCustomExecutionEvent;
import sleys.efedp.main.ExtendedDatapacks;
import sleys.efedp.system.innates.RegistryInnateHelper;
import sleys.efedp.system.thirdparty.combatevolution.json.ExecutionAnimationBuilderModifier;
import yesman.epicfight.api.animation.AnimationManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExtendedDatapacksRegistryExecution {
    private static final List<String> RUNTIME_ERRORS = Collections.synchronizedList(new ArrayList<>());
    private ExtendedDatapacksRegistryExecution() {}


    @SubscribeEvent
    public static void registerExecution(RegisterCustomExecutionEvent event) {

        ExtendedDatapacks.LOGGER.info("[Execution Animation Registry] Registering JSON Execution");

        var data = ExecutionAnimationBuilderModifier.getExecutionData();
        if (data.isEmpty()) {
            ExtendedDatapacks.LOGGER.info("[Execution Animation Registry] No JSON execution found");
            return;
        }

        for (var entry : data) {
            var itemCategory = entry.getParsedWeaponCategory();
            var item = entry.getParseItem();
            if (itemCategory != null && item != null) {
                RUNTIME_ERRORS.add(
                        RegistryInnateHelper.getError(
                                RegistryInnateHelper.ErrorsType.REGISTRY_BUILDER,
                                "Execution Registry Animation", "null",
                                null,
                                "A registration attempt can only be made for categories or objects, never for both."
                        )
                );
                continue;
            }

            String executionName = entry.executionAnimation();
            String executedName = entry.executedAnimation();
            
            ResourceLocation executionId = ResourceLocation.tryParse(executionName);
            ResourceLocation executedId = ResourceLocation.tryParse(executedName);

            if (executionId == null) {
                RUNTIME_ERRORS.add(RegistryInnateHelper.getError(
                        RegistryInnateHelper.ErrorsType.UNPARSEABLE,
                        "Execution Animation Registry", "null",
                        executionName, null
                ));
                continue;
            }
            
            if (executedId == null) {
                RUNTIME_ERRORS.add(RegistryInnateHelper.getError(
                        RegistryInnateHelper.ErrorsType.UNPARSEABLE,
                        "Executed Animation Registry", "null",
                        executionName, null
                ));
                continue;
            }

            try {
                var executionKey = AnimationManager.byKey(executionId);
                var executedKey = AnimationManager.byKey(executedId);
                if (executionKey == null) {
                    RUNTIME_ERRORS.add(
                            RegistryInnateHelper.getError(
                                    RegistryInnateHelper.ErrorsType.NULL_ANIMATION_KEY,
                                    "Execution Animation Registry", "null"
                                    , executionId, null)
                    );
                    continue;
                }
                
                if (executedKey == null) {
                    RUNTIME_ERRORS.add(
                            RegistryInnateHelper.getError(
                                    RegistryInnateHelper.ErrorsType.NULL_ANIMATION_KEY,
                                    "Executed Animation Registry", "null"
                                    , executionId, null)
                    );
                    continue;
                }


                if (itemCategory != null) {
                    event.registerExecutionByCategory(
                            entry.getParsedWeaponCategory(),
                            entry.getParseStyle(),
                            entry.getParsedExecutionManager(executionKey, executedKey)
                    );
                }

                if (item != null) {
                    event.registerExecutionByItem(
                            entry.getParseItemRegistry(),
                            entry.getParseStyle(),
                            entry.getParsedExecutionManager(executionKey, executedKey)
                    );
                }
            } catch (Exception e) {
                RUNTIME_ERRORS.add(
                        RegistryInnateHelper.getError(
                                RegistryInnateHelper.ErrorsType.REGISTRY_BUILDER,
                                "Execution Registry Animation", "null",
                                executionId, e.getCause()
                        )
                );
            }
        }
    }
}

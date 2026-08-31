package sleys.efedp.system.thirdparty.combatevolution;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.shelmarow.combat_evolution.api.event.RegisterCustomExecutionEvent;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.thirdparty.combatevolution.json.ExecutionAnimationBuilder;
import sleys.efedp.system.thirdparty.combatevolution.json.RegistryCombatEvolutionErrorHelper;
import sleys.sl.library.exceptions.RegistryObjectException;
import yesman.epicfight.api.animation.AnimationManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RegistryExecutionAnimations {
    private static final List<String> RUNTIME_ERRORS = Collections.synchronizedList(new ArrayList<>());
    private RegistryExecutionAnimations() {}


    @SubscribeEvent
    public static void registerExecution(RegisterCustomExecutionEvent event) {

        ExtendedDatapacks.LOGGER.info("[Execution Animation Registry] Registering JSON Execution");

        var data = ExecutionAnimationBuilder.getExecutionData();
        if (data.isEmpty()) {
            ExtendedDatapacks.LOGGER.info("[Execution Animation Registry] No JSON execution found");
            return;
        }

        for (var entry : data) {
            var itemCategory = entry.getParsedWeaponCategory();
            var item = entry.getParseItem();
            if (itemCategory != null && item != null) {
                RUNTIME_ERRORS.add(
                        RegistryCombatEvolutionErrorHelper.getError(
                                RegistryCombatEvolutionErrorHelper.ErrorsCombatEvolutionType.REGISTRY_BUILDER,
                                null, "A registration attempt can only be made for categories or objects, never for both."
                        )
                );
                continue;
            }

            String executionName = entry.executionAnimation();
            String executedName = entry.executedAnimation();

            ResourceLocation executionId = ResourceLocation.tryParse(executionName);
            ResourceLocation executedId = ResourceLocation.tryParse(executedName);

            if (executionId == null) {
                RUNTIME_ERRORS.add(RegistryCombatEvolutionErrorHelper.getError(
                        RegistryCombatEvolutionErrorHelper.ErrorsCombatEvolutionType.UNPARSEABLE,
                        executionName, null
                ));
                continue;
            }

            if (executedId == null) {
                RUNTIME_ERRORS.add(RegistryCombatEvolutionErrorHelper.getError(
                        RegistryCombatEvolutionErrorHelper.ErrorsCombatEvolutionType.UNPARSEABLE,
                        executionName, null
                ));
                continue;
            }

            try {
                var executionKey = AnimationManager.byKey(executionId);
                var executedKey = AnimationManager.byKey(executedId);
                if (executionKey == null) {
                    RUNTIME_ERRORS.add(
                            RegistryCombatEvolutionErrorHelper.getError(
                                    RegistryCombatEvolutionErrorHelper.ErrorsCombatEvolutionType.NULL_ANIMATION_KEY,
                                    executionId, null
                            )
                    );
                    continue;
                }

                if (executedKey == null) {
                    RUNTIME_ERRORS.add(
                            RegistryCombatEvolutionErrorHelper.getError(
                                    RegistryCombatEvolutionErrorHelper.ErrorsCombatEvolutionType.NULL_ANIMATION_KEY,
                                    executionId, null
                            )
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
                        RegistryCombatEvolutionErrorHelper.getError(
                                RegistryCombatEvolutionErrorHelper.ErrorsCombatEvolutionType.REGISTRY_BUILDER,
                                executionId, e.getCause()
                        )
                );
            }
        }
    }

    @SubscribeEvent
    public static void onClientModBusEvent(final FMLLoadCompleteEvent event) {
        if (!RUNTIME_ERRORS.isEmpty()) {
            throw new RegistryObjectException(
                    "Failure during the operation to create a Execution Animation...\n" +
                            "Total number of registry failures: " + RUNTIME_ERRORS.size() +
                            "\n\nProblematic Execution Animation\n\n" + String.join("\n", RUNTIME_ERRORS));
        }
    }
}

package sleys.efedp.forge.system.thirdparty.combatevolution;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.shelmarow.combat_evolution.api.event.RegisterCustomExecutionEvent;
import sleys.efedp.forge.ExtendedDatapacks;
import sleys.efedp.forge.api.registry.RegistryThirdPartiesTrackers;
import sleys.efedp.forge.system.thirdparty.combatevolution.definitions.ExecutionAnimationsDefinitions;
import sleys.efedp.forge.system.thirdparty.combatevolution.helper.RegistryCombatEvolutionErrorHelper;
import sleys.sl.library.exceptions.RegistryObjectException;
import sleys.sl.library.execution.policy.ErrorPolicy;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RegistryExecutionAnimations {
    private static final List<String> RUNTIME_ERRORS = Collections.synchronizedList(new ArrayList<>());

    private RegistryExecutionAnimations() {}

    @SubscribeEvent
    public static void registerExecution(RegisterCustomExecutionEvent event) {

        ExtendedDatapacks.LOGGER.info("[Execution Animation Registry] Registering JSON Execution");

        var data = RegistryThirdPartiesTrackers.EXECUTION_ANIMATIONS_REGISTRY.getDefinitionData().values();
        if (data.isEmpty()) {
            ExtendedDatapacks.LOGGER.info("[Execution Animation Registry] No JSON execution found");
            return;
        }

        for (var dataSet : RegistryThirdPartiesTrackers.EXECUTION_ANIMATIONS_REGISTRY.getDefinitionData().values()) {
            for (var entry : dataSet) {

                var itemCategory = entry.getParsedWeaponCategory();
                var item = entry.getParseItem();
                if (itemCategory != null && item != null) {
                    RUNTIME_ERRORS.add(
                            RegistryCombatEvolutionErrorHelper.getError(
                                    RegistryCombatEvolutionErrorHelper.ErrorsCombatEvolutionType.REGISTRY_BUILDER,
                                    null, "A registration attempt can only be made for categories or items, never for both."
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

                ExecutionTasks.runAndGetResult(
                                ExecutionPolicy.RESIST,
                                ErrorPolicy.DEPURATE,
                                "[Execution Animations Registry] Registering Execution Animations '[Execution " + executionId  + " Executed " + executionId + "]'",
                                () -> buildExecution(executionId, executedId, itemCategory, item, entry, event)
                        )
                        .peek(process ->  ExtendedDatapacks.LOGGER.info(
                                "[Execution Animations Registry] Registered Execution Animations: '[Execution: {} Executed: {}]' for {}",
                                executionId, executionId, (item == null ? "category: " + itemCategory : "item: " + item)
                        ))
                        .peekError(exception -> ExtendedDatapacks.LOGGER.fatal(
                                "[Execution Animations Registry] Error Stack: ", exception
                        ))
                        .ifFailure(exception -> RegistryCombatEvolutionErrorHelper.getError(
                                RegistryCombatEvolutionErrorHelper.ErrorsCombatEvolutionType.REGISTRY_BUILDER,
                                executionId, exception.getCause()
                        ));
            }
        }
    }

    private static void buildExecution(ResourceLocation executionId,
                                       ResourceLocation executedId,
                                       WeaponCategory itemCategory, Item item,
                                       ExecutionAnimationsDefinitions entry,
                                       RegisterCustomExecutionEvent event) {

        var executionKey = AnimationManager.byKey(executionId);
        var executedKey = AnimationManager.byKey(executedId);
        if (executionKey == null) {
            RUNTIME_ERRORS.add(
                    RegistryCombatEvolutionErrorHelper.getError(
                            RegistryCombatEvolutionErrorHelper.ErrorsCombatEvolutionType.NULL_ANIMATION_KEY,
                            executionId, null
                    )
            );
            return;
        }

        if (executedKey == null) {
            RUNTIME_ERRORS.add(
                    RegistryCombatEvolutionErrorHelper.getError(
                            RegistryCombatEvolutionErrorHelper.ErrorsCombatEvolutionType.NULL_ANIMATION_KEY,
                            executionId, null
                    )
            );
            return;
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

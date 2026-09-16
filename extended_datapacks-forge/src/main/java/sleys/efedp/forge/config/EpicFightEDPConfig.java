package sleys.efedp.forge.config;

import net.minecraftforge.common.ForgeConfigSpec;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;

public class EpicFightEDPConfig {

    public static final ForgeConfigSpec EPICFIGHT_CONFIG;

    public static final ForgeConfigSpec.BooleanValue USE_STAMINA_IN_CHARGED_ATTACKS;
    public static final ForgeConfigSpec.BooleanValue USE_WEIGHT_IN_CHARGED_ATTACKS;
    public static final ForgeConfigSpec.DoubleValue SET_WEIGHT_IN_CHARGED_ATTACKS;
    public static final ForgeConfigSpec.EnumValue<ExecutionPolicy> RUNNER_TYPE_ENUM_VALUE;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("Extended Datapacks");

        RUNNER_TYPE_ENUM_VALUE = builder
                .comment("""
                        Determine how Epic Fight EDP will handle errors.\
                        CATCH: Catches errors, preventing the game from closing if an error occurs. It does not provide any debugging data.\
                        EXCEPTION: Closes the game immediately upon an error. The closure is immediate and provides debugging data."""
                )
                .defineEnum("fatalEpicFightEDPRuntimeHandler", ExecutionPolicy.RESIST);


        builder.pop();
        builder.push("Extended Datapacks (Epic Fight Combat -/- Charged Attacks)");

        USE_STAMINA_IN_CHARGED_ATTACKS = builder
                .comment("Determine whether or not to use stamina when executing a charged attack.")
                .define("useStaminaInChargedAttacks", true)
        ;

        USE_WEIGHT_IN_CHARGED_ATTACKS = builder
                .comment("Determine whether or not to use weight when executing a charged attack,\n" +
                        "this means that as the player's weight increases, the stamina consumption per charged attack also increases. ")
                .define("useStaminaInChargedAttacks", true)
        ;

        SET_WEIGHT_IN_CHARGED_ATTACKS = builder
                .comment("""
                        Determine the value by which the stamina consumption result will be divided; this is a control factor.
                        The higher the value, the less stamina will be used, and the lower the value, the more realistic the value will be
                        (i.e., there is a greater demand for stamina).""")
                .defineInRange("setWeightDivisorValue", 2.3, 1.0, 4.0)
        ;
        builder.pop();
        EPICFIGHT_CONFIG = builder.build();
    }

    public static boolean getUseStaminaInChargedAttacks() {
        return ExecutionTasks.getAndFallback(
                ExecutionPolicy.RESIST,
                USE_STAMINA_IN_CHARGED_ATTACKS::get,
                true
        );
    }

    public static boolean getUseWeightInChargedAttacks() {
        return ExecutionTasks.getAndFallback(
                ExecutionPolicy.RESIST,
                USE_WEIGHT_IN_CHARGED_ATTACKS::get,
                true
        );
    }

    public static float getWeightValueInChargedAttacks() {
        return ExecutionTasks.getAndFallback(
                ExecutionPolicy.RESIST,
                SET_WEIGHT_IN_CHARGED_ATTACKS::get,
                2.3
        ).floatValue();
    }

    public static ExecutionPolicy getErrorHandlerEpicFightEDP() {
        return RUNNER_TYPE_ENUM_VALUE.get();
    }
}

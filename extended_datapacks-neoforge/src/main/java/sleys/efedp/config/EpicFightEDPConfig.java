package sleys.efedp.config;


import net.neoforged.neoforge.common.ModConfigSpec;
import sleys.sl.library.execution.policy.ExecutionPolicy;

public class EpicFightEDPConfig {

    public static final ModConfigSpec EDP;

    public static final ModConfigSpec.BooleanValue USE_STAMINA_IN_CHARGED_ATTACKS;
    public static final ModConfigSpec.BooleanValue USE_WEIGHT_IN_CHARGED_ATTACKS;
    public static final ModConfigSpec.DoubleValue SET_WEIGHT_IN_CHARGED_ATTACKS;

    public static final ModConfigSpec.EnumValue<ExecutionPolicy> RUNNER_TYPE_ENUM_VALUE;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("Extended Datapacks");

        RUNNER_TYPE_ENUM_VALUE = builder
                .comment("Determine how Epic Fight EDP will handle errors." +
                        "\n\nRESIST: Catches errors, preventing the game from closing if an error occurs. It does not provide any debugging data." +
                        "\n\nABORT: Closes the game immediately upon an error. The closure is immediate and provides debugging data."
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
                .comment("Determine the value by which the stamina consumption result will be divided; this is a control factor.\n" +
                        "The higher the value, the less stamina will be used, and the lower the value, the more realistic the value will be\n" +
                        "(i.e., there is a greater demand for stamina).")
                .defineInRange("setWeightDivisorValue", 2.3, 1.0, 4.0)
        ;
        builder.pop();
        EDP = builder.build();
    }

    public static boolean getUseStaminaInChargedAttacks() {
        return USE_STAMINA_IN_CHARGED_ATTACKS.get();
    }

    public static boolean getUseWeightInChargedAttacks() {
        return USE_WEIGHT_IN_CHARGED_ATTACKS.get();

    }

    public static float getWeightValueInChargedAttacks() {
        return SET_WEIGHT_IN_CHARGED_ATTACKS.get().floatValue();
    }

    public static ExecutionPolicy getErrorHandlerEpicFightEDP() {
        return RUNNER_TYPE_ENUM_VALUE.get();
    }
}

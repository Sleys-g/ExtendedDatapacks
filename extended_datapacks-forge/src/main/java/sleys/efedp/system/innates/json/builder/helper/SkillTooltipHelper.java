package sleys.efedp.system.innates.json.builder.helper;

import sleys.efedp.system.innates.json.data.ConditionalType;
import sleys.sl.library.annotations.Experimental;

public final class SkillTooltipHelper {
    private SkillTooltipHelper() {}

    private static final String[] STRIKE_NAMES = {
            "First ",
            "Second ",
            "Third ",
            "Fourth ",
            "Fifth ",
            "Sixth ",
            "Seventh ",
            "Eighth ",
            "Ninth ",
            "Tenth "
    };

    @Experimental(since = "2.4.6", note = "The input value is expected to always be at least 0")
    public static String intToOrdinalString(int strike, int maxStrike) {
        return maxStrike == 0 ? "On Strike:" :
                strike >= 0 && strike < STRIKE_NAMES.length ?
                        (STRIKE_NAMES[strike] + "Strike:") :
                        "Each Strike:";
    }

    @Experimental(since = "2.4.6", note = "The input (strike) value is expected to always be at least 0")
    public static String intToOrdinalString(int strike, int maxStrike, ConditionalType types) {
        return maxStrike == 0 ? "On " + types.tooltip :
                strike >= 0 && strike < STRIKE_NAMES.length ?
                        STRIKE_NAMES[strike] + types.tooltip :
                        "Each Strike:";
    }
}
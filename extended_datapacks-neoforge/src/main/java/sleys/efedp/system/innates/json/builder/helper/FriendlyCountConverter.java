package sleys.efedp.system.innates.json.builder.helper;

public class FriendlyCountConverter {

    private static final String[] WORDS = {
            "one", "two", "three", "four", "five",
            "six", "seven", "eight", "nine", "ten",
            "eleven", "twelve", "thirteen", "fourteen",
            "fifteen", "sixteen", "seventeen",
            "eighteen", "nineteen", "twenty"
    };

    public static String as(int number) {
        if (number >= 0 && number < WORDS.length) return WORDS[number];
        return String.valueOf(number);
    }
}

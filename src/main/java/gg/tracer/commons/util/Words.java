package gg.tracer.commons.util;

/**
 * @author Bradley Steele
 */
public final class Words {

    private Words() {}

    public static String title(String s) {
        StringBuilder builder = new StringBuilder();
        boolean titleNext = true;

        for (char c : s.toLowerCase().toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                titleNext = true;
            } else if (titleNext) {
                c = Character.toTitleCase(c);
                titleNext = false;
            }

            builder.append(c);
        }

        return builder.toString();
    }
}

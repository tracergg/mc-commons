package gg.tracer.commons.util;

import org.bukkit.ChatColor;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Bradley Steele
 */
public final class Messages {

    public static final char ALT_COLOR_CODE = '&';
    public static final char BUKKIT_COLOR_CODE = ChatColor.COLOR_CHAR;

    private Messages() {}

    /**
     * Converts alternative color codes into bukkit color codes
     * of a given string.
     *
     * @param s the string to color.
     * @return the colored string.
     */
    public static String color(String s) {
        return s == null ? null : ChatColor.translateAlternateColorCodes(ALT_COLOR_CODE, s);
    }

    public static List<String> color(Iterable<? extends String> it) {
        if (it == null) {
            return null;
        }

        return StreamSupport.stream(it.spliterator(), false)
                .filter(Objects::nonNull)
                .map(Messages::color)
                .collect(Collectors.toList());
    }

    public static List<String> color(String... args) {
        return color(Arrays.asList(args));
    }

    /**
     * Converts bukkit color codes into alternate color codes
     * of a given string.
     *
     * @param s the string to uncolor.
     * @return the uncolored string.
     */
    public static String uncolor(String s) {
        if (s == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        CharacterIterator it = new StringCharacterIterator(s);

        for (char c = it.first(); c != CharacterIterator.DONE; c = it.next()) {
            if (c == BUKKIT_COLOR_CODE) {
                builder.append(ALT_COLOR_CODE);
            } else {
                builder.append(c);
            }
        }

        return builder.toString();
    }

    public static List<String> uncolor(Iterable<? extends String> it) {
        if (it == null) {
            return null;
        }

        return StreamSupport.stream(it.spliterator(), false)
                .filter(Objects::nonNull)
                .map(Messages::uncolor)
                .collect(Collectors.toList());
    }

    public static List<String> uncolor(String... args) {
        return color(Arrays.asList(args));
    }
}

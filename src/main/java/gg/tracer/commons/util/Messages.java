package gg.tracer.commons.util;

import org.bukkit.ChatColor;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Bradley Steele
 */
public final class Messages {

    public static final char ALT_COLOR_CODE = '&';
    public static final char BUKKIT_COLOR_CODE = ChatColor.COLOR_CHAR;

    public static final Pattern HEX_PATTERN = Pattern.compile("(?<!\\\\)(&?)(#[a-fA-F0-9]{6})");

    private Messages() {}

    /**
     * Converts alternative color codes into bukkit color codes
     * of a given string.
     *
     * @param s the string to color.
     * @return the colored string.
     */
    public static String color(String s) {
        if (s == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder(s.length());
        Matcher m = HEX_PATTERN.matcher(s);

        while (m.find()) {
            String hex = m.group(2);
            StringBuilder res = new StringBuilder(BUKKIT_COLOR_CODE + "x");

            for (char digit : hex.toCharArray()) {
                res.append(BUKKIT_COLOR_CODE).append(digit);
            }

            m.appendReplacement(sb, Matcher.quoteReplacement(res.toString()));
        }

        m.appendTail(sb);

        return ChatColor.translateAlternateColorCodes(ALT_COLOR_CODE, sb.toString());
    }

    public static List<String> color(Iterable<? extends String> it) {
        if (it == null) {
            return null;
        }

        List<String> out = new ArrayList<>();

        for (String s : it) {
            if (s == null) {
                continue;
            }

            if (s.isEmpty()) {
                out.add(s);
            } else {
                out.add(color(s));
            }
        }

        return out;
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

        List<String> out = new ArrayList<>();

        while (it.iterator().hasNext()) {
            String s = it.iterator().next();

            if (s == null) {
                continue;
            }

            if (s.isEmpty()) {
                out.add(s);
            } else {
                out.add(uncolor(s));
            }
        }

        return out;
    }

    public static List<String> uncolor(String... args) {
        return color(Arrays.asList(args));
    }
}

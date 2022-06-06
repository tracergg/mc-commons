package gg.tracer.commons.register.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Bradley Steele
 */
public class CommandOptions {

    public static CommandOptions.Result parse(String[] args) {
        Result result = new Result();

        String option = null;
        List<String> options = null;

        for (String arg : args) {
            if (arg.charAt(0) == '-') {
                if (arg.length() < 2) {
                    continue;
                }

                option = arg;
                options = new ArrayList<>();

                // possible for an option to have no value
                result.args.put(arg, "");
            } else if (options != null) {
                options.add(arg);
                result.args.put(option, String.join(" ", options));
            }
        }

        return result;
    }

    public static class Result {

        private final Map<String, String> args = new HashMap<>();

        public boolean hasOption(String arg) {
            return args.containsKey(arg);
        }

        public String getOption(String arg) {
            return args.get(arg);
        }
    }
}

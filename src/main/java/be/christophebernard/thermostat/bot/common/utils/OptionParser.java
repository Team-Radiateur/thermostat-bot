package be.christophebernard.thermostat.bot.common.utils;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class OptionParser {
    public static OptionData parse(String option) throws IllegalArgumentException {
        String[] parts = option.split("\\|");
        String type = null;
        String name = null;
        String description = null;

        for (String part : parts) {
            String[] subParts = part.split(": ");
            switch (subParts[0].toLowerCase()) {
                case "type" -> type = subParts[1];
                case "name" -> name = subParts[1];
                case "description" -> description = subParts[1];
            }
        }

        if (type == null || name == null || description == null) {
            throw new IllegalArgumentException("Invalid option: " + option);
        }

        boolean required = Boolean.parseBoolean(parts[3].split(": ")[1]);
        return new OptionData(OptionType.valueOf(type), name, description, required);
    }
}

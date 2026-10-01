package de.rayzs.pat.utils.configuration.helper;

import de.rayzs.pat.api.storage.storages.ConfigStorage;
import java.io.Serializable;
import java.util.*;

public class MultipleMessagesHelper implements Serializable {

    private final List<String> lines;

    public MultipleMessagesHelper(ConfigStorage config, String path, List<String> input) {
        final ConfigSectionHelper<ArrayList<String>> sectionHelper = new ConfigSectionHelper<>(config, path, input);
        final Object resultObj = sectionHelper.getOrSet();

        if (resultObj instanceof List<?> list) {
            lines = new ArrayList<>(list.size());

            for (Object entry : list) {
                final String line = toLine(entry);
                if (line != null) lines.add(line);
            }

            return;
        }

        lines = input;
    }

    public List<String> getLines() {
        return lines;
    }

    /*
     * YAML entries like "- bukkit: pl" or "- plugins:" are parsed as maps instead of strings.
     * Convert them back into "key:value" / "key" so they don't break casting later on.
     */
    private static String toLine(Object entry) {
        if (entry == null) return null;

        if (entry instanceof Map<?, ?> map) {
            final StringJoiner joiner = new StringJoiner(" ");

            for (Map.Entry<?, ?> mapEntry : map.entrySet()) {
                final Object value = mapEntry.getValue();
                joiner.add(value == null ? String.valueOf(mapEntry.getKey()) : mapEntry.getKey() + ":" + toLine(value));
            }

            return joiner.toString();
        }

        return String.valueOf(entry);
    }
}

package at.rueckgr.smarthome.events.server;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ClientCommandProcessor {
    private static final Map<String, Command> COMMANDS;

    static {
        // TODO use annotations and reflections here
        final HashMap<String, Command> commandsMap = new HashMap<>();
        commandsMap.put("ping", new PingCommand());
        commandsMap.put("observation", new ObservationCommand());
        commandsMap.put("status", new StatusCommand());

        COMMANDS = Collections.unmodifiableMap(commandsMap);
    }

    public String processCommand(final String line) {
        if(StringUtils.isBlank(line)) {
            return null;
        }

        final String[] parts = StringUtils.split(line);
        Validate.isTrue(parts.length > 0); // parts,length cannot be 0 due to the StringUtils.isBlank() check above

        final String commandName = parts[0].toLowerCase();
        if(!COMMANDS.containsKey(commandName)) {
            return null;
        }

        final Command command = COMMANDS.get(commandName);
        return command.execute(parts);
    }
}

package at.rueckgr.smarthome.events.server;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.reflections.Reflections;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ClientCommandProcessor {
    private static final Map<String, Command> COMMANDS;

    static {
        final Map<String, Command> commandsMap =
                new Reflections(Command.class.getPackage().getName())
                        .getSubTypesOf(Command.class)
                        .stream()
                        .map(ClientCommandProcessor::newInstance)
                        .collect(Collectors.toMap(Command::getName, Function.identity()));

        COMMANDS = Collections.unmodifiableMap(commandsMap);
    }


    private static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
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

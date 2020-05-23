package at.rueckgr.smarthome.events.server.command;

public class PingCommand implements Command {
    private static final String NAME = "ping";

    @Override
    public String execute(final String[] parts) {
        if(parts.length > 1) {
            return null;
        }

        return "PONG";
    }

    @Override
    public String getName() {
        return NAME;
    }
}

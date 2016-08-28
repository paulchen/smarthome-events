package at.rueckgr.smarthome.events.server;

public class PingCommand implements Command {
    @Override
    public String execute(final String[] parts) {
        if(parts.length > 1) {
            return null;
        }

        return "PONG";
    }
}

package at.rueckgr.smarthome.events.server.command;

public interface Command {
    String execute(String[] parts);

    String getName();
}

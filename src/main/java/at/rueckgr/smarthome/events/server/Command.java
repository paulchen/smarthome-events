package at.rueckgr.smarthome.events.server;

public interface Command {
    String execute(String[] parts);

    String getName();
}

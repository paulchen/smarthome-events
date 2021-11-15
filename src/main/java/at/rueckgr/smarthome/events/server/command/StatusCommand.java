package at.rueckgr.smarthome.events.server.command;

import at.rueckgr.smarthome.events.model.HealthStateDescription;
import at.rueckgr.smarthome.events.model.SystemHealth;
import at.rueckgr.smarthome.events.server.SystemStateManager;

import java.text.MessageFormat;

public class StatusCommand implements Command {
    private static final String NAME = "status";

    @Override
    public String execute(final String[] parts) {
        if(parts.length > 1) {
            return null;
        }

        final SystemStateManager systemStateManager = SystemStateManager.getInstance();
        SystemHealth systemHealth = systemStateManager.getHealth();

        HealthStateDescription healthStateDescription = systemHealth.getOverallHealth();
        return MessageFormat.format("{0} {1}", healthStateDescription.getHealthState().ordinal(), healthStateDescription.getDescription());
    }

    @Override
    public String getName() {
        return NAME;
    }
}

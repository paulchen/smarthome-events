package at.rueckgr.smarthome.events.server;

import at.rueckgr.smarthome.events.model.HealthState;
import at.rueckgr.smarthome.events.model.HealthStateDescription;
import at.rueckgr.smarthome.events.model.SystemHealth;

import java.text.MessageFormat;
import java.util.Map;

public class StatusCommand implements Command {
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
}
